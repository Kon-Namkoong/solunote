package com.vol.solunote.comm.service;

import java.util.Map;

import org.springframework.core.io.Resource;

import com.vol.solunote.comm.model.Category;
import com.vol.solunote.comm.model.SttCallException;
import com.vol.solunote.model.vo.meeting.MeetingVo;

public interface SttService {

	boolean backendStt(Category category, MeetingVo vo, String sttUrl) throws Exception;

	Map<String, Object> callStt(Resource resource, String sttUrl, boolean callCurl) throws Exception;

	public Map<String, Object> callSttForMenu(org.springframework.core.io.Resource resource, boolean callCurl, String url) throws Exception;
	public Map<String, Object> callSttRestForMenu(org.springframework.core.io.Resource resource, String url) throws Exception;

	public Map<String, String> parseDiarizeAndStt(Map<String, Object> param, Map<String, Object> resultMap) throws Exception;	
	public Map<String, String> parseDiarizeAndSttForMenu(Map<String, Object> param, Map<String, Object> resultMap) throws Exception;	
	
	public	String parseDiarizeAndSttOnly(Category category, MeetingVo vo, Map<String, Object> resultMap, boolean apiFlag, String waveFilename) throws Exception;

	public String parseDiarizeAndSttOnly(MeetingVo vo, Map<String, Object> resultMap, boolean apiFlag) throws Exception;
	
	boolean updateSoundWithException(Category category, MeetingVo vo, SttCallException sce) throws Exception;

	void updateSound(Category category, MeetingVo vo)  throws Exception;

	boolean backendStt(Category category, MeetingVo vo) throws Exception;
}