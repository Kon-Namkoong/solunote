package com.vol.solunote.model.vo.meeting;

import java.time.LocalDateTime;

import jakarta.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingVo {

	private int seq;
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
	private String remark;
	private String fileSizeBytes;
	private int tcUserSeq;
	private String status;
	private Integer meetingSeq;
	private String uploadType;
	private String lang;
	
	@Transient
	private String tcName;
	@Transient
	private int hiddenCount;
	
	private String errorMessage;

	@Transient
	private  LocalDateTime errorFirstAt;
	
	@Transient
	private LocalDateTime sttStartedAt;
	@Transient
	private String sttDuration;
	@Transient
	private int reliability;
	
	@Transient
	private String timeDurationFormatted;
	
	@Override
	public String toString() {
		return "MeetingVo [seq=" + seq + ", subject=" + subject + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + ", deletedAt=" + deletedAt + ", trashedAt=" + trashedAt + ", timeDurationMs="
				+ timeDurationMs + ", fileOrgNm=" + fileOrgNm + ", fileNewNm=" + fileNewNm + ", remark=" + remark
				+ ", fileSizeBytes=" + fileSizeBytes + ", tcUserSeq=" + tcUserSeq + ", status=" + status+ ", lang=" + lang
				+ ", meetingSeq=" + meetingSeq + "]";
	}
	

}
