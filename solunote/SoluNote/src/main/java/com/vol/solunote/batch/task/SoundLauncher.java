package com.vol.solunote.batch.task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.vol.solunote.batch.task.testdata.TestDataConsumer;
import com.vol.solunote.batch.task.testdata.TestDataProducer;
import com.vol.solunote.batch.task.testdata.TestDataTask;
import com.vol.solunote.batch.task.traindata.TrainDataConsumer;
import com.vol.solunote.batch.task.traindata.TrainDataProducer;
import com.vol.solunote.batch.task.traindata.TrainDataTask;
import com.vol.solunote.comm.service.stt.SttService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SoundLauncher {
	
	public static final String API_STATUS_UPLOAD = "10";
	public static final String API_STATUS_NOT_FOUND = "15";
	public static final String API_STATUS_TIME_OUT = "16";
	public static final String API_STATUS_STT = "20";
	public static final String API_STATUS_SUMMARY = "30";

    @Autowired 
    SttService sttService;
    
	@Value("${task.sound.urls}")
    private List<String> sttUrls;
	
	@Value("${task.sound.train.max}")
	private int sttTrainMax;
	
	@Value("${task.sound.test.max}")
	private int sttTestMax;
	
	@Value("${task.sound.no_data_sleep}")
	private int sttNoDataSleep;
	
//	@PostConstruct
//	private void init() {
//		String string = this.sttUrls.get(1);
//		sttUrls = new ArrayList<>();
//		sttUrls.add(string);
//		
//		sttTestMax = 3;
//	}
	
    public void initSoundLauncher() throws Exception {
    	
    	launchTrainData();
    	
    	launchTestData();
    	
    }


	private void launchTrainData() {
		
		log.debug("SoundLauncher : started launchTrainData");
		
		TrainDataTask trainDataTask = new TrainDataTask(sttTrainMax, sttService, sttNoDataSleep);
		ExecutorService executorService = Executors.newFixedThreadPool(sttUrls.size() * sttTrainMax  + 1);
		
		executorService.execute(new TrainDataProducer(trainDataTask));
		for( int u = 0; u < sttUrls.size(); u++ ) {
			String url = sttUrls.get(u);
			for (int i = 0; i < sttTrainMax; i++) {
				log.debug("SoundLauncher : executorService.execute with {} - {}", url, i);
				executorService.execute(new TrainDataConsumer(trainDataTask, url));
			}
		}
		
		log.debug("SoundLauncher : ended launchTrainData");
	}

	
	private void launchTestData() {
		
		log.debug("SoundLauncher : started launchTestData");
		
		TestDataTask testDataTask = new TestDataTask(sttTestMax, sttService, sttNoDataSleep);
		ExecutorService executorService = Executors.newFixedThreadPool(sttUrls.size() * sttTestMax  + 1);
		
		executorService.execute(new TestDataProducer(testDataTask));
		for( int u = 0; u < sttUrls.size(); u++ ) {
			String url = sttUrls.get(u);
			for (int i = 0; i < sttTestMax; i++) {
				log.debug("SoundLauncher : executorService.execute with {} - {}", url, i);
				executorService.execute(new TestDataConsumer(testDataTask, url));
			}
		}		
		log.debug("SoundLauncher : ended launchTestData");
	}		
}