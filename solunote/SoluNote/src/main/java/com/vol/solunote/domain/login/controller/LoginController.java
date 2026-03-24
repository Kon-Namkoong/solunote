package com.vol.solunote.domain.login.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/user")
public class LoginController {
	
	@GetMapping("/login")
	public String login(Model model, Map<String,Object> param) throws IOException {
//		response.getWriter().println("login controller");
		log.info("login processs11 {}", param.toString());
		model.addAttribute("timestamp", param.get("timestamp"));
		model.addAttribute("exception", param.get("exception"));
		return "thymeleaf/login/login";
	}

	@GetMapping("/login2")
	public String login2(Model model, Map<String,Object> param) throws IOException {
//		response.getWriter().println("login controller");
		log.info("login processs11 {}", param.toString());
		model.addAttribute("timestamp", param.get("timestamp"));
		model.addAttribute("exception", param.get("exception"));
		return "thymeleaf/login/login2";
	}

	/*
	 *  미사용코드 제거 필요
	 */
	//@RequestMapping("/loginProcess")
	public String loginprocess(Model model) throws IOException {
		//response.getWriter().println("login controller");
		log.info("loginprocess processs222");

		return "menu18/cont/1";
	} 
	
	
//	@RequestMapping("/result")
//	@ResponseBody
//	public String result(Model model) throws IOException {
//		return "testgse4";
//	}
//	
	@GetMapping("/denied")
	public String denied(Model model, Authentication auth, HttpServletRequest req){
		AccessDeniedException ade = (AccessDeniedException) req.getAttribute(WebAttributes.ACCESS_DENIED_403);
		log.info("ex : {}",ade);
//		model.addAttribute("auth", auth);
//		model.addAttribute("errMsg", ade);
//		return "/user/denied";
		
        model.addAttribute("code", "403");
        model.addAttribute("status", "Access Denied");
        model.addAttribute("message", "해당 페이지 접속 권한이 없습니다");
        
        return "thymeleaf/errors/error.html";
	}


}
