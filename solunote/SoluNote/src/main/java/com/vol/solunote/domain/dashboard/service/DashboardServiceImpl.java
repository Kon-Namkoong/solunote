package com.vol.solunote.domain.dashboard.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vol.solunote.model.vo.login.LoginLogProjectionVo;
import com.vol.solunote.repository.login.LoginLogRepository;
import com.vol.solunote.repository.meeting.MeetingRepository;

@Service("dashboardService")
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    LoginLogRepository loginLogRepository;
    
    @Autowired
    MeetingRepository meetingRepository;
    
    @Override
    public List<LoginLogProjectionVo> countUserGroupDate(String startDate, String endDate){
        return loginLogRepository.countUserGroupDate(startDate, endDate);
    }
    
    @Override
    public List<Map <String, Object>> fileCountGroupDate(String startDate, String endDate){
        return meetingRepository.fileCountGroupDate(startDate, endDate);
    }
    
    @Override
    public int countByCreatedAtBetweenAndGroupDate(String startDate, String endDate) {
        return loginLogRepository.countByCreatedAtBetweenAndGroupDate(startDate, endDate);
    }
    
    @Override
    public     Map<String, Object> analyticsAvgMeeting(String startDate, String endDate) {
        return meetingRepository.analyticsAvgMeeting(startDate, endDate);
    }
}
