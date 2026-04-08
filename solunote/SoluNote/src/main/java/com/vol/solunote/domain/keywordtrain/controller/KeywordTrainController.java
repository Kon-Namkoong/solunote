package com.vol.solunote.domain.keywordtrain.controller;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.Exception.TrainCallException;
import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.stt.SttService;
import com.vol.solunote.comm.service.tts.TtsService;
import com.vol.solunote.domain.keywordtrain.service.KeywordTrainService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.repository.transcription.TranscriptionRepository;
import org.owasp.encoder.Encode;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/keywordtrain")
public class KeywordTrainController extends DefaultController {
	
	@Autowired
	private TtsService ttsService;	
	
	@Autowired
	private SttService	 sttService;
	
	@Autowired
	private CommonSteelServiceImpl commonService;
	
	@Autowired
    private TranscriptionRepository transcriptionRepository;          
    
	@Autowired
	private KeywordTrainService keywordTrainService;

	public static int processingFileCnt = 0;
	
	KeywordTrainController() {
		this.menuId="keywordtrain";
	}
	
	@RequestMapping(value= {"/cont", "/cont/1"})
	public String cont(HttpServletRequest request, 
			Model model,
			@Param("search") DefaultVo search,
			@RequestParam(required = false) Integer activeMenu, 
			@RequestParam(required = false) Integer seq
			) throws Exception {
		
		setSearchTerm(search);
		
		model.addAttribute("search", search);
		model.addAttribute("menuId", menuId);
		model.addAttribute("liveMenu", activeMenu);
		
		log.debug("activeMenu = " + activeMenu);
	
		return "thymeleaf/"+menuId+"/cont.html";
	}
	
	
	
	@GetMapping(value= {"/cont/loadList", "/cont/1/loadList"})
	public String loadList(Model model,
			@Param("pageable")  @PageableDefault(size = 10, page = 1) Pageable pageable,
			@RequestParam(value="activeMenu") int activeMenu,
			@RequestParam(required = false) String keyword,
			@RequestParam String searchStartDate , 
			@RequestParam String searchEndDate,
            @RequestParam(required = false) String candidate,
            @RequestParam(required = false) String useYn
			) throws Exception {
		
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
		
		SearchVo search = new SearchVo();
		search.setSearchStartDate(searchStartDate);
		search.setSearchEndDate(searchEndDate);
		
		Map<String, Object> param = null;
		List<?> pages = null;
		long hiddenCount = 0;
		
		if ( activeMenu == 1 ) {
			 param = generateRequestParam("offsetPageable, activeMenu, keyword, searchStartDate, searchEndDate", 
						offsetPageable, activeMenu, keyword, searchStartDate, searchEndDate);
			 
			pages = keywordTrainService.getList(param);
			
			if ( pages != null && pages.size() > 0 ) {
				Object firstPage = pages.get(0);
				if(firstPage instanceof Map<?, ?> map) {
					hiddenCount = Long.parseLong(String.valueOf(map.get("hiddenCount")));
				}
			}
			
		} else if ( activeMenu == 2 ) {
//			pages = keywordTrainService.getTextList(param);

			pages = keywordTrainService.getTtsTransListBatch(search, offsetPageable, keyword, "list");
			if ( pages != null && pages.size() > 0 ) {
				hiddenCount = (long) ((TransVo)pages.get(0)).getHiddenCount();
			}
			
		} else if ( activeMenu == 3 ) {
			
			search.setUseYn(useYn);
			
			pages = keywordTrainService.getTtsTransListBatch(search, offsetPageable, keyword, "reserve");
			if ( pages != null && pages.size() > 0 ) {
				hiddenCount = (long) ((TransVo)pages.get(0)).getHiddenCount();
			}
			
			
		} else if ( activeMenu == 4 ) {
			 param = generateRequestParam("offsetPageable, activeMenu, keyword, searchStartDate, searchEndDate", 
						offsetPageable, activeMenu, keyword, searchStartDate, searchEndDate);
			 
			pages = keywordTrainService.getList(param);
			
			if ( pages != null && pages.size() > 0 ) {
				Object firstPage = pages.get(0);
				if (firstPage instanceof Map<?, ?> map) { 
					hiddenCount = Long.parseLong(String.valueOf(map.get("hiddenCount")));
				}
			}
			
		} else {
			throw new RuntimeException("unknown active menu : " + activeMenu);
		}
		
		 Page<?> cPages = new PageImpl<>(pages, pageable, hiddenCount);
		 
		 model.addAttribute("cPages", cPages);
		 model.addAttribute("hiddenCount", hiddenCount);
		 
		 int conv = activeMenu == 4 ? 1 : activeMenu;
		return "thymeleaf/"+menuId+"/content/table-list" + conv;
	}	
	
	
	
