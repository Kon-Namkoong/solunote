package com.vol.solunote.comm.service;

import java.io.File;
import java.util.List;
import com.vol.solunote.comm.model.Category;
import com.vol.solunote.comm.vo.DirPathVo;


public interface DiskService {
		
	String getUploadPath(Category category) throws Exception;

	void copyFile(String string) throws Exception;

	File getUploadedFile(Category category, String path, String channelCount, String channelId) throws Exception;

	Category strToCategory(String text) throws Exception;
	
	public	String copyMeetFile(String target, String fromPath) throws Exception;
	
	public	String copyTrainFile(String fromPath) throws Exception;

	public List<DirPathVo> listDirectory(String category) throws Exception;
		
}
