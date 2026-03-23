package com.vol.solunote.model.entity.meeting;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import com.vol.solunote.model.entity.base.Base;

import jakarta.persistence.*;

@Getter
@Setter
@Audited
public class MeetingResult extends Base{

    private double start;
    private double end;
    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "meeting_seq" )
    private Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "meeting_speaker_id")
    private MeetingSpeaker meetingSpeaker;
    
    @Transient
    private String startTimeFormat;
    
    @Transient
    private String color;

}

