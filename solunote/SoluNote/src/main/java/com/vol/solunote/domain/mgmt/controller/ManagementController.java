package com.vol.solunote.domain.mgmt.controller;


import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.Exception.TrainCallException;
import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.domain.mgmt.service.ManagementService;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.stt.SttService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.menu.MenuListVo;
import com.vol.solunote.model.vo.siteuser.UserRegisterVo;
import com.vol.solunote.security.vo.SecurityMember;
import org.owasp.encoder.Encode;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/mgmt")
public class ManagementController extends DefaultController {

	@Autowired
	private ManagementService managementService;
	   		
	@Autowired
	private CommonSteelServiceImpl commonService;
	
	@Autowired
	private SttService sttService;

	
	ManagementController() {
		this.menuId="mgmt";
	}
	
	@RequestMapping(value= {"/cont", "/cont/1"})
	public String cont(HttpServletRequest request, 
			Model model,
			@Param("search") DefaultVo search,
			@RequestParam(required = false) Integer activeMenu,
			@Param("pageable")  @PageableDefault(size = 10) Pageable pageable
			) throws Exception {
		
		setSearchTerm(search);
		List<MenuListVo> MenuList = null;

		
		if (activeMenu == 3) {
			
			String role = "";	
			String tcId = "";
	        SecurityContext context = SecurityContextHolder.getContext();
	        if (context != null ) {
	        	Authentication authentication = context.getAuthentication();
	        	if (authentication != null) {
	        		role = ((SecurityMember) authentication.getPrincipal()).getMember().getMoveURL();
	        		tcId =((SecurityMember) authentication.getPrincipal()).getMember().getUserId();
	        		
	        	}
	        }
	        model.addAttribute("role", role);			
	        
//	        String userId = Util.getSessionAttr("userId");

			List<Map<String, Object>> list = managementService.userinfo(tcId);	 
			
			model.addAttribute("list", list);

		}else {
			MenuList = managementService.getAuthList();
			model.addAttribute("MenuList", MenuList);
		}
				
		model.addAttribute("search", search);
		model.addAttribute("menuId", menuId);
		model.addAttribute("liveMenu", activeMenu);
		
		return "thymeleaf/"+menuId+"/cont.html";
	}


	@GetMapping(value= {"/cont/loadList"})
	public String loadList(Model model,
			@Param("userType") int userType,
			@Param("tcId") String tcId,
			@Param("tcName") String tcName,
			@Param("tcPhone") String tcPhone,
			@Param("pageable")  @PageableDefault(size = 10) Pageable pageable,
			@Param("searchStartDate") String searchStartDate , 
			@Param("searchEndDate") String searchEndDate,			
			@RequestParam int activeMenu
			) throws Exception {
				
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
    
        
        // 관리자 통합계정 조회화면
        List<UserRegisterVo> pages = null;
        pages = managementService.getList(activeMenu,offsetPageable,userType, tcId ,tcName,tcPhone, searchStartDate, searchEndDate);
        
		int hiddenCount = pages.size();
		if ( hiddenCount > 0 ) {
			hiddenCount = pages.get(0).getHiddenCount();
		}
		
		
		 Page<UserRegisterVo> cPages = new PageImpl<>(pages, pageable,hiddenCount);		 
		 model.addAttribute("cPages", cPages);
		 model.addAttribute("hiddenCount", hiddenCount);
		 
		return "thymeleaf/"+menuId+"/content/table-list1";
		
		
	}
		
	
	@GetMapping(value= {"/cont/settingList"})
	public String settingList(Model model,
			@Param("useYn") String useYn,
			@Param("searchType") String searchType,
			@Param("searchKeyword") String searchKeyword,
			@Param("pageable")  @PageableDefault(size = 10) Pageable pageable,
			@Param("searchStartDate") String searchStartDate , 
			@Param("searchEndDate") String searchEndDate,			
			@RequestParam int activeMenu
			) throws Exception {
				
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
    
        
        List<Map<String, Object>> pages = null;
        
        pages = managementService.getSettingList(offsetPageable,useYn,searchType,searchKeyword,searchStartDate,searchEndDate);
        
        int hiddenCount = pages.size();
        if ( hiddenCount > 0 ) {
        
            hiddenCount = Integer.parseInt(String.valueOf(pages.get(0).get("hiddenCount")));
        }
        
         Page<Map<String, Object>> cPages = new PageImpl<>(pages, pageable,hiddenCount);         
         model.addAttribute("cPages", cPages);
         model.addAttribute("hiddenCount", hiddenCount);
        
        return "thymeleaf/"+menuId+"/content/table-list4";
		
		
	}	
	
		
	@PostMapping(value= {"/cont/check"}, consumes = { "application/x-www-form-urlencoded" })
	@ResponseBody
	public String check(@Param("code") String code) throws Exception {
		return String.valueOf(managementService.checkCode(code));
	}
	
	@PostMapping(value = {"/cont/insert"})
	@ResponseBody
	public String insert(Model model, @RequestParam Map<String,String> paramMap) throws Exception {
		
		JSONObject json = new JSONObject();
		
		json = managementService.insertContents(paramMap);
		
		return json.toString();
	}
	
	
	@PostMapping(value= {"/cont/removeId"}, consumes = { "application/x-www-form-urlencoded" })
	public String deleteUser(
			@RequestParam int activeMenu ,
			@Param("userId") String[] userId
			) throws Exception {
		
		if(activeMenu == 1) {
			managementService.deleteUser(userId);
		}else if (activeMenu == 2) {
			managementService.saveUser(userId);
		}
		

		return "1";
	}
	
