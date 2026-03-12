package com.vol.solunote.menu23.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletOutputStream;
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

import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.model.Category;
import com.vol.solunote.comm.model.TrainCallException;
import com.vol.solunote.comm.service.DiskService;
import com.vol.solunote.comm.service.FFMpegService;
import com.vol.solunote.comm.util.CommonUtil;
import com.vol.solunote.comm.vo.DefaultVo;
import com.vol.solunote.common.service.CommonDataService;
import com.vol.solunote.menu23.service.Menu23SteelServiceImpl;
import com.vol.solunote.model.dto.transcription.TranscriptionExt;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.repository.transcription.TranscriptionRepository;
import com.vol.solunote.comm.service.SttService;
import com.vol.solunote.comm.service.CommonSteelServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/menu23")
public class Menu23Controller extends DefaultController {

	@Autowired
	private FFMpegService	ffmpegService;
	
	
	@Autowired
	private DiskService diskService;
	
	@Autowired
	private SttService sttService;
	
	@Autowired
	private CommonSteelServiceImpl commonService;
	
	@Autowired
	CommonDataService commonDataService;
	
	@Autowired
	private TranscriptionRepository transcriptionRepository;
	
	@Autowired
	private Menu23SteelServiceImpl menu23Service;
	
	
	private Set<String> referSet = new HashSet<>(Arrays.asList(
			"/menu23/cont/1?activeMenu=1",
			"/menu25/cont/1?activeMenu=1",
			"/menu26/cont/1?activeMenu=5" 
	));
	
	private final static DateTimeFormatter updateAtFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

	public static int processingFileCnt = 0;
	
	Menu23Controller() {
		this.menuId="menu23";
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
	
		return "thymeleaf/"+menuId+"/cont.html";
	}

	@PostMapping(value= {"/cont/remark", "/cont/1/remark"})
	@ResponseBody
	public String remarkUpdate(Model model, @RequestBody String updateRemark, @RequestParam("seq") int meetSeq) throws Exception {

		LocalDateTime updatedAt = LocalDateTime.now();
		menu23Service.updateRemarkAndUpdatedAtBySeq(meetSeq, updatedAt, Boolean.parseBoolean(updateRemark));

		return updatedAt.format(updateAtFormatter);
	}

	@PostMapping(value= {"/cont/trans", "/cont/1/trans"})
	@ResponseBody
	public void transUpdate(Model model, @RequestBody String text, @RequestParam("seq") int meetSeq) throws Exception {

		transcriptionRepository.updateTrainTextBySeq(meetSeq, text);

	}

	@GetMapping(value= {"/cont/loadMeetResult", "/cont/1/loadMeetResult"}, produces = {"application/json"})
	@ResponseBody
	public List<TranscriptionVo> loadMeetResult(Model model, @RequestParam("seq") int meetSeq) throws Exception {
		
		Map<String, Object> param = generateRequestParam("meetSeq", meetSeq);
		
//		List<Transcription> meetingResultList = menu23Service.getTranscriptionList(meetSeq, -1, null,0);
		List<TranscriptionVo> meetingResultList = menu23Service.getTranscriptionList(param);

		return meetingResultList;
	}

	@GetMapping(value= {"/cont/loadList", "/cont/1/loadList"})
	public String loadList(Model model,
			@Param("pageable")  @PageableDefault(size = 10, page = 1) Pageable pageable,
			@RequestParam(value="activeMenu") int activeMenu,
			@RequestParam(required = false) String keyword,
			@RequestParam String searchStartDate , 
			@RequestParam String searchEndDate
			) throws Exception {
		
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
		
		Map<String, Object> param = generateRequestParam("offsetPageable, activeMenu, keyword, searchStartDate, searchEndDate", 
															offsetPageable, activeMenu, keyword, searchStartDate, searchEndDate);
		
		List<SoundVo> pages = menu23Service.getListData(param);

		int hiddenCount = 0;
		if ( pages.size() > 0 ) {
			hiddenCount = pages.get(0).getHiddenCount();
		}
		
		 Page<SoundVo> cPages = new PageImpl<>(pages, pageable, hiddenCount);
		 
		 model.addAttribute("cPages", cPages);
		 model.addAttribute("hiddenCount", hiddenCount);
		 
		return "thymeleaf/"+menuId+"/content/table-list";
	}

	@PostMapping(value= {"/cont/trash", "/cont/1/trash"})
	@ResponseBody
	public String trash(Model model,
			@RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
			menu23Service.trash(map);
		}

