package com.vol.solunote.mapper.login;

import java.util.List;

import com.vol.solunote.model.vo.login.LoginLogProjectionVo;

public interface LoginLogMapper {

    public	int countByCreatedAtBetweenAndGroupDate(String startYYMMdd, String endYYMMdd);
    public	List<LoginLogProjectionVo> countUserGroupDate(String startYYMMdd, String endYYMMdd);
    public int createLoginLog(String ip, int userSeq, String username);
    public int createLoginErrorLog(String ip, String tcId, String errorMessage);    
}
