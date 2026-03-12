package com.vol.solunote.batch.service.train;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.vol.solunote.model.vo.train.TrainVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.repository.meeting.MeetingRepository;
import com.vol.solunote.repository.rest.RestRepository;
import com.vol.solunote.repository.sound.SoundRepository;
import com.vol.solunote.repository.test.TestRepository;
import com.vol.solunote.repository.transcription.TranscriptionRepository;
import com.vol.solunote.repository.upload.UploadDiskRepository;
import com.vol.solunote.repository.train.TrainRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.batch.train.vo.TrainCompareVo;
import com.vol.solunote.batch.train.vo.TrainServerDataVo;
import com.vol.solunote.common.service.CommonDataService;
import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.comm.mapper.ReportMapper;
import com.vol.solunote.comm.model.Category;
import com.vol.solunote.comm.service.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.DiskService;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.comm.vo.DefaultVo;
import com.vol.solunote.comm.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;

@Service
@Slf4j
public class TrainSchedulerServiceImpl implements TrainSchedulerService {

	@Value("${train.url}")
	private String trainUrl; 
	
	@Value("${train.project_id}")
	private String PROJECT_ID; 
	
	
	@Autowired
	private TestRepository testRepository;

	@Autowired
	private TranscriptionRepository transcriptionRepository;
	
	@Autowired
	private TrainRepository trainRepository;
	
	@Autowired
	private DiskService diskService;
	
	@Autowired
	private CommonDataService commonDataService;
	
	
	@Autowired
	private CommonSteelServiceImpl commonService;
	
