package com.vol.solunote.model.vo.menu;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuVo {
	
	private int seq;
	private int userSeq;
	private int pUserSeq;
	private String pMenuSeq;
	private String pMenuName;
	private int pRowCnt;
	
	private String menuLang;
	private String menuCode;
	private String menuName;
	private int menuParentSeq;
	private int menuNo;
	private int menuUseYn;
	private int menuDel;
	private String regDate;
	private int menuRight;
	private int downRight;	
}

