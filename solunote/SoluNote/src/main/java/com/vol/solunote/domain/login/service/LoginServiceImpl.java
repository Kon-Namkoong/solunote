package com.vol.solunote.domain.login.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.model.entity.login.LoginLog;
import com.vol.solunote.repository.login.LoginLogRepository;
import org.springframework.stereotype.Service;

@Service("loginService")
public class LoginServiceImpl implements LoginService{
	
	@Autowired
    private  LoginLogRepository loginLogRepository;

    @Override
    public int createLoginLog(String ip, int userSeq, String username) 
    {
    	return	loginLogRepository.createLoginLog(ip, userSeq, username);
    }

    @Override
    public int createLoginErrorLog(String ip, String tcId, String errorMessage) 
    {   	
    	return loginLogRepository.createLoginErrorLog(ip, tcId, errorMessage);
    }    
}
