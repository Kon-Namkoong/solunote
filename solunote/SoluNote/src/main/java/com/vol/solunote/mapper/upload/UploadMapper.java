package com.vol.solunote.mapper.upload;

import java.util.List;
import java.util.Map;

import com.vol.solunote.comm.OffsetPageable;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadMapper {
	public	List<Map<String, Object>> getUploadDirList(OffsetPageable offsetPageable, Object object);
    public	void uploadDirRegister(String uploadDir, String uploadDirUseYn ,String uploadDirCategory);
    public 	int checkUploadDir(String uploadDir) throws Exception;
	public	List<Map<String, Object>>  uploadDirInfo(String uploadDir) throws Exception;
    public	void 	trainChangeToN();
    public	void	testChangeToN();
    public	void	changeToY(String uploadDirChange);
	public	String 	getLastRead(String category) throws Exception;		
	public	void 	createLastRead(Map<String, Object> param)  throws Exception;
}
