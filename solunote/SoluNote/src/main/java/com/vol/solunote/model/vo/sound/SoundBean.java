package com.vol.solunote.model.vo.sound;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoundBean {
	
	private int seq;
	private String division ;
	private String subject;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private Timestamp deletedAt;
	private Timestamp trashedAt;
	private String timeDurationMs;
	private String fileOrgNm;
	private String fileNewNm;
	private String fileConvNm;
	private boolean remark;
	private String fileSizeBytes;
	private int tcUserSeq;
	private String status ;
	private Timestamp sttStartedAt ;
	private String sttDuration ;
	private int reliability ;
	private int clickLeastOnce ;
	private String errorMessage ;

	
	@Override
	public String toString() {
		return "SoundBean [seq=" + seq + ", subject=" + subject + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + ", deletedAt=" + deletedAt + ", trashedAt=" + trashedAt + ", timeDurationMs="
				+ timeDurationMs + ", fileOrgNm=" + fileOrgNm + ", fileNewNm=" + fileNewNm + ", fileConvNm="
				+ fileConvNm + ", remark=" + remark + ", fileSizeBytes=" + fileSizeBytes + ", tcUserSeq=" + tcUserSeq
				+ "]";
	}
	

	public static SoundBean fromArray(String[] ary) throws ParseException {
		
		SoundBean bean = new SoundBean();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SimpleDateFormat durFormat = new SimpleDateFormat("HH:mm:ss");
		
		int i = 0;
		bean.setSeq( Integer.parseInt(ary[i++]) );
		bean.setDivision(ary[i++]);
		bean.setSubject(ary[i++]);
		bean.setCreatedAt( new Timestamp( dateFormat.parse(ary[i++]).getTime() ) );
		bean.setUpdatedAt( new Timestamp( dateFormat.parse(ary[i++]).getTime() ) );
		bean.setDeletedAt( "NULL".equals(ary[i++]) ? null : new Timestamp( dateFormat.parse(ary[i-1]).getTime() ) );
		bean.setTrashedAt( "NULL".equals(ary[i++]) ? null : new Timestamp( dateFormat.parse(ary[i-1]).getTime() ) );
		bean.setTimeDurationMs(ary[i++] );
		bean.setFileOrgNm(ary[i++] );
		bean.setFileNewNm(ary[i++] );
		bean.setFileConvNm(ary[i++] );
		bean.setRemark(Boolean.parseBoolean(ary[i++]) );
		bean.setFileSizeBytes(ary[i++] );
		bean.setTcUserSeq( Integer.parseInt(ary[i++]) );
		bean.setStatus(ary[i++]);
		bean.setSttStartedAt(  "NULL".equals(ary[i++]) ? null : new Timestamp( dateFormat.parse(ary[i-1]).getTime() ) );
		bean.setSttDuration(ary[i++]);
		bean.setReliability(Integer.parseInt(ary[i++]));
		bean.setClickLeastOnce(Integer.parseInt(ary[i++]));
		bean.setErrorMessage(ary[i++]);


		
		return bean;
	}
	
	public String[] toStrArray() {
		String[] ary = new String[30];
		
		int i = 0;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		ary[i++] = Integer.toString(seq);
		ary[i++] =  division;
		ary[i++] = subject;
		ary[i++] = dateFormat.format(createdAt);
		ary[i++] = dateFormat.format(updatedAt);
		ary[i++] = deletedAt == null ? "NULL" : dateFormat.format(deletedAt);
		ary[i++] = trashedAt == null ? "NULL" : dateFormat.format(trashedAt);
		ary[i++] = timeDurationMs;
		ary[i++] = fileOrgNm;
		ary[i++] = fileNewNm;
		ary[i++] = fileConvNm;
		ary[i++] = Boolean.toString(remark);
		ary[i++] = fileSizeBytes;
		ary[i++] = Integer.toString(tcUserSeq);
		ary[i++] =  status;
		ary[i++] =  sttStartedAt == null ? "NULL" : dateFormat.format(sttStartedAt);
		ary[i++] =  sttDuration == null ? "NULL" : sttDuration;
		ary[i++] =  Integer.toString(reliability);
		ary[i++] =  Integer.toString(clickLeastOnce);
		ary[i++] =  errorMessage;

		
		return ary;
	}

	public static String[] toHeadArray() {
		String[] ary = {
				"seq",
				"division",
				"subject",
				"createdAt",
				"updatedAt",
				"deletedAt",
				"trashedAt",
				"timeDurationMs",
				"fileOrgNm",
				"fileNewNm",
				"fileConvNm",
				"remark",
				"fileSizeBytes",
				"tcUserSeq",
				"status",
				"sttStartedAt",
				"sttDuration",
				"reliability",
				"clickLeastOnce",
				"errorMessage",

		};
		
		return ary;
	}


	public String toCsvString() {
		
		String[] strArray = toStrArray();
		for( int i = 0; i < strArray.length; i++ ) {
			strArray[i] = "\"" + strArray[i] + "\"";
		}
		
		return String.join(",", 
				strArray
		);

		
	}	
}
