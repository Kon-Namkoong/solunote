package com.vol.solunote.model.vo.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class STTResultVo {
	private int sttSeq;
	private int seq;
	private String callId;
	private String status;
	private String callUrl;
	
	private String regDate;
	private String regTime;
	
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private String fileId;
	private String fileName;
	private String duration;
	private String channel;
	private String frameSp;
	private String frameEp;
	private String sttResult;
}
