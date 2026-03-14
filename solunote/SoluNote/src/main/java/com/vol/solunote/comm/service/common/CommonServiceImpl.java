package com.vol.solunote.comm.service.common;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.Exception.FFMpegCallException;
import com.vol.solunote.Exception.TrainCallException;
import com.vol.solunote.comm.FilenameAwareByteArrayResource;
import com.vol.solunote.comm.LoggingRequestInterceptor;
import com.vol.solunote.comm.service.disk.DiskService;
import com.vol.solunote.comm.service.disk.DiskServiceImpl;
import com.vol.solunote.comm.service.ffmpec.FFMpegService;
import com.vol.solunote.comm.util.DateUtil;
import com.vol.solunote.mapper.comm.CommonMapper;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DomainVo;
import com.vol.solunote.model.vo.comm.ErrorShelf;
import com.vol.solunote.model.vo.transcription.TransVo;

import org.springframework.context.annotation.Primary;

import lombok.extern.slf4j.Slf4j;

@Primary  // <--- 이 어노테이션을 추가하세요.
@Slf4j
public class CommonServiceImpl implements CommonService {
	
	private ThreadLocal<ErrorShelf> threadLocal = new ThreadLocal<>();

	
	@Value("${file.upload.path.meet}")
	String UPLOAD_PATH;
	
	@Value("${train.project_id}")
	private String PROJECT_ID; 
	
	@Autowired
	FFMpegService	ffmpegService;
	
	@Autowired
	private DiskService diskService;
	
	@Autowired
	CommonMapper commonMapper;

	public List<DomainVo> selectDomainList() throws Exception {
		// TODO Auto-generated method stub
		return commonMapper.selectDomainList();
	}
	
	public List<DomainVo> selectSchedulerDomainList() throws Exception {
		// TODO Auto-generated method stub
		return commonMapper.selectSchedulerDomainList();
	}

	@Override
	public Long selectRatio() throws Exception {
		// TODO Auto-generated method stub
		return commonMapper.selectRatio();
	}


	@Override
	public List<String>  selectServerList() throws Exception {
		return commonMapper.selectServerList();
	}
	


	@Override
	public <T> T restPostData(String url, Map<String, Object> body) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonStr = objectMapper.writeValueAsString(body);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		return callRestTemplate(httpHeaders, HttpMethod.POST, url, jsonStr);
	}
	
	
	@Override
	public <T> T restPutData(String url, String dataId, String trainText, Category category, String useYn) throws Exception {
		
		Map<String, Object> body = new HashMap<>();
		body.put("id", dataId);
		body.put("transcript", trainText);
		body.put("useYN", useYn.equals("Y") ? true : false);
		body.put("test", category == Category.TEST);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonStr = objectMapper.writeValueAsString(body);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		return callRestTemplateGen(httpHeaders, HttpMethod.PUT, url, jsonStr);
	}
	
	@Override
	public <T> T restPostFile(String url, String name, String text, double start, double end, Category category) throws Exception {
		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		AudioUtil.copyAudio(file, out, start, end);
//		byte[] array = out.toByteArray();
//		out.close();
		
		
		File file = new File(name);
		
		byte[] all = null;
		try {
			all =  ffmpegService.cutAudio(category, name, start, end);
		} catch ( FFMpegCallException fce ) {
			log.debug("aa");
			throw fce;
		} catch ( Exception e) {
			throw e;
		}
		
		ByteArrayResource resource = new FilenameAwareByteArrayResource(all, file.getName());
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap <>();
		body.add("file", resource);
		body.add("project_id", this.PROJECT_ID);
		body.add("transcript", text);
		body.add("test", category == Category.TEST);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		
		return callRestTemplateGen(httpHeaders, HttpMethod.POST, url, body);
		
	}
	
	
	@Override
	public <T> T restPostStereoFile(String url, TransVo vo, String text, Category category) throws Exception {
		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		AudioUtil.copyAudio(file, out, start, end);
//		byte[] array = out.toByteArray();
//		out.close();
		
		String fileOrPrefix = null;
		String filename = null;
		
		int channelCount = vo.getChannelCount();
		if ( channelCount == 2 ) {
			fileOrPrefix = vo.getFileStereoPrefix();
			filename = vo.getFileStereoPrefix() + "_" + vo.getChannelId() + ".wav";
		} else {
			fileOrPrefix = vo.getFileNewNm();
			filename = vo.getFileNewNm();
		}
		
		byte[] all = null;
		try {
//			all =  ffmpegService.cutAudio(testFlag == true ? "test" : "train", name, start, end);
			all =   ffmpegService.cutAudio(category, fileOrPrefix, Double.toString(vo.getStart()),  Double.toString(vo.getEnd()), Integer.toString(channelCount), Integer.toString(vo.getChannelId()));
		} catch ( FFMpegCallException fce ) {
			log.debug("aa");
			throw fce;
		} catch ( Exception e) {
			throw e;
		}
		
		ByteArrayResource resource = new FilenameAwareByteArrayResource(all, filename);
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap <>();
		body.add("file", resource);
		body.add("project_id", this.PROJECT_ID);
		body.add("transcript", text);
		body.add("test", category == Category.TEST);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		
		return callRestTemplateGen(httpHeaders, HttpMethod.POST, url, body);
		
	}
	
