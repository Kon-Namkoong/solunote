package com.vol.solunote.rest;

import java.io.BufferedReader;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.model.vo.rest.RestUserVo;

import lombok.extern.slf4j.Slf4j;

 
@Controller
@Slf4j
@RequestMapping("/API") 
public class RestAPIController extends DefaultController {
	
	@Autowired
	RestAPIService restAPIService;
	
	RestAPIController () {
		this.menuId="restApi";
	}
	
	@RequestMapping(value= {"/test"})
	@ResponseBody
	public String test(@Param("userInfo") RestUserVo userInfo) throws Exception {
		
//		JSONObject json  = new JSONObject();
//		json.put("test", true);
		String sttRes="{\"result\":true,\"sttSeq\":10392,\"sttList\":[{\"sttSeq\":10392,\"seq\":1,\"callId\":\"RID7068176203075096\",\"regDate\":\"2021-10-12\",\"regTime\":\"14:40:31\",\"fileId\":\"BAT_7068176203075096.wav\",\"fileName\":\"voice_file.wav\",\"duration\":\"00:00:10.004\",\"channel\":\"1\",\"frameSp\":\"0.7000\",\"frameEp\":\"1.6100\",\"sttResult\":\"빨간색 \"},{\"sttSeq\":10392,\"seq\":1,\"callId\":\"RID7068176203075096\",\"regDate\":\"2021-10-12\",\"regTime\":\"14:40:31\",\"fileId\":\"BAT_7068176203075096.wav\",\"fileName\":\"voice_file.wav\",\"duration\":\"00:00:10.004\",\"channel\":\"1\",\"frameSp\":\"0.7000\",\"frameEp\":\"1.6100\",\"sttResult\":\"그려줘\"}]}";
		String res= restAPIService.callAPI(sttRes);
//		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! {}",res);
		return res;
	}

	@RequestMapping(value= {"/get_token"})
	@ResponseBody
	public String getToken(@Param("userInfo") RestUserVo userInfo) throws Exception {
		
		return restAPIService.getToken(userInfo );		
	}
	
	@PostMapping(value = "/send_batch")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public String sendBatch(HttpServletRequest request,  @RequestParam(value="upload_file", required = false) MultipartFile upLoadFile
			, @RequestParam HashMap<String, Object> paramMap) throws Exception {
		
		String accessToken = request.getHeader("accessToken");
		paramMap.put("isReal", false);
		String jsonStr =  restAPIService.sendSTT(upLoadFile, paramMap, accessToken);
		
		log.info("STT batch Upload {} ",jsonStr);
		
		return jsonStr;
	}
	
	@Value("${chatbot.use}")
	private boolean chatbotUse;
	
	@PostMapping(value = "/send_real")
	@ResponseBody
	public String sendRealTime(HttpServletRequest request,  @RequestParam(value="upload_file", required = false) MultipartFile upLoadFile
			, @RequestParam HashMap<String, Object> paramMap) throws Exception {
		
		String accessToken = request.getHeader("accessToken");
		paramMap.put("isReal", true);
		String jsonStr =  restAPIService.sendSTT(upLoadFile, paramMap, accessToken);
		
		log.info("STT realtime Upload {} ",jsonStr);
		
		// check chatbot
		if (chatbotUse) {
			jsonStr= restAPIService.callAPI(jsonStr);
			log.info("Chatbot REST RESULT {}",jsonStr);
		}
		
		/** 음원 파일 업로드 후  STT 결과 실시간 전송이 필요할 경우 처리
		 *   application.properties 파일  stt.realtime.use 설정갑 사용
		 *   true : 실시간 결과 전송, false : 실시간 전송 하지 않음
		 */
		/*
		if(jsonStr != null && !jsonStr.isEmpty()) {
			JSONObject json = new JSONObject(jsonStr);
			boolean isResult = json.getBoolean("result");
			if(isResult) {
				int sttSeq = json.getInt("sttSeq");
				jsonStr = restAPIService.getSTTResult(sttSeq, accessToken);
			}
			
			log.info("return STT Text {}", jsonStr);
		}
		*/
		
		return jsonStr;
	}
	
	
	@RequestMapping(value= {"/get_stt_result"})
	@ResponseBody
	public String getSttReuslt(HttpServletRequest request, @RequestParam("sttSeq") String setSeq) throws Exception {
		
		String accessToken = request.getHeader("accessToken");		
		return restAPIService.getSTTResult(Integer.parseInt(setSeq ), accessToken);		
	} 
	
	
	@RequestMapping(value= {"/result_stt"})
	@ResponseBody
	public String sttResult(HttpServletRequest request, @Param("userInfo") RestUserVo userInfo
			, @Param("domainCode") String  domainCode) throws Exception {
		
		String accessToken = request.getHeader("accessToken");		
		return restAPIService.getSTTResult(accessToken, userInfo, domainCode);		
	} 
	
	
	@ResponseBody
	@RequestMapping(value = "/sample/receive_stt")
	public String sampleResultSTT(HttpServletRequest request) throws Exception {
		StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        log.info("receive STT Result {}", jb.toString());
        return jb.toString();
	}
}
