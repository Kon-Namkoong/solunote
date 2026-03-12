package com.vol.solunote.login.service;


import org.springframework.stereotype.Service;


@Service
public interface LoginService {

    public int createLoginLog(String ip, int userSeq, String username);
 
    public int createLoginErrorLog(String ip, String tcId, String errorMessage);
    
}