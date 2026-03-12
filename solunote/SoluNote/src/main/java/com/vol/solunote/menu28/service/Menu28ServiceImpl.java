package com.vol.solunote.menu28.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.service.CommonSteelServiceImpl;
import com.vol.solunote.repository.transcription.TranscriptionRepository;
import com.vol.solunote.repository.tts.TtsRepository;
import com.vol.solunote.comm.util.HtmlVisitor;
import com.vol.solunote.comm.vo.SearchVo;
import com.vol.solunote.model.vo.transcription.TransVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Menu28ServiceImpl implements Menu28Service {

	@Value("${file.upload.path.tts}")
	String UPLOAD_PATH;	

	
	@Value("${stt.url}")
	private String sttUrl;	
	
	@Autowired
	private CommonSteelServiceImpl commonService;
	
	@Autowired
	private TranscriptionRepository transcriptionRepository;
	
	@Autowired
	TtsRepository ttsRepository;
	
	@Override
	public List<TransVo> getTtsTransListBatch(SearchVo search, OffsetPageable offsetPageable ,String keyword, String caller) throws Exception {
		
		List<TransVo> list = transcriptionRepository.getTtsTransListBatch(search, offsetPageable, keyword,  caller);
		
		for( TransVo vo : list ) {
			String stt = vo.getSttText();
			String train = vo.getTrainText();

			if ( train != null ) {
				HtmlVisitor htmlVisitor = new HtmlVisitor();
				htmlVisitor.diff(stt, train);
				
				vo.setSttText(htmlVisitor.getLeft());
				vo.setTrainText( htmlVisitor.getRight());
			}
		}		
		return list;
	}
	
	@Override
	public List<TransVo> getTtsTransListBatchTTS(SearchVo search, OffsetPageable offsetPageable ,String keyword, String caller) throws Exception {
		

		List<TransVo> list = transcriptionRepository.getTtsTransListBatch(search, offsetPageable, keyword,  caller);
		
		return list;
	}	
	
	@Override
	public List<Map<String, Object>> getList(Map<String, Object> param) throws Exception {
		
		List<Map<String, Object>> list = ttsRepository.getList(param);
		
		for (Map<String, Object> item : list) {

		    if (item.get("timeDurationFormatted") != null && !item.get("timeDurationFormatted").toString().isEmpty()) {
		        double durationMsDouble = (double) item.get("timeDurationFormatted");
		        long durationMs = (long) durationMsDouble; 
		        String formattedTime = formatDuration(durationMs);
		        item.put("timeDurationFormatted", formattedTime);
		    } else {
		        item.put("timeDurationFormatted", "0");  
		    }
		    if (item.get("reliability") == null || item.get("reliability").toString().isEmpty()) {
		        item.put("reliability", 0);
		    }else {
		        BigDecimal reliabilityBD = (BigDecimal) item.get("reliability"); 
		        long reliabilityInt = reliabilityBD.longValue(); 
		        item.put("reliability", reliabilityInt); 
		    }
		}		

		return list;
	}
	
	@Override
	public List<Map<String, Object>> getTextList(Map<String, Object> param) throws Exception {
		
		List<Map<String, Object>> list = ttsRepository.getTextList(param);
		
		for (Map<String, Object> item : list) {
			
			if (item.get("timeDurationFormatted") != null && !item.get("timeDurationFormatted").toString().isEmpty()) {
				double durationMsDouble = (double) item.get("timeDurationFormatted");
				long durationMs = (long) durationMsDouble; 
				String formattedTime = formatDuration(durationMs);
				item.put("timeDurationFormatted", formattedTime);
			} else {
				item.put("timeDurationFormatted", "0");  
			}
			if (item.get("reliability") == null || item.get("reliability").toString().isEmpty()) {
				item.put("reliability", 0);
			}else {
				BigDecimal reliabilityBD = (BigDecimal) item.get("reliability"); 
				long reliabilityInt = reliabilityBD.longValue(); 
				item.put("reliability", reliabilityInt); 
			}
		}		
		
		return list;
	}
	
	@Override
	public List<Map<String, Object>> getExcelList(MultipartFile file) throws Exception {
	    List<Map<String, Object>> pages = new ArrayList<>();

	    boolean isFirstRow = true;
	    
	    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
	        Sheet sheet = workbook.getSheetAt(0);

	        for (Row row : sheet) {
	            if (isFirstRow) {
	                isFirstRow = false;
	                continue; // 첫 번째 행을 건너뜀
	            }
	        		        	
	            Map<String, Object> list = new HashMap<>();
	            
	            Cell keywordCell = row.getCell(0);
	            Cell speechCell = row.getCell(1);
	            Cell detailCell = row.getCell(2);

	            if (keywordCell != null && speechCell != null) {
//	                String keyword = keywordCell.getStringCellValue();
//	                String speech = speechCell.getStringCellValue();          	
	                String keyword = (keywordCell != null) ? keywordCell.getStringCellValue() : "";
	                String speech = (speechCell != null) ? speechCell.getStringCellValue() : "";
	                String detail = (detailCell != null) ? detailCell.getStringCellValue() : "";
	                
	                list.put("keyword", keyword);
	                list.put("speech", speech);
	                list.put("detail", detail);
	                
	                // 빈 값으로 설정
	                list.put("keywordText", "");
	                list.put("pronounceText", "");

	                pages.add(list);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new Exception("Excel 파일 읽기 실패", e);
	    }
	    
	    return pages;
	}
	
	@Override
	public void saveAudio(Map<String, Object> result) throws Exception {
		ttsRepository.saveAudio(result);
	}
	
	
	
	@Override
	public Path getUploadPath(String fileName) throws Exception {
		return Paths.get(UPLOAD_PATH + File.separator  + fileName);
	}	
	
	
	@Override
	public int crateTitle(String title, int tcUserSeq) throws Exception {
		
	    Map<String, Object> params = new HashMap<>();
	    params.put("title", title);
	    params.put("tcUserSeq", tcUserSeq);		
		
	    ttsRepository.crateTitle(params);
	    
	    BigInteger seq = (BigInteger) params.get("SEQ");
				
		return	seq.intValue();
	}	
	
	
	@Override
	public void updateAudio(int titleSeq, int seq) throws Exception {
		
	    Map<String, Object> params = new HashMap<>();
	    params.put("titleSeq", titleSeq);
	    params.put("seq", seq);		
		
		ttsRepository.updateAudio(params);
	}	
	
	
	
	@Override
	public void excelForm(HttpServletResponse response) throws IOException {

	    XSSFWorkbook workbook = new XSSFWorkbook();
	    XSSFSheet sheet = workbook.createSheet();

	    
	    Row headerRow = sheet.createRow(0);
	    Cell keywordCell = headerRow.createCell(0);
	    keywordCell.setCellValue("키워드");
	    
	    Cell pronunciationCell = headerRow.createCell(1);
	    pronunciationCell.setCellValue("발음기호");	    
	    
	    Cell detailCell = headerRow.createCell(2);
	    detailCell.setCellValue("키워드 설명");		    


	    // 파일 출력 로직은 동일
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    workbook.write(baos);
	    workbook.close();

	    ServletOutputStream out = response.getOutputStream();
	    out.write(baos.toByteArray());
	    
	    response.setContentType("application/octet-stream");
	    response.setHeader("filename_base64", Base64.getEncoder().encodeToString(("키워드학습양식.xlsx").getBytes()));

	    out.flush();
	    out.close();
	}		
	
	
	@Override
	public void callStt(int titleSeq, int seq) throws Exception {		
	    Map<String, Object> params = new HashMap<>();
	    params.put("seq", seq);		
		
	    Map<String, Object> info = ttsRepository.getAudioInfo(params);
	    
	    String fileName = info.get("newnm").toString();
	    
	    String ttsText = info.get("ttsText").toString();
	    

	    Paths.get(UPLOAD_PATH + File.separator  + fileName);
	    
		Path convPath = Paths.get(UPLOAD_PATH + File.separator  + fileName);
		org.springframework.core.io.Resource resource = new FileSystemResource(convPath);
	    
	    
		Instant startNow = Instant.now();
		log.debug("4. stt started = {} data {}", startNow.toString(), info.get("newnm").toString());	    
	    
	    
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap <>();
		body.add("file", resource);
		
		String url = this.sttUrl;
		url += "/ko"; 		

		
		Map<String, Object> resultMap = commonService.callRestTemplateGen(httpHeaders, HttpMethod.POST, url, body);

		log.debug("rest resultMap = {}", resultMap);
		
		Map<String, Object> combinResult = new HashMap<>();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> sttResultList = (List<Map<String, Object>>) resultMap.get("stt_result");

		if (sttResultList != null && !sttResultList.isEmpty()) 
		{
		    StringBuilder textBuilder = new StringBuilder();
		    double start = (double) sttResultList.get(0).get("start"); 
		    double end = (double) sttResultList.get(sttResultList.size() - 1).get("end"); 

		    double totalConfidence = 0;
		    for (Map<String, Object> sttEntry : sttResultList) {
		        textBuilder.append(sttEntry.get("text")).append(" ");
		        totalConfidence += Double.parseDouble(sttEntry.get("confidence").toString());
		    }

		    int averageConfidence = (int) ((totalConfidence / sttResultList.size()) * 100);

		    combinResult.put("text", textBuilder.toString().trim()); 
		    combinResult.put("start", start);
		    combinResult.put("end", end); 
		    combinResult.put("reliability", averageConfidence); 
		}		
		
		int TranSeq = createTranscription(combinResult.get("start").toString(),
							combinResult.get("end").toString(),
							combinResult.get("text").toString().trim(),
							Integer.parseInt(combinResult.get("reliability").toString()),
							-1,
							-1,
							seq,
							0);		
				
		Map<String, Object> map = new HashMap<>();
		map.put("seq", seq);
		map.put("reliability", combinResult.get("reliability"));
		
		ttsRepository.updateTts(map);
		
		String replaceTtsText = ttsText.replaceAll("[.,]", "").trim();
		String replaceCombinResultText = combinResult.get("text").toString().replaceAll("[.,]", "").trim();
		
		if (!replaceTtsText.equals(replaceCombinResultText)) {
		    transcriptionRepository.updateTrainTextBySeq( TranSeq, ttsText);
		}	    
	}		
	
	

	public int createTranscription(
			String start, String end, String text, 
			int reliability, int meetingSeq, int soundSeq,int ttsSeq ,int channelId
			)throws Exception {
				
		Map<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		map.put("text", text);
		map.put("reliability", reliability);   // 0 - 100
		map.put("meetingSeq", meetingSeq);
		map.put("soundSeq", soundSeq);
		map.put("ttsSeq", ttsSeq);     
		map.put("channelId", channelId);	
		
		
		transcriptionRepository.createTranscriptionForTts(map);
		
		int seq = ((BigInteger) map.get("seq")).intValue();
		
		return seq;
	}	
	
	private String formatDuration(long durationMs) {
	    long seconds = (durationMs / 1000) % 60;
	    long minutes = (durationMs / (1000 * 60)) % 60;
	    long hours = (durationMs / (1000 * 60 * 60));

	    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	
    @Override
    public void clickLeastOnce(Integer seq) throws IOException {
        ttsRepository.clickLeastOnce(seq);
		
	}	
	
	
	@Override
	public List<Map<String, Object>> searchList(Map<String, Object> param) throws Exception {
		
		List<Map<String, Object>> list = ttsRepository.searchList(param);
									
		return list;
	}	
	
    @Override
    public List<Map<String, Object>> getTransList(Map<String, Object>param) throws Exception {
        
        List<Map<String, Object>> list = transcriptionRepository.getTransList(param);
        
        for( Map<String, Object> tr : list ) {
            if ( tr.get("trainText") == null ) {
                tr.put("trainText", "");
            }
                                    
        }
        return list;
    }		

    @Override
    public void updateTtsList(Integer seq,String type) throws IOException {
        ttsRepository.updateTtsList(seq,type);
		
	}		    
    
	@Override
	@Transactional
	public void trash( List<Map<String, Object>> list,String type) throws Exception {
				
		
	    int[] TransSeq = list.stream()
                .filter(map -> map.containsKey("seq"))
                .mapToInt(map -> (Integer) map.get("seq"))
                .toArray();
	    
	    String[] tts_text = list.stream()
	    	    .filter(map -> map.containsKey("tts_text"))
	    	    .map(map -> (String) map.get("tts_text"))  
	    	    .toArray(String[]::new);	    
	    
	    int[] keywordSeq = list.stream()
                .filter(map -> map.containsKey("keywordSeq"))
                .mapToInt(map -> (Integer) map.get("keywordSeq"))
                .toArray();	    
	    
	    
		if (TransSeq.length > 0) {		
			if(type.equals("trash")) {
				Map<String, Object> params = new HashMap<>();
		        params.put("TransSeq", TransSeq);	
		        params.put("tts_text", tts_text);			
		        transcriptionRepository.updateTransTranscription(params); 			
			}
			
	        Map<String, Object> params2 = new HashMap<>();
	        params2.put("keywordSeq", keywordSeq);
	        params2.put("type", type);

	        ttsRepository.updateKeywordDate(params2); 
		}
	}    	
}
