package com.vol.solunote.batch.task.testdata;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDataConsumer implements Runnable {
	
    private TestDataTask testDataTask;
	private String url;

    public TestDataConsumer(TestDataTask testDataTask, String url) {
        this.testDataTask = testDataTask;
        this.url = url;
        log.debug("TestDataConsumer(TestDataTask testDataTask) : {}", testDataTask);
    }
    
    public void run() {
    	while (!Thread.currentThread().isInterrupted()) {
	        try {
	        	log.debug("testDataTask.run() = {}", url);
	            Thread.sleep( 1000);
	            testDataTask.updateMessage(url);
	        } catch (InterruptedException ie) {
	        	Thread.currentThread().interrupt();
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        }
    	}
    }

}