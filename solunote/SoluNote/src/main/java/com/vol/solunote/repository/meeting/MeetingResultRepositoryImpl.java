package com.vol.solunote.repository.meeting;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.vol.solunote.model.vo.meeting.MeetingResultVo;
import com.vol.solunote.mapper.meeting.MeetingMapper;
import org.springframework.stereotype.Repository;
@Repository
public class MeetingResultRepositoryImpl implements MeetingResultRepository {

	@Autowired
	private	MeetingMapper	mapper;
	
	@Override
	public boolean existsByMeeting_SeqAndMeetingSpeaker_Name(int seq, String name) 
	{
		return mapper.existsByMeeting_SeqAndMeetingSpeaker_Name(seq, name);
	}

	@Override
	public void updateTextBySeq(String text, int seq) 
	{

		mapper.updateTextBySeq(text, seq);
	}

	@Override
	public List<MeetingResultVo> findByMeeting_SeqOrderByStartAsc(int seq) 
	{
		return mapper.findByMeeting_SeqOrderByStartAsc(seq);
	}

	@Override
	public void updateSpeakerBySeqArr(Long meetingSpeakerId, int[] seq) 
	{
		mapper.updateSpeakerBySeqArr(meetingSpeakerId, seq);
	}

}
