package com.vol.solunote.records;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.DatatypeConverter;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.comm.util.BasicIdGenerator;
import com.vol.solunote.comm.util.CommonUtil;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.comm.util.STTManagerHandler;
import com.vol.solunote.mapper.rest.RestMapper;
import com.vol.solunote.model.vo.rest.DomainVo;
import com.vol.solunote.model.vo.rest.STTResultVo;

import org.mybatis.spring.annotation.MapperScan;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@MapperScan("com.vol.solunote.rest")
public class RecorderServiceImpl extends STTManagerHandler implements RecorderService {
	

	@Value("${file.upload.path.was:#{null}}")
	String UPLOAD_PATH;
	
	@Value("${stt.service.manager.ip}")
	String STT_MANAGER_IP;
	
	@Value("${stt.service.manager.port}")
	int STT_MANAGER_PORT; 
	
	@Value("${stt.service.manager.use}")
	boolean IS_STT_MANAGER; 

	@Autowired
	private RestMapper mapper;
	
	
	@Override
	public JSONObject doRecord(Map<String, String> dataMap) throws Exception {
		// TODO Auto-generated method stub
		String base64data = dataMap.get("base64data").toString();
		String uid = String.valueOf(BasicIdGenerator.nextLong());
		String saveFileName = "BAT_" + uid + ".wav";
		
		boolean isSave = base64ToWav(base64data, saveFileName);
		
		JSONObject json = new JSONObject();
		json.put("result", false);
		
		if(!isSave) {
			
			log.error("Not Save File");
			
			json.put("fileName", "");
			json.put("errMsg", "No File");
			
			return json;
		}
		
		
		String domainName = CommonUtil.nvl(dataMap.get("s_domain_name"), "");
		String domainCode = CommonUtil.nvl(dataMap.get("s_domain_code"), "");
		
		if("".equals(domainCode)) {
			DomainVo domainVo = mapper.getDomainInfo();
			domainCode = domainVo.getDomainCode();
			domainName = domainVo.getDomainName();
		}
		
		if(domainCode == null || "".equals(domainCode)) {
			log.error("Not Save File");
			
			json.put("fileName", "");
			json.put("errMsg", "No Domain Code");
			
			return json;
		}

		
		Map<String, Object> pmap = new HashMap<>();
		pmap.put("s_domain_name", domainName);
		pmap.put("s_domain_code", domainCode);
		
		pmap.put("orgFileName", "ORG_" + uid + ".wav");
		pmap.put("saveFileName", saveFileName);
		
		
		String receiveType = CommonUtil.nvl(dataMap.get("receive_type"), "false");
		
		
		/** 음원 파일 업로드 후 신규 개발한 service.manager 로 정보 전송시 사용함
		 *   application.properties 파일  stt.service.manager.use 설정값 사용
		 *   true : service.manager에 소켓 통신 전송, false : db에 저장하고 끝냄
		 */
		if(IS_STT_MANAGER) {
			
			json = sendSTTManager(pmap, "REC"+uid);
			
			if("false".equals(receiveType))
				return json;
			
			// 실시간 결과 전송일 경우 start
			if(!json.getBoolean("result")) 
				return json;
			
			int sttSeq = (int)json.get("sttSeq");
			
			List<STTResultVo> sttList = mapper.getSTTResult(sttSeq);
//			mapper.setUpdateSTT(sttSeq);
			
			log.info("IS_STT_MANAGER STT LIST : {}", sttList);
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			String jsonStr = objectMapper.writeValueAsString(sttList);
			
			JSONArray jarr = new JSONArray(jsonStr);
			json.put("result", true);
			json.put("sttList", jarr);
			// 실시간 결과 전송일 경우 end
			
			return json;
		}
		
		
		
		int rsInt = saveData(pmap, uid);
		
		if(rsInt < 1) {
			
			log.error("No Insert Data ");
			
			json.put("fileName", "");
			json.put("errMsg", "No Insert Data");
			
			return json;
		}
			
		json.put("result", true);
		json.put("sttSeq", rsInt);
		json.put("fileName", saveFileName);
		json.put("errMsg", "");
		
		// 실시간 결과 전송 start
		if("false".equals(receiveType))
			return json;
		
		String jsonStr = null;
		int loopCount = 0;
		int loopTotalCount = 60;
		String status = "-1";
		int sttSeq = rsInt;
		
		while(++loopCount < loopTotalCount) {
			STTResultVo sttQMap = mapper.getSTTQ(sttSeq);
			
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
			List<STTResultVo> sttList = mapper.getSTTResult(sttSeq);
			mapper.setUpdateSTT(sttSeq);
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			jsonStr = objectMapper.writeValueAsString(sttList);
			
			JSONArray jarr = new JSONArray(jsonStr);
			json.put("result", true);
			json.put("sttList", jarr);

		} else {
			json.put("result", false);
			json.put("errMsg", "errorSTT");
		}
		// 실시간 결과 전송 end		
		
		return json;
	}
	
