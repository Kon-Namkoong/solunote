package com.vol.solunote.comm.mapper;

import java.util.Map;

public interface ReportMapper {

	public void setReportHourly(String dataStr)  throws Exception;
	
	public void setReportDaily(String dataStr)  throws Exception;

	public void insertStatus(Map<String, Object> map)  throws Exception;

}
