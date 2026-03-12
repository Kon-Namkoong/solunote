package com.vol.solunote.batch.task.traindata;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrainDataProducer implements Runnable {

    private TrainDataTask trainDataTask;
    
    public TrainDataProducer(TrainDataTask trainDataTask) {
        this.trainDataTask = trainDataTask;
        log.debug("TrainDataProducer(TrainDataTask TrainDataProducer) : " + trainDataTask);
    }

    public void run() {
    	int sleep = 0;
    	
    	while (!Thread.currentThread().isInterrupted()) {
	        try {
	        	log.debug("SLEEP 1, TrainDataProducer.run()-1;");
                Thread.sleep( 1000);
                sleep = trainDataTask.putMessage();
                if ( sleep > 0 ) {
                	log.debug("SLEEP {}, TrainDataProducer.run()-2", sleep);
                	Thread.sleep( sleep * 1000);
                }
	        } catch (InterruptedException ie) {
	        	Thread.currentThread().interrupt();
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        }
    	}
    }
}
