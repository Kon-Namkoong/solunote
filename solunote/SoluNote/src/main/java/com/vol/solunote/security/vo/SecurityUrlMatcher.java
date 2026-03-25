package com.vol.solunote.security.vo;

import lombok.Data;

@Data
public class SecurityUrlMatcher {

	private String role;
	private String url;
	
	public SecurityUrlMatcher(String role, String url) {
		this.role = role;
		this.url = url;
	}
}
