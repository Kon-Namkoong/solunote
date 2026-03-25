package com.vol.solunote.security.service;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import com.vol.solunote.repository.menu.MenuRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthorizationService {
		
	@Autowired
	private	MenuRepository menuRepository;

	public Map<String, Set<String>> menuMap = new HashMap<>();
	
	public Map<String, Set<String>> foundMap = new HashMap<>();
	
	
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