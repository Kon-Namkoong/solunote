package com.vol.solunote.security;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.domain.login.service.LoginService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {
	
	@Autowired
    private LoginService loginService;
    
    public CustomLoginFailureHandler() {

    }
	 
    private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
//
		log.debug(exception.getMessage());
//		response.encodeRedirectURL("/user/login");
//		response.sendRedirect("/user/login?exception=PasswordError");
		
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
		
		String username = request.getParameter("username");
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        
        loginService.createLoginErrorLog(ip, username, exception.getMessage());
		
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", Calendar.getInstance().getTime());
        data.put("exception", exception.getMessage());
        data.put("result", "fail");
        data.put("httpStatus", HttpStatus.UNAUTHORIZED.value());

        response.setCharacterEncoding("UTF-8");
        response.getWriter()
          .println(objectMapper.writeValueAsString(data));
       
	}

}
