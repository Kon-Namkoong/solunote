package com.vol.solunote.batch.service.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.config.ResultRestConfig;
import com.vol.solunote.mapper.comm.ReportMapper;
import com.vol.solunote.repository.rest.RestRepository;
import com.vol.solunote.model.vo.rest.STTResultVo;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;

@Service
@Slf4j
public class DemoSchedulerServiceImpl implements DemoSchedulerService, ResultRestConfig {
	
	private static Locale locale = Locale.KOREA;
	
	@Value("${rest.send.use}")
	boolean IS_POST_SEND;
	
	@Value("${rest.send.retry.cnt}")
	int RETRY_CNT;
	
	@Value("${rest.send.url}")
	String POST_SEND_URL;

	@Value("${stt.service.manager.ip}")
	String STT_MANAGER_IP;
	
	@Value("${stt.service.manager.port}")
	int STT_MANAGER_PORT; 
	
	/** STT Manager변경시 수정 필요 */
	@Value("${stt.service.manager.use}")
	boolean IS_STT_MANAGER; 
	
	@Autowired
	private	RestRepository restRepository;
	
	@Autowired
	private	ReportMapper reportMapper;

	public Map<String, Object> restPostSendResultSTT(String json, String callURL) {
		
		Map<String, Object> rsMap = new HashMap<>();
		String respon = null;
		HttpURLConnection con = null;
		try {
			//HttpURLConnection 객체를 생성해 openConnection 메소드로 url 연결
			URL url = null ;
			
			if(callURL == null || "".equals(callURL))
				callURL = POST_SEND_URL;
			
			// URI를 거쳐서 URL로 변환
			url = URI.create(callURL).toURL();
			
			con = (HttpURLConnection) url.openConnection();
			
			log.info("http conn {} {} ", con, callURL);
//		log.info(" http connection {}", con.getResponseCode());
			
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			
			//전송 방식 (POST)
			con.setRequestMethod("POST");
 
			//application/json 형식으로 전송, Request body를 JSON으로 던져줌.
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			
			//Response data를 JSON으로 받도록 설정
			con.setRequestProperty("Accept", "application/json");
			//Output Stream을 POST 데이터로 전송
			con.setDoOutput(true);
			
			//JSON 보내는 Output stream
			try(OutputStream os = con.getOutputStream()){
				byte[] input = json.getBytes("utf-8");
				os.write(input,0,input.length);
			} 
			 
			//응답을 받는 곳 
			try(BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream(),"utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while((responseLine = br.readLine()) != null)
				{
					response.append(responseLine.trim());
				}
				respon = response.toString();
				//System.out.println("####" + respon);
			}
			con.disconnect();
		} catch (MalformedURLException e) {
			log.info("MalformedURLException in restPostSendResultSTT");
		} catch (ProtocolException e) {
			log.info("ProtocolException in restPostSendResultSTT");
		} catch (UnsupportedEncodingException e) {
			log.info("UnsupportedEncodingException in restPostSendResultSTT");
		} catch (IOException e) {
			log.info("IOException in restPostSendResultSTT");
		} finally  {
			rsMap.put(rsStrKeyNm, respon);
			try {
				if (null != con )
					rsMap.put(rsCodeKeyNm, con.getResponseCode());
			} catch (IOException e) {
				log.error("IOException ", e);
			}	
		}
		
		return rsMap;
		
	}
	
