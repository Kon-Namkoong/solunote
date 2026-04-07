package com.vol.solunote.comm.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import com.vol.solunote.model.status.STTManagerRsSerivce;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class STTManagerHandler implements STTManagerRsSerivce {

	
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

	
	/**
	 * service.manager 로 보낼 json 정보 만들기 
	 * @param paramMap
	 * @param s_uid
	 * @return
	 * @throws Exception
	 */
	public JSONObject sendSTTManager(Map<String, Object> paramMap, String s_uid) throws Exception {
		
		
		String fileName = CommonUtil.nvl(paramMap.get("orgFileName"), "");
		String saveFileName = CommonUtil.nvl(paramMap.get("saveFileName"), "");
		
		String domainName = CommonUtil.nvl(paramMap.get("s_domain_name"), "");
		String domainCode = CommonUtil.nvl(paramMap.get("s_domain_code"), "");
		
		if("".equals(domainCode)) {
			JSONObject json = new JSONObject();
			json.put("fileName", fileName);
			json.put("saveFileName", saveFileName);
			json.put("result", false);
			json.put("errMsg", "no send Domain Info");
			return json;
		}
		
		JSONObject json  = new JSONObject();
		json.put("speechID", s_uid);
		json.put("domainCode", domainCode);
		json.put("domainName", domainName);
		json.put("fileID", saveFileName);
		json.put("filePath", UPLOAD_PATH);
		json.put("audioPath", AUDIO_PATH+File.separator+DateUtil.getShortDateString());
		json.put("fileName", fileName);
	
		
		String sendData = setSendSTTInfo(json.toString(), "1002");
		log.debug("sendData {}", sendData);
		
		if(sendData == null || "".equals(sendData))  {
			json =new JSONObject();
			json.put("fileName", fileName);
			json.put("saveFileName", saveFileName);
			json.put("callId", s_uid);
			json.put("result", false);
			json.put("errMsg", "noSendSTTServiceData 0");
			return json;
		}
		
		Socket socket = new Socket();
		try {
			socket.connect( new InetSocketAddress(STT_MANAGER_IP, STT_MANAGER_PORT));
	
			if(socket.isClosed()) {
				log.debug("SOCKET IS CLOSE");
			}
			
			if(!socket.isConnected()) {
				log.debug("SOCKET IS not CONNECTED");
			}
			
			try ( PrintWriter pw = 
					new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true) ) 
			{
				pw.print(sendData);
				pw.flush();
			}
			
			InputStream in =  socket.getInputStream();
			
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in)); 
//			log.debug("abcdd === {}",  reader.read());
			
			
			byte[] bytes = new byte[4096];
	
			int readByteCount = in.read(bytes);
			String message = new String(bytes, 0, readByteCount, "UTF-8");
	
			log.debug("[Service Manager 메시지]: {}", message);
			
			if(message == null || "".equals(message) || message.length() < 12)  {
				json =new JSONObject();
				json.put("fileName", fileName);
				json.put("saveFileName", saveFileName);
				json.put("result", false);
				json.put("errMsg", "noSendSTTServiceData 2");
				return json;
			}
			
			JSONObject receiveJson = new JSONObject(message.substring(12)) ;
			json =receiveJson;
			json.put("fileName", fileName);
			json.put("saveFileName", saveFileName);
			if(SUCCESS.equals(receiveJson.getString("result")) ) {
				json.remove("state");
//				json.put("callId", s_uid);
				json.put("callId", receiveJson.getString("speechID"));
				json.put("sttSeq", receiveJson.getInt("sttSeq"));
//				json.put("result", true);
//				json.put("sttList", receiveJson.getJSONArray("transcript"));
				json.put("errMsg", "");
				
//				json.remove("transcript");
				
				return json;
				
			} else  {
				json =new JSONObject();
				json.put("result", false);
				json.put("fileName", fileName);
				json.put("saveFileName", saveFileName);
				json.put("errMsg", "SendSTTServiceError");
				return json;
			}
			
		} catch (IOException e) {
			json =new JSONObject();
			json.put("fileName", fileName);
			json.put("saveFileName", saveFileName);
			json.put("callId", s_uid);
			json.put("result", false);
			json.put("errMsg", "noSendSTTServiceData 1");
			return json;
		}
		finally {
			if (null != socket)
			{
				socket.close();
			}
		}
	}

	/**
     * service.manager 로 보낼 때 사용하는 데이타 packet 정보 처리
     * @param sendData
     * @param restSendId 
     * @return
     * @throws UnsupportedEncodingException 
     */
    private String setSendSTTInfo(String sendData, String restSendId) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
    	
    	if(sendData == null && "".equals(sendData)) {
    		return null;
    	}
    	
//    	String lengcontent = String.format("%8d", sendData.getBytes().length);
//    	String restSendId = "1002";
		return restSendId+String.format("%8d", sendData.getBytes("UTF-8").length)+sendData;
	}
	
}

