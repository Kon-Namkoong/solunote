package com.vol.solunote.repository.meeting;


import com.vol.solunote.model.vo.meeting.MeetingSpeakerVo;

import java.util.List;

public interface MeetingSpeakerRepository 
{	
    void deleteByMeeting_SeqAndName( int seq,  String name);
    boolean existsByMeeting_SeqAndName( int seq,  String name);
    List <MeetingSpeakerVo> findSpeakerByMeeting_Seq( int seq);
    MeetingSpeakerVo findByNameAndMeeting_Seq( String name,  int seq);
    int	saveMeetingSpeaker(MeetingSpeakerVo speaker);
}