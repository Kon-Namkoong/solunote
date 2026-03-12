package com.vol.solunote.model.vo.comm;

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