package com.vol.solunote.model.entity.meeting;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import com.vol.solunote.model.entity.base.BaseTimeEntity;

import jakarta.persistence.*;

@Entity
@Table( name="`TB_CS_MEETING_SPEAKER`",
		uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "seq"})
})
@Getter
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingSpeaker extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="meeting_speaker_id")
    Long meetingSpeakerId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "seq")
    private Meeting meeting;

    public MeetingSpeaker(String name, Meeting meeting) {
        this.name = name;
        this.meeting = meeting;
    }

}

