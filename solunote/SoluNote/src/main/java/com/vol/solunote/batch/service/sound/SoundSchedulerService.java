package com.vol.solunote.batch.service.sound;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SoundSchedulerService {

	public void setDeletedAt(int seq, String fileNm, String fileConvNm) throws Exception;
	
	public boolean deleteWaveFile(int seq, String fileNm, String fileConvNm) throws Exception;
	
	public void removeWavFile(Map<String, Object> map) throws Exception;
	
	public void readDirectory(String category) throws Exception, IOException ;

	public	List<Map<String, Object>>	getSettingConfigList()	throws Exception;
}
