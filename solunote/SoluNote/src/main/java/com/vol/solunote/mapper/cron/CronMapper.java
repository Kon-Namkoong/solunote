package com.vol.solunote.mapper.cron;

import java.util.List;
import java.util.Map;

public interface CronMapper {
	public List<Map<String,Object>> selectSchedulList() throws Exception;
}

