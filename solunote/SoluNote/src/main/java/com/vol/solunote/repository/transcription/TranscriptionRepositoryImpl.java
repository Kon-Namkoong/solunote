package com.vol.solunote.repository.transcription;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.comm.DefaultController;
import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.vo.SearchVo;
import com.vol.solunote.mapper.transcription.TranscriptionMapper;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import org.springframework.stereotype.Repository;

@Repository
public class TranscriptionRepositoryImpl implements TranscriptionRepository {

	@Autowired
	private	TranscriptionMapper	mapper;
	
	@Override 
	public	List<TransVo> getTtsTransListBatch(SearchVo search, OffsetPageable offsetPageable ,String keyword, String caller) throws Exception
	{
		
		Map<String, Object> param = DefaultController.generateRequestParam("offsetPageable, search, keyword, caller", 
																			offsetPageable, search, keyword, caller);		

		List<TransVo> list = mapper.getTtsTransListBatch(param);
		return	(list);
	}
	
	@Override
	public	List<TranscriptionVo> getTranscriptionList(int meetSeq, int reliability, OffsetPageable offsetPageable,int changeTextValue)
	{
		List<TranscriptionVo>	resultList;
		resultList = mapper.getTranscriptionList(meetSeq, reliability, offsetPageable, changeTextValue);
		return	resultList;		
	}
	
	@Override
	public List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception
	{
		List <TranscriptionVo> resultMap;
		resultMap = mapper.getTranscriptionList(param);
		return	resultMap;		
	}
				
	@Override
	public	void createTranscription(String startDouble, String endDouble, String text, int reliability, int sound_seq, int channel_id)
	{
		mapper.createTranscriptionWithoutMeet(startDouble, endDouble, text, reliability, sound_seq, channel_id);
	}
	
	@Override
	public	void createTranscription(String startDouble, String endDouble, String text, int reliability, int meeting_seq, int sound_seq, int channel_id)
	{
		mapper.createTranscription(startDouble, endDouble, text, reliability, meeting_seq, sound_seq, channel_id);
	}

	@Override
	public	void clickLeastOnce(Integer seq)
	{
		mapper.clickLeastOnce(seq);
	}
	
	
	@Override	
	public	int changedTransCount(int seq) throws Exception
	{
		return	mapper.changedTransCount(seq);
	}
	
	@Override
	public	void deleteTrans(int seq) throws Exception
	{
		mapper.deleteTrans(seq);
	}

	@Override
	public	void updateTranscriptionForCombine(int seq) throws Exception
	{
		mapper.updateTranscriptionForCombine(seq);
	}
	
	@Override
	public	List<TranscriptionVo> getTranscription(int seq)  throws Exception
	{
		List<TranscriptionVo> resultList;
		resultList = mapper.getTranscription(seq);
		return	resultList;		
	}
	
	@Override	
	public	List<TranscriptionVo> getTranscriptionR(Map<String, Object> param) throws Exception
	{
		List<TranscriptionVo> resultList;
		resultList = mapper.getTranscriptionR(param);
		return	resultList;		
	}	
	
	@Override	
	public	List<TranscriptionVo> getTranscriptionF(Map<String, Object> param) throws Exception
	{
		List<TranscriptionVo> resultList;
		resultList = mapper.getTranscriptionF(param);
		return	resultList;				
	}
	
	@Override	
	public	List<TransVo> getDataTransListX(SearchVo search, OffsetPageable offsetPageable,String keyword, String caller)  throws Exception
	{
		List<TransVo> resultList;
		resultList = mapper.getDataTransListX(search, offsetPageable, keyword, caller);
		return	resultList;
	}
	
	@Override	
	public	List<TransVo> getDataTransListBatch(Map<String, Object> param) throws Exception
	{
		List<TransVo> resultList;
		resultList = mapper.getDataTransListBatch(param);
		return	resultList;
	}
	
