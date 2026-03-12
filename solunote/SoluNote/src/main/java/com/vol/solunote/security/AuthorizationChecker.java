package com.vol.solunote.security;

import java.util.Collection;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthorizationChecker {
	
    @Autowired
    private AuthorizationService authorizationService;
 
    public boolean check(HttpServletRequest request, Authentication authentication) {
        Object principalObj = authentication.getPrincipal();
 
        log.debug("url-1 : [{}]", request.getRequestURI());
        
        if (!(principalObj instanceof User)) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = ((org.springframework.security.core.userdetails.UserDetails)principalObj).getAuthorities();
//        for( GrantedAuthority a : authorities ) {
//        	log.debug("url-1.au : [{}]", a.getAuthority());
//        }
        
        if ( authorities.size() == 0 ) {
        	log.error("auth size = 0");
        	return false;
        }
        
        return authorizationService.checkAuth( request.getServletPath(), authorities.iterator().next().getAuthority());
    }
    
    public boolean check(HttpServletRequest request, Authentication authentication, String menuId) {
        log.debug("url-2 : {}", request.getRequestURI());
        log.debug("menuId2 : {}", menuId);
    	return check(request, authentication, menuId, "1");
    }    
    
    public boolean check(HttpServletRequest request, Authentication authentication, String menuId, String menuSubId) {
   	 
        log.debug("url-3 : {}", request.getRequestURI());
        log.debug("menuId : {} {}", menuId, menuSubId);
        
        if(!check(request, authentication)) return false;
        
        return true;
   }

}