	@PostMapping(value= {"/cont/upload"})
	public String upload(
			Model model, 
			MultipartFile file,
			Principal principal
			) throws Exception {
				

		if(file == null || file.isEmpty()) {
			throw new RuntimeException();
		}
            
        List<Map<String, Object>> pages = null;
        
        pages = keywordTrainService.getExcelList(file);
                
        Page<Map<String, Object>> cPages = new PageImpl<>(pages);         
        model.addAttribute("cPages", cPages);
        model.addAttribute("step", 1);

        
        return "thymeleaf/"+menuId+"/content/upload-keyword-list.html";
		
		
	}
	
	
	@PostMapping(value= {"/cont/getAudio"})
	public String getAudio(
			Model model, 
			@RequestParam int step , 
			@RequestBody List<Map<String, String>> data,
			Principal principal
			) throws Exception {
				
		List<Map<String, Object>> pages = new ArrayList<>();
		
		if(step == 2) {
		    for (Map<String, String> item : data) {
		        String keyword = item.get("keyword");
		        String speech = item.get("speech");
		        String detail = item.get("detail");
		        
		        Map<String, Object> resultText = ttsService.generateText(keyword, speech,detail);
		        
		        
		        String keyword_text  = (String) resultText.get("previous_text");
		        String pronounce_text  = (String) resultText.get("convert_text");
		        
		        Map<String, Object> resultAudio = new HashMap<>();
		        
		        resultAudio.put("keywordText",keyword_text);
		        resultAudio.put("pronounceText",pronounce_text);
		        resultAudio.put("detail",detail);
		        resultAudio.put("keyword",keyword);
		        resultAudio.put("speech",speech);
		        		        
		        pages.add(resultAudio);
		        
		        model.addAttribute("step", 2);

		    }
		}else if(step == 3) {
			
		    for (Map<String, String> item : data) {
		        String keyword = item.get("keyword");
		        String speech = item.get("speech");
		        String detail = item.get("detail");
		        String keywordText = item.get("keywordText");
		        String pronounceText = item.get("pronounceText");
		        
//		        Map<String, Object> resultAudio = ttsService.saveTts("man",pronounceText);
		        Map<String, Object> resultAudio = ttsService.saveTts("KR-m1",pronounceText);
		        

		        resultAudio.put("keyword",keyword);
		        resultAudio.put("speech",speech);
		        resultAudio.put("detail",detail);
		        resultAudio.put("keywordText",keywordText);
		        resultAudio.put("pronounceText",pronounceText);
		        		        
				int tcUserSeq = Integer.parseInt(principal.getName());

				resultAudio.put("tcUserSeq", tcUserSeq);	        
		        	        
		        keywordTrainService.saveAudio(resultAudio);
		        
		        pages.add(resultAudio);
		        
		        model.addAttribute("step", 3);

		    }
		}
		

        
        Page<Map<String, Object>> cPages = new PageImpl<>(pages);         
        model.addAttribute("cPages", cPages);
        
        return "thymeleaf/"+menuId+"/content/upload-keyword-list.html";
		
		
	}	
	
	

