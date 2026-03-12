package com.vol.solunote.model.vo.transcription;

import com.vol.solunote.model.vo.comm.DataVo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TransVo extends DataVo {

	private int seq;
	private String subject;
	private double start;
	private double end;
	private short reliability;
	private int channelCount;
	private int channelId;
	private String trainText;
	private String sttText;
	private String ttsText;
	private String keywordText;
	private int testSeq;
	private int soundSeq;
	private int ttsSeq;
	private String createdAt;
	private String updatedAt;
	private String useYn;
	private String candidate;
	private String dataId;
	private String timeDurationMs;
	private String fileNewNm;
	private String fileOrgNm;
	private String fileStereoPrefix;
	private String division;
	private String armyName;
	
	private String prDataId;
	private int prSeq;
	private String prTrainText;
	private String prUseYn;
	private double prStart;
	private double prEnd;
	
	public String toString() {
		return seq + "|" + prSeq + "|" + candidate + "|" + sttText + "|" + trainText + "|" + prTrainText + "|" + dataId + "|" + prDataId + "|" + useYn + "|" + prUseYn;
	}
	public static String toStringHeader() {
//		return seq + "|" + prSeq + "|" + candidate + "|" + sttText + "|" + trainText + "|" + prTrainText + "|" + dataId + "|" + prDataId + "|" + useYn + "|" + prUseYn;
		return "seq"+"|" +"prSe" + "|" +"candidat" + "|" +"sttTex" + "|" +"trainTex" + "|" +"prTrainTex" + "|" +"dataI" + "|" +"prDataI" + "|" +"useY" + "|" +"prUseYn";

	}

}
