package com.vol.solunote.domain.minutes.controller;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.owasp.encoder.Encode;
import java.util.HashMap;

import jakarta.annotation.PostConstruct;
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
import com.vol.solunote.common.service.CommonDataService;
import com.vol.solunote.domain.minutes.service.MinutesService;
import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.meeting.MeetingResultVo;
import com.vol.solunote.model.vo.meeting.MeetingSpeakerVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.comm.service.common.CommonSteelServiceImpl;
import com.vol.solunote.comm.service.stt.SttService;
import com.vol.solunote.comm.service.disk.DiskService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/minutes")
public class MinutesController extends DefaultController {
	
	private static final int ERROR_MESSAGE_SIZE = 4000;

	@Autowired
	MinutesService minutesService;
	
	@Autowired
	CommonDataService commonDataService;
	
	@Autowired
	CommonSteelServiceImpl commonService;

	@Autowired
	DiskService diskService;

	
	@Autowired
	SttService	 sttService;	

	private final static DateTimeFormatter updateAtFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

	public static int processingFileCnt = 0;
	
	MinutesController() {
		this.menuId="minutes";
	}
	
	@PostConstruct
	public void init() {
		log.debug("minutesService : {}", minutesService );
	}
	
	@RequestMapping(value= {"/cont", "/cont/1","/cont/"})
	public String cont(HttpServletRequest request,
			Model model, 
			@RequestParam(required = false) Integer activeMenu, 
			@RequestParam(required = false) Integer seq,
			@Param("search") DefaultVo search
			) throws Exception {

		setSearchTerm(search);
		
		model.addAttribute("search", search);
		model.addAttribute("menuId", menuId);
		model.addAttribute("liveMenu", activeMenu);
	
		return "thymeleaf/"+menuId+"/cont.html";
	}
	
	@PostMapping(value= {"/cont/view"})
	public String view(HttpServletRequest request,
			Model model, 
			@RequestParam(required = false) Integer activeMenu, 
			@RequestParam(required = false) Integer seq,
			@Param("search") DefaultVo search
			) throws Exception {
		
		
		if (seq == null) {
			throw new RuntimeException("SEQ Null");
		} else {
			
			model.addAttribute("seq", seq);
			model.addAttribute("speakerGroupList", minutesService.findSpeakerByMeeting_SEQ(seq).stream().map(MeetingSpeakerVo::getName).collect(Collectors.toList()));
			
			Map<String,Object> meetMap = minutesService.getMeetBySEQ(seq);
			int durationSec = (int) Math.round(Double.parseDouble(meetMap.get("time_duration_ms").toString())/1000);
			meetMap.put("durationFormat", String.format("%02d:%02d:%02d", (durationSec) / 3600, ((durationSec) % 3600) / 60, ((durationSec) % 60)));
			
			List<MeetingResultVo> resultList = minutesService.getMeetResultList(seq);
			model.addAttribute("resultList", resultList);
			
			StringBuilder resultString = new StringBuilder();		
			
			for (MeetingResultVo meetingResult : resultList) {

			    resultString.append("화자: ").append(meetingResult.getName()).append("\n");
			    resultString.append("내용: ").append(meetingResult.getText()).append("\n");
			    resultString.append("\n"); // 각 발언 사이에 빈 줄을 추가하여 구분		    
			    
			}							
			
			String reResult = resultString.toString();
						
			if(reResult.length() <500) {
				model.addAttribute("checkSummlen", "X");
			}else {
				model.addAttribute("checkSummlen", "O");
			}
			
			Map<String, List<String>> summaryData;
			Map<String, List<String>> summaryData2;
			Map<String, List<Map<String, Object>>> summaryData3;
			
			String summary = meetMap.get("summary").toString();
			String summaryStatus = meetMap.get("summaryStatus").toString();
			String summaryId = meetMap.get("summaryId").toString();
			String summary2 = meetMap.get("summary2").toString();
			String summaryStatus2 = meetMap.get("summaryStatus2").toString();
			String summaryId2 = meetMap.get("summaryId2").toString();
			String summary3 = meetMap.get("summary3").toString();
			String summaryStatus3 = meetMap.get("summaryStatus3").toString();
			String summaryId3 = meetMap.get("summaryId3").toString();			
			
						
			if (!summaryStatus.isEmpty() && !"SUCCESS".equals(summaryStatus)) {
				Map<String, Object> content = minutesService.summaryStatusApi(seq,summaryId,1);
				
				String status = (String) content.get("status");
				if("SUCCESS".equals(status) ) {
					summary = (String) content.get("summary");
					meetMap.put("summaryStatus",status);
				}else {
					meetMap.put("summaryStatus",status);					
				}
				
			}
			
			if (!summaryStatus2.isEmpty() && !"SUCCESS".equals(summaryStatus2)) {
				Map<String, Object> content = minutesService.summaryStatusApi(seq,summaryId2,2);
				
				String status = (String) content.get("status");
				if("SUCCESS".equals(status) ) {
					summary2 = (String) content.get("summary");
					meetMap.put("summaryStatus2",status);
				}else {
					meetMap.put("summaryStatus2",status);					
				}
				
			}			

			if (!summaryStatus3.isEmpty() && !"SUCCESS".equals(summaryStatus3)) {
				Map<String, Object> content = minutesService.summaryStatusApi(seq,summaryId3,3);
				
				String status = (String) content.get("status");
				if("SUCCESS".equals(status) ) {
					summary3 = (String) content.get("summary");
					meetMap.put("summaryStatus3",status);
				}else {
					meetMap.put("summaryStatus3",status);					
				}
				
			}			
			
			if(summary.isEmpty() && summary2.isEmpty() && summary3.isEmpty()) {			
				model.addAttribute("hasSummary", 0);	
			}else {
				model.addAttribute("hasSummary", 1);
			}

			summaryData = minutesService.parseSummary(summary, 0);
			summaryData2 = minutesService.parseSummary2(summary2,0);
			summaryData3 = minutesService.parseSummary3(summary3);
			

			model.addAttribute("meetMap", meetMap);						
			model.addAttribute("summary", summaryData);
			model.addAttribute("summary2", summaryData2);
			model.addAttribute("summary3", summaryData3);
		}
				
		return  "thymeleaf/"+menuId+"/content/detail";
	}
	
