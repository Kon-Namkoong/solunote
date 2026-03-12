package com.vol.solunote.model.vo.meeting;
import lombok.Getter;
import lombok.Setter;
import com.vol.solunote.model.entity.base.Base;

@Getter
@Setter
public class MeetingResultVo extends Base {
    private double 	start;
    private double 	end;
    private String 	text;
    private	String	speaker;
    private int 	meetingSeq;
    private int 	meetingSpeakerId; 
    
    private String 	startTimeFormat;
    
    private	String	name;
    
    private String color;
}
