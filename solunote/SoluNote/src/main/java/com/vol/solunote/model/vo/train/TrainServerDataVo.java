package com.vol.solunote.model.vo.train;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true) 
public class TrainServerDataVo {
	
	private String dataId;
	
	@JsonProperty("project_id")
	private String projectId;
	
	@JsonProperty("transcript")
	private String trainText;
	
	private double duration;
	
	@JsonProperty("test")
	private String testYn;
	
	@JsonProperty("useYN")
	private String useYn;
	
	private String mode;
//	private String created;
//	private String modified;
	
	public void setTestYn(String value) {
		if ("true".equals(value) || "Y".equals(value)  || "y".equals(value) ) {
			this.testYn = "Y";
		} else {
			this.testYn = "N";
		}
	}
	
	public void setUseYn(String value) {
		if ("true".equals(value) || "Y".equals(value)  || "y".equals(value) ) {
			this.useYn = "Y";
		} else {
			this.useYn = "N";
		}
	}

	public boolean getTestFlag() {
		return "Y".equals(this.testYn);
	}

	@Override
	public String toString() {
		return "TrainServerDataVo [dataId=" + dataId + ", projectId=" + projectId + ", trainText=" + trainText
				+ ", duration=" + duration + ", testYn=" + testYn + ", useYn=" + useYn + ", mode=" + mode + "]";
	}

	
}
