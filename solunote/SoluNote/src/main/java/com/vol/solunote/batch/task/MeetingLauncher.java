package com.vol.solunote.batch.task;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MeetingLauncher {
	
	public static final String API_STATUS_UPLOAD = "10";
	public static final String API_STATUS_NOT_FOUND = "15";
	public static final String API_STATUS_TIME_OUT = "16";
	public static final String API_STATUS_STT = "20";
	public static final String API_STATUS_SUMMARY = "30";

    
	@Value("${task.stt.max}")
    private int sttMax;
	
	@Value("${task.summary.max}")
	private int summaryMax;
	
	@Value("${task.stt.no_data_sleep}")
	private int sttNoDataSleep;
	
	@Value("${task.summary.no_data_sleep}")
	private int summaryNoDataSleep;

    public void initMeetingLauncher() throws Exception {
//    	
//    	launchStt();
//    	
//    	launchSummary();
    	
//        log.debug("shutdown");
//        executorService.shutdown();
        
    }

//
//	private void launchStt() {
//		
//		SttTask sttTask = new SttTask(sttMax, menu21Service, sttNoDataSleep);
//		ExecutorService executorService = Executors.newFixedThreadPool(sttMax  + 1);
//		
//		executorService.execute(new SttProducer(sttTask));
//		
//		for (int i = 0; i < sttMax; i++) {
//			executorService.execute(new SttConsumer(sttTask));
//		}
//	}
//
//	
//	private void launchSummary() {
//		
//		SummaryTask dataStore = new SummaryTask(summaryMax, menu21Service_1, summaryNoDataSleep);
//		ExecutorService executorService = Executors.newFixedThreadPool(summaryMax  + 1);
//		
//		executorService.execute(new SummaryProducer(dataStore));
//		
//		for (int i = 0; i < summaryMax; i++) {
//			executorService.execute(new SummaryConsumer(dataStore));
//		}
//	}
}