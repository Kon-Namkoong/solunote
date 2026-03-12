package com.vol.solunote.model.vo.train;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties({"_id"})

public class TrainResultVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String modelId;
	
	@JsonProperty("data_id")
	private String dataId;
	
	@JsonProperty("CER")
	private double cer;
	
	@JsonProperty("WER")
	private double wer;
	
	@JsonProperty("entire_char")
	private int entireChar;
	
	@JsonProperty("entire_word")
	private int entireWord;
	
	@JsonProperty("error_char")
	private int errorChar;	
	
	@JsonProperty("error_word")
	private int errorWord;
	
	@JsonProperty("answer")
	private String trainText;
	
	@JsonProperty("stt_result")
	private String sttText;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	@Override
	public String toString() {
		return "TrainResultVo [modelId=" + modelId + ", dataId=" + dataId + ", cer=" + cer + ", wer=" + wer
				+ ", entireChar=" + entireChar + ", entireWord=" + entireWord + ", errorChar=" + errorChar
				+ ", errorWord=" + errorWord + ", trainText=" + trainText + ", sttText=" + sttText + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}


}