	@PostMapping(value= {"/cont/permanentlyId"}, consumes = { "application/x-www-form-urlencoded" })
	public String permanentlyUser(
			@RequestParam int activeMenu ,
			@Param("userId") String[] userId
			) throws Exception {
		
		managementService.permanentlyUser(userId);
		

		return "1";
	}	
	

	@GetMapping(value= {"/cont/userinfo"}, consumes = { "application/x-www-form-urlencoded" })
	public String userinfo(Model model,
			@RequestParam(required = false) Integer activeMenu,
			@RequestParam(required = false) String userId)
			throws Exception {
				
		
		String role = "";		
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null ) {
        	Authentication authentication = context.getAuthentication();
        	if (authentication != null) {
        		role = ((SecurityMember) authentication.getPrincipal()).getMember().getMoveURL();
        	}
        }
        model.addAttribute("role", role);
			
		List<Map<String, Object>> list = managementService.userinfo(userId);
						 		 
		model.addAttribute("list", list);
		model.addAttribute("liveMenu", activeMenu);
		
		return "thymeleaf/"+menuId+"/content/user-register-change";
	}


	
	@PostMapping(value= {"/cont/userinfo"}, consumes = { "application/x-www-form-urlencoded" })
	public String changeUserInfo(
			@Param("tcId") String tcId,
			@Param("tcName") String tcName,
			@Param("tcPw") String tcPw,
			@Param("tcPhone") String tcPhone,
			@Param("tcEmail") String tcEmail,
			@Param("tcLevel") String tcLevel
			) throws Exception {

		boolean resultOk = managementService.changeUserinfo(tcId,tcPw,tcName,tcPhone,tcEmail,tcLevel,"changeUserInfo");

		if (!resultOk) {
			throw new RuntimeException();						
		}
	
		return "1";
	
	}
	

	
    @RequestMapping(value = {"/cont/checkSetting"})
    @ResponseBody
    public int checkSetting(
            @RequestParam("settingTitle") String settingTitle
            ) throws Exception {
        
        return managementService.checkSetting(settingTitle);
    }	
    
    
    @PostMapping(value= {"/cont/settingRes"}, consumes = { "application/x-www-form-urlencoded" })
    public String settingRes(
            @Param("settingTitle") String settingTitle,
            @Param("settingValue") String settingValue,
            @Param("settingUseYn") String settingUseYn,
            @Param("settingDetail") String settingDetail
            ) throws Exception {
                
        managementService.settingRes(settingTitle,settingValue,settingUseYn,settingDetail);
        
        return "1";
    }
        
	@GetMapping(value= {"/cont/settingRes"}, consumes = { "application/x-www-form-urlencoded" })
	public String settingInfo(Model model,
			@RequestParam(required = false) String settingTitle)
			throws Exception {
				
			
		List<Map<String, Object>> list = managementService.settingInfo(settingTitle);
						 		 
		model.addAttribute("list", list);
		
		return "thymeleaf/"+menuId+"/content/setting-register-change";
	}    
    
	@PostMapping(value= {"/cont/settingInfo_change"}, consumes = { "application/x-www-form-urlencoded" })
	public String changeSettinInfo(
			@Param("settingTitleChange") String settingTitleChange,			
            @Param("settingValueChange") String settingValueChange,
            @Param("settingUseYnChange") String settingUseYnChange,
            @Param("settingDetailChange") String settingDetailChange
			) throws Exception {
				
		managementService.changeMeetingInfo(settingTitleChange,settingValueChange,settingUseYnChange,settingDetailChange);
		
		return "1";
	}    
    
	@PostMapping(value= {"/cont/settingInfo_delete"}, consumes = { "application/x-www-form-urlencoded" })
	public String deleteSettingInfo(
			@Param("settingTitle") String[] settingTitle
			) throws Exception {
		
			managementService.deleteSettingInfo(settingTitle);
		

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
		
		param.put("type",Encode.forHtml(type));
		
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
	
	
	@GetMapping(value= {"/cont/createSeveral"})
	public String createKeyword(HttpServletRequest request 
			
			) throws Exception {

	    return "thymeleaf/"+menuId+"/content/user-register-several";
	} 	
	
	
	@GetMapping(value= {"/cont/excelForm"})
	@ResponseBody
	public void excelForm(
			HttpServletResponse response
			) throws Exception {

		managementService.excelForm(response);

	}	
	
	
	@PostMapping(value= {"/cont/uploadUserList"})
	public String upload(
			Model model, 
			MultipartFile file,
			Principal principal
			) throws Exception {
				

		if(file == null || file.isEmpty()) {
			throw new RuntimeException();
		}
            
        List<Map<String, Object>> pages = null;
        
        pages = managementService.getExcelList(file);
                
        Page<Map<String, Object>> cPages = new PageImpl<>(pages);         
        model.addAttribute("cPages", cPages);   
        return "thymeleaf/"+menuId+"/content/user-register-several-list.html";
		
	}	
	
}
