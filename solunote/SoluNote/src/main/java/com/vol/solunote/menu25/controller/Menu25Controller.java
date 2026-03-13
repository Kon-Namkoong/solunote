package com.vol.solunote.menu25.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.vol.solunote.menu25.service.Menu25SteelServiceImpl;
import com.vol.solunote.model.dto.transcription.TranscriptionExt;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.comm.service.disk.DiskService;
import com.vol.solunote.comm.service.ffmpec.FFMpegService;
import com.vol.solunote.comm.service.stt.SttService;
import com.vol.solunote.repository.division.DivisionRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/menu25")
public class Menu25Controller extends DefaultController {
	
	@Autowired
	private FFMpegService ffmpegService;
	
	@Autowired
	private DiskService diskService;
	
	@Autowired
	//@Resource(name="${service.class.menu25Service}")
	private Menu25SteelServiceImpl menu25Service;
	
	@Autowired
	//@Resource(name="${service.class.menu21Service}")
	private SttService	 sttService;
	
	@Autowired
	private DivisionRepository divisionRepository;
	
	public static int processingFileCnt = 0;
	
	Menu25Controller() {
		this.menuId="menu25";
	}
	
	@RequestMapping(value= {"/cont", "/cont/1"})
	public String cont(HttpServletRequest request, 
			Model model,
			@Param("search") DefaultVo search,
			@RequestParam(required = false) Integer activeMenu, 
			@RequestParam(required = false) Integer seq) throws Exception {
		
		setSearchTerm(search);

		List<Map<String, Object>> divisionList = divisionRepository.selectInfoDivision(null,null,0);
		
		model.addAttribute("divisionList", divisionList);		
		model.addAttribute("search", search);
		model.addAttribute("menuId", menuId);
		model.addAttribute("liveMenu", activeMenu);
		
		return "thymeleaf/"+menuId+"/cont.html";
	}


	@PostMapping(value= {"/cont/trans", "/cont/1/trans"})
	@ResponseBody
	public void transUpdate(Model model, @RequestBody String text, @RequestParam("seq") int seq) throws Exception {
		
		menu25Service.updateTrainTextBySeq(seq, text);
	}
	

	@GetMapping(value= {"/cont/loadList", "/cont/1/loadList"})
	public String loadList(Model model,
			@Param("search") DefaultVo search,
			@Param("pageable") @PageableDefault(size = 10) Pageable pageable,
			@RequestParam int activeMenu
			) throws Exception {
		
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
		
		List<?> pages = null;
		
		Map<String, Object> param = generateRequestParam("offsetPageable, search, activeMenu", 
															offsetPageable, search, activeMenu);
		
		int hiddenCount = 0;
        if(activeMenu == 1) {
    		List<SoundVo>soundList = menu25Service.getListData(param);
    		pages = soundList;
    		if ( !soundList.isEmpty() ) {
    			hiddenCount = soundList.get(0).getHiddenCount();
    		}
        } else if (activeMenu == 5) {        	
        	List<TransVo>transList = menu25Service.getTestTransListX(param);
        	pages = transList;
        	if ( !transList.isEmpty() ) {
        		hiddenCount = transList.get(0).getHiddenCount();
    		}
        } else {
        	
        }
        @SuppressWarnings("unchecked")
        Page<Object> cPages = new PageImpl<>((List<Object>) pages, pageable, hiddenCount);
		 
		 model.addAttribute("cPages", cPages);
		 model.addAttribute("hiddenCount", hiddenCount);
		 
		return "thymeleaf/"+menuId+"/content/table-list" + activeMenu;
	}
		
	
	@PostMapping(value= {"/cont/excludeTestCandiate", "/cont/1/excludeTestCandiate"})
	@ResponseBody
	public String excludeTestCandiate(Model model, @RequestParam("seq[]") int[] seq, @RequestParam("value") String value) throws Exception {
			
		menu25Service.excludeTestCandiate(seq, value);

		return "1";
	}



	@PostMapping(value= {"/cont/excludeTest", "/cont/1/excludeTest"})
	@ResponseBody
	public String excludeTest(Model model, @RequestParam("seq[]") int[] seq) throws Exception {
		for (int i : seq) {
			menu25Service.excludeTest(i);
		}


		return "1";
	}