	@RequestMapping(value= {"/download"})
	public void download(Model model, 
			@RequestParam(value="fileNm") String fileNm,
			HttpServletRequest request,
			HttpServletResponse response ) throws Exception {
					
		byte []all = Files.readAllBytes(keywordTrainService.getUploadPath(fileNm));

		response.setContentLength(all.length);
		// forces download
		response.setHeader("Content-Type", "audio/mpeg");
		response.setHeader("Accept-Ranges", "bytes");
		response.getOutputStream().write(all);
		response.flushBuffer();
	}	
	

	
	@PostMapping(value= {"/cont/addGroup"})
	public String addGroup(
			Model model, 
			@RequestParam("title") String title,
			@RequestBody List<Map<String, String>> data,
			Principal principal
			) throws Exception {
				
		int tcUserSeq = Integer.parseInt(principal.getName());
		
		try {
		    int titleSeq = keywordTrainService.crateTitle(title, tcUserSeq);
		    
		    for (Map<String, String> item : data) {
		       
		    	int seq = Integer.parseInt(item.get("seq"));
		    	log.debug("=======> seq is {}", seq);
		        keywordTrainService.updateAudio(titleSeq, seq);
		        keywordTrainService.callStt(titleSeq,seq);
		    }
		} catch (IOException e) {
		    return "IOException Occured";
		} catch (Exception e) {

		    e.printStackTrace();
		}
                
        return "1";
		
		
	}	
	
	@GetMapping(value= {"/cont/excelForm"})
	@ResponseBody
	public void excelForm(
			HttpServletResponse response
			) throws Exception {

		keywordTrainService.excelForm(response);

	}	
	
	
	@GetMapping(value= {"/cont/transList"})
	public String transListPopUp(HttpServletRequest request, 
	        Model model,
	        @Param("pageable")  @PageableDefault(size = 20, page = 1) Pageable pageable,
	        @RequestParam(required = false) Integer seq,
			@RequestParam String searchStartDate , 
			@RequestParam String searchEndDate,	        
	        @RequestParam(required = false) Integer reliability,
	        @RequestParam(required = false) Integer waveValue,
	        @RequestParam(required = false) Integer changeTextValue,
	        @RequestParam(required = false) String suffix) throws Exception {
	    
	    if (seq == null) {
	        throw new RuntimeException("SEQ Null");
	    }
		SearchVo search = new SearchVo();
		search.setSearchStartDate(searchStartDate);
		search.setSearchEndDate(searchEndDate);

	    OffsetPageable offsetPageable = new OffsetPageable(pageable);
	
	    
	    if ( reliability == null ) {
	        reliability = 100;
	    }
	
	    Map<String, Object> getpages = generateRequestParam("seq, searchStartDate, searchEndDate", 
	    														seq,search.getSearchStartDate(),search.getSearchEndDate() );	    
	    
	    Map<String, Object> param = generateRequestParam("offsetPageable, reliability,changeTextValue,seq, searchStartDate, searchEndDate", 
	                                                      offsetPageable,reliability,changeTextValue,seq,search.getSearchStartDate(),search.getSearchEndDate() );
	    
	    List<Map<String, Object>> pages = keywordTrainService.getList(getpages);
	    if ( pages.size() != 1 ) {
	        throw new RuntimeException("no matching data found : " + seq);
	    }
	    Map<String, Object> vo = pages.get(0);
	            
	    model.addAttribute("vo", vo);
	
	    List<Map<String, Object>> list = keywordTrainService.getTransList(param);
	    
	            
	    int hiddenCount = list.size();
	
	    if ( hiddenCount > 0 ) {
	        hiddenCount = ((Long) list.get(0).get("hiddenCount")).intValue();
	
	    }
	    
	    keywordTrainService.clickLeastOnce(seq);
	    
	    Page<?> cPages = new PageImpl<>(list, pageable, hiddenCount);
	    model.addAttribute("cPages", cPages);
	    model.addAttribute("hiddenCount", hiddenCount);        
	    model.addAttribute("search", search);
	    model.addAttribute("menuId", menuId);
	    model.addAttribute("hiddenSeq", seq);
//	    model.addAttribute("waveValue", waveValue);
	    if ( reliability != 100 ) {
	        model.addAttribute("reliability", reliability);
	    }
	    
	    return "thymeleaf/"+menuId+"/content/detail" + suffix;
	}       
		
		
	@PostMapping(value= {"/cont/trans", "/cont/1/trans"})
	@ResponseBody
	public void transUpdate(Model model, @RequestBody String text, @RequestParam("seq") int transSeq) throws Exception {
	
	    transcriptionRepository.updateTrainTextBySeq(transSeq, text);
	
	}    	
	
	
	@PostMapping(value= {"/cont/trash", "/cont/1/trash"})
	@ResponseBody
	public String trash(Model model,
			@RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
						
		    Map<String, Object> param = generateRequestParam("reliability,changeTextValue,seq", 
                    100,2,Integer.parseInt(map.get("seq")));
		    
		    keywordTrainService.updateTtsList(Integer.parseInt(map.get("seq")),"trash");	

		    List<Map<String, Object>> list = keywordTrainService.getTransList(param);	
		    
						
			keywordTrainService.trash(list,"trash");
			
		}

