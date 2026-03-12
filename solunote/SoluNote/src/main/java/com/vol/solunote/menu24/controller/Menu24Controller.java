package com.vol.solunote.menu24.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.vol.solunote.menu24.service.Menu24SteelServiceImpl;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DataVo;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.stt.SttService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/menu24")
public class Menu24Controller extends DefaultController {

	@Autowired
	//@Resource(name="${service.class.menu24Service}")
	private Menu24SteelServiceImpl menu24Service;

	@Autowired
	private CommonSteelServiceImpl commonService ;
	
	@Autowired
	//@Resource(name="${service.class.menu21Service}")
	SttService	 sttService;	
	
	private final static DateTimeFormatter updateAtFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

	public static int processingFileCnt = 0;
	
	Menu24Controller() {
		this.menuId="menu24";
	}

	@RequestMapping(value= {"/cont", "/cont/1"})
	public String cont(HttpServletRequest request, 
			Model model,
			@Param("search") DefaultVo search,
			@RequestParam(required = false) Integer activeMenu
			) throws Exception {
				
		setSearchTerm(search);
				
		model.addAttribute("search", search);
		model.addAttribute("menuId", menuId);
		model.addAttribute("liveMenu", activeMenu);
		
		return "thymeleaf/"+menuId+"/cont.html";
	}

	@PostMapping(value= {"/cont/remark", "/cont/1/remark"})
	@ResponseBody
	public String remarkUpdate(Model model, @RequestBody String updateRemark, @RequestParam("seq") int meetSeq) throws Exception {

		LocalDateTime updatedAt = LocalDateTime.now();
		menu24Service.updateRemarkAndUpdatedAtBySeq(meetSeq, updatedAt, Boolean.parseBoolean(updateRemark));

		return updatedAt.format(updateAtFormatter);
	}

	@PostMapping(value= {"/cont/trans", "/cont/1/trans"})
	@ResponseBody
	public void transUpdate(Model model, @RequestBody String text, @RequestParam("seq") int meetSeq) throws Exception {

		menu24Service.updateTrainTextBySeq(meetSeq, text);

	}



	@GetMapping(value= {"/cont/loadMeetResult", "/cont/1/loadMeetResult"}, produces = {"application/json"})
	@ResponseBody
	public List<TranscriptionVo> loadMeetResult(Model model, @RequestParam("seq") int meetSeq, @RequestParam("origin") String origin) throws Exception {
		List<TranscriptionVo> meetingResultList = menu24Service.getTranscriptionList(meetSeq, origin, -1);

		return meetingResultList;
	}

	@GetMapping(value= {"/cont/loadList", "/cont/1/loadList"})
	public String loadList(Model model,
			@Param("search") SearchVo search,
			@Param("pageable")  @PageableDefault(size = 10, page = 1) Pageable pageable,
            @RequestParam int activeMenu,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchStartDate,
            @RequestParam(required = false) String searchEndDate,
            @RequestParam(required = false) String useYn
		) throws Exception {
		
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
						
		search.setSearchStartDate(searchStartDate);
		search.setSearchEndDate(searchEndDate);
		
		Map<String, Object> param = generateRequestParam("offsetPageable, search, keyword", 
														offsetPageable, search, keyword);
		List<?> list = null;
		int hiddenCount = 0;
		
		if ( activeMenu == 1 ) {
			addRequestParam(param, "caller", "list");
			list = menu24Service.getList(param);
			
		}else if ( activeMenu == 2 ) {
			
			search.setUseYn(useYn);
			
			addRequestParam(param, "caller", "reserve");
//			list = menu24Service.getDataTransListX(param);
			list = menu24Service.getList(param);
			for( Object o : list ) {
				TransVo vo = (TransVo)o;
				if ( "".equals(vo.getTrainText() ) ) {
					vo.setTrainText(vo.getSttText());
				}
			}
			
		}
		
		hiddenCount = list.size();
		if ( hiddenCount > 0 ) {
			hiddenCount = Integer.parseInt(String.valueOf( ((DataVo)list.get(0)).getHiddenCount() ));
		}
		Page<?> cPages = new PageImpl<>(list, pageable, hiddenCount);
		model.addAttribute("cPages", cPages);			
	
		model.addAttribute("hiddenCount", hiddenCount);
		 
		return "thymeleaf/"+menuId+"/content/table-list" + activeMenu;
	}

	@PostMapping(value= {"/cont/requestTrain", "/cont/1/requestTrain"})
	@ResponseBody
	public String requestTrain(Model model, @RequestParam("seq[]") int[] seq) throws Exception {

		for (int i : seq) {
			menu24Service.requestTrain(i);
		}

		return "1";
	}
	
	@PostMapping(value= {"/cont/excludeTrans", "/cont/1/excludeTrans"})
	@ResponseBody
	public String excludeTrans(Model model,
			@RequestBody Map<String, String>[] array) throws Exception {
		
		int[] seqs = new int[array.length];
		int i = 0;
		for ( Map<String, String> map : array ) {
			int seq = Integer.parseInt(map.get("seq"));
			seqs[i++] = seq;
		}
		
		if ( seqs.length > 0 ) {
			menu24Service.excludeTrans(seqs);
		}

		return "1";
	}
	
	@PostMapping(value= {"/cont/includeTrans", "/cont/1/includeTrans"})
	@ResponseBody
	public String includeTrans(Model model,
			@RequestBody Map<String, String>[] array) throws Exception {
		
		int[] seqs = new int[array.length];
		int i = 0;
		for ( Map<String, String> map : array ) {
			int seq = Integer.parseInt(map.get("seq"));
			seqs[i++] = seq;
		}
		
		if ( seqs.length > 0 ) {
			menu24Service.includeTrans(seqs);
		}
		
		return "1";
	}
	
	@PostMapping(value= {"/cont/upload", "/cont/1/upload"}, produces = {"application/json"})
	@ResponseBody
	public ResponseEntity<Map<String, String>> upload(
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
		
		// 2. call summary
		Map<String, String> map = sttService.parseDiarizeAndSttForMenu(param, resultMap);

		return ResponseEntity.ok(map);
	}	

}
