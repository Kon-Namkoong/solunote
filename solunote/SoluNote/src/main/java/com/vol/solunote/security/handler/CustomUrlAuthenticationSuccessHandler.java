package com.vol.solunote.security.handler;

import java.io.IOException;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.vol.solunote.domain.login.service.LoginService;
import com.vol.solunote.security.vo.SecurityMember;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Component
public class CustomUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    
    public final Integer SESSION_TIMEOUT_IN_SECONDS = 60 * 60 * 5;
    
	@Value("${base.lang:ko}")
	private String baseLang;

	@Autowired
    private  LoginService loginLogService;

    public CustomUrlAuthenticationSuccessHandler(LoginService loginLogService) {
        this.loginLogService = loginLogService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

    	request.getSession().setMaxInactiveInterval(SESSION_TIMEOUT_IN_SECONDS);
    	
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
        }

        String accept = request.getHeader("accept");

        SecurityMember securityUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof UserDetails) {
                securityUser = (SecurityMember) principal;
            }
        }
        
        String userId = securityUser == null ? "null" : securityUser.getMember().getUserId(); 
        request.getSession(true).setAttribute("userId", userId);

        request.getSession(true).setAttribute("locale", baseLang);
        Locale locale = new Locale(baseLang);
        request.getSession(true).setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        
        // 일반 응답일 경우
        if (accept == null || accept.matches(".*application/json.*") == false) {

            request.getSession(true).setAttribute("loginNow", true);
            getRedirectStrategy().sendRedirect(request, response, "/user/login");  
            // 메인으로 돌아가! 
            // 이전페이지로 돌아가기 위해서는 인증페이지로 가기 전 URL을 기억해 놓았다가  
            return;
        }

        if(securityUser != null){
            // was 는 로드배런서 이용시 방화벽 내부에있고 실제 호출은 로드밸런서에서해서 remoteAddr 호출하면 로드밸런서의 ip가 호출됨
            // 로드밸런서 이용시 헤더값에 실제 클라이언트 ip가 담기니 해당 헤더가 없다면 remoteAddr 호출
            String ip = request.getHeader("X-FORWARDED-FOR");
            String username = request.getParameter("username");
            if (ip == null)
                ip = request.getRemoteAddr();
            int seq = Integer.parseInt(securityUser.getMember().getUsername());

            loginLogService.createLoginLog(ip, seq, username);
        }

        // application/json(ajax) 요청일 경우 아래의 처리!
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        MediaType jsonMimeType = MediaType.APPLICATION_JSON;

        JSONResult jsonResult = JSONResult.success(securityUser); 
        if (jsonConverter.canWrite(jsonResult.getClass(), jsonMimeType)) {
            jsonConverter.write(jsonResult, jsonMimeType, new ServletServerHttpResponse(response));
        }
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}