		return "1";
	}	
	
	@PostMapping(value= {"/cont/trash/rollback", "/cont/1/trash/rollback"})
	@ResponseBody
	public String trashRollback(Model model, 
			@RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
			keywordTrainService.updateTtsList(Integer.parseInt(map.get("seq")),"rollback");
			
		    Map<String, Object> param = generateRequestParam("reliability,changeTextValue,seq", 
                    100,2,Integer.parseInt(map.get("seq")));	
		    List<Map<String, Object>> list = keywordTrainService.getTransList(param);	

		    keywordTrainService.trash(list,"rollback");
		    
		    
		}

		return "1";
	}
	
	
	@PostMapping(value= {"/cont/delete", "/cont/1/delete"})
	@ResponseBody
	public String delete(Model model,
			@RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
			keywordTrainService.updateTtsList(Integer.parseInt(map.get("seq")),"delete");	
			
		    Map<String, Object> param = generateRequestParam("reliability,changeTextValue,seq", 
                    100,2,Integer.parseInt(map.get("seq")));	
		    List<Map<String, Object>> list = keywordTrainService.getTransList(param);	

		    keywordTrainService.trash(list,"delete");			
			
		}

		return "1";
	}		
	
	
	@GetMapping(value= {"/cont/createKeyword"})
	public String createKeyword(HttpServletRequest request ) throws Exception 
	{

	    return "thymeleaf/"+menuId+"/content/upload-keyword";
	} 	
	
	
	@PostMapping(value= {"/cont/excludeTrans", "/cont/1/excludeTrans"})
	@ResponseBody
	public String excludeTrans(Model model, @RequestParam("seq[]") int[] seq) throws Exception {
			
		transcriptionRepository.excludeTransTranscription(seq);
		transcriptionRepository.excludeTransTransPair(seq);		
		return "1";
	}	
	
	@PostMapping(value= {"/cont/includeTrans", "/cont/1/includeTrans"})
	@ResponseBody
	public String includeTrans(Model model, @RequestParam("seq[]") int[] seq) throws Exception {
		
		transcriptionRepository.includeTransTranscription(seq);
		
		return "1";
	}	
	
	
	
	@PostMapping(value= {"/cont/getAudioAdd"})
	@ResponseBody
	public ResponseEntity<List<Map<String, Object>>> getAudioAdd(
	        Model model, 
	        @RequestParam int step, 
	        @RequestBody List<Map<String, String>> data,
	        Principal principal) throws Exception {

	    List<Map<String, Object>> pages = new ArrayList<>();

	    if(step == 2) {
	        for (Map<String, String> item : data) {
	            String keyword = item.get("keyword");
	            String speech = item.get("speech");
	            String detail = item.get("detail");
				String rowid = item.get("rowid");	    

	            Map<String, Object> resultText = ttsService.generateText(keyword, speech, detail);
	            String keywordText = (String) resultText.get("previous_text");
	            String pronounceText = (String) resultText.get("convert_text");

	            Map<String, Object> resultAudio = new HashMap<>();
        		resultAudio.put("rowid", rowid);
	            resultAudio.put("keywordText", keywordText);
	            resultAudio.put("pronounceText", pronounceText);
	            resultAudio.put("detail", detail);
	            resultAudio.put("keyword", keyword);
	            resultAudio.put("speech", speech);

	            pages.add(resultAudio);
	        }
	    } else if(step == 3) {
	        for (Map<String, String> item : data) {
	            String keyword = item.get("keyword");
	            String speech = item.get("speech");
	            String detail = item.get("detail");
	            String keywordText = item.get("keywordText");
	            String pronounceText = item.get("pronounceText");
				String rowid = item.get("rowid");	

	            //Map<String, Object> resultAudio = ttsService.saveTts("man", pronounceText);
		        Map<String, Object> resultAudio = ttsService.saveTts("KR-m1",pronounceText);
				resultAudio.put("rowid", rowid);	    
	            resultAudio.put("keyword", keyword);
	            resultAudio.put("speech", speech);
	            resultAudio.put("detail", detail);
	            resultAudio.put("keywordText", keywordText);
	            resultAudio.put("pronounceText", pronounceText);
	            resultAudio.put("tcUserSeq", Integer.parseInt(principal.getName()));

	            keywordTrainService.saveAudio(resultAudio);
	            pages.add(resultAudio);
	        }
	    }
	    
	    

	    return ResponseEntity.ok(pages);
	}	
	
	@PostMapping(value= {"/cont/uploadDiff", "/cont/1/uploadDiff"}, produces = {"application/json"})
	@ResponseBody
	public ResponseEntity<Map<String, String>> uploadDiff(
			Model model, 
            @RequestParam("type") String type, 
            @RequestParam("file") MultipartFile file,
            @RequestParam(name="letter", required=false) String letter,
			Principal principal) throws Exception {

		if(file == null || file.isEmpty()) {
			throw new RuntimeException();
		}
		
		// 1. save uploaded file or convert file
		Map<String, Object> param = commonService.saveUploadFileConvert(Category.MEET, file);
		
		param.put("type", Encode.forHtml(type));
		
//		Resource resource = (Resource) param.get("resource");
		org.springframework.core.io.Resource resource = (org.springframework.core.io.Resource) param.get("resource");
		
		// 2. call stt
		Map<String, Object> resultMap = null;
		
		try {
			resultMap = sttService.callSttForMenu(resource, false, letter);
			
			BigDecimal bigDecimal = new BigDecimal(resultMap.get("duration").toString());
			BigDecimal durationMs = bigDecimal.multiply(new BigDecimal(1000));
			param.put("timeDurationStr", durationMs.toString());
			param.put("lang", Encode.forHtml(letter));			
		} catch ( TrainCallException tce ) {
			param.put("timeDurationStr", "0");
			param.put("errorMessage", tce.getDetail());
		} catch (Exception e ) {
			param.put("timeDurationStr", "0");
			param.put("errorMessage", e.getMessage());
		}
		
		int tcUserSeq = Integer.parseInt(principal.getName());
		param.put("tcUserSeq", tcUserSeq);
		
		// 2. call summary
		Map<String, String> map = sttService.parseDiarizeAndSttForMenu(param, resultMap);
		
		Map<String, String> safeMap = new HashMap<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
		    safeMap.put(entry.getKey(), Encode.forHtml(entry.getValue()));
		}
		
		return ResponseEntity.ok(safeMap);
	}		
	
	
}

