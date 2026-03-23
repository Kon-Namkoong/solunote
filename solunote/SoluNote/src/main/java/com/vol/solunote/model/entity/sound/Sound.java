package com.vol.solunote.model.entity.sound;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

import com.vol.solunote.model.entity.base.Base;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Audited
@Getter
@NoArgsConstructor
public class Sound extends Base {

    @Column(nullable = false)
    private String subject;
    
    @Column(nullable=false)
    private	String	division;

    @ColumnDefault(value = "false")
    private boolean remark;

    @Column(nullable=true,columnDefinition = "timestamp")
    private LocalDateTime deletedAt;
    
    @Column(nullable=true,columnDefinition = "timestamp")
    private LocalDateTime trashedAt;

    @Column(nullable = false)
    private String fileSizeBytes;
    
    @Column(nullable = false)
    private String timeDurationMs;
    
    @Column(nullable = true, unique=true)
    private String fileOrgNm;
    
    @Column(nullable = true)
    private String fileNewNm;
    
    @Column(nullable = true)
    private String fileConvNm;
    
    @Column(nullable = true)
    private String fileStereoPrefix;
    
    @Column(nullable=false,columnDefinition = "int default 1")
    private	int	channelCount;
    
    @Column(name="TC_USER_SEQ", nullable=false)
    private int	tcUserSeq;
    
    @Column(nullable=false)
    @ColumnDefault(value = "10")
    private String	staprivatetus;
    
    @Column(nullable=true, columnDefinition = "timestamp")
    private	LocalDateTime	sttStartedAt;
    
    @Column(nullable = true)
    private String	sttDuration;
    
    @Column(nullable=false, columnDefinition = "int default 0" )
    private	int	reliability;
    
    @Column(nullable=false, columnDefinition = "int default 0")
    private	int	clickLeastOnce;
    
    @Column(nullable=true, length=400)
    private	String	errorMessage;
    
    @Column(length=255)
    private	String	status;  
}