	@GetMapping(value= {"/cont/loadMeetResult", "/cont/1/loadMeetResult"}, produces = {"application/json"})
	@ResponseBody
	public List<MeetingResultVo> loadMeetResult(Model model, @RequestParam("seq") int meetSeq) throws Exception {
		List<MeetingResultVo> meetingResultList = minutesService.getMeetResultList(meetSeq);

		return meetingResultList;
	}	
	

	@PostMapping(value= {"/cont/remark", "/cont/1/remark"})
	@ResponseBody
	public String remarkUpdate(Model model, @RequestBody String updateRemark, @RequestParam("seq") int meetSeq) throws Exception {

		LocalDateTime updatedAt = LocalDateTime.now();
		minutesService.updateRemarkAndUpdatedAtBySeq(meetSeq, updatedAt, Boolean.parseBoolean(updateRemark));

		return updatedAt.format(updateAtFormatter);
	}

	@PostMapping(value= {"/cont/subject", "/cont/1/subject"})
	@ResponseBody
	public String subjectUpdate(Model model, @RequestBody String updateSubject, @RequestParam("seq") int meetSeq) throws Exception {

		LocalDateTime updatedAt = LocalDateTime.now();
		minutesService.updateSubjectAndUpdatedAtBySeq(meetSeq, updateSubject, updatedAt);

		return updatedAt.format(updateAtFormatter);
	}

	@PostMapping(value= {"/cont/text", "/cont/1/text"})
	@ResponseBody
	public String textUpdate(Model model, @RequestBody Map<Integer, String> body, @RequestParam int meetSeq) throws Exception {
		LocalDateTime updatedAt = LocalDateTime.now();
		minutesService.updateTextAndUpdatedAtByMap(meetSeq, body, updatedAt);

		return updatedAt.format(updateAtFormatter);
	}

