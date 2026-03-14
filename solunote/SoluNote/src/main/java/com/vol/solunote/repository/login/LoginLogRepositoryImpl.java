package com.vol.solunote.repository.login;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import com.vol.solunote.model.vo.login.LoginLogProjectionVo;

import com.vol.solunote.mapper.login.LoginLogMapper;

import org.springframework.stereotype.Repository;
@Repository
public class LoginLogRepositoryImpl implements LoginLogRepository 
{
	@Autowired	
	private	LoginLogMapper	mapper;
	
    @Override
    public	int countByCreatedAtBetweenAndGroupDate(String startYYMMdd, String endYYMMdd)
    {
    	return	mapper.countByCreatedAtBetweenAndGroupDate(startYYMMdd, endYYMMdd);
    }

    @Override
    public	List<LoginLogProjectionVo> countUserGroupDate(String startYYMMdd, String endYYMMdd)
    {
    	return	mapper.countUserGroupDate(startYYMMdd, endYYMMdd);
    }

    @Override
    public int createLoginErrorLog(String ip, String tcId, String errorMessage)
    {
    	return	mapper.createLoginErrorLog(ip, tcId, errorMessage);
    }
    
    @Override
    public int createLoginLog(String ip, int userSeq, String username)
    {
    	return	mapper.createLoginLog(ip, userSeq, username);
    }
    
}