/*	
	 curl -X 'PUT' \
	  'http://192.168.0.247:8989/data/9f8745251d1311ef85990242ac170002' \
	  -H 'accept: application/json' \
	  -H 'Content-Type: multipart/form-data' \
	  -F 'change_transcript=가장 인상깊었던 말이 있습니ㅎ' \
	  -F 'change_useYN=Y' \
	  -F 'change_test=false' \
	  -F 'change_file=@코로나_안내방송1.wav;type=audio/wav'

	 {
	  "id": "6655e20bfe37ef038eee2755"
	}
	*/
	 
	@Override
	public <T> T  restPutFile(String url, TransVo vo, String text, Category category) throws Exception {
			 
		 String name = vo.getFileNewNm();
		 double start = vo.getStart();
		 double end = vo.getEnd();
		
		url += vo.getDataId();
		File file = new File(name);
		
		byte[] all = null;
		try {
			all =  ffmpegService.cutAudio(category, name, start, end);
		} catch ( FFMpegCallException fce ) {
			log.debug("aa");
			throw fce;
		} catch ( Exception e) {
			throw e;
		}
		
		ByteArrayResource resource = new FilenameAwareByteArrayResource(all, file.getName());
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap <>();
		body.add("change_file", resource);
		body.add("change_transcript", text);
		body.add("change_test", category == Category.TEST);
		body.add("change_useYN", vo.getUseYn());
		
		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));    //  Accept=[text/plain, application/json, application/*+json, */*] 가 되면서 에러 남
//		httpHeaders.set("Accept", "application/json");
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		return callRestTemplateGen(httpHeaders, HttpMethod.PUT, url, body);
		
	}
	
