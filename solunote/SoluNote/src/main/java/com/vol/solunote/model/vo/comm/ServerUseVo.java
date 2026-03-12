package com.vol.solunote.model.vo.comm;

import java.text.DecimalFormat;

import com.vol.solunote.comm.util.CommUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerUseVo {
	private int serverSeq;
	private String workDate;
	private String workTime;
	
	private long hddFull ;
	private long hddFree ;
	
	private long ramFull ;
	private long ramFree ;
	
	private int cpuRatio;
	
	private String hddFullUnit;
	private String hddFreeUnit;
	
	
	public String getHddFullSize() {
		return new CommUtil().getFileSize(hddFull);
	}	
	
	public String getHddUseSize() {
		return new CommUtil().getFileSize(hddFull-hddFree);
	}
	
	public String[] getHddRate() {
		DecimalFormat df = new DecimalFormat("0.00"); 
		String rates[] = {df.format((double)hddFree / (double)hddFull * 100.0), df.format(((double)hddFull -(double)hddFree) / (double)hddFull * 100.0)}; // 출력값 : 12345.7
		return rates;
	}
	
	public String[] getHddFreeSize() {
		return  new CommUtil().getHddSize(hddFree);
	}
	
	public String getRamFullSize() {
		return new CommUtil().getFileSize(ramFull);
	}
	
	public String[] getRamFreeSize() {
		return new CommUtil().getHddSize(ramFree);
	}
	
	public String getRamUseSize() {
		return new CommUtil().getFileSize(ramFull-ramFree);
	}
	
	
	public String[] getRamRate() {
		DecimalFormat df = new DecimalFormat("0.00"); 
		String rates[] = {
				df.format((double)ramFree / (double)ramFull * 100.0)
				, df.format(((double)ramFull -(double)ramFree) / (double)ramFull * 100.0)
				}; // 출력값 : 12345.7
		return rates;
	}
	
}
