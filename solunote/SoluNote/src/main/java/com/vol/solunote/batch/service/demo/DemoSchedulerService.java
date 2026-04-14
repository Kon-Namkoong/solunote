package com.vol.solunote.batch.service.demo;

import org.springframework.stereotype.Service;


public interface DemoSchedulerService {
	public void sendPOSTResultSTT() throws Exception;
	
	public void getServerStatus()  throws Exception;

	public void setReport() throws Exception;	
}
