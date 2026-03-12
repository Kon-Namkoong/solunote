package com.vol.solunote.rest;

import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.model.vo.rest.RestUserVo;

public interface RestAPIService {

	public String getToken(RestUserVo userInfo) throws Exception;

	public String sendSTT(MultipartFile upLoadFile, HashMap<String, Object> paramMap, String accessToken) throws Exception;
	
	public String getSTTResult(int sttSeq, String accessToken) throws Exception;

	public String getSTTResult(String accessToken, RestUserVo userInfo, String domainCode)  throws Exception;
	public String callAPI(String sttRes)  throws Exception;

}
