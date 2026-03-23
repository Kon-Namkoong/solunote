package com.vol.solunote.repository.sound;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.mapper.sound.SoundMapper;
import com.vol.solunote.model.vo.comm.SearchVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.sound.SoundVo;
import org.springframework.stereotype.Repository;
@Repository
public class SoundRepositoryImpl implements SoundRepository {

	@Autowired
	private	SoundMapper	mapper;

	@Override
	public	int updateRemarkAndUpdatedAtBySeq(boolean updateRemark, LocalDateTime updatedAt, int meetSeq) throws Exception
	{
		return	mapper.updateRemarkAndUpdatedAtBySeq(updateRemark, updatedAt,  meetSeq);
	}

	@Override
	public	List<SoundVo> getListDataWithMap(Map<String, Object> param) throws Exception 
	{
		return	mapper.getListDataWithMap(param);
	}
	
	@Override
	public	List<SoundVo> getListData(OffsetPageable offsetPageable, String keyword, String startDate, String endDate,int activeMenu) throws Exception 
	{
		return	mapper.getListData(offsetPageable, keyword, startDate, endDate, activeMenu);
	}
	
	@Override
	public	List<Map<String, Object>> steelGetSoundData(int seq) throws Exception 
	{
		return	mapper.steelGetSoundData(seq);
	}
	
	@Override
	public	void createSound(Map<String,Object> param) throws Exception 
	{
		mapper.createSound(param);
	}
	
	@Override
	public	void updateSound(Map<String, Object> map) throws Exception 
	{
		mapper.updateSound(map);
	}
	
	@Override
	public	void updateSoundMode(String mode, int seq) throws Exception 
	{
		
		mapper.updateSoundMode(mode, seq);
	}
	
	@Override
	public	void updateSoundWithMeet(MeetingVo vo) throws Exception 
	{
		mapper.updateSoundWithMeet(vo);
	}
	
	@Override
	public	void copySound(Map<String, Object> param) throws Exception 
	{
		mapper.copySound(param);
	}
	
	@Override
	public	List<Map<String, Object>> getSoundData(int seq) throws Exception 
	{
		return	mapper.getSoundData(seq);
	}
	
	@Override
	public	Map<String, Object> readSoundBySeq(int seq) throws Exception 
	{
		return	mapper.readSoundBySeq(seq);
	}
	
	@Override
	public	Map<String, Object> getMeetBySEQ(int meetSeq) 
	{
		return	mapper.getMeetBySEQ(meetSeq);
	}
	
	@Override
	public	List<Map<String, Object>> getFailList(SearchVo search, OffsetPageable offsetPageable,String keyword)  throws Exception 
	{
		return	mapper.getFailList(search, offsetPageable, keyword);
	}
	
	@Override
	public	Map<String, Object> getMeetBySEQ(int meetSeq, String origin) 
	{
		return	mapper.getMeetBySEQ(meetSeq, origin);
	}
	
	@Override
	public	void clickLeastOnce(Integer seq)
	{
		mapper.clickLeastOnce(seq);
	}
	
	@Override
	public	List<Map<String, Object>> getMeettingRemoveCandiate(int term)  throws Exception
	{
		return	mapper.getMeettingRemoveCandiate(term);
	}
	
	@Override
	public	void updateDeletedAtBySeq(int seq) throws Exception
	{
		mapper.updateDeletedAtBySeq(seq);
	}
	
	@Override
	public	List<MeetingVo> getSoundFromSeq(MeetingVo vo) throws Exception
	{
		return	mapper.getSoundFromSeq(vo);
	}
	
	@Override
	public	List<Map<String, Object>> getSoundByName(Map<String, Object> param)   throws Exception
	{
		return	mapper.getSoundByName(param);
	}
	
	@Override
	public void insertSoundAndRead(Map<String, Object> param) throws Exception
	{
		List<Map<String, Object>> list = mapper.getSoundByName(param);
		if ( list == null || list.size() == 0 ) {
			mapper.createSound(param);		
		}	
	}
}
