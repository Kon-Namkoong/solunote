package com.vol.solunote.records;

import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

public interface RecorderService {

	JSONObject doRecord(Map<String, String> dataMap) throws Exception;

	String getSTTResult(int parseInt)  throws Exception; 
}
