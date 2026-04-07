package com.vol.solunote.domain.dashboard.controller;


import com.vol.solunote.Exception.TrainCallException;
import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.stt.SttService;
import com.vol.solunote.config.CustomHttpSessionListener;
import com.vol.solunote.domain.dashboard.service.DashboardService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.login.LoginLogProjectionVo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.owasp.encoder.Encode;

@Controller
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController extends DefaultController {

	@Autowired
	SttService	 sttService;
		
	@Autowired
	CommonSteelServiceImpl	 commonService;	
	
	@Autowired
    private  DashboardService dashboardService;

	private final CustomHttpSessionListener httpSessionListener;    
	
    
	DashboardController(DashboardService dashboardService, CustomHttpSessionListener httpSessionListener) {
        this.dashboardService = dashboardService;
        this.httpSessionListener = httpSessionListener;
        this.menuId="dashboard";
    }

    
    @GetMapping(value= {"/cont", "/cont/1", "/cont/"})
    public String cont(HttpServletRequest request, Model model, @RequestParam(required = false) Integer seq, @Param("search") DefaultVo search) throws Exception {

        model.addAttribute("menuId", menuId);
        // 나중에 적절한 수로 채워 넣을것
        model.addAttribute("processingFileCnt", 0);
        model.addAttribute("activeSessionCnt", httpSessionListener.getActiveSessions().stream()
                .filter(session -> {
                    Date nowDate = new Date();
                    long diffTime = nowDate.getTime() - session.getLastAccessedTime();
                    return (diffTime / 1000) < 1800;
                }).count());

		setSearchTerm(search);
		
		model.addAttribute("search", search);
        return "thymeleaf/"+menuId+"/cont.html";
    }
    
    @GetMapping("/cont/test")
    @ResponseBody  // HTML 파일을 찾지 않고 문자열을 바로 출력하게 함
    public String test() {
        return "Mapping Success!";
    }
    
    @GetMapping(value= {"/cont/dashboardCard", "/cont/1/dashboardCard"})
    @ResponseBody
    public Map <String, Object> dashboardCard(Model model, @RequestParam String startDate, @RequestParam String endDate) throws Exception {

        int userCount = dashboardService.countByCreatedAtBetweenAndGroupDate(startDate, endDate);
        Map<String, Object> map = dashboardService.analyticsAvgMeeting(startDate, endDate);

        double avgTimeDurationMs;
        String durationFormat = null;
		if (map.get("avgTimeDurationMs") != null) {
        	avgTimeDurationMs = (double) map.get("avgTimeDurationMs");
            int durationSec = (int) Math.round(avgTimeDurationMs/1000);
            durationFormat = String.format("%02d:%02d:%02d", (durationSec) / 3600, ((durationSec) % 3600) / 60, ((durationSec) % 60));        	
        }

        double avgFileSizeBytes;
        String avgFileSizeValue = null;
        String avgFileSizeFormat = "";        
		if (map.get("avgFileSizeBytes") != null) {
			avgFileSizeBytes = (double) map.get("avgFileSizeBytes");
	        if(avgFileSizeBytes > 1000000000) {
	            double fileSizeGB = avgFileSizeBytes / 1000000000;
	            avgFileSizeValue = Double.toString(Math.round(fileSizeGB*100.0)/100.0);
	            avgFileSizeFormat = "GB";
	        } else if(avgFileSizeBytes > 1000000) {
	            double fileSizeMB = avgFileSizeBytes / 1000000;
	            avgFileSizeValue = Double.toString(Math.round(fileSizeMB*100.0)/100.0);
	            avgFileSizeFormat = "MB";
	        } else if(avgFileSizeBytes > 1000) {
	            double fileSizeKB = avgFileSizeBytes / 1000;
	            avgFileSizeValue = Double.toString(Math.round(fileSizeKB*100.0)/100.0);
	            avgFileSizeFormat = "KB";
	        } else if(avgFileSizeBytes > 0) {
	            avgFileSizeValue = Double.toString(Math.round(avgFileSizeBytes*100.0)/100.0);
	            avgFileSizeFormat = "B";
	        }                
			
        }        

        Map<String, Object> returnMap = new HashMap <>();
        returnMap.put("userCount", userCount);
        returnMap.put("meetingCount", map.get("count"));
        returnMap.put("avgMeetingTimeDurationMs", durationFormat);
        returnMap.put("avgFileSizeValue", avgFileSizeValue);
        returnMap.put("avgFileSizeFormat", avgFileSizeFormat);

        return returnMap;
    }

    @GetMapping(value= {"/cont/userCountGroupDate", "/cont/1/userCountGroupDate"})
    @ResponseBody
    public List <LoginLogProjectionVo> userCountGroupDate(Model model, @RequestParam String startDate, @RequestParam String endDate) throws Exception {

    	log.debug("startData = {}, endDate = {}", startDate, endDate);
    	
        List<LoginLogProjectionVo> dateUserCountMap = dashboardService.countUserGroupDate(startDate, endDate);

        return dateUserCountMap;
    }

    @GetMapping(value= {"/cont/fileCountGroupDate", "/cont/1/fileCountGroupDate"})
    @ResponseBody
    public List <Map <String, Object>> fileCountGroupDate(Model model, @RequestParam String startDate, @RequestParam String endDate) throws Exception {

        List<Map <String, Object>> fileCountGroupDate = dashboardService.fileCountGroupDate(startDate, endDate);

        return fileCountGroupDate;
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
    
}

