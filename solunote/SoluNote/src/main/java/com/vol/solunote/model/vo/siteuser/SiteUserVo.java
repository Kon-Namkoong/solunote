package com.vol.solunote.model.vo.siteuser;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteUserVo {
	private int seq;
	private String tcId;
	private String tcPw;
	private String tcName;
	private String tcNick;
	private String tcEmail;
	private String tcPhone;
	private int tcType;
	private int tcGroup;
	private int tcLevel;
	private short tcUseYn;
	private int tcLogin;
	private String tcRegDate; // 날짜 타입 추후 변경 예정	
	
	private Collection<? extends GrantedAuthority> authorities;
	
	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
}