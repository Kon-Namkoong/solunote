package com.vol.solunote.batch.task.testdata;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDataProducer implements Runnable {

    private TestDataTask testDataTask;
    
    public TestDataProducer(TestDataTask testDataTask) {
        this.testDataTask = testDataTask;
        log.debug("TrainDataProducer(TestDataTask testDataTask)  : " + testDataTask);
    }

    public void run() {
    	int sleep = 0;
    	
    	while (!Thread.currentThread().isInterrupted()) {
	        try {
	        	log.debug("TestDataProducer.run()-1");
                Thread.sleep( 1000);
                sleep = testDataTask.putMessage();
                if ( sleep > 0 ) {
                	log.debug("SLEEP {}, TestDataProducer.run()-2", sleep);
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
