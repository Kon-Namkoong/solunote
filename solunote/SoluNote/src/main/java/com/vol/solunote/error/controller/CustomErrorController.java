package com.vol.solunote.error.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {
//	private String VIEW_PATH = "/thymeleaf/errors/";

	@RequestMapping(value = "/error")
	public String handleError(HttpServletRequest request, Model model) {
        
//        Enumeration<String> en = request.getAttributeNames();
//        while( en.hasMoreElements() ) {
//        	String name = en.nextElement();
//        	Object value = request.getAttribute(name);
//        	log.debug("request : " + name + " = " + value);
//        }
		Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.valueOf(status);
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if ( errorMessage == null ) {
        	errorMessage = "";
        }
        
        model.addAttribute("code", status);
        model.addAttribute("status", httpStatus.getReasonPhrase());
        model.addAttribute("message", errorMessage);
        
        return "thymeleaf/errors/error.html";
    }
	
//	public String handleError(HttpServletRequest request) {
//		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//		log.error("aaaaaa==="+RequestDispatcher.ERROR_STATUS_CODE);
//		if (status != null) {
//			int statusCode = Integer.valueOf(status.toString());
//			if (statusCode == HttpStatus.NOT_FOUND.value()) {
//				return VIEW_PATH + "404";
//			}
//			if (statusCode == HttpStatus.FORBIDDEN.value()) {
//				return VIEW_PATH + "500";
//			}
//		}
//		log.error("aaaaaa=== aaaaaa");
//
////		return "error";
//		return "thymeleaf/errors/404.html";
////		return VIEW_PATH + "404";
//		
//		// 	return "thymeleaf/"+menuId+"/cont.html";
//	}

}

