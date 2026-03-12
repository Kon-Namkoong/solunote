package com.vol.solunote.mapper.sound;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;

import com.vol.solunote.comm.OffsetPageable;
import com.vol.solunote.comm.vo.SearchVo;
import com.vol.solunote.model.vo.meeting.MeetingVo;
import com.vol.solunote.model.vo.sound.SoundVo;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SoundMapper {

	public	List<SoundVo> getListDataWithMap(Map<String, Object> param) throws Exception;
	public	List<SoundVo> getListData(OffsetPageable offsetPageable, String keyword, String startDate, String endDate, @Param("activeMenu") int activeMenu) throws Exception;
	
	public	List<Map<String, Object>> steelGetSoundData(int seq) throws Exception;
	public	void createSound(Map<String,Object> param) throws Exception;
	
	public	void updateSound(Map<String, Object> map) throws Exception;
	
	public	void updateSoundMode(String mode, int seq) throws Exception;
	
	public	void updateSoundWithMeet(MeetingVo vo) throws Exception;
	
	public	void copySound(Map<String, Object> param) throws Exception;
	public	List<Map<String, Object>> getSoundData(int seq) throws Exception;		
	
	
	public	Map<String, Object> readSoundBySeq(int seq) throws Exception;
	public	Map<String, Object> getMeetBySEQ(@Param("meetSeq") int meetSeq);
	public	List<Map<String, Object>> getFailList(SearchVo search, OffsetPageable offsetPageable,String keyword)  throws Exception;
	public	Map<String, Object> getMeetBySEQ(@Param("meetSeq") int meetSeq, @Param("origin") String origin);
	public	void clickLeastOnce(Integer seq);
	public	List<Map<String, Object>> getMeettingRemoveCandiate(int term)  throws Exception;
	public	void updateDeletedAtBySeq(int seq) throws Exception;
	public	List<MeetingVo> getSoundFromSeq(MeetingVo vo) throws Exception;
	public	List<Map<String, Object>> getSoundByName(Map<String, Object> param)   throws Exception;
	
	public	int updateRemarkAndUpdatedAtBySeq(boolean updateRemark, LocalDateTime updatedAt, int meetSeq) throws Exception;
}
