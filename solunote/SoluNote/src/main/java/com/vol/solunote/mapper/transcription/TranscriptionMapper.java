package com.vol.solunote.mapper.transcription;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface TranscriptionMapper {

	List<TransVo> getTtsTransListBatch(Map<String, Object> param) throws Exception;
	
	List<TranscriptionVo> getTranscriptionList(int meetSeq, int reliability, OffsetPageable offsetPageable,int changeTextValue);
	
	List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception;
	
	
	List<TranscriptionVo> getTranscription(int transSeq);	
	
	void createTranscription(String startDouble, String endDouble, String text, int reliability, int meeting_seq, int sound_seq, int channel_id);
	void createTranscriptionWithoutMeet(String startDouble, String endDouble, String text, int reliability, int sound_seq, int channel_id);
	
	
	void clickLeastOnce(Integer seq);
		
	int changedTransCount(int seq) throws Exception;
	
	void deleteTrans(int seq) throws Exception;
	
	void updateTranscriptionForCombine(int seq) throws Exception;
	
	List<TranscriptionVo> getTranscriptionR(Map<String, Object> param) throws Exception;
	
	List<TranscriptionVo> getTranscriptionF(Map<String, Object> param) throws Exception;
	
	List<TransVo> getDataTransListX(SearchVo search, OffsetPageable offsetPageable,String keyword, String caller)  throws Exception;

	List<TransVo> getDataTransListBatch(Map<String, Object> param) throws Exception;
	
	List<TransVo> getDataTransListBatchWithParams(SearchVo search, OffsetPageable offsetPageable, String keyword, String caller) throws Exception;
	
	List<TransVo> getDataTransListReserve(Map<String, Object> param)  throws Exception;

	void createTranscriptionWithMap(Map<String, Object> map)  throws Exception;
	void createTranscriptionForTts(Map<String, Object> map)  throws Exception;

	void copyTrans(Map<String, Object> param) throws Exception;
	
	void excludeTransTranscription(int[] seq);
	void excludeTransTransPair(int[] seq);


	List<TransVo> getDataTransListReserveWithParams(SearchVo search, OffsetPageable offsetPageable,String keyword, String caller)  throws Exception;
	
	void updateDataId(int seq, String dataId, boolean reset, String errorMsg, String useYn) throws Exception;

	List<TransVo> selectTransPair(String dataId) throws Exception;

	void insertTransPair(String dataId, int seq, String trainText, String useYn, double start, double end) throws Exception;


	void updateTrainTextNull(int seq) throws Exception;

	String getUuid()  throws Exception;

	int updateTranscriptionForSplit(Map<String, String> map) throws Exception;

	int updateTranscriptionForReset(Map<String, String> map) throws Exception;

	//List<TransVo> getDataTransListBatch(SearchVo search, OffsetPageable offsetPageable, String keyword, String caller) throws Exception;

	void includeTransTranscription(int[] seq) throws Exception;

	public	List<Map<String, Object>> getTransList(Map<String, Object> param) throws Exception;
	
	public	void updateTransTranscription(Map<String, Object> params);	
	
    int updateTrainTextAndUseYnBySeq( int seq, String trainText, String useYn);
    
    int updateTrainTextBySeq( int seq, String trainText);   
}
