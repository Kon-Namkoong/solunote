package com.vol.solunote.menu26.service;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.vol.solunote.model.vo.train.TrainVo;

public interface Menu26Service {


	public List <TrainVo> getList(Map<String, Object> param) throws Exception;
	public List <TrainVo> requestList(Map<String, Object> param) throws Exception;
	public List<Map<String, Object>> getTrainResultList(Map<String, Object> param) throws Exception;
	
	void listAdd(String startTime) throws Exception;
	
//	void registerChange(int seq ,String type, String day,String time,String useYn) throws Exception;
	
	int checkdata(String tstartTime) throws Exception;
	
	public void excludeTrans(int[] seq) throws IOException;
	
	public List<Map<String, Object>> getTrainList(String modelId) throws Exception;
}
