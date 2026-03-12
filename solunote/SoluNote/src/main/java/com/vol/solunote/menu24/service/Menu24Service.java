package com.vol.solunote.menu24.service;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.transcription.TransVo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface Menu24Service {


	public List<TransVo> getList(Map<String, Object> param) throws Exception;

	public List<TranscriptionVo> getTranscriptionList(int meetSeq, String origin, int reliability) throws Exception;
	
	public void requestTrain(int seq);

	int	 updateRemarkAndUpdatedAtBySeq(int meetSeq, LocalDateTime updatedAt, boolean updateRemark) throws Exception;

    int	 updateTrainTextBySeq(int meetSeq, String trainText) throws Exception;
	
    public void excludeTrans(int[] seq) throws IOException;
	
    public List<TransVo> getDataTransListX(Map<String, Object> param) throws Exception;
	
    void updateDataId(int seq, String dataId, boolean reset, String errorMsg, String useYn) throws Exception;
	
    void insertTransPair(String dataId, int seq, TransVo vo) throws Exception;
	
    void excludeTransTransPair(int[] array) throws IOException;
    
	public List<Map<String, Object>> getFailList(SearchVo search, OffsetPageable offsetPageable,String keyword) throws Exception;

	void updateTrainTextNull(int seq) throws Exception;

	public String getUuid() throws Exception;

	public int updateTranscriptionForSplit(Map<String, String> map)  throws Exception;

	public int updateTranscriptionForReset(Map<String, String> map) throws Exception;

	List<TransVo> getDataTransListBatch(SearchVo search, OffsetPageable offsetPageable, String keyword, String caller) throws Exception;

	List<TransVo> getDataTransListBatch(Map<String, Object> param) throws Exception;

	void includeTrans(int[] seq) throws Exception;
    
}