	public TrainVo convertTrainData(Map<String, Object> map) 
	{
		TrainVo vo = new TrainVo();
		
		vo.setModelId( (String) map.get("_id") );
		/* isSuccessed 
		 * null = 학습 중일때
		 * false = 학습이 에러로 종료함
		 * true = 학습이 성공으로 종료함
		 */
		Object obj = map.get("isSuccessed");
		if ( obj == null ) {
			vo.setStatus(TrainRepository.TRAIN_RUNNING);
		} else if ( obj instanceof Boolean ) {
			vo.setStatus(TrainRepository.TRAIN_ENDED);
			vo.setSuccess(((Boolean)obj).booleanValue());
		} else {
			throw new RuntimeException("todo");
		}
		
		if ( vo.getStatus().equals(TrainRepository.TRAIN_RUNNING) ) {
			return vo;
		}
						
//		vo.setDataCnt( (Integer)map.get("data_cnt") == null ? 0 : map.get("data_cnt") );
		vo.setStartedAt( DateUtil.gmtToKstLdt( (String) map.get("startTime") ) );
		if ( map.get("endTime") != null ) {
			vo.setEndedAt( DateUtil.gmtToKstLdt( (String) map.get("endTime") ) );
		}
		if ( map.get("data_duration") != null ) {
			vo.setDuration( Double.toString( (double) map.get("data_duration") ) );
		}
		if ( map.get("CER") != null ) {
			vo.setCer( (double) map.get("CER") );
		} else {
			vo.setCer(  (double) 0);
		}
		if ( map.get("WER")  != null ) {
			vo.setWer(  (double) map.get("WER") );
		} else {
			vo.setWer( (double)  0 );
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		String detail = null;
		try {
			detail = mapper.writeValueAsString(map.get("stepLog"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		vo.setDetail( detail );		
		return vo;
	}
	
	
	@Override
	public	List<TrainVo>		getTrainList(SearchVo	search)	throws	Exception
	{
		return	trainRepository.getTrainData(search, null);
	}
	
	@Override
	public TrainVo callTrainHistoryFromModel(String modelId) throws Exception {
		
		if ( modelId.equals("")) {
			throw new RuntimeException("ERROR : modelId should not empty");
		}
		
		String url = this.trainUrl + "/model/" + modelId;
		
		Map<String, Object> map = commonService.restGetData(url);
		if ( map == null ) {
			return null;
		}
		
		TrainVo vo = convertTrainData(map);
		
		return vo;
	}

	@Override
	public void updateTrain(TrainVo resultVo) throws Exception {
		trainRepository.updateTrainData(resultVo);
	}
	
	@Override
	public TrainVo fillFailedMap(String modelId) throws Exception {
		Map<String, Object> map = new HashMap<>();
		
		map.put("_id", modelId );
		map.put("isSuccessed", Boolean.FALSE);
		map.put("data_cnt", 0);
		
		ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		map.put("startTime",  utc.format(format));
		
		map.put("stepLog", "{}");
		
		return convertTrainData(map);
	}

	@Override
	public int getTrainingCnt() throws Exception {
		return trainRepository.getTransPairCount();
	}

	@Override
	public String requestTraining() throws Exception 
	{
		
		String url = this.trainUrl + "/model/";
		Map<String, Object> body = new HashMap<>();
		body.put("project_id", PROJECT_ID);
		
		String modelId = null;
		
		Map<String, Object> resultMap = commonService.restPostData(url, body);
		
		if ( resultMap == null ) {
			return null;
		} else {
			modelId = (String) resultMap.get("model_id");
		}		
		return modelId;
	}

	@Override
	public void afterTrainingCall(String modelId, int count, TrainVo fistScheduleVo) throws Exception {
		
		TrainVo vo = new TrainVo();
		vo.setScheduleSeq(fistScheduleVo.getSeq());		// seq 를 schedule_seq 로
		vo.setName(fistScheduleVo.getStartTime());
		vo.setModelId(modelId);
		vo.setDataCnt(count);
		vo.setStatus("10");
		
		trainRepository.insertTrain(vo);
		trainRepository.updateTransPairModel(vo);
		trainRepository.updateTestTransPairModel(vo);		
	}

	@Override
	public void sendDataTrans() throws Exception {
		
		Map<String, Object> param = DefaultController.generateRequestParam("caller", "batch");
			
		List<TransVo> transList = transcriptionRepository.getDataTransListBatch(param);
		
		boolean send;
		boolean reset;
		
		for( TransVo vo : transList ) {
			
			send = true;
			reset = false;

			if ( vo.getTrainText() == null || vo.getTrainText().equals("") ) {
				reset = true;
				vo.setTrainText(vo.getSttText());   // reset flag = true 이면, STT 서버에 전송할 때는 원본 text 를 전송한다.
				if ( vo.getPrDataId() == null ) {
					send = false;
				}
			}
			
			log.debug("post data : " + vo.toString());
		
		try {
				if ( send == true ) {
					commonDataService.postData(vo, Category.TRAIN, reset, false);
				} else {
					transcriptionRepository.updateTrainTextNull(vo.getSeq());
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendTtsTrans() throws Exception {
		List<TransVo> transList = transcriptionRepository.getTtsTransListBatch(null, null, null, "batch");
		
		boolean send;
		boolean reset;
		
		for( TransVo vo : transList ) {
			
			send = true;
			reset = false;
			
			if ( vo.getTrainText() == null || vo.getTrainText().equals("") ) {
				reset = true;
				vo.setTrainText(vo.getSttText());   // reset flag = true 이면, STT 서버에 전송할 때는 원본 text 를 전송한다.
				if ( vo.getPrDataId() == null ) {
					send = false;
				}
			}
			
			log.debug("post data : " + vo.toString());
			
			try {
				if ( send == true ) {
					commonDataService.postData(vo, Category.TTS, reset, false);
				} else {
					transcriptionRepository.updateTrainTextNull(vo.getSeq());
				}
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		


	}

	private void insertTrainServerDataOne(TrainServerDataVo vo) throws Exception {
		trainRepository.insertTrainServerData(vo);
	}
	
	private void getTrainServerData() throws Exception {

		// 1. 현재 학습중인지 확인
		log.debug("started getTrainServerData");
		
		String url = this.trainUrl + "/data_list/?project_id=" + PROJECT_ID;

		int page = 1;
		int pageNum = 20;
//		int pageNum = 200;
		boolean flag = false;
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		trainRepository.deleteAllTrainServerData();
		
		while ( true ) {
			String fullUrl = url + "&page=" + page++ + "&page_num=" + pageNum;
			
			Map<String, Object> map  = commonService.restGetData(fullUrl);
//			List<Map<String, Object>> mapList = (List<Map<String, Object>>) map.get("id_list");
			
			if (null == map || map.isEmpty())
			{
				break;
			}
			
			@SuppressWarnings("unchecked")
			List<String> mapList = (List<String>)map.get("id_list");
			
			if ( mapList == null ) {
				flag = true;
			} else {
				
				if ( mapList.size() < pageNum ) {
					flag = true;
				}
				
				for( String dataId : mapList ) {
					String getUrl = this.trainUrl + "/data/" + dataId;
					Map<String, Object> data = commonService.restGetData(getUrl);
					TrainServerDataVo vo = objectMapper.convertValue(data, TrainServerDataVo.class);
//					if ( this.PROJECT_ID.equals(vo.getProjectId())) {
//						log.error("ERROR : project id is different = {}", vo.getProjectId());
//						continue;
//					}
					
					vo.setDataId(dataId);
					log.debug(vo.toString());
					
					insertTrainServerDataOne(vo);
				}
			}
				
			if ( flag == true ) {
				break;
			}
		}			
		log.debug("ended getTrainServerData");		
	}
	
	private void updateTransServerModeTxn(List<TrainCompareVo> list) throws Exception {
		
		for( TrainCompareVo vo : list ) {
			trainRepository.updateTransServerMode(vo);
		}	
	}
	
	private void sendUpdateServerData(TrainCompareVo vo) throws Exception {

		String text = vo.getTrainText().trim();
		String url = this.trainUrl + "/data/";
		Map<String, Object> resultMap = null;
		
		String category = vo.getTestYn().equals("Y") ? "test" : "train";
		
		try {
			resultMap = commonService.restPutData(url, vo.getDataId(), text, diskService.strToCategory(category), "N");
		} catch ( Exception e ) {
			e.printStackTrace();
			log.error("sendUpdateServerData SEND ERROR - continue-4 : Exception");
			return;
		} 
		
		if ( resultMap != null ) {
			vo.setUseYn("N");
			vo.setMode("D");
			trainRepository.updateTransServerMode(vo);
		}
		
	}
	
	private void syncTrainServer() throws Exception {
		
		log.debug("start syncTrainData");
		
		List<TrainCompareVo> transList = trainRepository.readTrainServerAndTrans();
		updateTransServerModeTxn(transList);
		
		List<TrainCompareVo> testList = trainRepository.readTrainServerAndTestTrans();
		updateTransServerModeTxn(testList);
		
		List<TrainCompareVo> nonList = trainRepository.readTrainServerNoneExists();
		
		for (TrainCompareVo vo : nonList ) {
			sendUpdateServerData(vo);
		}
		
		log.debug("ended syncTrainData");
	}
	@Override
	public void deleteTransServer() throws Exception 
	{
		getTrainServerData();		
		syncTrainServer();
	}

	@Override
	public void sendTestTrans() throws Exception
	{
		DefaultVo search = new DefaultVo();
		search.setSearchType(-1);
		
		List<TransVo> transList = testRepository.getTestTransListBatch("", null, search);    // search 가  null 이면 에러 발생
		
		for( TransVo vo : transList ) {
			
			
			int seq = vo.getSeq();
			log.debug("post test seq : " + seq);
			
			commonDataService.postData(vo, Category.TEST, false, true);
			
		}		
	}
	
	@Override
	public 	List<TrainVo> 	callTrainHistory(int page, int size) throws Exception
	{
		
		String url = this.trainUrl + "/model/?project_id=" +PROJECT_ID + "&page=" + page + "&data_per_page=" + size;
	
		log.debug("Resuested URL is {}", url);
		
		List<Map<String, Object>> list = commonService.restGetData(url);
		if ( list == null ) {
			return null;
		}
		
		List<TrainVo> converted = new ArrayList<>();
		
		for( Map<String, Object> map : list ) {
			TrainVo vo = convertTrainData(map);
			converted.add(vo);
		}		
		return converted;
	}
	
	@Override
	public	List<TrainVo>	getTrainScheduleList() throws Exception
	{
		return	trainRepository.getSchedule();
	}
		
}
