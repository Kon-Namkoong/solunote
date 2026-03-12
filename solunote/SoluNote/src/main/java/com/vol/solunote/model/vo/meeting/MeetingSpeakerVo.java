package com.vol.solunote.model.vo.meeting;


import lombok.Getter;
import lombok.Setter;
import com.vol.solunote.model.entity.base.BaseTimeEntity;


@Getter
@Setter

public class MeetingSpeakerVo extends BaseTimeEntity {

    Long 	meetingSpeakerId;
    private String name;
    private int	seq;

}
