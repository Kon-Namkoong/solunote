package com.vol.solunote.domain.traindata.service;

import com.vol.solunote.model.vo.transcription.TranscriptionVo;
import com.vol.solunote.model.vo.sound.SoundVo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TraindataService {


	public List<SoundVo> getListData(Map<String, Object> param) throws Exception;
	
	public List<TranscriptionVo> getTranscriptionList(Map<String, Object> param) throws Exception;
	
	public List<TranscriptionVo> getTranscriptionR(Map<String, Object> param) throws Exception;
	
	public List<TranscriptionVo> getTranscriptionF(Map<String, Object> param) throws Exception;
	
	Map<String, Object> getMeetBySEQ(int meetSeq);
	
	String successSoundAndStt(Map<String, Object> param, Map<String, Object> resultMap) throws Exception;

	void updateRemarkAndUpdatedAtBySeq(int meetSeq, LocalDateTime updatedAt, boolean updateRemark) throws Exception;
     
	public void trash(Map<String, String> map) throws Exception;
	
	public void delete(Map<String, String> map) throws Exception;
	
	public int createTranscription(String start, String end, String text, int reliability, int meetingSeq, int soundSeq, int channelId) throws Exception;
	
	void trashRollback(Map<String, String> map) throws Exception;
	
	void sendtest(Map<String, String> map) throws Exception;
	
    void clickLeastOnce(Integer seq) throws IOException;
    
    //파일 삭제 로직 시작
    public List<Map<String, Object>> getMeettingRemoveCandiate(int term) throws Exception;

    public void setDeletedAt(int seq, String fileNm, String fileConvNm) throws Exception;
    //파일 삭제 로직 종료
	int split(Map<String, String> map) throws Exception;
	
	int combine(Map<String, String> map) throws Exception;
	
	List<TranscriptionVo> getTranscription(int seq) throws Exception;
	int resetFrame(Map<String, String> map) throws Exception;
    
}

