package com.vol.solunote.repository.test;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.mapper.test.TestMapper;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.DefaultVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.sound.SoundBean;
import com.vol.solunote.model.vo.sound.SoundVo;
import com.vol.solunote.model.vo.train.TrainVo;
import com.vol.solunote.model.vo.transcription.TransVo;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
@Repository
public class TestRepositoryImpl implements TestRepository {

	@Autowired
	TestMapper	mapper;
	
	@Override
	public	void createTest(Map<String, Object>  param)  throws Exception
	{
		mapper.createTest(param);
	}
	
	@Override
	public	List<Map<String, Object>> getTestByName(Map<String, Object> param)   throws Exception
	{
		List<Map<String, Object>> resultList;
		resultList = mapper.getTestByName(param);
		return	resultList;
	}
	
	@Override
	public	List<MeetingVo> getTestFromSeq(MeetingVo vo) throws Exception
	{
		List<MeetingVo> result;	
		result = mapper.getTestFromSeq(vo);
		return	result;		
	}
	
	@Override
	public	void updateTest(Map<String, Object>  param)  throws Exception
	{
		mapper.updateTest(param);
	}
	
	@Override
	public	void updateTestWithMeet(MeetingVo vo)  throws Exception
	{
		mapper.updateTest(vo);
	}
	
	@Override
	public	void createTestTran(String start, String end, String text, int reliability, int soundSeq, int channelId)  throws Exception
	{
		mapper.createTestTran(start, end, text, reliability, soundSeq, channelId);
	}

	@Override
	public	void createTestTrans(String startDouble, String endDouble, String text, int reliability, int test_seq) throws Exception
	{
		mapper.createTestTrans(startDouble, endDouble, text, reliability, test_seq);
	}
	
	
	@Override
	public	List<SoundBean> getList() throws Exception
	{
		List<SoundBean> result;
		result = mapper.getList();
		return	result;		
	}
	

	@Override
	public	List<Map<String, Object>> getList(DefaultVo search, OffsetPageable offsetPageable,String keyword,String division) throws Exception
	{
		List<Map<String, Object>> resultList;
		resultList = mapper.getListWithParams(search, offsetPageable, keyword, division);
		return(resultList);
	}
	
	@Override
	public	void excludeTest(int seq)  throws Exception
	{
		mapper.excludeTest(seq);	
	}
	
	@Override
	public	void updateTestUseYesFromTrans()  throws Exception
	{
		mapper.updateTestUseYesFromTrans();
	}
	
	@Override
	public	void updateTestUseNoFromTrans()  throws Exception
	{
		mapper.updateTestUseNoFromTrans();
	}
	
	@Override
	public	void excludeTestTrans(int seq)  throws Exception
	{
		mapper.excludeTestTrans(seq);		
	}
	
	@Override
	public	List<Integer> getDataIdFromTestTrans(int seq)  throws Exception
	{
		List<Integer> resultList;
		resultList = mapper.getDataIdFromTestTrans(seq);
		return(resultList);
	}
	
	@Override
	public	void excludeTestTransPair(Map<String, Object> map)  throws Exception
	{
		mapper.excludeTestTransPair(map);
	}
	
	@Override
	public	List<Map<String, Object>> getTestTransList(int seq, OffsetPageable offsetPageable,int reliability )
	{
		List<Map<String, Object>> resultList;
		resultList = mapper.getTestTransList(seq, offsetPageable, reliability);
		return(resultList);
	}
	
	
	@Override
	public	List<TransVo> getTestTransListX(Map<String, Object> param) throws Exception
	{
		List<TransVo> resultList;
		resultList = mapper.getTestTransListX(param);
		return(resultList);
	}	
	
	
	@Override
	public	List<TransVo> getTestTransListBatch(String useYn, OffsetPageable offsetPageable, DefaultVo search) throws Exception	
	{
		List<TransVo> resultList;
		resultList = mapper.getTestTransListBatch(useYn, offsetPageable, search);
		return(resultList);
	}
	
	@Override
	public	void excludeTestCandiate(Map<String, Object> map) throws Exception
	{
		mapper.excludeTestCandiate(map);
	}
	
