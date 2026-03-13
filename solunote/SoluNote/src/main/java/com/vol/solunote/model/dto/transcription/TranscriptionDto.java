package com.vol.solunote.model.dto.transcription;

import com.vol.solunote.model.entity.base.Base;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranscriptionDto extends Base{
    private double start;
    private double end;
    
    private int reliability;

    private String sttText;

    private String meetText;
    
    private String trainText;
    
    private int	 meetingSeq;
    private int soundSeq;
    
    private int	 ttsSeq;
    
    private String useYn;
    
    private String dataId;
    
    private String	errorMsg;
       
    private int channelId;

    private	String	speaker;
    
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
