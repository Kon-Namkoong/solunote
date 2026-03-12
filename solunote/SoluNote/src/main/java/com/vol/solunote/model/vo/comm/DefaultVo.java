package com.vol.solunote.model.vo.comm;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultVo {
	
	private String isPaging = "Y"; 
	
	private int searchServerSeq= 0;
	private int searchDomainSeq= 0;
	private String searchDomainCode= "";
	
	private int searchType=0 ;
	private String searchText="";
	private String searchDivision="";
	

	private String searchStartDate="";
	private String searchEndDate="";
	
	private List<String> searchFilds;
	
	private String searchTermType;
	
	private String searchUseYn;
	
	public void setSearchUseYn(String value) {
		this.searchUseYn = value;
		if ( "undefined".equals(value) ) {
			this.searchUseYn = null;
		} else if ( value != null ) {
			this.searchUseYn = value.trim();
		}
	}
	
	public void setSearchText(String value) {
		this.searchText = value;
		if ( value != null ) {
			searchText = value.trim();
		}
	}

}
