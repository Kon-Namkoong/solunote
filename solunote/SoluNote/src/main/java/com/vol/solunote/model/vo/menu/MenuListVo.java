package com.vol.solunote.model.vo.menu;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuListVo {
	private int seq;

	private int menuCode;
	private String menuMain;
	private String menuSub;
	private int menuLevel;
	private int menuCount;

}