    @GetMapping(value= {"/cont/transListPopUp"})
    public String transListPopUp(
            Model model,
            @RequestParam(required = false) Integer seq,
        	@Param("pageable")  @PageableDefault(size = 20, page = 1) Pageable pageable,
            @RequestParam(required = false) Integer reliability,
            @RequestParam(required = false) Integer waveValue,
            @RequestParam(required = false) Integer changeTextValue,
			@RequestParam(required = false) String suffix) throws Exception {
    	
        
    	OffsetPageable offsetPageable = new OffsetPageable(pageable);
		double lFirstLimit = -1;
		double lLastLimit = -1;
		double rFirstLimit = -1;
		double rLastLimit = -1;
    	
		if ( reliability == null ) {
			reliability = 100;
		}		
		
        if (seq == null) {
            throw new RuntimeException("SEQ Null");
        } 
            
        
        Map<String, Object> param = generateRequestParam("seq", seq);
        List<SoundVo> voList = menu25Service.getListData(param);
        if (voList.size() != 1 ) {
            throw new RuntimeException("vo should be 1");
        } 
        SoundVo soundVo = voList.get(0);
		
//        if ( soundVo.getChannelCount() == 2 )  ) {
//        	meetMap.put("fileOrgNm", meetMap.get("fileStereoPrefix"));
//        }
        
        menu25Service.clickLeastOnce(seq);
        model.addAttribute("vo", soundVo);
        
//		List<Map<String, Object>> list = menu25Service.getTestTransList(seq ,offsetPageable,reliability);
		addRequestParam(param, "reliability, offsetPageable,changeTextValue, meetSeq", 
				reliability, offsetPageable,changeTextValue, soundVo.getSeq());
		List<TranscriptionVo> list = menu25Service.getTranscriptionList(param);
		
		int hiddenCount = list.size();
		String firstChannel = "";
		String lastChannel = "";
		double timeDuration = Math.round(Double.parseDouble(soundVo.getTimeDurationMs()) / 1000 * 100 ) / 100.0;
		if ( hiddenCount > 0 ) {
			hiddenCount = list.get(0).getHiddenCount();
			firstChannel = list.get(0).getChannelChar();
			lastChannel = list.get(list.size()-1).getChannelChar();
			
			if ( "L".equals(firstChannel) ) {
				if ( pageable.getPageNumber() == 1 ) {   // 1 page
					lFirstLimit = 0D;
				} else {
					lFirstLimit = list.get(0).getStart();
				} 
				
				if ( "R".equals(lastChannel)) {
					lLastLimit = timeDuration;
					rFirstLimit = 0D;						
					if ( pageable.getPageNumber() - 1 == (int) (hiddenCount / offsetPageable.getPageSize()) ) {  // last page
						rLastLimit = timeDuration;
					} else {
						rLastLimit =  list.get(list.size()-1).getEnd();
					}
				} else {
					if ( pageable.getPageNumber() - 1 == (int) (hiddenCount / offsetPageable.getPageSize()) ) {  // last page
						lLastLimit = timeDuration;
					} else {
						lLastLimit =  list.get(list.size()-1).getEnd();
					}
				}
			} else {
				if ( pageable.getPageNumber() == 1 ) {   // 1 page
					rFirstLimit = 0D;
				} else {
					rFirstLimit = list.get(0).getStart();
				}
				
				if ( pageable.getPageNumber() - 1 == (int) (hiddenCount / offsetPageable.getPageSize()) ) {  // last page
					rLastLimit = timeDuration;
				} else {
					rLastLimit =  list.get(list.size()-1).getEnd();
				}
			}
			
		}
		
		Page<?> cPages = new PageImpl<>(list, pageable, hiddenCount);
		model.addAttribute("cPages", cPages);
		model.addAttribute("hiddenCount", hiddenCount);
		model.addAttribute("hiddenSeq", seq); 
		model.addAttribute("lFirstLimit", lFirstLimit);
		model.addAttribute("lLastLimit", lLastLimit);
		model.addAttribute("rFirstLimit", rFirstLimit);
		model.addAttribute("rLastLimit", rLastLimit);
        model.addAttribute("waveValue", waveValue);
            
		if ( reliability != 100 ) {
			model.addAttribute("reliability", reliability);
		}        
        
        return "thymeleaf/"+menuId+"/content/detail" + suffix;
    }    
	
    
	@PostMapping(value= {"/cont/split", "/cont/1/split"},  produces = {"application/json"})
	@ResponseBody
	public String split(Model model, @RequestBody Map<String, String> map) throws Exception {
		
		int id = menu25Service.split(map);
		
		return Integer.toString(id);
	}
	

