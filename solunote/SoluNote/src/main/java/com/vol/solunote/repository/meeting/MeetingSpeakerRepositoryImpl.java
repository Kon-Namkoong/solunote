package com.vol.solunote.repository.meeting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.mapper.meeting.MeetingMapper;
import com.vol.solunote.model.vo.meeting.MeetingSpeakerVo;
import org.springframework.stereotype.Repository;
@Repository
public class MeetingSpeakerRepositoryImpl implements MeetingSpeakerRepository 
{
	@Autowired
	private	MeetingMapper	mapper;
	
	@Override
	public void deleteByMeeting_SeqAndName(int seq, String name) {
		// TODO Auto-generated method stub
		mapper.deleteByMeeting_SeqAndName(seq,name);
	}

	@Override
	public boolean existsByMeeting_SeqAndName(int seq, String name) {
		// TODO Auto-generated method stub
		return mapper.existsByMeeting_SeqAndName(seq,name);
	}

	@Override
	public	List <MeetingSpeakerVo> findSpeakerByMeeting_Seq( int seq) {
		// TODO Auto-generated method stub
		return mapper.findSpeakerByMeeting_Seq(seq);
	}

	@Override
	public MeetingSpeakerVo findByNameAndMeeting_Seq(String name, int seq) {
		// TODO Auto-generated method stub
		return mapper.findByNameAndMeeting_Seq(name,seq);
	}
	
	@Override
    public	void	saveMeetingSpeaker(MeetingSpeakerVo speaker)
    {
		mapper.saveMeetingSpeaker(speaker);
    }
}