		return "1";
	}
	
	@PostMapping(value= {"/cont/sendtest", "/cont/1/sendtest"})
	@ResponseBody
	public String sendtest(Model model,
			@RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
			menu23Service.sendtest(map);
		}
		
		return "1";
	}

	@PostMapping(value= {"/cont/trash/rollback", "/cont/1/trash/rollback"})
	@ResponseBody
	public String trashRollback(Model model, 
			@RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
			menu23Service.trashRollback(map);
		}

		return "1";
	}
	
	
	@PostMapping(value= {"/cont/delete", "/cont/1/delete"})
	@ResponseBody
	public String delete(Model model,
			@RequestBody Map<String, String>[] array) throws Exception {
		
		for ( Map<String, String> map : array ) {
			menu23Service.delete(map);
		}

		return "1";
	}	
	


	@RequestMapping(value= {"/downloadPart"})
	public void downloadPart(Model model, 
			@RequestParam(value="fileNm") String fileNm,
			@RequestParam(value="start") float start,
			@RequestParam(value="end") float end,
			@RequestParam(value="category") String category,
//			@RequestHeader Map<String, String> headers,
			HttpServletRequest request, HttpServletResponse response ) throws Exception {
//		System.out.println("==#== " + flag);
//		System.out.println("==#====== " + start);
//		headers.forEach((key, value) -> {
//			System.out.println(String.format("==# Header %s = %s", key, value));
//	    });
		
		String referer = request.getHeader("referer");
		 if ( referer == null ) {
			 return;
		 }
		 
		 boolean flag = true;
		for( String s : referSet ) {
			if ( referer.contains(s) ) {
				flag = false;
				break;
			}
		}
		
		if ( flag == true ) {
			log.warn("WARNING : disable download file : " + referer);
			return;
		}

//		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] all =  ffmpegService.cutAudio(diskService.strToCategory(category), fileNm, start, end);

//		byte []all = Files.readAllBytes(Paths.get(UPLOAD_PATH+File.separator+fileNm));
//		byte[] all = out.toByteArray();

		response.setContentLength(all.length);
		// forces download
		response.setHeader("Content-Type", CommonUtil.getMimeType(fileNm));
		response.setHeader("Accept-Ranges", "bytes");
		response.getOutputStream().write(all);
		response.flushBuffer();
	}

	@RequestMapping(value= {"/downloadStereo"})
	public void downloadStereo(Model model, 
			@RequestParam(value="fileNm") String fileNm,
			@RequestParam(value="start") String start,
			@RequestParam(value="end") String end,
			@RequestParam(value="category") String category,
			@RequestParam(required = false, value="count") String count,
			@RequestParam(required = false, value="id") String id,
//			@RequestHeader Map<String, String> headers,
			HttpServletRequest request, HttpServletResponse response ) throws Exception {
//		System.out.println("==#== " + flag);
//		System.out.println("==#====== " + start);
//		headers.forEach((key, value) -> {
//			System.out.println(String.format("==# Header %s = %s", key, value));
//	    });
		
		String referer = request.getHeader("referer");
		if ( referer == null ) {
			return;
		}
		
		boolean flag = true;
		for( String s : referSet ) {
			if ( referer.contains(s) ) {
				flag = false;
				break;
			}
		}
		
		if ( flag == true ) {
			log.warn("WARNING : disable download file : " + referer);
			return;
		}
		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] all =  ffmpegService.cutAudio(diskService.strToCategory(category), fileNm, start, end, count, id);
		
