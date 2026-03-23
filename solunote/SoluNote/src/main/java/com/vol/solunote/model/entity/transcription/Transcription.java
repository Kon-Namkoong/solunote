package com.vol.solunote.model.entity.transcription;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import com.vol.solunote.model.entity.base.Base;

import jakarta.persistence.*;

@Getter
@Setter
@Audited

public class Transcription extends Base{

    private double start;
    private double end;
    
    @Column(columnDefinition="smallint(6) not null default 0")
    private int reliability;

    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    private String sttText;

    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    private String meetText;
    
    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    private String trainText;
    
    @Column(name = "meeting_seq")
    private int	 meeting;
    
    @Column(name = "sound_seq")
    private int sound;
    
    @Column(name = "tts_seq",nullable=false, columnDefinition="int(11) default -1" )
    private int	 ttsSeq;
    
    @Column(nullable=false, columnDefinition="varchar(1) default 'N'")
    private String useYn;
    
    @Column(nullable=false, length=255, columnDefinition="default ''")
    private String dataId;
    
    @Column(nullable=true, length=4000)
    private String	errorMsg;
       
    @Column(nullable=false, columnDefinition="int(11) default 0")
    private int channelId;

    @Column(nullable=true, length=255)
    private	String	speaker;
    
    @Column(nullable=true, length=1)
    private	String	apiUpdate;
   
    @Transient
    private String origin;
   
    @Transient
    private int hiddenCount;
    
    @Transient
    private double prevEnd;
    @Transient
    private double nextStart;
    @Transient
    private String channelChar; 
     
}

