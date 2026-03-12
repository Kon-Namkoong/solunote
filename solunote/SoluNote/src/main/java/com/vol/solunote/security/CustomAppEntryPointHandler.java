package com.vol.solunote.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.thymeleaf.util.StringUtils;

public class CustomAppEntryPointHandler extends AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// TODO Auto-generated method stub
		 String requestType = request.getHeader("x-requested-with");
        if (!StringUtils.isEmpty(requestType)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print("{\"invalid_session\": true}");
            response.getWriter().flush();
        } else {
        	getRedirectStrategy().sendRedirect(request, response, "/user/login");
        }
	}

}