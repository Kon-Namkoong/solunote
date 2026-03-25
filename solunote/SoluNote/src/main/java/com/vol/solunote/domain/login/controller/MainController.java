package com.vol.solunote.domain.login.controller;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vol.solunote.security.vo.SecurityMember;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	
	
	@Value("${base.redirect.url}")
	String REDIRECT_URL;
	
	@RequestMapping({"/", "/main"})
	public String root(Model model) throws IOException {
//		response.getWriter().println("login controller");
		log.info("login root");
		String role = hasRole();
		
		if(role != null && !"".equals(role)) {
			if (role.equals("ROLE_ADMIN")) {
				return "redirect:"+REDIRECT_URL;
			}else {
				return "redirect:/minutes/cont/?activeMenu=1";
			}
		} else {
			return "redirect:/user/login";
		}
		
//		return "/";
	} 
	
	protected String hasRole() {
        // get security context from thread local
		
		String role = "";
		
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return role;

        Authentication authentication = context.getAuthentication();
        if (authentication == null)
            return role;
        
        role = ((SecurityMember) authentication.getPrincipal()).getMember().getMoveURL();
        
        /*
        for (GrantedAuthority auth : authentication.getAuthorities()) {
	        if(auth.getAuthority().length() == 12) { 
	        	role = auth.getAuthority();
	        	break;
	        }
	        
        }
        */

        return role;
    }

}
