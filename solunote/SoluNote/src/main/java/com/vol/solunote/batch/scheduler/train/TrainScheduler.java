package com.vol.solunote.batch.scheduler.train;


import java.util.List;
import java.util.stream.Collectors;

//import javax.annotation.Resource;
import java.time.LocalDateTime;
//import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.model.vo.comm.ErrorShelf;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.train.TrainVo;
import com.vol.solunote.repository.train.TrainRepository;
import com.vol.solunote.batch.service.train.TrainSchedulerService;

import lombok.extern.slf4j.Slf4j;

// ScheduledJobs.java 에서  profile 에 따라 선택적으로 Bean 을 생성하므로, 여기에 @Component 를 사용하면 무조건 생성된다. 
@Component
@Slf4j
public class TrainScheduler {
	
	private static final int SCHEDULE_EMPTY = -1;
	
	private int runningSeq = SCHEDULE_EMPTY;
	
	@Autowired
	Environment env;
	
	@Value("${train.enable:false}")
	private boolean trainEnable;
	
	@Autowired
	private TrainSchedulerService schedulerService;
	
	
	@Autowired
	private CommonSteelServiceImpl commonService;
		
	private int getNextScheduleSeq(List<TrainVo> scheduleList) {
		
		int result = SCHEDULE_EMPTY;
		
		// runningSeq 의 값이 -1이 아니면, DB 의 값과 직접 비교한다
		TrainVo filtered = scheduleList.stream().filter(o -> o.getStatus() == null ).findFirst().orElse(null);
		if (filtered != null) {
			result = filtered.getSeq();
		}
		return result;
	}
	
	
	@Scheduled( fixedDelay = 1000L * 60 )
	public void startUpdate() throws Exception {
		
		if ( false == this.trainEnable ) {
			log.debug("SCHEDULE : started startUpdate skip TrainSchedule");
			return;
		}

		// 1. 현재 학습중인지 확인
		log.debug("1. SCHEDULE : started startUpdate TrainSchedule");
		
		SearchVo search = DateUtil.setSearchTerm("monthTerm");
		search.setSearchText("30");
						
		List<TrainVo> trainList = schedulerService.getTrainList(search);
		
		log.debug("2. SCHEDULE : started startUpdate TrainSchedule");		
		for( TrainVo vo : trainList ) {			
			log.debug("3. SCHEDULE : started startUpdate TrainSchedule");			
			switch( vo.getStatus() ) {
			case TrainRepository.TRAIN_CANCELED :
				log.debug("DEBUG : skip for canceled request : " + vo.getStartTime());
				continue;					
			default : 
				break;
			}
			
			TrainVo resultVo = schedulerService.callTrainHistoryFromModel(vo.getModelId());
			
			if ( resultVo == null ) {
				log.debug("4. SCHEDULE : started startUpdate TrainSchedule");			
				ErrorShelf es = commonService.getErrorShelf();
				commonService.removeErrorShelf();
				log.debug(  es.getMessage() );
//				return;
				
				// 404 Not Found: "{"detail":"존재하지 않는 모델 ID 입니다."}"
				if ( es.getCode() == -1 && es.getMessage().startsWith("404 ") ) {
					log.debug("5. WARNING :404 라서 실패로 처리 함");
					resultVo = schedulerService.fillFailedMap(vo.getModelId());
					resultVo.setDetail(es.getMessage());
				} else {
					log.error("6. ERROR : get model error : callTrainHistoryFromModel.callTrainHistoryFromModel, modelid = {}", vo.getModelId());
					continue;
				}
			}
			
			if ( resultVo.getStatus().equals(TrainRepository.TRAIN_RUNNING)) {
				log.debug("7. DEBUG : skip for running request : " + vo.getStartTime());
				continue;
			} else {
				log.debug("8. DEBUG : skip for running request : " + vo.getStartTime());
				resultVo.setSeq(vo.getSeq());
				schedulerService.updateTrain(resultVo);
			}
		}
		
		log.debug("9. SCHEDULE : ended startUpdate");
	}
	
	
	@Scheduled( cron = "0 * * * * *")
	public void startTrain() throws Exception {
		
		if ( this.trainEnable == false ) {
			log.debug("startTrain SCHEDULE : started startTrain skip...{}", this.trainEnable);
			return;
		}
		
		log.debug("startTrain - SCHEDULE : started startTrain");
		
		LocalDateTime now = LocalDateTime.now();
		int min = now.getMinute();
		log.debug("startTrain - nos {}, min {} ", now.toString(), min);
		

		// 1. 스케쥴 시간 가져오기
		List<TrainVo> scheduleList = schedulerService.getTrainScheduleList();

		if ( scheduleList.size() == 0 ) {
			log.debug("startTrain - WARNING : 현재시각에 실행할 스케쥴이 없습니다. - 1");
			log.debug("startTrain - SCHEDULE : ended startTrain");
			return;
		}
		
		// 2. 현재 학습서버에서 학습이 진행 중인지 확인
		List<TrainVo> historyList = schedulerService.callTrainHistory(1, 1);
		
		log.debug("startTrain - Check  : historyList ");
		if ( historyList == null ) {
			log.debug("startTrain - Check  : historyList Null ");			
			ErrorShelf es = commonService.getErrorShelf();
			commonService.removeErrorShelf();
			log.debug(  es.getMessage() );
//			return;
			
			if ( es.getCode() == -1 && es.getMessage().startsWith("404 ") ) {
				log.debug("WARNING :404 라서 계속 진행함");
			} else {
				log.debug("WARNING : 현재 학습중이라서 새로 학습을 요청할 수 없습니다.");
				return;
			}
		} else {
			log.debug("startTrain - Check  : historyList Good ");	
			if ( historyList.size() == 0 ) {
				log.debug("WARNING : history 가 {} 이므로 첫번째 요청으로 진행함 ", historyList.size());
			} else if ( historyList.get(0).getStatus().equals(TrainRepository.TRAIN_RUNNING) ) {
				log.debug("startTrain - WARNING : 1. 학습서버에서  학습이 진행되고 있으므로 새로 학습을 요청할 수 없습니다");
				return;	
			}
		}
		
		// runningSeq 의 값이 -1이 아니면, DB 의 값이 NULL인지 직접 비교한다
		if ( this.runningSeq != SCHEDULE_EMPTY ) {
			if ( checkRunning(scheduleList) == true ) {
				log.debug("startTrain - WARNING : 2. 학습서버에서  학습이 진행되고 있으므로 새로 학습을 요청할 수 없습니다 - 1" );				
				return;
			}
		}
		
		 int nextSeq = getNextScheduleSeq(scheduleList);
		
		 if ( nextSeq == SCHEDULE_EMPTY ) {
				log.info("startTrain - WARNING : 3. 현재시각에 실행할 스케쥴이 없습니다. - 2");
				return;	
		 }
		 
		 log.info("startTrain - WARNING : 4. Now set runningSeq = {}", nextSeq);
		 this.runningSeq = nextSeq;
		 
		 log.debug("start 1");
		
		// 3. 학습서버로 TB_CS_TEST_TRANS 전송
		schedulerService.sendTestTrans();
		
		// 4. 학습서버로 TB_CS_TRANSCRIPTION 전송 (SOUND)
		schedulerService.sendDataTrans();
		
		//5 . 학습서보로 TB_CS_TRANSCRIPTION 전송 (TTS)
		schedulerService.sendTtsTrans();
		
		// 5. 학습요청 호출
		requestTrain(scheduleList);
		
		log.debug("SCHEDULE : ended startTrain");
	}
	
	
	private boolean checkRunning(List<TrainVo> scheduleList) {
		
		boolean result = false;
		
		// runningSeq 의 값이 -1이 아니면, DB 의 값과 직접 비교한다
		List<TrainVo> filtered = scheduleList.stream().filter(o -> o.getSeq() == this.runningSeq ).collect(Collectors.toList());
		
		log.debug("==========>  Filetred List = {}", filtered.size());
		
		if ( filtered.size() > 0 ) {
			if ( filtered.get(0).getStatus() == null ) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = false;
		}
		
		return result;
	}

	private boolean requestTrain(List<TrainVo> scheduleList) throws Exception {
		
		// 1. 보낼 데이타가 있는지 확인
		int	cnt = schedulerService.getTrainingCnt();
				
		if ( cnt < 1 ) {
			log.debug("WARNING : 학습 요청할 데이타가 추가되지 않아도 학습요청 합니다.");
		}	

		
		// 3. 학습 요청을 보냄
		String modelId = schedulerService.requestTraining();
		
		if ( modelId == null ) {
			log.debug("ERROR : 학습요청이 실패하였습니다, modelId is null ");
			return false;
		}
		
		log.debug("train requested succeed  - modelId : {}", modelId);
			
		TrainVo findFirst = scheduleList.stream().filter( o -> o.getSeq() == this.runningSeq).findFirst().orElse(null);
		if ( findFirst != null ) {
			schedulerService.afterTrainingCall(modelId, cnt , findFirst);
			log.debug("train requested save to db - modelId : {}, runningSeq : {}", modelId, this.runningSeq);
		} else {
			log.debug("WARNING : 학습요청이 실패하였습니다, getSeq is null");
			return false;
		}
		return true;
	}
	
}