	@PostMapping(value= {"/cont/speaker_change", "/cont/1/speaker_change"})
	@ResponseBody
	public String speakerUpdate(HttpServletRequest request, @RequestParam int meetSeq, @RequestBody String updateSpeakerText, @RequestParam("seq[]") int []meetResultSeq) throws Exception {
		LocalDateTime updatedAt = LocalDateTime.now();
		int result = minutesService.updateSpeakerAndUpdatedAtBySeqArr(meetResultSeq, updateSpeakerText, meetSeq, updatedAt);
		return result+"/"+updatedAt.format(updateAtFormatter);
	}

	@PostMapping(value= {"/cont/speaker", "/cont/1/speaker"})
	@ResponseBody
	public String speakerCreate(Model model, @RequestParam int meetSeq, @RequestBody String speakerText) throws Exception {
		LocalDateTime createdAt = minutesService.createSpeaker(meetSeq, speakerText);
		return createdAt == null ? null:createdAt.format(updateAtFormatter);
	}

	@PostMapping(value= {"/cont/speaker_delete", "/cont/1/speaker_delete"})
	@ResponseBody
	public String speakerDelete(Model model, @RequestParam int meetSeq, @RequestBody String speakerText) throws Exception {
		LocalDateTime createdAt = minutesService.deleteSpeaker(meetSeq, speakerText);
		return createdAt == null ? null:createdAt.format(updateAtFormatter);
	}
	
	@GetMapping(value= {"/cont/loadList", "/cont/1/loadList"})
	public String loadList(Model model, 
//			HttpSession sess, 
			@RequestParam int activeMenu,
			@Param("pageable")   @PageableDefault(size = 10, page = 1) Pageable pageable,
			@RequestParam String keyword , 
			@RequestParam String category , 
			@RequestParam String searchStartDate , 
			@RequestParam String searchEndDate) throws Exception {
		
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
		
		List<MeetingVo> pages = minutesService.getList(keyword,category ,activeMenu,searchStartDate,searchEndDate, offsetPageable);
				
        int hiddenCount = pages.size();
        if ( hiddenCount > 0 ) {        
            hiddenCount = Integer.parseInt(String.valueOf(pages.get(0).getHiddenCount()));
        }
        Page<MeetingVo> cPages = new PageImpl<>(pages, pageable,hiddenCount); 
        model.addAttribute("cPages", cPages);
        model.addAttribute("hiddenCount", hiddenCount);
        
        return "thymeleaf/"+menuId+"/content/table-list";
	}	
	
	

	@GetMapping(value= {"/cont/view/download", "/cont/1/view/download"})
	@ResponseBody
	public void createTxt(HttpServletResponse response, @RequestParam("seq") Integer meetSeq, @RequestParam("kind") int kind, @RequestParam("type") String type) throws Exception {

		Map<String,Object> meetMap = minutesService.getMeetBySEQAndDuration(meetSeq);

		List<MeetingResultVo> pages = minutesService.getMeetResultList(meetSeq);


		if(kind == 1) {
			if(type.equals("xlsx")) {
				minutesService.createXlsx(response, pages, meetMap, "summary");
			} else if(type.equals("txt")) {
				minutesService.createTxt(response, pages, meetMap, "summary");
			} else if(type.equals("docx")) {
				minutesService.createDocx(response, pages, meetMap, "summary");
			} else if(type.equals("hwp")) {
				minutesService.createHwp(response, pages, meetMap, "summary");
			}			
		}else if (kind ==2 ) {
			if(type.equals("xlsx")) {
				minutesService.createSummaryXlsx(response, meetMap);
			} else if(type.equals("txt")) {
				minutesService.createSummaryTxt(response, meetMap);
			} else if(type.equals("docx")) {
				minutesService.createSummaryDocx(response,meetMap);
			} else if(type.equals("hwp")) {
				minutesService.createSummaryHwp(response, meetMap);
			}	
		}else {
			minutesService.createFileDownload(response,meetMap);
		}
	}