	@Override	
	public	List<TransVo> getDataTransListBatch(SearchVo search, OffsetPageable offsetPageable, String keyword, String caller) throws Exception
	{
		List<TransVo> resultList;
		resultList = mapper.getDataTransListBatchWithParams(search, offsetPageable, keyword, caller);
		return	resultList;	
	}
	
	@Override	
	public	List<TransVo> getDataTransListReserve(Map<String, Object> param)  throws Exception
	{
		List<TransVo> resultList;
		resultList = mapper.getDataTransListReserve(param);
		return	resultList;			
	}
	
	@Override	
	public	void createTranscription(Map<String, Object> map)  throws Exception
	{
		mapper.createTranscriptionWithMap(map);
	}
	
	@Override	
	public	void createTranscriptionForTts(Map<String, Object> map)  throws Exception
	{
		mapper.createTranscriptionForTts(map);
	}
	
	@Override	
	public	void copyTrans(Map<String, Object> param) throws Exception
	{
		mapper.copyTrans(param);
	}
	
	@Override	
	public	void excludeTransTranscription(int[] seq)
	{
		mapper.excludeTransTranscription(seq);
	}
	
	@Override	
	public	void excludeTransTransPair(int[] seq)
	{
		mapper.excludeTransTransPair(seq);
	}
	
	@Override	
	public	List<TransVo> getDataTransListReserve(SearchVo search, OffsetPageable offsetPageable,String keyword, String caller)  throws Exception
	{
		List<TransVo>	resultList;
		resultList = mapper.getDataTransListReserveWithParams(search, offsetPageable, keyword, caller);
		return	resultList;
	}
	
	@Override	
	public	void updateDataId(int seq, String dataId, boolean reset, String errorMsg, String useYn) throws Exception
	{
		mapper.updateDataId(seq, dataId, reset, errorMsg, useYn);
	}
	
	@Override	
	public	List<TransVo> selectTransPair(String dataId) throws Exception
	{
		List<TransVo>	resultList;
		resultList = mapper.selectTransPair(dataId);
		return	resultList;
	}
	
	@Override	
	public	void insertTransPair(String dataId, int seq, String trainText, String useYn, double start, double end) throws Exception
	{
		mapper.insertTransPair(dataId, seq, trainText, useYn, start, end);
	}

	@Override	
	public	void updateTrainTextNull(int seq) throws Exception
	{
		mapper.updateTrainTextNull(seq);
	}

	public	String getUuid()  throws Exception
	{
		return	mapper.getUuid();
	}
	
	@Override	
	public	int updateTranscriptionForSplit(Map<String, String> map) throws Exception
	{
		return	mapper.updateTranscriptionForSplit(map);
	}
	
	@Override	
	public	int updateTranscriptionForReset(Map<String, String> map) throws Exception
	{
		return	mapper.updateTranscriptionForReset(map);
	}
	
	@Override	
	public	void includeTransTranscription(int[] seq) throws Exception
	{
		mapper.includeTransTranscription(seq);
	}
	
	@Override
	public int updateTrainTextBySeq( int meetSeq, String trainText) throws Exception 
	{
		
		if ( "NULL".equals(trainText) ) {
			trainText = null;
		}
		String useYn =  ( trainText != null && trainText.trim().length() > 0 ) ? "Y" : "N";
		int	rslt = mapper.updateTrainTextAndUseYnBySeq( meetSeq, trainText, useYn);
		
		
		int[] array = new int[1];
		array[0] = meetSeq;
		
		mapper.excludeTransTransPair(array);	
		return	rslt;
	}
	
	@Override
	public	List<Map<String, Object>> getTransList(Map<String, Object> param) throws Exception {
		return	mapper.getTransList(param);
	}
	
	@Override
	public	void updateTransTranscription(Map<String, Object> params) {
		mapper.updateTransTranscription(params);
	}
	
	@Override
    public	int updateTrainTextAndUseYnBySeq( int seq, String trainText, String useYn) {
    	return	mapper.updateTrainTextAndUseYnBySeq(seq, trainText, useYn);
    }

}
