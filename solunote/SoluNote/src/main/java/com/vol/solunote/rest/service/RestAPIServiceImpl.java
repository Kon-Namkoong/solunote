package com.vol.solunote.rest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.comm.util.BasicIdGenerator;
import com.vol.solunote.comm.util.CommonUtil;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.comm.util.JWTUtil;
import com.vol.solunote.comm.util.STTManagerHandler;
import com.vol.solunote.model.status.STTManagerRsSerivce;
import com.vol.solunote.model.vo.rest.DomainVo;
import com.vol.solunote.model.vo.rest.RestUserVo;
import com.vol.solunote.model.vo.rest.STTResultVo;
import com.vol.solunote.repository.rest.RestRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RestAPIServiceImpl extends STTManagerHandler implements RestAPIService, STTManagerRsSerivce {

	private static final String DEFAULT_DOMAIN_CODE = "SG0001";

	@Value("${file.upload.path.was:#{null}}")
	String UPLOAD_PATH;
	
	@Value("${file.upload.path.audio:#{null}}")
	String AUDIO_PATH;

	@Value("${file.upload.extension.wav}")
	String FILE_EXTENSION;
	
	@Value("${rest.token.key}")
	String restTokenKey; 
	
	@Value("${stt.service.manager.ip}")
	String STT_MANAGER_IP;
	
	@Value("${stt.service.manager.port}")
	int STT_MANAGER_PORT; 
	
	@Value("${stt.service.manager.use}")
	boolean IS_STT_MANAGER; 

	@Autowired
	private RestRepository restRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	

	@Override
	public String getToken(RestUserVo param) throws Exception {
		
		JSONObject json  = new JSONObject();
		try {
			RestUserVo userInfo = restRepository.getMember(param);
			if(userInfo != null && userInfo.getUid() != null && !"".equals(userInfo.getUid()) 
					&& passwordEncoder.matches(param.getUpwd(), userInfo.getTcPwd())) {
			
				json.put("result", true);
				json.put("errMsg", "");
				json.put("accessToken", new JWTUtil().createToken(restTokenKey, "Solugate JWT"));

			} else {
				json.put("result", false);
				json.put("errMsg", "invalidUserinfo");
			}
		} 
		catch (JSONException e)
		{
			log.info("Json Exception Occured");
		} finally {
			log.debug("get tocken succeed");
		}
			
		return json.toString();

	}

	/**
	 * 음원 파일 처리 함
	 */
	@Override
	public String sendSTT(MultipartFile upLoadFile, HashMap<String, Object> paramMap, String accessToken) throws Exception {

		JSONObject json = new JSONObject();
		json.put("result", false);
		
		// 업로드한 파일이 없는 경우  파일 없음 처리
		if(upLoadFile == null || upLoadFile.getOriginalFilename().isEmpty()) {
			log.debug("No File");
			
			json.put("fileName", "");
			json.put("errMsg", "No File");
			
			return json.toString();
			
		}

		json.put("fileName", upLoadFile.getOriginalFilename());
		
		// 토큰 정보 확인
		JSONObject tokenJson = new JWTUtil().getTokenExpired(restTokenKey, accessToken);
		log.info(tokenJson.toString());
		
		// 토큰 정보가 유효하지 않은 경우 처리
		if(!tokenJson.has("expired") || (boolean)tokenJson.get("expired") ) {
			json.put("errMsg", "ExpiredToken");
			return json.toString();
		}
		
		String s_uid = BasicIdGenerator.nextLong()+"";

		// 파얼 저장 
		Map<String, Object> rsMap =  saveFile(upLoadFile, s_uid);
		
		// 파일 저장이 정상적이지 않을 경우 반환
		boolean isSave =  rsMap.get("result") ==  null ? false : (Boolean)rsMap.get("result")  ;
		if(!isSave) {
			json.put("errMsg", rsMap.get("errMsg"));
			return json.toString();
		}
		
		paramMap.put("orgFileName", URLDecoder.decode(upLoadFile.getOriginalFilename(), "utf-8"));
		paramMap.put("saveFileName", rsMap.get("saveFileName"));
		
		
		/** 음원 파일 업로드 후 신규 개발한 service.manager 로 정보 전송시 사용함
		 *   application.properties 파일  stt.service.manager.use 설정값 사용
		 *   true : service.manager에 소켓 통신 전송, false : db에 저장하고 끝냄
		 */
		if(IS_STT_MANAGER) {
			
			String domainName = CommonUtil.nvl(paramMap.get("s_domain_name"), "");
			String domainCode = CommonUtil.nvl(paramMap.get("s_domain_code"), "");
			if("".equals(domainCode)) {
				DomainVo domainVo = restRepository.getDomainInfo();
				domainCode = domainVo.getDomainCode();
				domainName = domainVo.getDomainName();
				paramMap.put("s_domain_code", domainCode);
				paramMap.put("s_domain_name", domainName);
			}
			
			
			
			json = sendSTTManager(paramMap, "RID"+s_uid);
			return json.toString();
		}
		
		// manager 전송이 아닐 경우 DB에 저장ㅎ함
		// 업로드 DB 저장 처리
		int sttSeq =  saveData(paramMap, s_uid);
		if(sttSeq <= 0 ) {
			json.put("result", false);
			json.put("errMsg", "errDBInsert");
		} else {
			json.put("saveFileName", rsMap.get("saveFileName"));
			json.put("callId", s_uid);
			json.put("sttSeq", sttSeq);
			json.put("result", true);
			json.put("errMsg", "");

		}
		
		if((boolean)paramMap.get("isReal")) {
			return getResult(sttSeq);
		}

		return json.toString();
	}

	/**
	 * 파일 업로드(음원파일) 후 db에 정보 저장 처리함
	 * 도메인 code를 보내지 않으면 사용 가능한 도메인 정보 중 가장 먼저 입력된 값으로 처리됨
	 * @param paramMap
	 * @param s_uid : call ID 값으로 사용 함(UUID)
	 * @return
	 * @throws Exception
	 */
	private int saveData(HashMap<String, Object> paramMap, String s_uid) throws Exception {
		
		String domainName = CommonUtil.nvl(paramMap.get("s_domain_name"), "");
		String domainCode = CommonUtil.nvl(paramMap.get("s_domain_code"), "");
		String s_update = DateUtil.getDateString();
		String s_uptime = DateUtil.getTimeString();
		
		if("".equals(domainCode)) {
			DomainVo domainVo = restRepository.getDomainInfo();
			domainCode = domainVo.getDomainCode();
			domainName = domainVo.getDomainName();
		}
		
		Map<String,Object> param = new HashMap<>();
		param.put("TC_CALL_ID", "RID" + s_uid);
		param.put("TC_CALL_STARTDATE", s_update);
		param.put("TC_CALL_STARTTIME", s_uptime);
		param.put("TC_CALL_ENDDATE", s_update);
		param.put("TC_CALL_ENDTIME", s_uptime);
		param.put("TC_DOMAIN_CODE", domainCode);
		param.put("TC_DOMAIN_NAME", domainName);
		param.put("TC_FILE_ID", paramMap.get("orgFileName"));
		param.put("TC_FILE_NAME", paramMap.get("saveFileName"));
		param.put("TC_FILE_ORG_PATH", UPLOAD_PATH + "/");
		int cntSave = restRepository.insertWavFile(param);
		
		return cntSave;
	}

	/**
	 * 
	 * @param upLoadFile
	 * @param s_uid : call ID 값으로 사용 함(UUID)
	 * @return
	 */
	private Map<String, Object> saveFile(MultipartFile upLoadFile, String s_uid) {

		String originalFileName = upLoadFile.getOriginalFilename(); // 실제 파일명
		
		Map<String, Object> rsMap = new HashMap<>();
		
		String fileExt = "";
		String saveFileName = "";

		if (originalFileName != null && !originalFileName.trim().equals("") && originalFileName.trim().length() > 0) {
			String s_temp = originalFileName.trim().substring(originalFileName.trim().length() - 4,
					originalFileName.trim().length());
			String s_dot = s_temp.trim().substring(0, 1);
			if (s_dot != null && !s_dot.trim().equals("") && s_dot.trim().length() > 0) {
				if (s_dot.trim().equals(".")) {
					s_temp = s_temp.trim().substring(1, 4);
				}
			}
			fileExt = s_temp.toLowerCase();
		}
		
		if (fileExt != null && !fileExt.equals("") && fileExt.length() > 0) {
			if ((fileExt.equals("wav")) || (fileExt.equals("aif")) || (fileExt.equals("mp3")) || (fileExt.equals("mid")) || 
			          (fileExt.equals("mp2")) || (fileExt.equals("wam")) || (fileExt.equals("ogg")) || (fileExt.equals("m4a")) || (fileExt.equals("aac"))) {
				saveFileName = "BAT_" + s_uid + "." + fileExt;
			} else {
				rsMap.put("errMsg", "업로드 가능한 컨테츠 타입이 아님!");
				rsMap.put("result", false);
				return rsMap;
			}
		}
		
		try {
			File file = new File(UPLOAD_PATH);
			
			if (!file.exists()) {
				file.mkdirs();
			}
			
			file = new File(UPLOAD_PATH + File.separator + saveFileName);
			upLoadFile.transferTo(file);
			
			rsMap.put("errMsg", "");
			rsMap.put("saveFileName", saveFileName);
			rsMap.put("result", true);
			return rsMap;
			
		} catch (IllegalStateException | IOException e) {
			rsMap.put("errMsg", "업로드 실패");
			rsMap.put("result", false);
			return rsMap;
		}

	}
	
	/**
	 * STT 결과 보내는 함수
	 */
	@Override
	public String getSTTResult(int sttSeq, String accessToken) throws Exception {
		
		JSONObject json = new JSONObject();
		json.put("result", false);
		
		// 토큰 정보 확인
		JSONObject tokenJson = new JWTUtil().getTokenExpired(restTokenKey, accessToken);
		log.info("access token : {}", tokenJson.toString());
		
		if(!tokenJson.has("expired") || (boolean)tokenJson.get("expired") ) {
			json.put("errMsg", "ExpiredToken");
			return json.toString();
		}
		
		/**
		 * STT Manager 처리시 변경 필요 
		 */
		if(IS_STT_MANAGER) {
//			Thread.sleep(1000);
			List<STTResultVo> sttList = restRepository.getSTTResult(sttSeq);
//			restRepository.setUpdateSTT(sttSeq);
			
			log.info("IS_STT_MANAGER STT LIST : {}", sttList);
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			String jsonStr = objectMapper.writeValueAsString(sttList);
			
			JSONArray jarr = new JSONArray(jsonStr);
			json.put("result", true);
			json.put("sttSeq", sttSeq);
			json.put("sttList", jarr);
			return json.toString();
		}
		
		
		return getResult(sttSeq);
	}

	@Override
	public String getSTTResult(String accessToken, RestUserVo param, String domainCode) throws Exception {
		
		JSONObject json  = new JSONObject();
		
		RestUserVo userInfo = restRepository.getMember(param);
		if(userInfo == null || userInfo.getUid() == null || "".equals(userInfo.getUid()) 
				|| !passwordEncoder.matches(param.getUpwd(), userInfo.getTcPwd())) {
			json.put("result", false);
			json.put("errMsg", "invalidUserinfo");
			
			return json.toString();
		}
		
		
		if(domainCode == null || "".equals(domainCode)) domainCode = DEFAULT_DOMAIN_CODE;
		STTResultVo resultVo = restRepository.getLastSTTSEQ(domainCode);
		if(resultVo == null ||  resultVo.getSeq() <= 0) {
			json.put("result", false);
			json.put("errMsg", "No STT info");
			
			return json.toString();
		}
		
		List<STTResultVo> sttList = restRepository.getSTTResult(resultVo.getSeq());
		restRepository.setUpdateSTT(resultVo.getSeq());
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String jsonStr = objectMapper.writeValueAsString(sttList);
		
		JSONArray jarr = new JSONArray(jsonStr);
		json.put("result", true);
		json.put("sttSeq", resultVo.getSeq());
		json.put("sttList", jarr);
		
		return json.toString();
		
	}

	private String getResult(int sttSeq)
			throws Exception, InterruptedException, JsonProcessingException, JSONException {
		String jsonStr = null;
		
		JSONObject json = new JSONObject();
		int loopCount = 0;
		int loopTotalCount = 60;
		String status = "-1";
		
		while(++loopCount < loopTotalCount) {
			STTResultVo sttQMap = restRepository.getSTTQ(sttSeq);
			
			if(sttQMap != null && STT_SUCCESS.equals(sttQMap.getStatus())) {
				status = STT_SUCCESS;
				break;
			}
			
			log.debug("loopCount {} status {}", loopCount);
			if(sttQMap != null )
				log.debug("loopCount {} status {}", loopCount, sttQMap.getStatus());
			
			Thread.sleep(1000);
		}
		
		if(STT_SUCCESS.equals(status)) {
			List<STTResultVo> sttList = restRepository.getSTTResult(sttSeq);
			restRepository.setUpdateSTT(sttSeq);
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			jsonStr = objectMapper.writeValueAsString(sttList);
			
			JSONArray jarr = new JSONArray(jsonStr);
			json.put("result", true);
			json.put("sttSeq", sttSeq);
			json.put("sttList", jarr);

		} else {
			json.put("result", true);
			json.put("errMsg", "errorSTT");
		}
		
		return json.toString();
	}
	
	@Value("${chatbot.info.url}")
	private URL url;
    public String callAPI(String sttRes) throws JsonProcessingException {
    	// chatbot REST API 
    	
    	StringBuffer content=null;
    	HttpURLConnection con = null;
    	
    	try {
    		con = (HttpURLConnection) url.openConnection();
    		con.setRequestMethod("GET");
    	
    		con.setRequestProperty("Content-Type", "application/json; utf-8");
    		con.setDoOutput(true);
    		con.setConnectTimeout(5000);
    		con.setReadTimeout(5000);
    		con.setInstanceFollowRedirects(false);
    	
    		try(OutputStream os = con.getOutputStream())
    		{
    			byte[] input = sttRes.getBytes("utf-8");
    			os.write(input,0,input.length);
    		}

    	
    		//get result
    		try ( BufferedReader in = new BufferedReader(
    				new InputStreamReader(con.getInputStream(),"UTF-8"))) {
    			String inputLine;
    			content = new StringBuffer();
    			while ((inputLine = in.readLine()) != null) {
    				content.append(inputLine);
    			}
    		}    		
    	}
    	catch (IOException e) {
			log.error("IOException in try",e);
    	}
    	catch (Exception e) {
			log.error("IOException in inner try",e);
    	}
    	finally
    	{    		
    		if (null != con )
    		{
    			con.disconnect();
    		}    		
    	}	    	
        return content.toString();
    }	
}
