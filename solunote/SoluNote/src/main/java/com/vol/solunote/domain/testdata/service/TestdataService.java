package com.vol.solunote.domain.testdata.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.transcription.TransVo;


public interface TestdataService {


	public List<SoundVo> getList(Map<String, Object> param) throws Exception;
	
	public List <Map<String, Object>> getFailList(DefaultVo search, OffsetPageable offsetPageable ,String keyword,String division) throws Exception;
	
	public List<Map<String, Object>> getTestTransList(int seq, OffsetPageable offsetPageable,int reliability) throws Exception;
	
	public List<SoundVo> getListData(Map<String, Object> param)  throws Exception;
	
	public void excludeTest(int seq) throws Exception;
	
	String successSoundAndStt(Map<String, Object> param, Map<String, Object> resultMap) throws Exception;

	int updateRemarkAndUpdatedAtBySeq(int meetSeq, LocalDateTime updatedAt, boolean updateRemark) throws Exception;

//
    void updateTrainTextBySeq(int seq, String trainText) throws IOException, Exception;
	
    public List<TransVo> getTestTransListX(Map<String, Object> param) throws Exception;
	
	public void excludeTestCandiate(int[] seq, String value) throws Exception;
	public void updateTestDataId(int seq, String dataId, String errorMsg, String useYn) throws Exception;
	void insertTestTransPair(String dataId, int seq, TransVo vo) throws Exception;
	
	void clickLeastOnce(Integer seq) throws IOException;
	
	int split(Map<String, String> map) throws Exception;
	
	int combine(Map<String, String> map) throws Exception;	
	
	int resetFrame(Map<String, String> map) throws Exception;
	
	List<TransVo> getTestTransListBatch(String useYn, OffsetPageable offsetPageable, DefaultVo search) throws Exception;

	public List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception;
}