	// 음성데이터를 wav 파일로 생성
	public boolean base64ToWav(String base64data, String saveFileName) throws IOException{
		String voiceFilePath = UPLOAD_PATH+File.separator;
		FileOutputStream fos = null;
//		File Folder = null;
		boolean isSave = false;
		
		try{

			// 파일명 추가
			voiceFilePath += saveFileName;

			
	        File file = new File(voiceFilePath);
			file.createNewFile();
						
			byte[] decodedByte = DatatypeConverter.parseBase64Binary(base64data.split(",")[1]); 
		
			fos = new FileOutputStream(file);
			fos.write(decodedByte);
	        fos.close();
//	        log.info("파일명 : "+voiceFilePath);
	        isSave = true;
		}
		catch(IOException e){
			log.info("IOException : " + e.getMessage());

		}
		catch(Exception e){
			log.info("Exception : " + e.getMessage());
		}
		finally {
			if(fos != null ) fos.close();
		}
		return isSave;
	}
	
	/**
	 * 파일 업로드(음원파일) 후 db에 정보 저장 처리함
	 * 도메인 code를 보내지 않으면 사용 가능한 도메인 정보 중 가장 먼저 입력된 값으로 처리됨
	 * @param pmap
	 * @param s_uid : call ID 값으로 사용 함(UUID)
	 * @return
	 * @throws Exception
	 */
	private int saveData(Map<String, Object> pmap, String s_uid) throws Exception {
		// TODO Auto-generated method stub
		
		String domainName = CommonUtil.nvl(pmap.get("s_domain_name"), "");
		String domainCode = CommonUtil.nvl(pmap.get("s_domain_code"), "");
		String s_update = DateUtil.getDateString();
		String s_uptime = DateUtil.getTimeString();
//		
//		if("".equals(domainCode)) {
//			DomainVo domainVo = mapper.getDomainInfo();
//			domainCode = domainVo.getDomainCode();
//			domainName = domainVo.getDomainName();
//		}
		
		Map<String,Object> param = new HashMap<>();
		param.put("TC_CALL_ID", "REC" + s_uid);
		param.put("TC_CALL_STARTDATE", s_update);
		param.put("TC_CALL_STARTTIME", s_uptime);
		param.put("TC_CALL_ENDDATE", s_update);
		param.put("TC_CALL_ENDTIME", s_uptime);
		param.put("TC_DOMAIN_CODE", domainCode);
		param.put("TC_DOMAIN_NAME", domainName);
		param.put("TC_FILE_ID", pmap.get("orgFileName"));
		param.put("TC_FILE_NAME", pmap.get("saveFileName"));
		param.put("TC_FILE_ORG_PATH", UPLOAD_PATH + "/");
		int cntSave = mapper.insertWavFile(param);
		
		return cntSave;
	}

	@Override
	public String getSTTResult(int sttSeq) throws Exception {
		// TODO Auto-generated method stub
		
		List<STTResultVo> sttList = mapper.getSTTResult(sttSeq);
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String jsonStr = objectMapper.writeValueAsString(sttList);
		
		JSONArray jarr = new JSONArray(jsonStr);
		JSONObject json = new JSONObject();
		json.put("result", true);
		json.put("sttSeq", sttSeq);
		json.put("sttList", jarr);
		
		return json.toString();
	}

	
}
