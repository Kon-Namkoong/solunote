package com.vol.solunote.model.entity.login;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.vol.solunote.model.entity.base.BaseTimeEntity;

import jakarta.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginLog extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long loginLogId;

    private String ip;

    private int userSeq;
    
    private String tcId;
    private String status;
    private String errorMessage;

    public LoginLog(String ip, int userSeq, String username) {
        this.ip = ip;
        this.userSeq = userSeq;
        this.tcId = username;
    	this.status = "S";
    }

    public LoginLog(String ip, String tcId, String errorMessage) {
    	this.ip = ip;
    	this.userSeq = -1;
    	this.tcId = tcId;
    	this.status = "F";
    	this.errorMessage = errorMessage;
    }
}

