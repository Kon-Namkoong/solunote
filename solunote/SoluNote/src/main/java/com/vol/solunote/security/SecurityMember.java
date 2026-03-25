package com.vol.solunote.security;

import java.util.ArrayList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.vol.solunote.model.entity.siteuser.SiteUser;

public class SecurityMember extends User{

	private static final long serialVersionUID = 1L;
	
	private SiteUser member;
	
	private String ip;
	
	public SecurityMember() {
		super("-", "-", new ArrayList<GrantedAuthority>());
	}
	
	public SecurityMember(SiteUser member) {
		super(member.getUsername(), member.getPassword(), member.getAuthorities());
		this.member = member;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public SiteUser getMember() {
		return member;
	}

	public void setMember(SiteUser member) {
		this.member = member;
	}

}
