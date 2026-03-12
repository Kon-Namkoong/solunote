package com.vol.solunote.comm.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchVo {
	
	private String searchText="";
	private String searchStartDate="";
	private String searchEndDate="";
	
	private String useYn;
	private String candidate;

}