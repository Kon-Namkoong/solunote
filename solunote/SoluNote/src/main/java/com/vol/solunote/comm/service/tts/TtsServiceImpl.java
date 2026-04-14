package com.vol.solunote.comm.service.tts;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.Exception.TrainCallException;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.disk.DiskServiceImpl;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.repository.tts.TtsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class TtsServiceImpl implements TtsService {
	
	
	@Value("${tts.url}")
	private String TTS_URL;
	
	@Value("${file.upload.path.tts:#{null}}")
	private String TTS_PATH;
	
	@Autowired
	private CommonSteelServiceImpl commonService;

	@Autowired
	private TtsRepository ttsRepository;

	@Override
	public Map<String, Object> generateText(String keyword, String speech , String detail) throws Exception {

		String url = TTS_URL + "/generation-text";
		
		Map<String, Object> body = new HashMap<>();
		body.put("keyword", keyword);
		body.put("speech", speech);
		body.put("detail", detail);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonStr = objectMapper.writeValueAsString(body);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		Map<String, Object> resultMap = commonService.callRestTemplateGen(httpHeaders, HttpMethod.POST, url, jsonStr);
		
		log.debug("result : {}", resultMap);

		return resultMap;
		
	}
	
	@Override
	public Map<String, Object> saveTts(String voice, String sentence) throws Exception {
		
		String url = TTS_URL + "/save-tts";
		
		Map<String, Object> resultMap = new HashMap<>();
		
		Map<String, Object> body = new HashMap<>();
		
		log.debug("voice = {}, sentence = {}", voice, sentence);
		
		body.put("voice", voice);
		body.put("sentence", sentence);
		
		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		try {
			
			HttpEntity<Object> httpEntity = new HttpEntity<>(body,httpHeaders);
			
			RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
			ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, byte[].class);	
			
//			System.out.println(response);
			HttpStatus statusCode = (HttpStatus) response.getStatusCode();
			
			if (statusCode.is2xxSuccessful()) {
				
				byte[] result = response.getBody();
				
				resultMap = saveTtsFile(sentence, result);
			
				System.out.println("statusCode:" + statusCode);
			} else {
				response.getStatusCode();
				response.getHeaders();
				throw new Exception("Error : IMPOSSIBLE CODE");
			}
		} catch (Exception e) {
			log.error("error : " + e.getMessage());
			
			TrainCallException tce = new TrainCallException(e, url);
			if ( ! tce.getStatus().startsWith("4") ) {
				throw new Exception(e.getMessage());
			}
			throw tce;
		} finally {
		}
	
		return resultMap;
	}

	private Map<String, Object> saveTtsFile(String sentence, byte[] result) throws IOException {
		
		Map<String, Object> resultMap = new HashMap<>();		

		String saveFileName = UUID.randomUUID().toString() + ".wav";
		
		String subdir = DateUtil.getFormatString(DiskServiceImpl.subdirPattern);
			
		String fileNewName = subdir + "/" + saveFileName;
		
		long durationMs = getWavFileDuration(result); 

		resultMap.put("subject", sentence);
		resultMap.put("timeDurationStr", durationMs);
		resultMap.put("orgnm", resultMap.get("subject"));
		resultMap.put("newnm", fileNewName);		
				
		Path path = Paths.get(TTS_PATH + File.separator  + subdir);	
		
		if ( Files.exists(path) == false ) {
			Files.createDirectories(path);
		}
		
		Path fullPath = Paths.get(path.toString(), saveFileName);
	
		Files.write(fullPath, result);	
		
		resultMap.put("fileSizeBytes", Files.size(fullPath));
		
		return resultMap;		
	}
		
	private long getWavFileDuration(byte[] audioData) {
	    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioData))) {
	        AudioFormat format = audioInputStream.getFormat();
	        long frames = audioInputStream.getFrameLength();
	        return (long) ((frames / format.getFrameRate()) * 1000);  // ms 단위로 변환
	    } catch (IOException e) {
	    	return	0;
	    } catch (Exception e) {
	        return 0;
	    }
	}	

}
