package com.vol.solunote.model.vo.train;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int seq;
	private int scheduleSeq;
	private String name;
	private String startTime;
	private String modelId;
	private int dataCnt;
	private LocalDateTime requestedAt;
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private String duration;
	private String status;
	private boolean isSuccess;
	private double cer;
	private double wer;
	private String detail;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private int hiddenCount;
	
	private int color;
	private String successTrueFalse;
	
	public String getStrStatus() {
		String text = null;
		
        switch( status!= null ? status: "NULL" ) {
        case "10" : text = "학습요청"; break;
        case "15" : text = "취소"; break; 
        case "20" : text =  "학습중"; break;
        case "30" : text = "학습완료"; break;
        default : text = "UNDEFINED"; break;
        }

        return text;
	}
}

