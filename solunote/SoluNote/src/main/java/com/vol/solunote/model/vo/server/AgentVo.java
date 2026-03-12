package com.vol.solunote.model.vo.server;

import com.vol.solunote.model.vo.comm.DefaultVo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentVo extends DefaultVo {
	private int seq;
	private String agentId;
	private String agentName;
	private String agentExtno;
	private String agentIp;
	private String agentEmail;
	private String agentPhoneno;
	private String regDate;
	private String upDate;	
}
