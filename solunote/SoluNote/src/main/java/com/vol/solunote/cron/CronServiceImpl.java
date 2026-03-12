package com.vol.solunote.cron;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.mybatis.spring.annotation.MapperScan;

@MapperScan("com.vol.solunote.cron")
@Service
public class CronServiceImpl implements CronService{
	
	@Autowired
	CronMapper cronMapper;

	@Override
	public List<Map<String,Object>> selectSchedulList() throws Exception {
		// TODO Auto-generated method stub
		return cronMapper.selectSchedulList();
	}
	

	
	
	

}