	@PostMapping(value= {"/cont/delete", "/cont/1/delete"})
	@ResponseBody
	public String delete(Model model, @RequestBody Map<String, String>[] array) throws Exception {

		for ( Map<String, String> map : array ) {
			int seq = Integer.parseInt(map.get("seq").toString());
			minutesService.setDeletedAt(seq);
		}
		return "0";
	}

	@PostMapping(value= {"/cont/trash", "/cont/1/trash"})
	@ResponseBody
	public String trash(Model model, @RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
			int seq = Integer.parseInt(map.get("seq").toString());
			minutesService.setTrashedAt(seq);
		}
		return "0";
	}
	
	@PostMapping(value= {"/cont/toTrain", "/cont/1/toTrain"})
	@ResponseBody
	public String toTrain(Model model, @RequestBody Map<String, String>[] array) throws Exception {
		String result = "0";
		
		for ( Map<String, String> map : array ) {
			int seq = Integer.parseInt(map.get("seq").toString());
			Map<String, Object> meetMap = minutesService.readMeetBySeq(seq);
					
			log.info("seq = {} Result Count = {}", seq, meetMap.get("tr_count"));
			if ( (Long)meetMap.get("tr_count") < 1 ) {
				result = "[" + (String)meetMap.get("subject") + "] 회의록을 구성하는 문장 리스트가 없어서 학습 Data로 전송할 데이타가 없습니다.";
				break;
			}
			
			commonDataService.copySoundAndTrans(seq, meetMap);
		}
		
		return result;
	}
	
	@PostMapping(value= {"/cont/toTest", "/cont/1/toTest"})
	@ResponseBody
	public String toTest(Model model, @RequestBody Map<String, String>[] array) throws Exception {
		String result = "0";
		
		for ( Map<String, String> map : array ) {
			int seq = Integer.parseInt(map.get("seq").toString());
			Map<String, Object> meetMap = minutesService.readMeetBySeq(seq);
			if ( (Long)meetMap.get("tr_count") < 1 ) {
				result = "[" + (String)meetMap.get("subject") + "] 회의록을 구성하는 문장 리스트가 없어서 테스트 Data로 전송할 데이타가 없습니다.";
				break;
			}
			commonDataService.copyTestAndTestTrans(seq, meetMap);
		}
		
		return result;
	}

	@PostMapping(value= {"/cont/trash/rollback", "/cont/1/trash/rollback"})
	@ResponseBody
	public String trashRollback(Model model, @RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
			int seq = Integer.parseInt(map.get("seq").toString());
			minutesService.setNullTrashedAt(seq);
		}

		return "0";
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
		
		Map<String, Object> param = commonService.saveUploadFileConvert(Category.MEET, file);
		
		param.put("type",Encode.forHtml(type));
		
		org.springframework.core.io.Resource resource = (org.springframework.core.io.Resource) param.get("resource");
		
		Map<String, Object> resultMap = null;
		
		try {			
			resultMap = sttService.callSttForMenu(resource, false, letter);
			BigDecimal bigDecimal = new BigDecimal(resultMap.get("duration").toString());
			BigDecimal durationMs = bigDecimal.multiply(new BigDecimal(1000));
			param.put("timeDurationStr", durationMs.toString());
			param.put("lang", Encode.forHtml(letter));
		} catch ( TrainCallException tce ) {
			param.put("timeDurationStr", "0");
			String message = tce.getDetail();
			param.put("errorMessage", message.length() > ERROR_MESSAGE_SIZE ? message.substring(0, ERROR_MESSAGE_SIZE) : message);
		} catch (Exception e ) {
			param.put("timeDurationStr", "0");
			String message = e.getMessage();
			param.put("errorMessage", message.length() > ERROR_MESSAGE_SIZE ? message.substring(0, ERROR_MESSAGE_SIZE) : message);
		}
		
		int tcUserSeq = Integer.parseInt(principal.getName());
		param.put("tcUserSeq", tcUserSeq);
		
		log.debug("Call parseDiarizeAndStt in ResponseEntity.... ");
		Map<String, String> map = sttService.parseDiarizeAndSttForMenu(param, resultMap);
		
		Map<String, String> safeMap = new HashMap<>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
		    safeMap.put(entry.getKey(), Encode.forHtml(entry.getValue()));
		}
		
		
		return ResponseEntity.ok(safeMap);
	}

	@RequestMapping(value= {"/cont/download", "/download"})
	public void download(Model model, @RequestParam(value="fileNm") String fileNm
			, HttpServletRequest request, HttpServletResponse response ) throws Exception {
					
		byte []all = Files.readAllBytes(diskService.getUploadFilePath( Category.MEET, fileNm));

		response.setContentLength(all.length);
		// forces download
		response.setHeader("Content-Type", "audio/mpeg");
		response.setHeader("Accept-Ranges", "bytes");
		response.getOutputStream().write(all);
		response.flushBuffer();
	}

	
	@PostMapping("/cont/view/sendSummary")
	@ResponseBody
	public ResponseEntity<String> sendSummary(
			
			@RequestParam("seq") Integer seq,
			@RequestParam("summaryType") Integer summaryType
			
			) throws Exception {
	    

		String summaryId = minutesService.summary(seq,summaryType);
		
		return ResponseEntity.ok(summaryId);
	}
	
	@PostMapping(value = {"/cont/view/statusSummary"})
	@ResponseBody  
	public String summary(
	        @RequestParam("summaryId") String summaryId, 
	        @RequestParam("seq") Integer seq, 
	        @RequestParam("summaryType") Integer summaryType, 
	        Model model) 
	    throws Exception {

		Map<String, Object> content = minutesService.summaryStatusApi(seq,summaryId,summaryType);
		
		String status = (String) content.get("status");
	
	    return status;
	}
	
	

	@GetMapping(value= {"/cont/view/summary"})
	public String summary(
			@RequestParam("seq") Integer seq, 
			@RequestParam("summaryType") Integer summaryType,
			@RequestParam("summaryId") String summaryId, 
			Model model 
			) 
		throws Exception {

		
		Map<String,Object> meetMap = minutesService.getMeetBySEQ(seq);	
		
		if( summaryType ==3) {
			Map<String, List<Map<String, Object>>> summaryData = minutesService.parseSummary3(meetMap.get("summary3").toString());
			model.addAttribute("summary", summaryData);
			
			return "thymeleaf/"+menuId+"/content/summary3";
		}else if (summaryType ==2){
			Map<String, List<String>> summaryData = minutesService.parseSummary2(meetMap.get("summary2").toString(),0);
			
			model.addAttribute("summary", summaryData);
			
			return "thymeleaf/"+menuId+"/content/summary2";
		}else{
			Map<String, List<String>> summaryData = minutesService.parseSummary(meetMap.get("summary").toString(),0);
			
			model.addAttribute("summary", summaryData);
			
			return "thymeleaf/"+menuId+"/content/summary";
		}

	
	}
	

	@GetMapping(value= {"/cont/view/summaryData"})
	@ResponseBody
	public int getTypeSumm(
			@RequestParam("seq") Integer seq, 
			@RequestParam("summaryType") Integer summaryType, 
			Model model 
			) 
		throws Exception {

		
		Map<String,Object> meetMap = minutesService.getMeetBySEQ(seq);

		String summary1 =meetMap.get("summary").toString();
		String summary2 =meetMap.get("summary2").toString();
		String summary3 =meetMap.get("summary3").toString();
	
		int result ; 
		
		if(summaryType == 1) {
			result = summary1.isEmpty() ? 0 : 1;
		}else if (summaryType == 2) {
			result = summary2.isEmpty() ? 0 : 1;
		}else {
			result = summary3.isEmpty() ? 0 : 1;
		}
		
		return result;
	}
		
	@PostMapping(value= {"/cont/summaryUpdate", "/cont/1/summaryUpdate"})
	@ResponseBody
	public String summaryUpdate(@RequestBody String result, 
			@RequestParam int meetSeq,
			@RequestParam int summaryType
			) throws Exception {
		LocalDateTime updatedAt = LocalDateTime.now();

		minutesService.summaryUpdate(result,meetSeq,summaryType);
		
		return updatedAt.format(updateAtFormatter);
	}	
}

