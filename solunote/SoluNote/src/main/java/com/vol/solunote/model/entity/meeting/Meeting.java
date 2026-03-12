package com.vol.solunote.model.entity.meeting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.Audited;

import com.vol.solunote.model.entity.base.Base;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Audited
@Entity
@Getter
@Table(name = "`TB_CS_MEETING`")
@NoArgsConstructor
public class Meeting extends Base {

    @Column(nullable = false)
    private String subject;

    @ColumnDefault(value = "false")
    private boolean remark;

    private LocalDateTime deletedAt;
    private LocalDateTime trashedAt;
    private LocalDateTime removedAt;

    @Column(nullable = false)
    private String fileSizeBytes;
    @Column(nullable = false)
    private String timeDurationMs;
    @Column(nullable = false)
    private String fileOrgNm;
    @Column(nullable = false)
    private String fileNewNm;
    @Column(nullable = true)
    private String fileConvNm;    
    @Column(nullable = false)
    private int tcUserSeq;       
    @Column(nullable = true)
    private String lang;
    @Column(nullable = true)
    private String uploadType; 
    @Column(nullable = true)    
	private String errorMessage;
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summary;    
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summary2;
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summary3;
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summaryId;    
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summaryId2;
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summaryId3;    
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summaryStatus;    
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summaryStatus2;
    @Column(nullable = true,columnDefinition="TEXT")    
	private String summaryStatus3;
    @Column(nullable = false, columnDefinition="smallint(6) default 0")
    private int reliability;

}