//	@Override
//	public Map<String, Object> restPostData(String url, File file, String name, String text, double start, double end) throws Exception {
//		
//		return restPostFile(url, file, name, text, start, end, false);
//	}
	
	@Override
	public <T> T restGetData(String url) {
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		return callRestTemplate(httpHeaders, HttpMethod.GET, url, null);
	}

	@Override
	public <T,S> S callRestTemplate(HttpHeaders httpHeaders, HttpMethod method, String url, T body)  {
		
		S resultMap = null;
		HttpEntity<Object> httpEntity;
		
		try {
			httpEntity = new HttpEntity<>(body, httpHeaders);

//			RestTemplate restTemplate = new RestTemplate();			
			RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
			List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
			interceptors.add(new LoggingRequestInterceptor());
			restTemplate.setInterceptors(interceptors);
			
			
			ResponseEntity<String> response = restTemplate.exchange(url, method, httpEntity, String.class);		

			System.out.println(response);
			HttpStatusCode statusCode = response.getStatusCode();
			
			if (statusCode.is2xxSuccessful()) {
				String result = response.getBody();
				log.debug(response.toString());
				ObjectMapper objectMapper = new ObjectMapper();
				resultMap = objectMapper.readValue(result, new TypeReference<S>() {});
//				System.out.println("objectMapper.readValue():" + resultMap);
			} else {
				int status = statusCode.value();
				log.error("http error : statusCodeValue = " + status);
				
				HttpHeaders headers = response.getHeaders();
				log.error("http error : headers = " );
				headers.forEach( (key, value) -> log.debug(key + " : " + value) );
				
				ErrorShelf es = new ErrorShelf(status);
				threadLocal.set(es);
			}
			
		} catch (Exception e) {
			log.error("error : " + e.getMessage());
			e.printStackTrace();
//			throw new Exception(e);
			ErrorShelf es = new ErrorShelf(-1, e.getMessage());
			threadLocal.set(es);
			resultMap = null;
		} finally {
		}
		
		return resultMap;
	}
	
	@Override
	public <T,S> S callRestTemplateGen(HttpHeaders httpHeaders, HttpMethod method, String url, T body) throws Exception  {
		
		S resultMap = null;
		HttpEntity<Object> httpEntity;
		log.debug(" Url = {}", url );
		
		try {
			httpEntity = new HttpEntity<>(body, httpHeaders);
			
			RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
			ResponseEntity<String> response = restTemplate.exchange(url, method, httpEntity, String.class);		
			
			System.out.println(response);
			HttpStatus statusCode = (HttpStatus) response.getStatusCode();
			
			if (statusCode.is2xxSuccessful()) {
				String result = response.getBody();
				log.debug(response.toString());
				ObjectMapper objectMapper = new ObjectMapper();
				resultMap = objectMapper.readValue(result, new TypeReference<S>() {});
//				System.out.println("objectMapper.readValue():" + resultMap);
			} else {
				throw new Exception("Error : IMPOSSIBLE CODE");
			}
			
		} catch (Exception e) {
			log.error("error : " + e.getMessage());
			e.printStackTrace();
			
			TrainCallException tce = new TrainCallException(e, url);
			if ( ! tce.getStatus().startsWith("4") ) {
				throw new Exception(e.getMessage());
			}
			throw tce;
		} finally {
		}
		
		return resultMap;
	}

	/**
	 * getErrorShelf() 를 호출한 후에는, 반드시 removeErrorShelf() 를 호출해야 한다.
	 */
	@Override
	public ErrorShelf getErrorShelf() {
		return threadLocal.get();
	}
	
	@Override
	public void removeErrorShelf() {
		threadLocal.remove();
	}
	
	@Override
	public Path getUploadPath(String fileName) throws Exception {
		return Paths.get(UPLOAD_PATH + File.separator  + fileName);
	}
	
	@Override
	public Map<String, Object> saveUploadFileConvert(Category category, MultipartFile file) throws Exception {
		
		
		String originalfileName = file.getOriginalFilename();
		String extension = FilenameUtils.getExtension(originalfileName);
		Resource resource = null;
		long size = 0;
		if ( extension.equalsIgnoreCase("wav") || extension.equalsIgnoreCase("mp3") ) {
			resource = file.getResource();
			size = file.getSize();
		} else {
			
		}
		
//		String uploadFileName = commonService.saveUploadFileConvert(file);
		String subdir = DateUtil.getFormatString(DiskServiceImpl.subdirPattern);
		
		Path path = Paths.get(UPLOAD_PATH + File.separator  + subdir);
		if ( Files.exists(path) == false ) {
			Files.createDirectories(path);
		}
		
		// 1. save file AS-IS
		Path fullPath = Paths.get(path.toString(), originalfileName);
		file.transferTo(Paths.get(path.toString(), originalfileName));
		String uploadFileName = subdir +  "/"  + originalfileName;   // db 에 저장하는 path 이므로 File.separator 대신에 "/" 를 사용함
		
		String division =  originalfileName.length() > 1 ? originalfileName.substring(0, 2) : "";
		
		Map<String, Object> param = new HashMap <>();
		param.put("subject", originalfileName);
		// timeDurationStr 은 callStt()  이후 set 해야 함
//		param.put("timeDurationStr", durationMs);
		param.put("orgnm", uploadFileName);
		param.put("newnm", fullPath.toString());
		param.put("division", division);
		param.put("fileSizeBytes", size);
		// tcUserSeq 은 별도로 set 해야 함
//		param.put("tcUserSeq", tcUserSeq);
		
		param.put("resource", resource);
		
		return param;		
	}
	
	@Override
	public String saveUploadFile(MultipartFile file, String saveFileName) throws IOException {
		
		String subdir = DateUtil.getFormatString(DiskServiceImpl.subdirPattern);
		
		Path path = Paths.get(UPLOAD_PATH + File.separator  + subdir);
		if ( Files.exists(path) == false ) {
			Files.createDirectories(path);
		}
		
		file.transferTo(Paths.get(path.toString(), saveFileName));
		
		return subdir +  "/"  + saveFileName;   // db 에 저장하는 path 이므로 File.separator 대신에 "/" 를 사용함
	}
	
	@Override
	public void removeDiskFile(Category category, String subPath) throws Exception  {
		
		String root = diskService.getUploadPath(category);
		Path path = Paths.get(root, subPath);
		
//		try {
			boolean flag = Files.deleteIfExists(path);
			log.info("delete file : {} = {}", flag, path.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
		
}