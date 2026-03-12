package com.vol.solunote.model.vo.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestUserVo {
	private String uid;
	private String upwd;
	private boolean auth;
	private int seq ;
	private String tcId ;
	private String tcPwd ;
}

