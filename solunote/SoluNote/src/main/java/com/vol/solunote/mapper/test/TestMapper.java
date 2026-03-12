package com.vol.solunote.mapper.test;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.vo.DefaultVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.sound.SoundBean;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.train.TrainVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
	
	public	void createTest(Map<String, Object>  param)  throws Exception;
	public	void updateTest(Map<String, Object> map) throws Exception;
	
	
	public	List<Map<String, Object>> getTestByName(Map<String, Object> param)   throws Exception;
	public	List<MeetingVo> getTestFromSeq(MeetingVo vo) throws Exception;	
	public	void updateTest(MeetingVo vo)  throws Exception;
	
	public	void createTestTran(String start, String end, String text, int reliability, int soundSeq, int channelId)  throws Exception;
	
	public	void createTestTrans(String startDouble, String endDouble, String text, int reliability, int test_seq) throws Exception;	
	
	public	List<Map<String, Object>> getListWithParams(DefaultVo search, OffsetPageable offsetPageable,String keyword,String division) throws Exception;
	public	List<SoundBean> getList() throws Exception;
	public	List<SoundVo> getListWithMap(Map<String, Object> param) throws Exception;	
	
	public	List<SoundVo> getListData(Map<String, Object> param) throws Exception;
	public	void excludeTest(int seq)  throws Exception;
	public	void updateTestUseYesFromTrans()  throws Exception;
	public	void updateTestUseNoFromTrans()  throws Exception;	
	public	void excludeTestTrans(int seq)  throws Exception;
	public	List<Integer> getDataIdFromTestTrans(int seq)  throws Exception;
	public	void excludeTestTransPair(Map<String, Object> map)  throws Exception;
	
	public	List<Map<String, Object>> getTestTransList(int seq, OffsetPageable offsetPageable,int reliability );
	
	public	List<TransVo> getTestTransListBatch(String useYn, OffsetPageable offsetPageable, DefaultVo search) throws Exception;
	
	public	void excludeTestCandiate(Map<String, Object> map) throws Exception;
	public	void updateTestDataId(int seq, String dataId, String errorMsg, String useYn)  throws Exception;
	
	public	void insertTestTransPair(String dataId, int seq, String trainText, String useYn, double start, double end)  throws Exception;
	
	public	List<TransVo> selectTestTransPair(String dataId)  throws Exception;
	public	Map<String, Object> getMeetBySEQ(@Param("meetSeq") int meetSeq);
	public	void updateTrainTextBySeq(int seq, String trainText);
	public	void clickLeastOnce(Integer seq);
	public	List<Map<String, Object>> getTranscription(int seq) throws Exception;	
	public	int updateTranscriptionForSplit(Map<String, String> map) throws Exception;
	public	void createTranscriptionGen(Map<String, Object> map) throws Exception;
	public	void updateTranscriptionForCombine(int seq) throws Exception;
	public	int updateTranscriptionForReset(Map<String, String> map) throws Exception;	
	
	public	int getTransPairCount() throws Exception;
	public	int updateTestTransPairModel(TrainVo vo)  throws Exception;	
	public	List<TranscriptionVo> getAllTestTransList() throws Exception;
	


	public	void copyTest(Map<String, Object> param) throws Exception;
	
	public	void trainCopyTest(Map<String, Object> param) throws Exception;
	public	List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception;
	public	List<TransVo> getTestTransListX(Map<String, Object> param) throws Exception;
	public	void copyTestTrans(Map<String, Object> param)  throws Exception;
	public	void trainCopyTestTrans(Map<String, Object> param)  throws Exception;	
	public	List<Map<String, Object>> getFailList(DefaultVo search, OffsetPageable offsetPageable,String keyword,String division) throws Exception;
	
}
