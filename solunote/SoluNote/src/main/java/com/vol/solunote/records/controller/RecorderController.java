package com.vol.solunote.records.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.records.service.RecorderService;

import lombok.extern.slf4j.Slf4j;

/**
 * 레코드  RecordController Class
 * 
 * @author 윤기정
 * @since 2021.03.15
 * @version 0.1
 * @brief base64 인코딩 음성 버퍼 -> wav 
 * sample code 입니다.   
 */

@Controller
@Slf4j
@RequestMapping("/RECORD") 
public class RecorderController extends DefaultController {
	
	@Autowired
	RecorderService recorderService;
	
	RecorderController () {
		this.menuId="record";
	}
	
	@RequestMapping({"/recorde", "/recorde/{idx}"})
	public String recorder(Model model, @PathVariable(value="idx",required = false) String idx,  @RequestParam Map<String, String> dataMap
			, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		if(idx == null || "".equals(idx) ) idx = "1";
		
		model.addAttribute("idx", idx);
		return "thymeleaf/"+menuId+"/record";
	}
	
	@RequestMapping(value= {"/get_stt_result","/get_stt_result/{idx}"})
	@ResponseBody
	public String getSttReuslt(HttpServletRequest request, @RequestParam("sttSeq") String setSeq) throws Exception {
		
		return recorderService.getSTTResult(Integer.parseInt(setSeq ));		
	} 
	

	@RequestMapping({"/test"})
	public String text(Model model, @RequestParam Map<String, String> dataMap
			, HttpServletRequest request, HttpServletResponse response) throws Exception{

		return "thymeleaf/"+menuId+"/test";
	}
	
	@RequestMapping({"/recorde_real", "/recorde_real/{idx}"})
	public String recorder_real(Model model, @PathVariable(value="idx",required = false) String idx,  @RequestParam Map<String, String> dataMap
			, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		if(idx == null || "".equals(idx) ) idx = "1";
		
		model.addAttribute("idx", idx);
		return "thymeleaf/"+menuId+"/record_real";
	}
	
	// 음성 녹음 데이터 전송
	@ResponseBody
	@PostMapping(value="/doRecord", produces="text/plain;charset=UTF-8")
	public String doRecord(@RequestParam Map<String, String> dataMap, HttpServletRequest request, HttpServletResponse response) throws Exception{
		// cross origin policy (필요시 추가)
		/* 
		response.setHeader("Access-Control-Allow-Origin","*");
	    response.setHeader("Access-Control-Allow-headers", "x-requested-with");
	    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Max-Age", "3600");
	    */
		JSONObject rsJson = recorderService.doRecord(dataMap);

		return rsJson.toString();
	} 
	
}