	@PostMapping(value= {"/cont/combine", "/cont/1/combine"},  produces = {"application/json"})
	@ResponseBody
	public String combine(Model model, @RequestBody Map<String, String> map) throws Exception {
		
		int id = menu25Service.combine(map);
		
		return Integer.toString(id);
	}	    
    
	@GetMapping(value= {"/cont/appendWaveSpectrum"}, produces = {"application/json"})
	@ResponseBody
	public List<Map<String, Object>> appendWaveSpectrum(
			Model model, 
			@Param("search") DefaultVo search,
    		@Param("pageable")  @PageableDefault(size = 20, page = 1) Pageable pageable,
    		@RequestParam(required = false) Integer seq,  
    		@RequestParam(required = false) String origin,
    		@RequestParam(required = false) String name,
    		@RequestParam(required = false) Integer reliability,
    		@RequestParam(required = false) Integer waveValue,
    		@RequestParam(required = false) String category,
			@RequestParam(required = false) Integer changeTextValue) throws Exception {

		OffsetPageable offsetPageable = new OffsetPageable(pageable);

		if (reliability == null) {
			reliability = 100;
		}

		if (seq == null) {
			throw new RuntimeException("SEQ Null");
		}

        Map<String, Object> param = generateRequestParam("seq", seq);
        List<SoundVo> voList = menu25Service.getListData(param);
        if (voList.size() != 1 ) {
            throw new RuntimeException("vo should be 1");
        } 
        SoundVo soundVo = voList.get(0);
        
//		model.addAttribute("meetMap", soundVo);

		List<Map<String, Object>> mapList = menu25Service.getTestTransList(seq, offsetPageable, reliability);
		List<TranscriptionVo> list = new ArrayList<>();

		int hiddenCount = mapList.size();
		for (int i = 0; i < hiddenCount; i++) {
			Map<String, Object> map = mapList.get(i);
			TranscriptionVo vo = new TranscriptionVo();

			vo.setStart((double) map.get("start"));
			vo.setEnd((double) map.get("end"));
			vo.setSeq((int)map.get("seq"));

			if (i == 0) {
				vo.setPrevEnd(0D);
			} else {
				vo.setPrevEnd((double) mapList.get(i - 1).get("end"));
			}

			if (i == hiddenCount - 1) {
				vo.setNextStart((double) map.get("end"));
			} else {
				vo.setNextStart((double) mapList.get(i + 1).get("start"));
			}

			list.add(vo);
		}
		
		return ffmpegService.appendWaveSpectrum(diskService.strToCategory(category), soundVo, list);
	}
  
	@GetMapping(value= {"/cont/replaceWaveSpectrum"}, produces = {"application/json"})
	@ResponseBody
	public List<Map<String, Object>> replaceWaveSpectrum(HttpServletRequest request, Model model,
			@Param("search") DefaultVo search,
			@Param("pageable") @PageableDefault(size = 20, page = 1) Pageable pageable,
			@RequestParam(required = false) Integer hiddenSeq, 
			@RequestParam(required = false) Integer[] seq,
			@RequestParam(required = false) double[] start,
			@RequestParam(required = false) double[] end,
			@RequestParam(required = false) double prevEnd, 
			@RequestParam(required = false) double nextStart,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String channelChar) throws Exception {

		if (seq == null) {
			throw new RuntimeException("SEQ Null");
		}

        Map<String, Object> param = generateRequestParam("seq", hiddenSeq);
        List<SoundVo> voList = menu25Service.getListData(param);
        if (voList.size() != 1 ) {
            throw new RuntimeException("vo should be 1");
        } 
//		Map<String, Object> meetMap = menu25Service.getMeetBySEQ(hiddenSeq);

		List<TranscriptionExt> list = new ArrayList<>();
		TranscriptionExt vo = new TranscriptionExt();
		
    	int channelId = "L".equals(channelChar) ? 0 : 1;
		int size = seq.length;
		if (size == 1) {
			vo.setSeq(seq[0]);
			vo.setStart(start[0]);
			vo.setEnd(end[0]);
			vo.setPrevEnd(prevEnd);
			vo.setNextStart(nextStart);
    		vo.setChannelId(channelId);
			list.add(vo);
		} else if (size == 2) {
			vo.setSeq(seq[0]);
			vo.setStart(start[0]);
			vo.setEnd(end[0]);
			vo.setPrevEnd(prevEnd);
			vo.setNextStart(start[1]);
    		vo.setChannelId(channelId);
			list.add(vo);

			vo = new TranscriptionExt();
			vo.setSeq( seq[1]);
			vo.setStart(start[1]);
			vo.setEnd(end[1]);
			vo.setPrevEnd(end[0]);
			vo.setNextStart(nextStart);
    		vo.setChannelId(channelId);
			list.add(vo);
		} else {
			throw new RuntimeException("array parameter should be 1 or 2");
		}

		throw new RuntimeException("aa");
//		return soundService.replaceWaveSpectrum(category, meetMap, list);
	}

