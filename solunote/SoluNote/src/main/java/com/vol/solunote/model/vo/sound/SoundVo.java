package com.vol.solunote.model.vo.sound;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class SoundVo {
	
	private int seq;
	
	private String division;
	private String subject;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;
	private LocalDateTime trashedAt;
	private String timeDurationMs;
	private int channelCount;
	private String fileOrgNm;
	private String fileNewNm;
	private String fileConvNm;
	private String fileStereoPrefix;
	private int remark;
	private String fileSizeBytes;
	private int tcUserSeq;
	private String status;
	private LocalDateTime sttStartedAt;
	private String sttDuration;
	private int reliability;
	private int clickLeastOnce;
	private String errorMessage;
	
	private int hiddenCount;
	private int trainTextCount;
	private String timeDurationFormatted;
	

}

