package com.vol.solunote.comm.service.stt;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.Exception.SttCallException;
import com.vol.solunote.batch.task.MeetingLauncher;
import com.vol.solunote.batch.task.SoundLauncher;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.disk.DiskService;
import com.vol.solunote.comm.util.CommonUtil;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.meeting.MeetingSpeakerVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.repository.test.TestRepository;
import com.vol.solunote.repository.transcription.TranscriptionRepository;
import com.vol.solunote.repository.meeting.MeetingRepository;
import com.vol.solunote.repository.meeting.MeetingSpeakerRepository;
import com.vol.solunote.repository.sound.SoundRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SttServiceImpl implements SttService {
	
	private SttService self;
	
	@Autowired
	CommonSteelServiceImpl	commonService;	
	
	@Autowired
	private MeetingRepository meetingRepository;
	
	@Autowired
	private MeetingSpeakerRepository meetingSpeakerRepository;
	
	@Autowired
	private TranscriptionRepository transcriptionRepository;
		
	private static final int ERROR_MESSAGE_LEN = 200;
	private static final int CHANNEL_COUNT = 1;
	
	@Autowired
	private DiskService diskService;
	
	
	@Autowired
	private SoundRepository soundRepository;	
	
	@Autowired
	private TestRepository testRepository;
	
	@Value("${stt.retry-days:5}")
	private int sttRetryDays;
	
	@Value("${stt.alternate.curl.size:500000000}")
	private long sttAlternateCurlSize;
	
	@Value("${stt.alternate.curl.command:/usr/bin/curl}")
	private String[] sttAlternateCurlCommand;
	
	@Value("${stt.alternate.curl.quote:'}")
	private String quote;
	
	@Value("${stt.confidence:false}")
	private boolean sttConfidence;
	
	@Value("${stt.url}")
	private String sttUrl;

	@Value("${stt.multi-lang:false}")
	private boolean sttMultiLang;	
	
	
	@Override
	public boolean backendStt(Category category, MeetingVo vo, String sttUrl) throws Exception {
		log.debug(vo.toString());
		
		String root = diskService.getUploadPath(category);
		
		Path path = Paths.get(root, vo.getFileNewNm());
		
		if ( ! Files.exists(path)) {
			log.error("ERROR : file not found for {}", path.toString());
			vo.setStatus(SoundLauncher.API_STATUS_NOT_FOUND);			
			vo.setErrorMessage("file not found: " + vo.getFileNewNm());			
			// txn-1 : callStt() 가 오랜 시간 소요되므로 txn 시간을 줄이기 위하여 updateMeetApi() 에서  txn 을 사용함
			self.updateSound(category, vo);  // 이것이 txn method
			return false;
		}
		
		// 1. stt 요청
		FileSystemResource resource = new FileSystemResource(path);
		Map<String, Object> resultMap = null;
		try {
			resultMap = callStt(resource, sttUrl, true);
		} catch ( SttCallException sce ) {
			// 2-1. 에러 처리함
			log.debug("INSERT-DB 1 : {}", sce.toString());
			return self.updateSoundWithException(category, vo, sce);
		} catch ( Exception e ) {
			log.debug("INSERT-DB 2 : {}", e.toString());
			vo.setErrorMessage(e.getMessage());			
			self.updateSound(category, vo);
			return false;
		}
		
		BigDecimal bigDecimal = new BigDecimal(resultMap.get("duration").toString());
		BigDecimal durationMs = bigDecimal.multiply(new BigDecimal(1000));
		vo.setTimeDurationMs(durationMs.toString());
		vo.setSttStartedAt((LocalDateTime)resultMap.get("stt_started_at"));
		vo.setSttDuration((String)resultMap.get("stt_duration"));
		vo.setStatus(SoundLauncher.API_STATUS_STT);
		
		// 2. STT 결과를 DB 에 저장
		// txn-2 : callStt() 가 오랜 시간 소요되므로 txn 시간을 줄이기 위하여 parseDiarizeAndSttOnly() 에서 txn 사용함 
		log.info("parseDiarizeAndSttOnly in SttServiceImpl ");
		String msg = self.parseDiarizeAndSttOnly(category, vo, resultMap, true, null);  // 이것이 txn method
		
		if ( msg != null ) {
			// 2-3. text 내용이 아무것도 없는 경우 에러 처리함
			log.debug("INSERT-DB 3 : {} for = {}", msg, resultMap);
			SttCallException sce = new SttCallException("self", msg);
			updateSoundWithException(category, vo, sce);
			return true;
		}
		
		log.debug("resultMap = {}", resultMap);
		
		return false;			
	}
	

	@Override
	public Map<String, Object> callStt(Resource resource, String sttUrl, boolean callCurl) throws Exception {
		
		long contentLength = resource.contentLength();
		log.debug("contentLength = " + contentLength);
		
		String result = null;
		
		Instant start = Instant.now();
		log.debug("2. stt started = {} data {}", start.toString(), resource.getFilename());
		
		if ( callCurl == true && contentLength > sttAlternateCurlSize ) {
			result = callSttCurl(resource, sttUrl);
		} else {
			result = callSttRest(resource, sttUrl);
		}
		
		Instant finish = Instant.now();
  	    int min = Math.min(400, result.length());
		log.debug("stt ended   = {} data {}", finish.toString(), result.substring(0, min));
		
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> resultMap = objectMapper.readValue(result, new TypeReference<HashMap <String, Object>>() {});
		
		long timeElapsed = Duration.between(start, finish).toMillis();
		String sttDuration = DurationFormatUtils.formatDuration(timeElapsed, "HH:mm:ss");
		resultMap.put("stt_duration", sttDuration);
		resultMap.put("stt_started_at", LocalDateTime.ofInstant(start, ZoneId.of("Asia/Seoul")));
		
		if ( sttConfidence == true ) {
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>)resultMap.get("stt_result");
			
			for( Map<String, Object> map : list ) {
				if ( map.get("confidence") != null ) {
					log.info("DELTE THIS CODE !!!!");
					continue;
				}
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> wordList = (List<Map<String, Object>>) map.get("word_confidence");
				double avg = wordList.stream().mapToDouble( m ->  (double)m.get("confidence")).average().orElse(Double.NaN);
				map.put("confidence", String.format("%.2f", avg));
			}
		}
				
		return resultMap;
	}
	
	@Override
	public Map<String, Object> callSttForMenu(org.springframework.core.io.Resource resource, boolean callCurl, String	letter)
			throws Exception {

		long contentLength = resource.contentLength();
		log.debug("contentLength = " + contentLength);

		Map<String, Object> resultMap = null;

		Instant start = Instant.now();
		log.debug("3. stt started = {} data {} url {} letter {} ", start.toString(), resource.getFilename(), callCurl,
				letter);

		if (callCurl == true && contentLength > sttAlternateCurlSize) {
			resultMap = callSttCurlForMenu(resource, letter);
		} else {
			resultMap = callSttRestForMenu(resource, letter);
		}

		Instant finish = Instant.now();

		long timeElapsed = Duration.between(start, finish).toMillis();
		String sttDuration = DurationFormatUtils.formatDuration(timeElapsed, "HH:mm:ss");
		
		log.debug("stt_duration = " + sttDuration);
		
		resultMap.put("stt_duration", sttDuration);
		resultMap.put("stt_started_at", LocalDateTime.ofInstant(start, ZoneId.of("Asia/Seoul")));

		return resultMap;
	}
	
	private Map<String, Object> callSttCurlForMenu(org.springframework.core.io.Resource resource, String letter)
			throws Exception {

		String url = this.sttUrl;
		if (this.sttMultiLang == true) {
			if (letter == null) {
				letter = "ko";
			}
			url += "/" + letter;
		}

		List<String> cmds = new ArrayList<>();
		cmds.addAll(Arrays.asList(sttAlternateCurlCommand));

		String[] args = { "-X", "POST", url, "-H", quote + "accept: application/json" + quote, "-H",
				quote + "Content-Type: multipart/form-data" + quote, "-F" };
		cmds.addAll(Arrays.asList(args));

		StringBuilder buffer = new StringBuilder();
//		buffer.append(quote);
		buffer.append("file=@");
		buffer.append(resource.getFile().getAbsolutePath());
		buffer.append(";type=");
		buffer.append(CommonUtil.getMimeType(resource.getFilename()));
//		buffer.append(quote);

		cmds.add(buffer.toString());

//		String[] cmds = { "cmd", "/c", "curl", "-X", "POST", "-H", "accept: application/json", "-H", "Content-Type: multipart/form-data", "-F", "file=@D:/e2e///sgsas_train/upload/meet\\2023/11/88bfa814-c63c-4efa-8656-f0b71fb67449.wav;type=audio/wav", url };

		log.debug("stt CURL cmd {}", String.join(" ", cmds));

		String result = new ProcessExecutor().command(cmds)
				.redirectError(
							Slf4jStream.of(this.getClass()).asInfo()
						).readOutput(true).execute()
				.outputUTF8();

		log.debug("curl result = {}", result);

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> resultMap = objectMapper.readValue(result, new TypeReference<HashMap<String, Object>>() {
		});

		return resultMap;
	}	
	
	public Map<String, Object> callSttRestForMenu(org.springframework.core.io.Resource resource, String letter) throws Exception {

		String url = this.sttUrl;
		if (this.sttMultiLang == true) {
			if (letter == null) {
				letter = "ko";
			}
			url += "/" + letter;
		}
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", resource);

		Map<String, Object> resultMap = commonService.callRestTemplateGen(httpHeaders, HttpMethod.POST, url, body);

		log.debug("rest resultMap = {}", resultMap);

		return resultMap;
	}
	
	private	void	createSpeakerAndResult( List<Map<String, Object>> sttResultListMap, int meetSeq )
	{
		List<MeetingSpeakerVo> meetingSpeakerList = new ArrayList<>();
		
		for (Map<String, Object> stringObjectMap : sttResultListMap) {

			Long meetingSpeakerId = null;
			for (MeetingSpeakerVo speaker : meetingSpeakerList) {
				if (speaker.getName().equals(stringObjectMap.get("speaker").toString())) {
					meetingSpeakerId = speaker.getMeetingSpeakerId();
					log.debug("Speaker Id = {}", meetingSpeakerId);
				}
			}

			// 3. meeting_speaker
			if (meetingSpeakerId == null) {
				MeetingVo meeting = new MeetingVo();
				meeting.setSeq(meetSeq);
				MeetingSpeakerVo meetingSpeaker = new MeetingSpeakerVo();
				meetingSpeaker.setName(stringObjectMap.get("speaker").toString());
				meetingSpeaker.setSeq(meetSeq);
				
				int	result = meetingSpeakerRepository.saveMeetingSpeaker(meetingSpeaker);
				
				if (result > 0)
				{
					meetingSpeaker = meetingSpeakerRepository.findByNameAndMeeting_Seq(meetingSpeaker.getName(), meetingSpeaker.getSeq());
					meetingSpeakerList.add(meetingSpeaker);
					meetingSpeakerId = meetingSpeaker.getMeetingSpeakerId();					
				}
				else
				{
					return;
				}				
			}
			// 4. TB_CS_MEETING_RESULT
			meetingRepository.createMeetResult(stringObjectMap.get("start").toString(), stringObjectMap.get("end").toString(),
					stringObjectMap.get("text").toString(), meetingSpeakerId, meetSeq);				
		}
	}
	
	
	@Override
	@Transactional
	public Map<String, String> parseDiarizeAndStt(Map<String, Object> param, Map<String, Object> resultMap) throws Exception {

		Map<String, String> map = new HashMap<>();

		if (param.get("errorMessage") != null) {
			map.put("errorMessage", (String) param.get("errorMessage"));
			meetingRepository.createMeet(param);
			return map;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> checkStt = (List<Map<String, Object>>) resultMap.get("stt_result");

		if (checkStt == null || checkStt.isEmpty()) {
			map.put("errorMessage", "speaker diarize Faile, list index out of range");
			meetingRepository.createMeet(param);
			return map;
		}

		meetingRepository.createMeet(param);

		int meetSeq = ((Number) param.get("seq")).intValue();

		log.info("Start Meeting in parseDiarizeAndStt.......{}", meetSeq);

		// --------------------------------------
		List<Map<String, Object>> sttResultListMap = new ArrayList<>();
		int speakerNo = 1;
		Map<Integer, Integer> speakerKeyMap = new HashMap<>();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listSttResult = (List<Map<String, Object>>) resultMap.get("stt_result");

		if (listSttResult != null) {
			for (Map<String, Object> stt_result : listSttResult) {

				String currentOrgSpeaker = stt_result.get("speaker").toString();
				String updateNewSpeaker = null;

				int currentSpeakerKey = Integer.parseInt(currentOrgSpeaker.substring(8));
				if (speakerKeyMap.containsKey(currentSpeakerKey)) {
					updateNewSpeaker = ("참석자_" + speakerKeyMap.get(currentSpeakerKey));
				} else {
					speakerKeyMap.put(currentSpeakerKey, speakerNo++);
					updateNewSpeaker = ("참석자_" + speakerKeyMap.get(currentSpeakerKey));
				}
				stt_result.put("speaker", updateNewSpeaker);
				boolean newFlag = true;
				if (sttResultListMap.size() > 0) {
					Map<String, Object> sttResultMap = sttResultListMap.get(sttResultListMap.size() - 1);
					if (sttResultMap.get("speaker").toString().equals(updateNewSpeaker)) {
						sttResultMap.put("end", stt_result.get("end"));
						sttResultMap.put("text", (sttResultMap.get("text") + "\n" + stt_result.get("text")));
						newFlag = false;
					}
				}
				if (newFlag) {
					sttResultListMap.add(stt_result);
				}

			}
		}

		// --------------------------------------
		createSpeakerAndResult(sttResultListMap, meetSeq);
		map.put("meetSeq", Integer.toString(meetSeq));
		return map;
	}
	
	
	@Override
	@Transactional
	public Map<String, String> parseDiarizeAndSttForMenu(Map<String, Object> param, Map<String, Object> resultMap)
			throws Exception {

		Map<String, String> map = new HashMap<>();

		if (param.get("errorMessage") != null) {
			map.put("errorMessage", (String) param.get("errorMessage"));
			meetingRepository.createMeet(param);
			return map;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> checkStt = (List<Map<String, Object>>) resultMap.get("stt_result");

		if (checkStt == null || checkStt.isEmpty()) {
			map.put("errorMessage", "speaker diarize Faile, list index out of range");
			meetingRepository.createMeet(param);
			return map;
		}

		meetingRepository.createMeet(param);

		int meetSeq = ((Number) param.get("seq")).intValue();
		log.info("Make Meeting  in parseDiarizeAndStt.......{}", meetSeq);
		// --------------------------------------
		List<Map<String, Object>> sttResultListMap = new ArrayList<>();
		int speakerNo = 1;
		Map<Integer, Integer> speakerKeyMap = new HashMap<>();

		long sum = 0L;
		int count = 0;
		int confidence = 0;
		log.info("Start Making Transcription in parseDiarizeAndStt.......{}", meetSeq);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listSttResult = (List<Map<String, Object>>) resultMap.get("stt_result");
		if (listSttResult != null) {
			for (Map<String, Object> stt_result : listSttResult) {
				// 2. TB_CS_TRANSCRIPTION
				confidence = (int) (Double.parseDouble(stt_result.get("confidence").toString()) * 100);
				sum += confidence;
				count++;

				log.info("Making Transcription in parseDiarizeAndStt......@SuppressWarnings(\"unchecked\").{}", count);

				transcriptionRepository.createTranscription(stt_result.get("start").toString(), stt_result.get("end").toString(),
						stt_result.get("text").toString().trim(), confidence, meetSeq, -1, 0);

				String currentOrgSpeaker = (String) stt_result.get("speaker");
				if (currentOrgSpeaker == null) {
					currentOrgSpeaker = "speaker_0";
				}
//			String currentOrgSpeaker = stt_result.get("speaker").toString();
				String updateNewSpeaker = null;

				int currentSpeakerKey = Integer.parseInt(currentOrgSpeaker.substring(8));
				if (speakerKeyMap.containsKey(currentSpeakerKey)) {
					updateNewSpeaker = ("참석자_" + speakerKeyMap.get(currentSpeakerKey));
				} else {
					speakerKeyMap.put(currentSpeakerKey, speakerNo++);
					updateNewSpeaker = ("참석자_" + speakerKeyMap.get(currentSpeakerKey));
				}
				stt_result.put("speaker", updateNewSpeaker);
				boolean newFlag = true;
				if (sttResultListMap.size() > 0) {
					Map<String, Object> sttResultMap = sttResultListMap.get(sttResultListMap.size() - 1);
					if (sttResultMap.get("speaker").toString().equals(updateNewSpeaker)) {
						sttResultMap.put("end", stt_result.get("end"));
						sttResultMap.put("text", (sttResultMap.get("text") + "\n" + stt_result.get("text")));
						newFlag = false;
					}
				}
				if (newFlag) {
					sttResultListMap.add(stt_result);
				}
			}
		}

		if (count > 0) {
			int avg = (int) (sum / count);
			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("seq", meetSeq);
			updateMap.put("reliability", avg);

			meetingRepository.updateMeet(updateMap);
		}

		createSpeakerAndResult(sttResultListMap, meetSeq);	
		
		map.put("meetSeq", Integer.toString(meetSeq));
		return map;
	}

	
	
	@Override
	@Transactional
	public String parseDiarizeAndSttOnly(MeetingVo vo, Map<String, Object> resultMap, boolean apiFlag) throws Exception {

		// --------------------------------------
		// 1. TB_CS_MEETING
		// 편집할 음성 데이터 저장
		Map<String, Object> param = new HashMap<>();
		param.put("subject", vo.getSubject());
		param.put("timeDurationStr", vo.getTimeDurationMs());
		param.put("orgnm", vo.getFileOrgNm());
		param.put("newnm", vo.getFileNewNm());
		param.put("convnm", vo.getFileConvNm());
		param.put("fileSizeBytes", vo.getFileSizeBytes());
		param.put("tcUserSeq", vo.getTcUserSeq());

		meetingRepository.createMeet(param);

		int meetSeq = ((Number) param.get("seq")).intValue();
		log.info("2. Start Meeting in parseDiarizeAndSttOnly.......{}", meetSeq);

		// --------------------------------------
		List<Map<String, Object>> sttResultListMap = new ArrayList<>();
		int speakerNo = 1;
		Map<Integer, Integer> speakerKeyMap = new HashMap<>();
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listSttResult = (List<Map<String, Object>>) resultMap.get("stt_result");

		if (listSttResult != null) {
			for (Map<String, Object> stt_result : listSttResult) {

				log.info("Start TB_CS_TRANSCRIPTION.......{}");

				// 2. TB_CS_TRANSCRIPTION
				// stt 결과값 저장
				transcriptionRepository.createTranscription(stt_result.get("start").toString(), stt_result.get("end").toString(),
						stt_result.get("text").toString().trim(),
//					stt_result.get("confidence").toString(),
						(int) (Double.parseDouble(stt_result.get("confidence").toString()) * 100), meetSeq, -1, 0);

				String currentOrgSpeaker = stt_result.get("speaker").toString();
				String updateNewSpeaker = null;

				// stt에서 분리된 화자값을 분리하여 화자 편집
				int currentSpeakerKey = Integer.parseInt(currentOrgSpeaker.substring(8));
				if (speakerKeyMap.containsKey(currentSpeakerKey)) {
					updateNewSpeaker = ("참석자_" + speakerKeyMap.get(currentSpeakerKey));
				} else {
					speakerKeyMap.put(currentSpeakerKey, speakerNo++);
					updateNewSpeaker = ("참석자_" + speakerKeyMap.get(currentSpeakerKey));
				}
				stt_result.put("speaker", updateNewSpeaker);
				boolean newFlag = true;

				// 불리된 텍스트 길이 별로 편집
				if (sttResultListMap.size() > 0) {
					Map<String, Object> sttResultMap = sttResultListMap.get(sttResultListMap.size() - 1);
					if (sttResultMap.get("speaker").toString().equals(updateNewSpeaker)) {
						sttResultMap.put("end", stt_result.get("end"));
						sttResultMap.put("text", (sttResultMap.get("text") + "\n" + stt_result.get("text")));
						newFlag = false;
					}
				}
				if (newFlag) {
					sttResultListMap.add(stt_result);
				}

			}
		}
		// --------------------------------------
		createSpeakerAndResult(sttResultListMap, meetSeq);	
		
		if (apiFlag == true) {

			// 5. TB_CS_MEEING_API 에 status = 20 기록
			// 이 method (backendStt()) 가 transaction 이므로, 위의 parseDiarizeAndStt() 와 아래의
			// meetingRepository.updateMeetApi() 가 하나의 txn 이 됨
			vo.setStatus(MeetingLauncher.API_STATUS_STT);
			vo.setMeetingSeq(meetSeq);
//			vo.setTimeDurationMs(durationMs.toString());

			meetingRepository.updateMeetApi(vo);
		}
		return Integer.toString(meetSeq);
	}

	
	private String callSttCurl(Resource resource, String sttUrl) throws Exception {
		
		List<String> cmds = new ArrayList<>();		
		cmds.addAll(Arrays.asList(sttAlternateCurlCommand));
		
		String[] args = { "-X", "POST", sttUrl, "-H", quote + "accept: application/json" + quote, "-H", quote + "Content-Type: multipart/form-data" + quote, "-F"};
		cmds.addAll(Arrays.asList(args));
		
		StringBuilder buffer = new StringBuilder();
//		buffer.append(quote);
		buffer.append("file=@");
		buffer.append(resource.getFile().getAbsolutePath());
		buffer.append(";type=");
		buffer.append(CommonUtil.getMimeType(resource.getFilename()));
//		buffer.append(quote);
		
		cmds.add(buffer.toString());
		
//		String[] cmds = { "cmd", "/c", "curl", "-X", "POST", "-H", "accept: application/json", "-H", "Content-Type: multipart/form-data", "-F", "file=@D:/e2e/upload/sgsas_train/upload/meet\\2023/11/88bfa814-c63c-4efa-8656-f0b71fb67449.wav;type=audio/wav", url };

		log.debug("stt CURL cmd {}", String.join(" ", cmds));
		String result = "";
		final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		try {
			ProcessResult processResult = new ProcessExecutor().command(cmds)
					.redirectError(
							Slf4jStream.of(this.getClass()).asInfo()
							)
					.redirectErrorAlsoTo(errorStream)   
					.readOutput(true).execute();
			
			int exitValue = processResult.getExitValue();
			if (exitValue == 0) {
				result = processResult.outputUTF8();
			} else {
				String msg = new String(errorStream.toByteArray(), Charset.defaultCharset());
//				log.error(msg);
				String errorLine = "";
				if ( msg != null ) {
					String[] array = msg.split("\n");
					if ( array.length > 0 ) {
						errorLine =  array[array.length-1].trim();
					}
				}
				SttCallException sce = new SttCallException("curl", errorLine);
				throw sce;
			}
		} catch (Exception e) {
			e.printStackTrace();
			SttCallException sce = new SttCallException("curl", e.getMessage());
			throw sce;
		}
		
		
		if ( result.startsWith("{\"file_name\":") ) {
			return result;
		} else {
			log.error(result);
			if ( result.length() > ERROR_MESSAGE_LEN ) {
				result = result.substring(0,  ERROR_MESSAGE_LEN);
			}
			SttCallException sce = new SttCallException("curl", result);
			throw sce;
		}
		
	}

	private String callSttRest(Resource resource, String sttUrl) throws Exception {
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap <>();
		body.add("file", resource);
		
		HttpEntity <Object> httpEntity = new HttpEntity<>(body, httpHeaders);
		
		RestTemplate restTemplate = new RestTemplate();
		log.debug("stt REST data {}", httpEntity.toString());
		
		String result = null;
		
		try {
			result = restTemplate.postForObject(sttUrl, httpEntity, String.class);
		} catch ( Exception rce ) {
//			rce.printStackTrace();
			String message = rce.getMessage();
			if ( message.length() > ERROR_MESSAGE_LEN ) {
				message = message.substring(0,  ERROR_MESSAGE_LEN);
			}
			SttCallException sce = new SttCallException("rest", message);
			throw sce;
		} 
		
		log.debug("rest result = {}", result);
		return result;
	}
	
	
	/**
	 * 
	 * @param vo
	 * @param sce
	 * @return  true :  stt url connection 에러이므로 errorList 에 추가하여 다시 시도함
	 *          false : 성공이든 에러이든 해서 해당 record update 처리
	 * @throws Exception
	 */
	@Override
	@Transactional
	public boolean updateSoundWithException(Category category, MeetingVo vo, SttCallException sce) throws Exception {
		
		/**
		 * rest
		 * 400 Bad Request: "{"detail":"학습이 진행중이기 때문에 STT를 사용할 수 없습니다."}"
		 * 510 Not Extended: "{"detail":"학습이 진행중이기 때문에 STT를 사용할 수 없습니다."}"
		 * curl
		 * {"detail":"학습이 진행중이기 때문에 STT를 사용할 수 없습니다."}
		 */
		
		boolean timeout = false;
		boolean cont = false;
		
		String pgm = sce.getPgm();
		String message = sce.getMessage();
		
		// stt url 이 접속되지 않으면 errorlist 에 넣어서 다시호출하고, 나머지 에러는 15 로 update 하고 끝냄
		if ( pgm.equals("rest") ) {
			if ( message.contains("Connection timed out")) {
				timeout = true;
			} else if ( message.contains("510 Not Extended") && message.contains("detail") && message.contains("학습이 진행중") ) {
				timeout = true;
				cont = true;
			}
		} else if ( pgm.equals("curl") ) {
			if ( message.contains("Failed to connect to")) {
				timeout = true;
			} else if ( message.contains("detail") && message.contains("학습이 진행중") ) {
				timeout = true;
				cont = true;
			}
		}
		
		if ( timeout == true ) {
			if ( cont == true ) {
				log.debug("CONTINUE : {} ", vo.getErrorFirstAt());
				
				LocalDateTime errorFirstAt = vo.getErrorFirstAt();
				LocalDateTime now = LocalDateTime.now();
				
				if ( errorFirstAt == null ) {
					errorFirstAt = now;
					vo.setErrorFirstAt(now);
					vo.setErrorMessage(message);
				} else {
					LocalDateTime dead = now.minusDays(this.sttRetryDays);
//					dead = now.minusMinutes(5);
					log.debug("errorFirstAt : {}, dead : {}", errorFirstAt, dead);
					if ( dead.isAfter(errorFirstAt) ) {
						vo.setStatus(SoundLauncher.API_STATUS_TIME_OUT);
						vo.setErrorMessage(message);
						self.updateSound(category, vo);
						return false;
					}
				}
			}
			
			log.debug("CONT : {}", vo.toString());
			return true;
		}
		
		vo.setStatus(SoundLauncher.API_STATUS_NOT_FOUND);
		vo.setErrorMessage(message);
		self.updateSound(category, vo);
		
		return false;
	}
	
	@Override
	@Transactional
	public void updateSound(Category category, MeetingVo vo) throws Exception {
		vo.setChannelCount(CHANNEL_COUNT);
		
		if ( "train".equals(category) ) {
			soundRepository.updateSoundWithMeet(vo);
		} else {
			testRepository.updateTestWithMeet(vo);
		}
	}

	public void createTranscription(Category category, String start, String end, String text, int reliability, int soundSeq, int channelId) throws Exception {
		
		if ( "train".equals(category) ) {
			transcriptionRepository.createTranscription(start, end, text, reliability, soundSeq, channelId);
		
		} else {
			testRepository.createTestTran(start, end, text, reliability, soundSeq, channelId);
		}	
	}		
	
	
	@Override
	@Transactional
	public String parseDiarizeAndSttOnly(Category category, MeetingVo vo, Map<String, Object> resultMap, boolean apiFlag, String waveFilename) throws Exception {
		
		int seq = vo.getSeq();
		//--------------------------------------
		// 1. TB_CS_TRANSCRIPTION / TB_CS_TEST_TRAN
		
		int count = 0;
		int sum = 0;
		
		// 1. null 및 타입 안전성 확보를 위한 리스트 추출
		@SuppressWarnings("unchecked")
		List <Map <String, Object>> listSttResult = (List <Map <String, Object>>) resultMap.get("stt_result");
		for (Map <String, Object> stt_result : listSttResult) {
			
			int reliability = (int)((Double)stt_result.get("confidence") * 100) ;
			count++;
			sum += reliability;
			
			createTranscription(category, stt_result.get("start").toString(),
					stt_result.get("end").toString(),
					stt_result.get("text").toString().trim(),
					reliability,
					0,
					seq);
		}
		
		//--------------------------------------
		// 2. TB_CS_SOUND / TB_CS_TEST
		
		if ( count == 0 ) {
			return "text로 전환할 내용이 없습니다.";
		}
		
		int average = sum / count;
		
		vo.setReliability(average);		
		vo.setChannelCount(CHANNEL_COUNT);
		
		if ( "train".equals(category) ) {
			soundRepository.updateSoundWithMeet(vo);
		} else {
			testRepository.updateTestWithMeet(vo);
		}
		
		return null;		
	}

	@Override
	public boolean backendStt(Category category, MeetingVo vo) throws Exception {
		throw new RuntimeException("Not yet defined");
	}
	
}
