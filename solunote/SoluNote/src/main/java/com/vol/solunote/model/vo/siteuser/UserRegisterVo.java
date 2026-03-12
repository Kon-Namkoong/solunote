package com.vol.solunote.model.vo.siteuser;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserRegisterVo {
	private int seq;
	private String tcId;
	private String tcPw;
	private String tcName;
	private String tcPhone;
	private String tcEmail;
	private int tcLevel;
	private LocalDateTime tcRegdate;
	private int hiddenCount;
	
}