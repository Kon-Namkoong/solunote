package com.vol.solunote.comm.service.disk;

import java.io.File;
import java.util.List;
import java.nio.file.Path;

import com.vol.solunote.model.type.Category;
import com.vol.solunote.model.vo.comm.DirPathVo;


public interface DiskService {
		
	public	String getUploadPath(Category category) throws Exception;
	
	public	Path getUploadFilePath(Category category, String fileName) throws Exception;

	public	void copyFile(String string) throws Exception;

	public	File getUploadedFile(Category category, String path, String channelCount, String channelId) throws Exception;

	public	Category strToCategory(String text) throws Exception;
	
	public	String copyMeetFile(String target, String fromPath) throws Exception;
	
	public	String copyTrainFile(String fromPath) throws Exception;

	public List<DirPathVo> listDirectory(String category) throws Exception;
	
	public	boolean	hasDirectoryScanChar(String fileName);
	
	public	String	removeDirScanChar(String fileName);
		
}
