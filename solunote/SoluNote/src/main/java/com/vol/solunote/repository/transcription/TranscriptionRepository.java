package com.vol.solunote.repository.transcription;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.transcription.TransVo;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;

public interface TranscriptionRepository {
   
    int updateTrainTextAndUseYnBySeq( @NonNull int seq, @NonNull String trainText, @NonNull String useYn);

	List<TransVo> getTtsTransListBatch(SearchVo search, OffsetPageable offsetPageable ,String keyword, String caller) throws Exception;
	
	List<TranscriptionVo> getTranscriptionList(int meetSeq, int reliability, OffsetPageable offsetPageable,int changeTextValue);
	
	List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception;
		
	public	void createTranscription(String startDouble, String endDouble, String text, int reliability, int sound_seq, int channel_id);
	
	public	void createTranscription(String startDouble, String endDouble, String text, int reliability, int meeting_seq, int sound_seq, int channel_id);

	public	void createTranscriptionForTts(Map<String, Object> map) throws Exception;
	
	public	void createTranscription(Map<String, Object> map)  throws Exception;
	
	void clickLeastOnce(Integer seq);
	
	int changedTransCount(int seq) throws Exception;
	
	void deleteTrans(int seq) throws Exception;
	
	void updateTranscriptionForCombine(int seq) throws Exception;
	
	List<TranscriptionVo> getTranscription(int seq)  throws Exception;
	
	
	List<TranscriptionVo> getTranscriptionR(Map<String, Object> param) throws Exception;
	
	List<TranscriptionVo> getTranscriptionF(Map<String, Object> param) throws Exception;
	
	List<TransVo> getDataTransListX(SearchVo search, OffsetPageable offsetPageable,String keyword, String caller)  throws Exception;

	List<TransVo> getDataTransListBatch(Map<String, Object> param) throws Exception;
	
	List<TransVo> getDataTransListBatch(SearchVo search, OffsetPageable offsetPageable, String keyword, String caller) throws Exception;
	
	List<TransVo> getDataTransListReserve(Map<String, Object> param)  throws Exception;

	void copyTrans(Map<String, Object> param) throws Exception;
	
	void excludeTransTranscription(int[] seq);
	void excludeTransTransPair(int[] seq);

	List<TransVo> getDataTransListReserve(SearchVo search, OffsetPageable offsetPageable,String keyword, String caller)  throws Exception;
	
	void updateDataId(int seq, String dataId, boolean reset, String errorMsg, String useYn) throws Exception;

	List<TransVo> selectTransPair(String dataId) throws Exception;

	void insertTransPair(String dataId, int seq, String trainText, String useYn, double start, double end) throws Exception;


	void updateTrainTextNull(int seq) throws Exception;

	String getUuid()  throws Exception;

	int updateTranscriptionForSplit(Map<String, String> map) throws Exception;

	int updateTranscriptionForReset(Map<String, String> map) throws Exception;

	void includeTransTranscription(int[] seq) throws Exception;		

	public int	 updateTrainTextBySeq( int meetSeq, String trainText) throws Exception;
	
	public	List<Map<String, Object>> getTransList(Map<String, Object> param) throws Exception;	
	
	public	void updateTransTranscription(Map<String, Object> params);
	
}