	@Override
	public void sendPOSTResultSTT() throws Exception {

		if(!IS_POST_SEND) {
			log.debug("is NOT POST REST SEND");
			return;
		}
		
		List<STTResultVo> sttQMap = restRepository.getSTTQList();
		for (STTResultVo vo : sttQMap) {
			log.debug("Rest DB Info {}  {}  {}", vo.getSttSeq(), vo.getCallId(), vo.getStatus());
			List<STTResultVo> sttList = restRepository.getSTTResult(vo.getSttSeq());
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			String jsonStr = objectMapper.writeValueAsString(sttList);
			
			JSONArray jarr = new JSONArray(jsonStr);
			
			log.debug("Rest STT Info {}  ", jarr.toString());
			
			JSONObject json = new JSONObject();
			json.put("result", true);
			json.put("sttSeq", vo.getSttSeq());
			json.put("sttList", jarr);
			
			Map<String, Object> rsMap = null;
			for (int i = 0; i < RETRY_CNT; i++) {
				rsMap = restPostSendResultSTT(json.toString(), vo.getCallUrl());
				if(rsMap != null 
						&& rsMap.get(rsCodeKeyNm) != null && (Integer)rsMap.get(rsCodeKeyNm) == HttpURLConnection.HTTP_OK 
//						&& rsMap.get(rsStrKeyNm) != null && !"".equals(rsMap.get(rsStrKeyNm))
						) {
					break;
				} else {
					if(i == (RETRY_CNT-1))
						log.error("err {} {} {} ",i , json.toString(), rsMap);
				}
			}
			
			log.info("rest Send Info {} {} ", json.toString(), rsMap);
			restRepository.setUpdateSTT(vo.getSttSeq());	
		}	
	}

	@Override
	public void getServerStatus() throws Exception {
		
		if(!IS_STT_MANAGER) {
			log.debug("is NOT STATUS");
			return;
		}
		
		
		JSONObject json =new JSONObject();
		json.put("type", "all");
		
		String sendData = setSendSTTInfo(json.toString(), "1003");
		log.debug("sendData {}", sendData);
		
		Socket socket=null;
		try {			
			socket = new Socket();
			socket.connect( new InetSocketAddress(STT_MANAGER_IP, STT_MANAGER_PORT));
						
			try ( PrintWriter pw = 
					new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true) ) {
				pw.print(sendData);
				pw.flush();
			}
						
			InputStream in =  socket.getInputStream();
			
			byte[] bytes = new byte[4096];

			int readByteCount = in.read(bytes);
			String message = new String(bytes, 0, readByteCount, "UTF-8");

			log.debug("[Service Manager 메시지]: {}", message);
			
			if(message == null || "".equals(message) || message.length() < 12)  {
				return;
			}
			
			JSONObject receiveJson = new JSONObject(message.substring(12)) ;
			
			JSONObject CPU = receiveJson.getJSONObject("cpu") ;
			String CPUratio = CPU.getString("user") ;
			
			JSONObject RAM = receiveJson.getJSONObject("memory") ;
			String RAMfull = RAM.getString("total") ;
			String RAMfree = RAM.getString("free") ;
			
			JSONArray HDD = receiveJson.getJSONArray("disk") ;
			String HDDfull = ((JSONObject)HDD.get(0)).getString("total") ;
			String HDDfree = ((JSONObject)HDD.get(0)).getString("free") ;
			
			String update = DateUtil.getDateString();
			String uptime = DateUtil.getTimeString();
			
			Map<String, Object> map = Map.of(
				    "serverSeq", 1,
				    "workDate", update,
				    "workTime", uptime,
				    "CPUratio", CPUratio,
				    "RAMfull", RAMfull,
				    "RAMfree", RAMfree,
				    "HDDfull", HDDfull,
				    "HDDfree", HDDfree
				);

			reportMapper.insertStatus(map);
				
		} catch (IOException e) {
			log.info("IOException in getServerStatus");
		} catch (Exception e) {
			log.info("Exception in getServerStatus");			
		}		
		finally {
			if (null != socket)
			{
				socket.close();
			}
		}
	}

    public String setSendSTTInfo(String sendData, String restSendId) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
    	
    	if(sendData == null && "".equals(sendData)) {
    		return null;
    	}
    	
		return restSendId+String.format("%8d", sendData.getBytes("UTF-8").length)+sendData;
	}

	@Override
	public void setReport() throws Exception {
		// TODO Auto-generated method stub
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", locale);
		String dataStr = formatter.format(new java.util.Date());
		log.info("dataStr {}", dataStr);
		
		reportMapper.setReportHourly(dataStr);
		reportMapper.setReportDaily(dataStr);		
	}
}
