package com.vol.solunote.repository.meeting;

import com.vol.solunote.model.vo.meeting.MeetingResultVo;

import java.util.List;

public interface MeetingResultRepository 
{
    public	boolean existsByMeeting_SeqAndMeetingSpeaker_Name( int seq,  String name);
    
    public	List <MeetingResultVo> findByMeeting_SeqOrderByStartAsc( int seq);

    public	void updateSpeakerBySeqArr( Long meetingSpeakerId,  int []SEQ);
    public	void updateTextBySeq( String text,  int seq);

}