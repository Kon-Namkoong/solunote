package com.vol.solunote.security.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.vol.solunote.repository.menu.MenuRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthorizationService {
		
	@Autowired
	private	MenuRepository menuRepository;

	public Map<String, Set<String>> menuMap = new HashMap<>();
	
	public Map<String, Set<String>> foundMap = new HashMap<>();
	
    private static	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 비밀번호 정규식
    // 최소 8자, 대문자, 소문자, 숫자, 특수문자 포함
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])" +      // 소문자
            "(?=.*[A-Z])" +       // 대문자
            "(?=.*\\d)" +         // 숫자
            "(?=.*[@$!%*?&])" +  // 특수문자
            "[A-Za-z\\d@$!%*?&]{8,}$";
    
    public static boolean isValidPassword(String password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }
    
    public static boolean isSameAsOldPassword(String newPassword, String oldPasswordHashed) {
        return passwordEncoder.matches(newPassword, oldPasswordHashed);
    }
    	
	@PostConstruct
	public void init() throws Exception {
		
		List<Map<String, Object>> list = menuRepository.readMenuAll();
		
		for( Map<String, Object> map : list ) {
			String role = (String)map.get("authorityName");
			String url = (String)map.get("url");
			
			addMenuMap(role, url);
			
			if ( "ROLE_USER".equals(role)) {
				addMenuMap("ROLE_ADMIN", url);
			}
		}
	}

	
	private void addMenuMap(String role, String url) {
		
		if ( url == null ) {
			return;
		}
		
		
		Set<String> set = null;
		
		if ( menuMap.containsKey(role) ) {
			set = menuMap.get(role);
		} else {
			set = new HashSet<>();
		}
		
		String[] urls = url.split("\\s*,\\s*");
		for( String s : urls ) {
			set.add( s );
		}
		
		menuMap.put(role, set);
	}

	public boolean checkAuth(String path, String role) {


		Set<String> foundSet = foundMap.get(role);
		if ( foundSet == null ) {
			foundSet = new HashSet<>();
			foundMap.put(role, foundSet);
		} else {
			if ( foundSet.contains(path) ) {
				return true;
			}
		}
		
		Set<String> menuSet = menuMap.get(role);
		for( String s : menuSet ) {
			boolean match = new AntPathMatcher().match(s, path);
			
			if ( match == true ) {
				foundSet.add(path);
				return true;
			}
			
		} 	
		return false;
	}
}