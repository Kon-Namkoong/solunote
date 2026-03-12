package com.vol.solunote.model.vo.server;

import com.vol.solunote.model.vo.comm.DefaultVo;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class ServerVo extends DefaultVo {
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
