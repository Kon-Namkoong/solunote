package com.vol.solunote.model.entity.transcription;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import com.vol.solunote.model.entity.base.BaseTimeEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"TB_CS_TRANS_PAIR\"")
@Getter
@Setter
@Audited

public class TransPair extends	BaseTimeEntity{
	@Id
	@Column(length=255)
	private	String	dataId;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "seq",          			// FK 컬럼명
        referencedColumnName = "seq"  	// 부모 PK
    )
    private	Transcription	trans;

	@Column(nullable = true, columnDefinition = "TEXT")
	private	String	trainText;
	
	@Column(length = 1)
	private	String	useYn;
	
	private	String	modelId;
	
	private	double	start;
	private	double	end;	
}
