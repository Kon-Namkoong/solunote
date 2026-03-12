package com.vol.solunote.menu21.service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.meeting.MeetingResultVo;
import com.vol.solunote.model.vo.meeting.MeetingSpeakerVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;

public interface Menu21Service {


	public List<MeetingVo> getList(String keyword ,String category ,int activeMenu ,String searchStartDate ,String searchEndDate, OffsetPageable offsetPageable) throws Exception;
	
	public List<MeetingResultVo> getMeetResultList(int meetSeq) throws Exception;
	
	Map<String, Object> getMeetBySEQ(@Param("seq") int meetSeq);
	
	public void setDeletedAt(int seq) throws Exception;
	public void setTrashedAt(int seq) throws Exception;
	public void setNullTrashedAt(int seq) throws Exception;

//	String successDiarizeAndStt(MultipartFile file, String durationMs, int tcUserSeq, Map<String, Object> resultMap) throws Exception;

	void updateRemarkAndUpdatedAtBySeq(int meetSeq, LocalDateTime updatedAt, boolean updateRemark) throws Exception;

    int updateSpeakerAndUpdatedAtBySeqArr(int []meetResultSeqArr, String updateSpeakerText, int meetSeq, LocalDateTime updatedAt) throws Exception;

	List<MeetingSpeakerVo> findSpeakerByMeeting_SEQ(int meetSeq);

    void updateSubjectAndUpdatedAtBySeq(int meetSeq, String updateSubject, LocalDateTime updatedAt) throws Exception;

	void updateTextAndUpdatedAtByMap(int meetSeq, Map<Integer, String> body, LocalDateTime updatedAt) throws Exception;

    LocalDateTime createSpeaker(int meetSeq, String speakerText) throws Exception;

	LocalDateTime deleteSpeaker(int meetSeq, String speakerText) throws Exception;

	String createTxt(OutputStream out, List <MeetingResultVo> pages, Map <String, Object> meetMap) throws Exception;
	void createTxt(HttpServletResponse response, List <MeetingResultVo> pages, Map <String, Object> meetMap, String summary) throws Exception;

	String createXlsx(OutputStream out, List <MeetingResultVo> pages, Map <String, Object> meetMap) throws Exception;
	void createXlsx(HttpServletResponse response, List <MeetingResultVo> pages, Map <String, Object> meetMap, String summary) throws Exception;

    String createDocx(OutputStream out, List <MeetingResultVo> pages, Map <String, Object> meetMap) throws Exception;
    void createDocx(HttpServletResponse response, List <MeetingResultVo> pages, Map <String, Object> meetMap, String summary) throws IOException;
    
    void createHwp(HttpServletResponse response, List<MeetingResultVo> pages, Map<String, Object> meetMap, String flag) throws Exception;
    

	void createSummaryTxt(HttpServletResponse response,Map <String, Object> meetMap) throws IOException;

	void createSummaryXlsx(HttpServletResponse response,Map <String, Object> meetMap) throws IOException;

    void createSummaryDocx(HttpServletResponse response,Map <String, Object> meetMap) throws IOException;
    
    void createSummaryHwp(HttpServletResponse response,Map <String, Object> meetMap) throws Exception;
    
    void createFileDownload(HttpServletResponse response,Map <String, Object> meetMap) throws Exception;
    
    
    
    
    
	String saveDiarizeAndStt(Map<String, Object> param, MultipartFile file) throws Exception;
	public List<MeetingVo> getMeetApi(MeetingVo vo) throws Exception;
	/*public void backendStt(MeetingVo vo, String letter) throws Exception;
	*/
	/*
	Map<String, String> parseDiarizeAndStt(Map<String, Object> param, Map<String, Object> resultMap) throws Exception;
	String parseDiarizeAndSttOnly(MeetingVo vo, Map<String, Object> resultMap, boolean apiFlag) throws Exception;
	*/
	byte[] getTxt(List<MeetingResultVo> pages, Map<String, Object> meetMap) throws Exception;
	Map<String, Object> getMeetBySEQAndDuration(int meetSeq);
	
	//public Map<String, Object> callStt(Resource resource, boolean callCurl, String letter) throws Exception;
	
	String callSummary3(String text) throws Exception;
	List<MeetingVo> getMeetApiForce(MeetingVo vo) throws Exception;

	public List<MeetingVo> getMeetApiFromSeq(MeetingVo search) throws Exception;

	String createSummaryTxt(List<MeetingResultVo> pages, Map<String, Object> meetMap) throws Exception;
	public List<Map<String, Object>> getMeettingRemoveCandiate(int term) throws Exception;
	
	String summary(Integer seq ,Integer summaryType) throws Exception;
	
	//summary status 조회
	Map<String, Object> summaryStatusApi(Integer seq,String summId,Integer summaryType) throws Exception;
	
	Map<String, List<String>> parseSummary(String summary,int down) throws Exception;
	
	Map<String, List<String>> parseSummary2(String summary,int down) throws Exception;
	
	Map<String, List<Map<String, Object>>> parseSummary3(String summary) throws Exception;
	
	public void summaryUpdate(String result , int meetSeq , int summaryType) throws Exception;
	public Map<String, Object> readMeetBySeq(int seq) throws Exception;
	

}
