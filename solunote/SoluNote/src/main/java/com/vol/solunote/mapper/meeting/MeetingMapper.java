package com.vol.solunote.mapper.meeting;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.model.vo.meeting.MeetingResultVo;
import com.vol.solunote.model.vo.meeting.MeetingSpeakerVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.sound.SoundBean;
import com.vol.solunote.security.SecurityMember;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeetingMapper {

	public	void createMeet(Map<String,Object> param);

	public	void createMeetResult(@Param("startDouble") String startDouble, @Param("endDouble") String endDouble, @Param("text") String text, @Param("meetingSpeakerId") long meetingSpeakerId, @Param("MEETING_SEQ") int MEETING_SEQ);

	public	List<MeetingVo> getList(@Param("keyword") String keyword, String category,@Param("securityMember") SecurityMember securityMember, @Param("activeMenu") int activeMenu,String searchStartDate ,String searchEndDate, OffsetPageable offsetPageable) throws Exception;
	public	List<MeetingVo> getListForTrash(@Param("keyword") String keyword, String category,@Param("securityMember") SecurityMember securityMember,String searchStartDate ,String searchEndDate, OffsetPageable offsetPageable) throws Exception;

	public	Map<String, Object> getMeetBySEQ(@Param("seq") int meetSeq);

	public	void updateMeeting(String mode, int seq) throws Exception;

	public	void createMeetApi(Map<String, Object> param) throws Exception;

	public	List<MeetingVo> getMeetApi(MeetingVo vo) throws Exception;

	public	void updateMeetApi(MeetingVo vo)  throws Exception;

	public	List<MeetingVo> getMeetApiForce(MeetingVo vo)   throws Exception;

	public	void updateMeetApiForce(MeetingVo vo) throws Exception;

	public	List<MeetingVo> getMeetApiFromSeq(MeetingVo vo) throws Exception;

	public	void updateMeetApiStatus(MeetingVo vo) throws Exception;

	public	int changedTransCount(int seq) throws Exception;

	public	void deleteTrans(int seq) throws Exception;
	public	void deleteMeetingResult(int seq) throws Exception;
	public	void deleteMeetingSpeaker(int seq) throws Exception;

	public	Map<String, Object> readMeetBySeq(int seq) throws Exception;

	public	void deleteMeetingApi(int seq) throws Exception;

	public	List<Map<String, Object>> getMeettingRemoveCandiate(int term)  throws Exception;
	
	public	void summaryUpdate(String result ,int seq ,int summaryType) throws Exception;

	public	void updateMeet(Map<String, Object> updateMap) throws Exception;
	
	//summary Id 업데이트
	public	void updateSummaryId(Map<String, Object> param)  throws Exception;
	
	public	void updateSummarySuccess(Map<String, Object> param)  throws Exception;
		
	public	void updateSummaryStatus(Map<String, Object> param)  throws Exception;
	
	public	void updateSummaryFAILUER(Map<String, Object> param)  throws Exception;
	
	public	List<SoundBean> getMeetingList(String startDate, String endDate, int from, int to) throws Exception;	

    public	List <Map <String, Object>> fileCountGroupDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    public	Map<String, Object> analyticsAvgMeeting(String startDate, String endDate);
    
	public	void changeMeetingInfo(String meetingTitle,String meetingValue,String meetingUseYn,String meetingDetail);    

	public	void deleteMeetingInfo(String[] meetingTitle);

	public	List<Map<String, Object>> getSettingList(OffsetPageable offsetPageable ,String useYn,String searchType, String searchKeyword,String searchStartDate,String searchEndDate);

    public	void settingRes(String settingTitle, String settingValue, String settingUseYn,String settingDetail);
    
    public int checkSetting(String settingTitle) throws Exception;
    
    public	List<Map<String, Object>>  settingInfo(String settingTitle) throws Exception; 
    
    public	void changeSettingInfo(String settingTitleChange,String settingValueChange,String settingUseYnChange,String settingDetailChange);    
    
    public	void deleteSettingInfo(String[] settingTitle);    
    
    public	List<Map<String, Object>> getSettingConfigList() throws Exception; 
    
    public	void updateUpdatedAtBySeq( LocalDateTime updatedAt, int seq) throws Exception;
    
    public	void updateDeletedAtBySeq(int seq) throws Exception; 
    
    public	void updateDeletedRemovedAtBySeq(int seq) throws Exception; 
    
    public	void updateTrashedAtBySeq( LocalDateTime trashedAt,  int seq) throws Exception; 
    
    public	int updateSubjectAndUpdatedAtBySeq( String subject,  int seq, LocalDateTime updatedAt) throws Exception; 

    public	int updateRemarkAndUpdatedAtBySeq( boolean updateRemark,  LocalDateTime updatedAt, int meetSeq) throws Exception; 

    
    public	boolean existsByMeeting_SeqAndMeetingSpeaker_Name( int seq,  String name);
    
    public	List <MeetingResultVo> findByMeeting_SeqOrderByStartAsc( int seq);

    public	void updateSpeakerBySeqArr( Long meetingSpeakerId,  int []SEQ);
    public	void updateTextBySeq( String text,  int seq);
    
    public	void deleteByMeeting_SeqAndName( int seq,  String name);
    public	boolean existsByMeeting_SeqAndName( int seq,  String name);
    public	List <MeetingSpeakerVo> findByMeeting_Seq( int seq);
    public	MeetingSpeakerVo findByNameAndMeeting_Seq( String name,  int seq);
    
    public	void	saveMeetingSpeaker(MeetingSpeakerVo speaker);
    
    public	List <MeetingSpeakerVo> findSpeakerByMeeting_Seq(int seq);
}

