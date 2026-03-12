package com.vol.solunote.repository.upload;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.mapper.upload.UploadMapper;
import org.springframework.stereotype.Repository;
@Repository
public class UploadDiskRepositoryImpl implements UploadDiskRepository {
	
	@Autowired
	private UploadMapper mapper;
		
	@Override
	public	List<Map<String, Object>> getUploadDirList(OffsetPageable offsetPageable, Object object)
	{
		return	mapper.getUploadDirList(offsetPageable, object);
	}
	
	@Override
    public	void uploadDirRegister(String uploadDir, String uploadDirUseYn ,String uploadDirCategory)
    {
		mapper.uploadDirRegister(uploadDir, uploadDirUseYn, uploadDirCategory);
    }
	@Override
    public 	int checkUploadDir(String uploadDir) throws Exception
    {
		return	mapper.checkUploadDir(uploadDir);
    }
	@Override
	public	List<Map<String, Object>>  uploadDirInfo(String uploadDir) throws Exception
	{
		return	mapper.uploadDirInfo(uploadDir);
	}
	@Override
    public	void 	trainChangeToN()
    {
		mapper.trainChangeToN();
    }
	@Override
    public	void	testChangeToN()
    {
		mapper.testChangeToN();
    }
	@Override
    public	void	changeToY(String uploadDirChange)
    {
		mapper.changeToY(uploadDirChange);
    }
	
	@Override
	public	String 	getLastRead(String category) throws Exception
	{
		return	mapper.getLastRead(category);
	}
	
	@Override
	public	void 	createLastRead(Map<String, Object> param)  throws Exception
	{
		mapper.createLastRead(param);
	}

}
