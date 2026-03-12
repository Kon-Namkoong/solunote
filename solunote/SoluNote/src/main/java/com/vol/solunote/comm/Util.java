package com.vol.solunote.comm;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.vol.solunote.security.SecurityMember;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {
	
	public static int getSessionUserSeq() {
		
		int seq = -1;
		
		SecurityContext context = SecurityContextHolder.getContext();
        if (context != null ) {
        	Authentication authentication = context.getAuthentication();
        	if (authentication != null) {
//        		String role = ((SecurityMember) authentication.getPrincipal()).getMember().getMoveURL();  // ROLE_ADMIN
//        		String tcId =((SecurityMember) authentication.getPrincipal()).getMember().getUserId();    // yosikim
        		String username = ((SecurityMember) authentication.getPrincipal()).getMember().getUsername();  // 50
        		if ( username != null ) {
        			seq = Integer.parseInt(username);
        		}
        	}
        }
        
        return seq;
		
//		log.debug("getUserName : {}");
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//		Object principalObj = authentication.getPrincipal();
//        if (!(principalObj instanceof User)) {
////            return "0";
//            return "1";
//        }
//
//        String username = authentication.getName();
//		if("anonymousUser".equals(username)) username = "";
//		
//		log.debug("current username : {}", username);
//		return username;

	}

	public static String getSessionUserLevel() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		SecurityMember securityMember = (SecurityMember) authentication.getPrincipal();
		
		return securityMember.getMember().getUserLevel();
	}
	
	public static SecurityMember getSessionSecurityMember() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (SecurityMember) authentication.getPrincipal();
	}
	
	   
   public static HttpSession getSession() {
      ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
      return (HttpSession) attr.getRequest().getSession();
   }

   public static String getSessionAttr(String key) {
      return getSession().getAttribute(key).toString();
   }
	
}
