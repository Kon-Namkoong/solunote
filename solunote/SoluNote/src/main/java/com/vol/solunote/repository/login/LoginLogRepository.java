package com.vol.solunote.repository.login;


import com.vol.solunote.model.vo.login.LoginLogProjectionVo;

import java.util.List;

public interface LoginLogRepository 
{
    public	int countByCreatedAtBetweenAndGroupDate(String startYYMMdd, String endYYMMdd);
    public	List<LoginLogProjectionVo> countUserGroupDate(String startYYMMdd, String endYYMMdd);
    public int createLoginLog(String ip, int userSeq, String username);
    public int createLoginErrorLog(String ip, String tcId, String errorMessage);
}