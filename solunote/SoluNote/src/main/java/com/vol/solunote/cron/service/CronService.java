package com.vol.solunote.cron.service;

import java.util.List;
import java.util.Map;

public interface CronService {
	public  List<Map<String, Object>> selectSchedulList() throws Exception;

}
