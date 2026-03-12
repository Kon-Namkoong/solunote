package com.vol.solunote.model.entity.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter     
@Setter     

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false, columnDefinition = "timestamp default now()", nullable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "timestamp default now()", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}