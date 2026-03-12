package com.vol.solunote.model.vo.server;

import com.vol.solunote.comm.vo.DefaultVo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainVo extends DefaultVo {
	private int seq;
	private int serverSeq;
	private String domainCode;
	private String domainName;
	private String domainExplain;
	private int userSeq;
	private String userId;
	private String userName;
	private String useYn;
	private String regDate;	
}