	@PostMapping(value= {"/cont/resetFrame", "/cont/1/resetFrame"},  produces = {"application/json"})
	@ResponseBody
	public String resetFrame(Model model, @RequestBody Map<String, String> map) throws Exception {
		
		int id = menu25Service.resetFrame(map);
		
		return Integer.toString(id);
	}    
    
	@PostMapping(value= {"/cont/upload", "/cont/1/upload"})
	@ResponseBody
	public ResponseEntity<String> upload(Model model, MultipartFile file, Principal principal) throws Exception {

		if(file == null || file.isEmpty()) {
			throw new RuntimeException();
		}
		
		// 1. save uploaded file or convert file
		Map<String, Object> param = commonService.saveUploadFileConvert(Category.TEST, file);
//		Resource resource = (Resource) param.get("resource");
		org.springframework.core.io.Resource resource = (org.springframework.core.io.Resource) param.get("resource");

		
		// 2. call stt
//		Resource resource = file.getResource();
		Map<String, Object> resultMap = sttService.callSttForMenu(resource, false, null);
		
//		soundService.adjustTimestamp(resource, resultMap, (String)param.get("newnm"), null);
		LocalTime tm = LocalTime.parse(resultMap.get("stt_duration").toString());
		long bigDecimal = tm.toSecondOfDay() * 1000;
		BigDecimal durationMs = BigDecimal.valueOf(bigDecimal);

		int tcUserSeq = Integer.parseInt(principal.getName());
		
		param.put("timeDurationStr", durationMs.toString());
		param.put("tcUserSeq", tcUserSeq);
		
		param.put("division", "00");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		System.out.println("2. auth = " + auth);
		System.out.println("2. principal = " + principal);
		System.out.println("2. tcUserSeq = " + tcUserSeq);
		System.out.println("2. isAuthenticated = " + auth.isAuthenticated());
		System.out.println("2. timeDurationStr = " + durationMs.toString() );
		
		
		String meetSeq = menu25Service.successSoundAndStt(param, resultMap);
		
		return ResponseEntity.ok(meetSeq);
		
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
		
		param.put("type",type);
		
//		Resource resource = (Resource) param.get("resource");
		org.springframework.core.io.Resource resource = (org.springframework.core.io.Resource) param.get("resource");
		
		// 2. call stt
		Map<String, Object> resultMap = null;
		
		try {
			resultMap = sttService.callSttForMenu(resource, false, letter);
			
			BigDecimal bigDecimal = new BigDecimal(resultMap.get("duration").toString());
			BigDecimal durationMs = bigDecimal.multiply(new BigDecimal(1000));
			param.put("timeDurationStr", durationMs.toString());
			param.put("lang", letter);			
		} catch ( TrainCallException tce ) {
			param.put("timeDurationStr", "0");
			param.put("errorMessage", tce.getDetail());
		} catch (Exception e ) {
			param.put("timeDurationStr", "0");
			param.put("errorMessage", e.getMessage());
		}
		
		int tcUserSeq = Integer.parseInt(principal.getName());
		param.put("tcUserSeq", tcUserSeq);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		System.out.println("1. auth = " + auth);
		System.out.println("1. principal = " + principal);
		System.out.println("1. tcUserSeq = " + tcUserSeq);
		System.out.println("1. isAuthenticated = " + auth.isAuthenticated());
		// 2. call summary
		Map<String, String> map = sttService.parseDiarizeAndSttForMenu(param, resultMap);

		return ResponseEntity.ok(map);
	}	
    
}