	@Override
	public	void updateTestDataId(int seq, String dataId, String errorMsg, String useYn)  throws Exception
	{
		mapper.updateTestDataId(seq, dataId, errorMsg, useYn);
	}
	
	@Override
	public	void insertTestTransPair(String dataId, int seq, String trainText, String useYn, double start, double end)  throws Exception
	{
		mapper.insertTestTransPair(dataId, seq, trainText, useYn, start, end);
	}
	
	
	@Override
	public	List<TransVo> selectTestTransPair(String dataId)  throws Exception
	{
		List<TransVo> resultList;
		resultList = mapper.selectTestTransPair(dataId);
		return(resultList);				
	}
	
	@Override
	public	Map<String, Object> getMeetBySEQ(int meetSeq)
	{
		Map<String,Object> resultMap;
		resultMap = mapper.getMeetBySEQ(meetSeq);
		return(resultMap);
	}
	
	@Override
	public	void updateTrainTextBySeq(int seq, String trainText)
	{
		mapper.updateTrainTextBySeq(seq, trainText);
	}
	
	@Override
	public	void clickLeastOnce(Integer seq)
	{
		mapper.clickLeastOnce(seq);
	}
	
	@Override
	public	List<Map<String, Object>> getTranscription(int seq) throws Exception	
	{
		List<Map<String, Object>> resultList;
		resultList = mapper.getTranscription(seq);
		return(resultList);
	}
	
	@Override
	public	int updateTranscriptionForSplit(Map<String, String> map) throws Exception
	{
		return(mapper.updateTranscriptionForSplit(map));
	}
	
	@Override
	public	void createTranscriptionGen(Map<String, Object> map) throws Exception
	{
		mapper.createTranscriptionGen(map);
	}
	
	@Override
	public	void updateTranscriptionForCombine(int seq) throws Exception
	{
		mapper.updateTranscriptionForCombine(seq);
	}
	
	@Override
	public	int updateTranscriptionForReset(Map<String, String> map) throws Exception
	{
		return(mapper.updateTranscriptionForReset(map));
	}
	
	@Override
	public	int getTransPairCount() throws Exception
	{
		return(mapper.getTransPairCount());
	}
	
	@Override
	public	int updateTestTransPairModel(TrainVo vo)  throws Exception
	{
		return(mapper.updateTestTransPairModel(vo));
	}
	
	@Override
	public	List<TranscriptionVo> getAllTestTransList() throws Exception
	{
		List<TranscriptionVo> resultList;
		resultList = mapper.getAllTestTransList();
		return(resultList);
	}
	
	@Override
	public	List<SoundVo> getListData(Map<String, Object> param) throws Exception
	{
		List<SoundVo> resultList;
		resultList = mapper.getListData(param);
		return(resultList);
	}
	
	@Override
	public	List<SoundVo> getList(Map<String, Object> param) throws Exception
	{
		List<SoundVo> resultList;
		resultList = mapper.getListWithMap(param);
		return(resultList);
	}
	
	@Override
	public	void copyTest(Map<String, Object> param) throws Exception
	{
		mapper.copyTest(param);
	}
	
	@Override
	public	void trainCopyTest(Map<String, Object> param) throws Exception
	{
		mapper.trainCopyTest(param);
	}
	
	@Override
	public	List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception
	{	
		List<TranscriptionVo> resultList;
		resultList = mapper.getTranscriptionList(param);
		return(resultList);
	}
	

	
	@Override
	public	void copyTestTrans(Map<String, Object> param)  throws Exception
	{
		mapper.copyTestTrans(param);
	}
	
	@Override
	public	void trainCopyTestTrans(Map<String, Object> param)  throws Exception
	{
		mapper.trainCopyTestTrans(param);
	}
	
	@Override
	public	List<Map<String, Object>> getFailList(DefaultVo search, OffsetPageable offsetPageable,String keyword,String division) throws Exception {
		return	mapper.getFailList( search, offsetPageable, keyword, division);
	}
	
	@Transactional
	@Override
	public void insertTestAndRead(Map<String, Object> param) throws Exception {

		List<Map<String, Object>> list = mapper.getTestByName(param);
		if ( list == null || list.size() == 0 ) {
			mapper.createTest(param);
		}		
	}	

}
