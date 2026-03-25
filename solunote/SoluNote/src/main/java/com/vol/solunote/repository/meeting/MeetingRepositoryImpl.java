package com.vol.solunote.repository.meeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.mapper.meeting.MeetingMapper;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.sound.SoundBean;
import com.vol.solunote.security.vo.SecurityMember;

import org.springframework.stereotype.Repository;
@Repository
public class MeetingRepositoryImpl implements MeetingRepository {

	@Autowired
	private		MeetingMapper		mapper;
	
	@Override
	public void updateUpdatedAtBySeq(LocalDateTime updatedAt, int seq) throws Exception 
	{
		mapper.updateUpdatedAtBySeq(updatedAt, seq);
	}

	@Override
	public void updateDeletedAtBySeq(int seq) throws Exception 
	{
		mapper.updateDeletedAtBySeq(seq);
	}

	@Override
	public void updateDeletedRemovedAtBySeq(int seq)  throws Exception
	{
		mapper.updateDeletedRemovedAtBySeq(seq);
	}

	@Override
	public void updateTrashedAtBySeq(LocalDateTime trashedAt, int seq)  throws Exception
	{
		mapper.updateTrashedAtBySeq(trashedAt, seq);
	}

	@Override
	public int updateSubjectAndUpdatedAtBySeq(String subject, int seq, LocalDateTime updatedAt) throws Exception
	{

		return mapper.updateSubjectAndUpdatedAtBySeq(subject, seq, updatedAt);
	}

	@Override
	public int updateRemarkAndUpdatedAtBySeq(boolean updateRemark, LocalDateTime updatedAt, int meetSeq) throws Exception
	{

		return mapper.updateRemarkAndUpdatedAtBySeq(updateRemark, updatedAt, meetSeq);
	}

	@Override
	public	void createMeet(Map<String,Object> param)
	{
		mapper.createMeet(param);
	}

	@Override
	public	void createMeetResult(String start, String end, String text,  long meetingSpeakerId, int meeting_seq)
	{
		mapper.createMeetResult(start, end, text, meetingSpeakerId, meeting_seq);
	}

	@Override
	public	List<MeetingVo> getList( String keyword, String category, SecurityMember securityMember, int activeMenu,String searchStartDate ,String searchEndDate, OffsetPageable offsetPageable) throws Exception
	{
		return	mapper.getList(keyword, category, securityMember, activeMenu, searchStartDate, searchEndDate, offsetPageable);
	}
	
	@Override
	public	List<MeetingVo> getListForTrash( String keyword, String category,SecurityMember securityMember,String searchStartDate ,String searchEndDate, OffsetPageable offsetPageable) throws Exception
	{
		return	mapper.getListForTrash(keyword, category, securityMember, searchStartDate, searchEndDate, offsetPageable);
	}

	@Override
	public	Map<String, Object> getMeetBySEQ(int meetSeq)
	{
		return	mapper.getMeetBySEQ(meetSeq);
	}

	@Override
	public	void updateMeeting(String mode, int seq) throws Exception
	{
		mapper.updateMeeting(mode, seq);
	}

	@Override
	public	void createMeetApi(Map<String, Object> param) throws Exception
	{
		mapper.createMeetApi(param);
	}

	@Override
	public	List<MeetingVo> getMeetApi(MeetingVo vo) throws Exception
	{
		return	mapper.getMeetApi(vo);
	}

	@Override
	public	void updateMeetApi(MeetingVo vo)  throws Exception
	{
		mapper.updateMeetApi(vo);
	}

	@Override
	public	List<MeetingVo> getMeetApiForce(MeetingVo vo)   throws Exception
	{
		return	mapper.getMeetApiForce(vo);
	}

	@Override
	public	void updateMeetApiForce(MeetingVo vo) throws Exception
	{
		mapper.updateMeetApiForce(vo);
	}

	@Override
	public	List<MeetingVo> getMeetApiFromSeq(MeetingVo vo) throws Exception
	{
		return	mapper.getMeetApiFromSeq(vo);
	}

