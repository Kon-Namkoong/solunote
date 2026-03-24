package com.vol.solunote.domain.trainschedule.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.vol.solunote.domain.trainschedule.service.TrainScheduleService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.train.TrainVo;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.stt.SttService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/trainschedule")
public class TrainScheduleController extends DefaultController {

	@Autowired
	TrainScheduleService trainScheduleService;
	
	@Autowired
	SttService sttService;    	
	
	@Autowired
	CommonSteelServiceImpl commonService;    

	public static int processingFileCnt = 0;
	
	TrainScheduleController() {
		this.menuId="trainSchedule";
	}
	
	@RequestMapping(value= {"/cont", "/cont/1"})
	public String cont(HttpServletRequest request, 
			Model model,
			@Param("search") DefaultVo search,
			@RequestParam(required = false) Integer activeMenu, 
			@RequestParam(required = false) String modelId, 
			@RequestParam(required = false) Integer seq) throws Exception {
		
		setSearchTerm(search);
		
		model.addAttribute("list", new ArrayList<>());
		model.addAttribute("search", search);
		model.addAttribute("menuId", menuId);
		model.addAttribute("liveMenu", activeMenu);
		
		return "thymeleaf/"+menuId+"/cont.html";
	}


	@GetMapping(value= {"/cont/loadList"})
	public String loadList(Model model,
			@Param("search") DefaultVo search,
			@Param("pageable")  @PageableDefault(size = 10) Pageable pageable,
			@RequestParam int activeMenu,
			@RequestParam(required = false) Integer isSuccess
			) throws Exception {
				
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
		List<TrainVo> pages = null;
		
		Map<String, Object> param = generateRequestParam("offsetPageable, search, activeMenu", 
														offsetPageable, search, activeMenu);
		
		if(activeMenu == 1 ) {
			
			param.put("isSuccess", isSuccess);
			
			pages = trainScheduleService.getList(param);
				
		}else if ( activeMenu == 5){
			pages = trainScheduleService.requestList(param);
			
		}
		
		int hiddenCount = pages.size();
		if ( hiddenCount > 0 ) {
			hiddenCount = pages.get(0).getHiddenCount();
		}
		
		
		 Page<TrainVo> cPages = new PageImpl<>(pages, pageable,hiddenCount);		 
		 model.addAttribute("cPages", cPages);
		 model.addAttribute("hiddenCount", hiddenCount);
		return "thymeleaf/"+menuId+"/content/table-list" + activeMenu;
	}
	

	@RequestMapping(value= {"/cont/trainAdd"})
	@ResponseBody
	public String listAdd(Model model, 
			@RequestBody HashMap<String, Object> map, 
			@Param("search") DefaultVo search,
			@Param("pageable") Pageable pageable
			) throws Exception {
		
		
		int checkdata = trainScheduleService.checkdata(map.get("startTime").toString());
		if (checkdata == 1){
			return "2";
		}	
		
		trainScheduleService.listAdd(map.get("startTime").toString());

		return "1";
	}
	


	@PostMapping(value= {"/cont/removeList"})
	@ResponseBody
	public String excludeTrans(Model model, @RequestParam("seq[]") int[] seq) throws Exception {
			
		trainScheduleService.excludeTrans(seq);

		return "1";
	}
		
	
    @GetMapping(value= {"/cont/trainListPopUp"})
    public String trainListPopUp(HttpServletRequest request, 
			Model model,
			@Param("pageable")  @PageableDefault(size = 20, page = 1) Pageable pageable,
			@RequestParam(required = false) String modelId,
			@RequestParam(required = false) String orderby,
			@RequestParam int seq,
			@RequestParam(required = false) String suffix) throws Exception {
		

		Map<String, Object> param = generateRequestParam("seq", 
														seq);
		List<TrainVo> list = trainScheduleService.requestList(param);
		if ( list.size() != 1 ) {
			throw new RuntimeException("should be 1 record");
		}
		model.addAttribute("vo", list.get(0));

			OffsetPageable offsetPageable = new OffsetPageable(pageable);
			boolean failFlag = false;
			
			if(orderby == null) {
				orderby = "fileNewNm";
			}
			
			if (modelId == null) {
				throw new RuntimeException("modelId Null");
			} else {
				
				List<Map<String, Object>> resultList = trainScheduleService.getTrainList(modelId);
				Map<String, Object> meetMap = null;
				
				if (  resultList.size() > 0 ) {
					meetMap = resultList.get(0);
					model.addAttribute("meetMap", meetMap);
					
					if ( "1".equals((String)meetMap.get("isSuccess")) ) {
						Map<String, Object> resultParam = generateRequestParam("offsetPageable, modelId, orderby", 
																				offsetPageable, modelId, orderby);
						List<Map<String, Object>> pages = trainScheduleService.getTrainResultList(resultParam);
						
						int hiddenCount = pages.size();
						if ( hiddenCount > 0 ) {
							hiddenCount = Integer.parseInt(String.valueOf( pages.get(0).get("hiddenCount") ));
						}
						Page<?> cPages = new PageImpl<>(pages, pageable, hiddenCount);
						model.addAttribute("cPages", cPages);
						model.addAttribute("hiddenCount", hiddenCount);
					} else {
						failFlag = true;
						String detail = (String) meetMap.get("detail");
						if ( detail == null ) {
							detail = "데이터가 없습니다.";
						}
						model.addAttribute("detail", detail);
					}
					
				} else {
					model.addAttribute("meetMap", "");
				}

			}
			
			model.addAttribute("hiddenModelId", modelId);
			model.addAttribute("hiddenSeq", seq);
		
			if ( failFlag == true ) {
				model.addAttribute("hiddenFail", "0");
				return "thymeleaf/"+menuId+"/content/train-popup-fail";
			} else {
				model.addAttribute("hiddenFail", "1");
				return "thymeleaf/"+menuId+"/content/detail" + suffix;
			}
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