//		byte []all = Files.readAllBytes(Paths.get(UPLOAD_PATH+File.separator+fileNm));
//		byte[] all = out.toByteArray();
		
		response.setContentLength(all.length);
		// forces download
		response.setHeader("Content-Type", CommonUtil.getMimeType(fileNm));
		response.setHeader("Accept-Ranges", "bytes");
		response.getOutputStream().write(all);
		response.flushBuffer();
	}	
	
	
	@RequestMapping(value= {"/downloadWave"})
	public void downloadWave(Model model, 
			@RequestParam(value="fileNm") String fileNm,
			@RequestParam(value="start") float start,
			@RequestParam(value="end") float end,
			@RequestParam(value="nextStart") float nextStart,
			@RequestParam(value="prevEnd") float prevEnd,
			@RequestParam(value="category") String category,
//			@RequestHeader Map<String, String> headers,
			HttpServletRequest request, HttpServletResponse response ) throws Exception {
//		System.out.println("==#== " + flag);
//		System.out.println("==#====== " + start);
//		headers.forEach((key, value) -> {
//			System.out.println(String.format("==# Header %s = %s", key, value));
//	    });
		
		String referer = request.getHeader("referer");
		if ( referer == null ) {
			return;
		}
		
		boolean flag = true;
		for( String s : referSet ) {
			if ( referer.contains(s) ) {
				flag = false;
				break;
			}
		}
		
		if ( flag == true ) {
			log.warn("WARNING : disable download spectrum image : " + referer);
			return;
		}
		
		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		byte[] all =  ffmpegService.cutAudio(category, fileNm, start, end);
		
//		byte []all = Files.readAllBytes(Paths.get(UPLOAD_PATH+File.separator+fileNm));
//		byte[] all = out.toByteArray();
		
		
		ServletOutputStream outputStream = response.getOutputStream();
		
		ffmpegService.writeWave(outputStream, diskService.strToCategory(category), fileNm, start, end, prevEnd, nextStart);
		
//		response.setContentLength(all.length);
		// forces download
		response.setHeader("Content-Type", "image/png");
		response.setHeader("Accept-Ranges", "bytes");
//		response.getOutputStream().write(all);
		response.flushBuffer();
	}
	
    @GetMapping(value= {"/cont/transListPopUp"})
    public String transListPopUp(HttpServletRequest request, 
			Model model,
			@Param("search") DefaultVo search,
			@Param("pageable")  @PageableDefault(size = 20, page = 1) Pageable pageable,
			@RequestParam(required = false) Integer seq,
			@RequestParam(required = false) Integer reliability,
			@RequestParam(required = false) Integer waveValue,
			@RequestParam(required = false) Integer changeTextValue,
			@RequestParam(required = false) String suffix) throws Exception {
		
    	if (seq == null) {
    		throw new RuntimeException("SEQ Null");
    	}
    	
		setSearchTerm(search);
		OffsetPageable offsetPageable = new OffsetPageable(pageable);
		double lFirstLimit = -1;
		double lLastLimit = -1;
		double rFirstLimit = -1;
		double rLastLimit = -1;
		
		if ( reliability == null ) {
			reliability = 100;
		}

		Map<String, Object> param = generateRequestParam("seq", seq);
		
		List<SoundVo> pages = menu23Service.getListData(param);
		if ( pages.size() != 1 ) {
			throw new RuntimeException("no matching data found : " + seq);
		}
		
		SoundVo soundVo = pages.get(0);		
		if ( soundVo.getChannelCount() == 2 ) {
			soundVo.setFileOrgNm( soundVo.getFileStereoPrefix() );
		}
		
		menu23Service.clickLeastOnce(seq);
		model.addAttribute("vo", soundVo);

		log.debug("changedTextValue = {}", changeTextValue);
		
		addRequestParam(param, "reliability, offsetPageable,changeTextValue, meetSeq", 
						reliability, offsetPageable,changeTextValue, soundVo.getSeq());
				
		List<TranscriptionVo> list = menu23Service.getTranscriptionList(param);

		int hiddenCount = list.size();
		
		log.debug("hiddenCount = {}, pages = {}", hiddenCount, pages.size());
		
		List<TranscriptionVo> listR = null ;
		List<TranscriptionVo> listF = null;
	
		int  indexLast = 0 ;
		int  indexFirst = 0 ;
		
		if ( 0 < hiddenCount )
		{
			int hiddenCountlist = list.get(0).getHiddenCount();
			int checklen = (int) (offsetPageable.getOffset()+ offsetPageable.getPageSize());

		
			if(offsetPageable.getOffset() == 0) {
				indexFirst = 0;
				indexLast = (int) offsetPageable.getPageSize();
				listR = null;
			
			
				if(hiddenCountlist <= checklen) {
					param.put("listF", hiddenCountlist-1);
				}else {
					param.put("listF", offsetPageable.getPageSize());	
				}
			
				listF = menu23Service.getTranscriptionF(param);			

			}else {
				indexFirst = offsetPageable.getOffset()-1;
				indexLast = (int) (offsetPageable.getOffset()+ offsetPageable.getPageSize());			
				param.put("listR", indexFirst);
			
				if(hiddenCountlist <= checklen) {
					param.put("listF", hiddenCountlist-1);
				}else {
					param.put("listF", indexLast);	
				}
						
			
				listR = menu23Service.getTranscriptionR(param);
				listF = menu23Service.getTranscriptionF(param);
			
			}
		
				
			String firstChannel = "";
			String lastChannel = "";
			double timeDuration = Math.round( Double.parseDouble(soundVo.getTimeDurationMs()) / 1000 * 100 ) / 100.0;
			if ( hiddenCount > 0 ) {
				hiddenCount = Integer.parseInt(String.valueOf( ((TranscriptionVo)list.get(0)).getHiddenCount() ));
				firstChannel = list.get(0).getChannelChar();
				lastChannel = list.get(list.size()-1).getChannelChar();
			
				if ( "L".equals(firstChannel) ) {
					if ( pageable.getPageNumber() == 1 ) {   // 1 page
						lFirstLimit = 0D;
					} else {
						lFirstLimit = listR.get(0).getStart();
					} 
				
					if ( "R".equals(lastChannel)) {
						lLastLimit = timeDuration;
						rFirstLimit = 0D;						
						if ( pageable.getPageNumber() - 1 == (int) (hiddenCount / offsetPageable.getPageSize()) ) {  // last page
							rLastLimit = timeDuration;
						} else {
							rLastLimit = listF.get(0).getEnd();
						}
					} else {
						if ( pageable.getPageNumber() - 1 == (int) (hiddenCount / offsetPageable.getPageSize()) ) {  // last page
							lLastLimit = timeDuration;
						} else {
							lLastLimit = listF.get(0).getEnd();
						}
					}
				} else {
					if ( pageable.getPageNumber() == 1 ) {   // 1 page
						rFirstLimit = 0D;
					} else {
						rFirstLimit = listR.get(0).getStart();
					}
				
					if ( pageable.getPageNumber() - 1 == (int) (hiddenCount / offsetPageable.getPageSize()) ) {  // last page
						rLastLimit = timeDuration;
					} else {
						rLastLimit = listF.get(list.size()-1).getEnd();
					}
				}
			}
		}

		Page<?> cPages = new PageImpl<>(list, pageable, hiddenCount);
		
		log.debug("cPages.isEmpty = {}, pageable.getPageNumber() = {}", 
					cPages.isEmpty(), pageable.getPageNumber());
		
		model.addAttribute("listR", listR);	
		model.addAttribute("listF", listF);	
		model.addAttribute("indexFirst", indexFirst);	
		model.addAttribute("indexLast", indexLast);	
		
		model.addAttribute("cPages", cPages);
			
		model.addAttribute("hiddenCount", hiddenCount);
		
		model.addAttribute("search", search);
		model.addAttribute("menuId", menuId);
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
		
		int id = menu23Service.split(map);
		
		return Integer.toString(id);
	}
	
	@PostMapping(value= {"/cont/combine", "/cont/1/combine"},  produces = {"application/json"})
	@ResponseBody
	public String combine(Model model, @RequestBody Map<String, String> map) throws Exception {
		
		int id = menu23Service.combine(map);
		
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
    	
    	if (seq == null) {
    		throw new RuntimeException("SEQ Null");
    	}
    	long start = System.currentTimeMillis();
    	
    	setSearchTerm(search);
    	OffsetPageable offsetPageable = new OffsetPageable(pageable);
    	
    	if ( reliability == null ) {
    		reliability = 100;
    	}
    	
    	Map<String, Object> param = generateRequestParam("seq", seq);
    	
		List<SoundVo> pages = menu23Service.getListData(param);
		if ( pages.size() != 1 ) {
			throw new RuntimeException("no matching data found : " + seq);
		}
		SoundVo soundVo = pages.get(0);

		addRequestParam(param, "reliability, offsetPageable,changeTextValue, meetSeq", 
				reliability, offsetPageable,changeTextValue, soundVo.getSeq());
		List<TranscriptionVo> list = menu23Service.getTranscriptionList(param);
		
		int hiddenCount = list.size();
		
		for( int i = 0; i < hiddenCount; i++ ) {
			TranscriptionVo vo = list.get(i);
			if ( i == 0 ) {
				vo.setPrevEnd(0D);
			} else  {
				if ( list.get(i-1).getChannelId() != vo.getChannelId() ) {
					vo.setPrevEnd(0D);
				} else {
					vo.setPrevEnd( list.get(i-1).getEnd());
				}
			}
			
			if ( i == hiddenCount - 1 ) {
				vo.setNextStart( vo.getEnd() );
			} else {
				if ( vo.getChannelId() != list.get(i+1).getChannelId() ) {
					vo.setNextStart( vo.getEnd() );
				} else {
					vo.setNextStart( list.get(i+1).getStart() );
				}
			}
			
		}
		
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;
		log.debug("appendWaveSpectrum : {}", timeElapsed);
    	
		return ffmpegService.appendWaveSpectrum(diskService.strToCategory(category), soundVo, list);
    }   
    
    @GetMapping(value= {"/cont/replaceWaveSpectrum"}, produces = {"application/json"})
    @ResponseBody
    public List<Map<String, Object>> replaceWaveSpectrum(HttpServletRequest request, 
    		Model model,
    		@Param("search") DefaultVo search,
    		@Param("pageable")  @PageableDefault(size = 20, page = 1) Pageable pageable,
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
    	
//    	Map<String,Object> meetMap = menu23Service.getMeetBySEQ(hiddenSeq);
    	Map<String, Object> param = generateRequestParam("seq", hiddenSeq);
    	List<SoundVo> pages = menu23Service.getListData(param);
    	
    	List<TranscriptionExt> list = new ArrayList<>();
    	TranscriptionExt vo = new TranscriptionExt();
    	
    	int channelId = "L".equals(channelChar) ? 0 : 1;
    	
    	int size = seq.length;
    	if ( size == 1 ) {
    		vo.setSeq(seq[0]);
    		vo.setStart(start[0]);
    		vo.setEnd(end[0]);
    		vo.setPrevEnd(prevEnd);
    		vo.setNextStart(nextStart);
    		vo.setChannelId(channelId);
    		list.add(vo);
    	} else if ( size == 2 ) {
    		vo.setSeq(seq[0]);
    		vo.setStart(start[0]);
    		vo.setEnd(end[0]);
    		vo.setPrevEnd(prevEnd);
    		vo.setNextStart(start[1]);
    		vo.setChannelId(channelId);
    		list.add(vo);
    		
    		vo = new TranscriptionExt();
    		vo.setSeq(seq[1]);
    		vo.setStart(start[1]);
    		vo.setEnd(end[1]);
    		vo.setPrevEnd(end[0]);
    		vo.setNextStart(nextStart);
    		vo.setChannelId(channelId);
    		list.add(vo);
    	} else {
    		throw new RuntimeException("array parameter should be 1 or 2");
    	}
    	
    	return ffmpegService.replaceWaveSpectrum(diskService.strToCategory(category), pages.get(0), list);
    }  
	
	@PostMapping(value= {"/cont/resetFrame", "/cont/1/resetFrame"},  produces = {"application/json"})
	@ResponseBody
	public String resetFrame(Model model, @RequestBody Map<String, String> map) throws Exception {
		
		int id = menu23Service.resetFrame(map);
		
		return Integer.toString(id);
	}
	
	
	
	@PostMapping(value= {"/cont/upload", "/cont/1/upload"})
	@ResponseBody
	public ResponseEntity<String> upload(Model model, 
						MultipartFile file, 
						Principal principal) throws Exception {

		if(file == null || file.isEmpty()) {
			throw new RuntimeException();
		}
		
		// 1. save uploaded file or convert file
		Map<String, Object> param = commonService.saveUploadFileConvert(Category.TRAIN, file);
//		Resource resource = (Resource) param.get("resource");
		org.springframework.core.io.Resource resource = (org.springframework.core.io.Resource) param.get("resource");

		
		// 2. call stt
//		Resource resource = file.getResource();
		Map<String, Object> resultMap = sttService.callSttForMenu(resource, false, null);
		
//		soundService.adjustTimestamp(resource, resultMap, (String)param.get("newnm"), null);
		
		BigDecimal bigDecimal = new BigDecimal(resultMap.get("duration").toString());
		BigDecimal durationMs = bigDecimal.multiply(new BigDecimal(1000));

		int tcUserSeq = Integer.parseInt(principal.getName());
		
		param.put("timeDurationStr", durationMs.toString());
		param.put("tcUserSeq", tcUserSeq);
		
		param.put("division", "00");
		
		String meetSeq = menu23Service.successSoundAndStt(param, resultMap);
		
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
		
		// 2. call summary
		Map<String, String> map = sttService.parseDiarizeAndSttForMenu(param, resultMap);

		return ResponseEntity.ok(map);
	}
	
	@PostMapping(value= {"/cont/toTest", "/cont/1/toTest"})
	@ResponseBody
	public String toTest(Model model, @RequestBody Map<String, String>[] array) throws Exception {
		String result = "0";
		
		
		
		for ( Map<String, String> map : array ) {
			int seq = Integer.parseInt(map.get("seq").toString());
			Map<String, Object> param = generateRequestParam("seq", seq);
			
			List<SoundVo> trainMap = menu23Service.getListData(param);
			if (trainMap == null || trainMap.isEmpty() ) {
				result = "테스트 Data로 전송할 데이타가 없습니다.";
				break;
			}
						
			commonDataService.trainCopyTestAndTestTrans(seq, trainMap.get(0));
		}
		
		return result;
	}	
	
	
}

