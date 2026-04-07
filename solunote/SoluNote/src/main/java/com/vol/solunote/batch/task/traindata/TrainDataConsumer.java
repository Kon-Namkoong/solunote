package com.vol.solunote.batch.task.traindata;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrainDataConsumer implements Runnable {
	
    private TrainDataTask trainDataTask;
	private String url;

    public TrainDataConsumer(TrainDataTask trainDataTask, String url) {
        this.trainDataTask = trainDataTask;
        this.url = url;
        log.debug("TrainDataConsumer(TrainDataTask trainDataTask) : {}", trainDataTask);
    }
    
    public void run() {
    	while (!Thread.currentThread().isInterrupted()) {
	        try {
	        	log.debug("trainDataTask.run() = {}", url);
	            Thread.sleep( 1000);
	            trainDataTask.updateMessage(url);
	        } catch (InterruptedException ie) {
	        	Thread.currentThread().interrupt();
	        } catch (Exception ex) {
	        	log.info("Exception in run of TrainDataConsumer");
	        }
    	}
    }

}