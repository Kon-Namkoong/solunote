package com.vol.solunote.batch.train.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainCompareVo {
	
	private String dataId;
	
	private String testYn;
	private String useYn;
	private double duration;
	private String trainText;
	private String tUseYn;
	private double tDuration;
	private String tTrainText;
	private int tRelSeq;
	private String mode;
	
}