	@Override
	public	void updateMeetApiStatus(MeetingVo vo) throws Exception
	{
		mapper.updateMeetApiStatus(vo);
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
	public	void deleteMeetingResult(int seq) throws Exception
	{
		mapper.deleteMeetingResult(seq);
	}
	
	@Override
	public	void deleteMeetingSpeaker(int seq) throws Exception
	{
		mapper.deleteMeetingSpeaker(seq);
	}

	@Override
	public	Map<String, Object> readMeetBySeq(int seq) throws Exception
	{
		return	mapper.readMeetBySeq(seq);
	}

	@Override
	public	void deleteMeetingApi(int seq) throws Exception
	{
		mapper.deleteMeetingApi(seq);
	}

	@Override
	public	List<Map<String, Object>> getMeettingRemoveCandiate(int term)  throws Exception
	{
		return	mapper.getMeettingRemoveCandiate(term);
	}
	
	@Override
	public	void summaryUpdate(String result ,int seq ,int summaryType) throws Exception
	{
		mapper.summaryUpdate(result, seq, summaryType);
	}

	@Override
	public	void updateMeet(Map<String, Object> updateMap) throws Exception
	{
		mapper.updateMeet(updateMap);
	}
	
	//summary Id 업데이트
	@Override
	public	void updateSummaryId(Map<String, Object> param)  throws Exception
	{
		mapper.updateSummaryId(param);
	}
	
	@Override
	public	void updateSummarySuccess(Map<String, Object> param)  throws Exception
	{
		mapper.updateSummarySuccess(param);
	}
		
	@Override
	public	void updateSummaryStatus(Map<String, Object> param)  throws Exception
	{
		mapper.updateSummaryStatus(param);
	}
	
	@Override
	public	void updateSummaryFAILUER(Map<String, Object> param)  throws Exception
	{
		mapper.updateSummaryFAILUER(param);
	}
	
	@Override
	public	List<SoundBean> getMeetingList(String startDate, String endDate, int from, int to) throws Exception
	{
		return	mapper.getMeetingList(startDate, endDate, from, to);
	}

	@Override
    public	List <Map <String, Object>> fileCountGroupDate(String startDate, String endDate)
    {
		return	mapper.fileCountGroupDate(startDate, endDate);
    }
    
	@Override
    public	Map<String, Object> analyticsAvgMeeting(String startDate, String endDate)
    {
		return	mapper.analyticsAvgMeeting(startDate, endDate);
    }
    
	@Override
	public	void changeMeetingInfo(String meetingTitle,String meetingValue,String meetingUseYn,String meetingDetail)
	{
		mapper.changeMeetingInfo(meetingTitle, meetingValue, meetingUseYn, meetingDetail);
	}

	@Override
	public	void deleteMeetingInfo(String[] meetingTitle)
	{
		mapper.deleteMeetingInfo(meetingTitle);
	}

	@Override
	public	List<Map<String, Object>> getSettingList(OffsetPageable offsetPageable ,String useYn,String searchType, String searchKeyword,String searchStartDate,String searchEndDate)
	{
		return	mapper.getSettingList(offsetPageable, useYn, searchType, searchKeyword, searchStartDate, searchEndDate);
	}

	@Override
    public	void settingRes(String settingTitle, String settingValue, String settingUseYn,String settingDetail)
    {
		mapper.settingRes(settingTitle, settingValue, settingUseYn, settingDetail);
    }
    
	@Override
    public int checkSetting(String settingTitle) throws Exception
    {
		return	mapper.checkSetting(settingTitle);
    }
    
	@Override
    public	List<Map<String, Object>>  settingInfo(String settingTitle) throws Exception
    {
		return	mapper.settingInfo(settingTitle);
    }
    
	@Override
    public	void changeSettingInfo(String settingTitleChange,String settingValueChange,String settingUseYnChange,String settingDetailChange)
    {
		mapper.changeSettingInfo(settingTitleChange, settingValueChange, settingUseYnChange, settingDetailChange);
    }
    
	@Override
    public	void deleteSettingInfo(String[] settingTitle)
    {
		mapper.deleteSettingInfo(settingTitle);
    }
    
	@Override
    public	List<Map<String, Object>> getSettingConfigList() throws Exception
    {
		return	mapper.getSettingConfigList();
    }
}
