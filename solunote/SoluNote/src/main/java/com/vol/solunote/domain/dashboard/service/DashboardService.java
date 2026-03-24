package com.vol.solunote.domain.dashboard.service;

import org.springframework.stereotype.Service;

import com.vol.solunote.model.vo.login.LoginLogProjectionVo;

import java.util.List;
import java.util.Map;

@Service
public interface DashboardService {
  
    public List<LoginLogProjectionVo> countUserGroupDate(String startDate, String endDate);


    public List<Map <String, Object>> fileCountGroupDate(String startDate, String endDate);

    public int countByCreatedAtBetweenAndGroupDate(String startDate, String endDate);

    public     Map<String, Object> analyticsAvgMeeting(String startDate, String endDate);

}

