package com.vol.solunote.domain.traindata.service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.disk.DiskService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.repository.sound.SoundRepository;
import com.vol.solunote.repository.transcription.TranscriptionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TraindataServiceImpl implements TraindataService {
	
	@Autowired
	private SoundRepository soundRepository;
	
	@Autowired
	//@Resource(name="${service.class.commonService}")
	private CommonSteelServiceImpl commonService;
	
	@Autowired
	private DiskService diskService;
	
	@Autowired
	private TranscriptionRepository 	transcriptionRepository;
	

	@Override
	public List<SoundVo> getListData(Map<String, Object> param) throws Exception {
		
		List<SoundVo> list = soundRepository.getListDataWithMap(param);
		for( SoundVo vo : list ) {
			
			vo.setTimeDurationFormatted(  DurationFormatUtils.formatDuration(Double.valueOf(vo.getTimeDurationMs()).longValue(), "H:mm:ss") );
		
//			  
//			int durationSec = (int) Math.round(Double.parseDouble(vo.getTimeDurationMs())/1000);
//			vo.setTimeDurationFormatted(String.format("%02d:%02d:%02d", (durationSec) / 3600, ((durationSec) % 3600) / 60, ((durationSec) % 60)));
			
//			String sttDuration = DurationFormatUtils.formatDuration(Double.valueOf(vo.getTimeDurationMs()).longValue(), "HH:mm:ss");
//			String format = String.format("%02d:%02d:%02d", (durationSec) / 3600, ((durationSec) % 3600) / 60, ((durationSec) % 60));
//			log.debug("{} =ssss {}", sttDuration, format);
		}
						
		return list;
	}


	@Override
	public List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception {
		
		if ( (int)param.get("reliability") > 100) {
			param.put("reliability", "100");
		}
				
		List<TranscriptionVo> list = transcriptionRepository.getTranscriptionList(param);
		
		for( TranscriptionVo tr : list ) {
			if ( tr.getTrainText() == null ) {
				if ( tr.getMeetText() != null ) {
					tr.setTrainText(tr.getMeetText());
				} else {
//					tr.setTrainText(tr.getSttText());
					tr.setTrainText("");
				}
			}
		}
		
		return list;
	}
	
	
	@Override
	public List<TranscriptionVo> getTranscriptionR(Map<String, Object> param) throws Exception {
		
		if ( (int)param.get("reliability") > 100) {
			param.put("reliability", "100");
		}
				
		List<TranscriptionVo> list = transcriptionRepository.getTranscriptionR(param);
		
		for( TranscriptionVo tr : list ) {
			if ( tr.getTrainText() == null ) {
				if ( tr.getMeetText() != null ) {
					tr.setTrainText(tr.getMeetText());
				} else {
//					tr.setTrainText(tr.getSttText());
					tr.setTrainText("");
				}
			}
		}
		
		return list;
	}
	
	
	@Override
	public List<TranscriptionVo> getTranscriptionF(Map<String, Object> param) throws Exception {
		
		if ( (int)param.get("reliability") > 100) {
			param.put("reliability", "100");
		}
				
		List<TranscriptionVo> list = transcriptionRepository.getTranscriptionF(param);
		
		for( TranscriptionVo tr : list ) {
			if ( tr.getTrainText() == null ) {
				if ( tr.getMeetText() != null ) {
					tr.setTrainText(tr.getMeetText());
				} else {
//					tr.setTrainText(tr.getSttText());
					tr.setTrainText("");
				}
			}
		}
		
		return list;
	}	
	
	
	@Override
	public List<TranscriptionVo> getTranscription(int seq) throws Exception {
		
		List<TranscriptionVo> list = transcriptionRepository.getTranscription(seq);
		
		return list;
	}
	
	@Override
	public Map <String, Object> getMeetBySEQ(int meetSeq) {
		return soundRepository.getMeetBySEQ(meetSeq);
	}
	
	@Override
	@Transactional
	public String successSoundAndStt( Map<String, Object> param, Map<String, Object> resultMap) throws Exception {


		soundRepository.createSound(param);

		int soundSeq = ((Number)param.get("id")).intValue();
		
		long sum = 0L;
		int count = 0;
		int confidence= 0;

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> sttResultList = (List<Map<String, Object>>) resultMap.get("stt_result");
		
		if (sttResultList != null) {
		    for (Map<String, Object> stt_result : sttResultList) {
				confidence = (int)( Double.parseDouble(stt_result.get("confidence").toString()) * 100 );
				sum += confidence;
				count++;
				
				createTranscription(stt_result.get("start").toString(),
						stt_result.get("end").toString(),
						stt_result.get("text").toString().trim(),
//						stt_result.get("confidence").toString(),
						confidence,
						-1,
						soundSeq,
						0);
		    }
		}
		

//		String fileResult = saveUploadFile(file, saveFileName);
//		if(fileResult == null) throw new RuntimeException();
		
		if ( count > 0 ) {
			int avg = (int) (sum / count);
			Map<String, Object> map = new HashMap<>();
			map.put("seq", soundSeq);
			map.put("reliability", avg);
			
			soundRepository.updateSound(map);
		}

		return Integer.toString(soundSeq);
	}
		
	@Override
	public void updateRemarkAndUpdatedAtBySeq( int meetSeq, LocalDateTime updatedAt, boolean updateRemark) throws Exception
	{
		soundRepository.updateRemarkAndUpdatedAtBySeq(updateRemark, updatedAt, meetSeq);
	}


	@Override
	@Transactional
	public void sendtest(Map<String, String> map) throws Exception {
		
		int seq = Integer.parseInt(map.get("seq"));
				
    	Map<String, Object> param = DefaultController.generateRequestParam("seq", seq);
    	
		List<SoundVo> pages = getListData(param);
		if ( pages.size() != 1 ) {
			throw new RuntimeException("no matching data found : " + seq);
		}
		SoundVo soundVo = pages.get(0);
		
		diskService.copyFile(soundVo.getFileNewNm());

	}

	@Override
	@Transactional
	public void trash(Map<String, String> map) throws Exception {
		
		int seq = Integer.parseInt(map.get("seq"));
		int reliability = -1;
		
		Map<String, Object> param = DefaultController.generateRequestParam("meetSeq, reliability", seq, reliability);
		List<TranscriptionVo> meetingResultList = getTranscriptionList(param);
//		List<Transcription> meetingResultList = getTranscriptionList(seq, -1, null,0);
		
		int[] array = meetingResultList.stream().mapToInt( e -> Long.valueOf(e.getSeq()).intValue()).toArray();
		
		transcriptionRepository.excludeTransTranscription(array);
		
		log.debug("Seq : {}", seq);
		
		soundRepository.updateSoundMode("trash", seq);

	}
	
	@Override
	@Transactional
	public void trashRollback(Map<String, String> map) throws Exception {
		
		int seq = Integer.parseInt(map.get("seq"));
		
		soundRepository.updateSoundMode("rollback", seq);
		
		
	}

	@Override
	@Transactional
	public void delete(Map<String, String> map) throws Exception {
		
		int seq = Integer.parseInt(map.get("seq"));
		
		soundRepository.updateSoundMode("delete", seq);
		
		
	}	
	
	
//	@Override
//	public int createTranscription(String start, String end, String text, String confidence, int meetingSeq, int soundSeq, int channelId) throws Exception {
//		
//		int reliability = (int)( Double.parseDouble(confidence) * 100 );
//		
//		Map<String, Object> map = new HashMap<>();
//		map.put("start", start);
//		map.put("end", end);
//		map.put("text", text);
//		map.put("reliability", reliability);   // 0 - 100
//		map.put("meetingSeq", meetingSeq);
//		map.put("soundSeq", soundSeq);
//		map.put("channelId", channelId);
//	
//		map.put("seq", -1);     // useGeneratedKeys 가 return 하는 type 을 integer 로 하기 위하여, 미리 map 에 seq 를 set 해 놓음
//		transcriptionRepository.createTranscription(map);
//		
//		int seq =  (int) map.get("seq");
//		
//		return seq;
//	}
	
	@Override
	public int createTranscription(String start, String end, String text, int reliability, int meetingSeq, int soundSeq, int channelId) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		map.put("text", text);
		map.put("reliability", reliability);   // 0 - 100
		map.put("meetingSeq", meetingSeq);
		map.put("soundSeq", soundSeq);
		map.put("channelId", channelId);
		
		map.put("seq", -1);     // useGeneratedKeys 가 return 하는 type 을 integer 로 하기 위하여, 미리 map 에 seq 를 set 해 놓음
		transcriptionRepository.createTranscription(map);
		
		int seq =  (int) map.get("seq");
		
		return seq;
	}

	@Override
	public void clickLeastOnce(Integer seq) throws IOException {
		soundRepository.clickLeastOnce(seq);
		
	}
	
	//파일 삭제 로직 시작
	@Override
	public List<Map<String, Object>> getMeettingRemoveCandiate(int term) throws Exception {

		return soundRepository.getMeettingRemoveCandiate(term);
	}
	
	@Override
	@Transactional
	public void setDeletedAt(int seq, String fileNm, String fileConvNm) throws Exception {
		
		boolean flag = deleteWaveFile(seq, fileNm, fileConvNm);
		
		if (flag == true ) {
			soundRepository.updateDeletedAtBySeq(seq);
		}
	}	
	
	private boolean deleteWaveFile(int seq, String fileNm, String fileConvNm) throws Exception {
		
		transcriptionRepository.deleteTrans(seq);
		
		
		if ( fileNm != null ) {
			commonService.removeDiskFile(Category.TRAIN, fileNm);
		}
		
		if ( fileConvNm != null ) {
			commonService.removeDiskFile(Category.TRAIN, fileConvNm);
		}
		
		return true;
	}	
	
	@Override
	@Transactional
	public int split(Map<String, String> map) throws Exception {
		
		
		int seq = Integer.parseInt( map.get("seq"));
		String start2 = map.get("start2");
		String text1 = map.get("text1");
		String soundSeq =  map.get("soundSeq");
		
		List<TranscriptionVo> list = getTranscription(seq);
		if ( list.size() != 1 ) {
			throw new RuntimeException("data not found for seq = " + seq);
		}
		TranscriptionVo record = list.get(0);
		
		map.put("end", start2);
		map.put("sttText", text1);
		
		int id = createTranscription(map.get("start2"),
				Double.toString(record.getEnd()),
				map.get("text2").trim(),
				record.getReliability(),
				-1,
				Integer.parseInt(soundSeq),
				0);
		
		return id;
	}

	@Override
	@Transactional
	public int combine(Map<String, String> map) throws Exception {
		
		
		int seq = Integer.parseInt( map.get("seq"));
		int seq2 = Integer.parseInt( map.get("seq2"));
		String text = map.get("text").trim();
		
		List<TranscriptionVo> list = getTranscription(seq);
		if ( list.size() != 1 ) {
			throw new RuntimeException("data not found for seq = " + seq);
		}
		
		map.put("sttText", text);
		
		int updateCount = transcriptionRepository.updateTranscriptionForSplit(map);
		
		transcriptionRepository.updateTranscriptionForCombine(seq2);
		
		return updateCount;
	}	
	
	
	@Override
	@Transactional
	public int resetFrame(Map<String, String> map) throws Exception {
		
//		
//		int seq = Integer.parseInt( map.get("seq"));
//		String start = map.get("start");
//		String end = map.get("end");
//		
//		map.put("end", start2);
//		map.put("sttText", text1);
		int count = transcriptionRepository.updateTranscriptionForReset(map);
		
		return count;
	}	
}

