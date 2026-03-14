package com.vol.solunote.batch.scheduler.sound;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.vol.solunote.batch.service.sound.SoundSchedulerService;

import lombok.extern.slf4j.Slf4j;

// ScheduledJobs.java 에서  profile 에 따라 선택적으로 Bean 을 생성하므로, 여기에 @Component 를 사용하면 무조건 생성된다. 
//@Component
@Slf4j
public class SoundScheduler {
	
	@Autowired
	SoundSchedulerService	schedulerService;
	
	/*
	 아래 Schedueld 를 enable 하는 조합
	 1. com.vol.solunote.config.ScheduledJobs.scheduledSoundJob() 에서 profile 에 맞게 @Bean 을 enable
	 2. 아래 @Scheduled enable
	 3. 위에서 @Component 를 disable
	 BeanRunner 에서 아래 method 를 호출할때는 1,2,3을 반대로 함
	 */
	
	@Scheduled( cron = "0 * * * * *")
	public void startUpdateTrainDir() throws Exception {

		log.debug("SCHEDULE : started startUpdateTrainDir");
		
		String category = "train";
		schedulerService.readDirectory(category);
		
		log.debug("SCHEDULE : ended startUpdateTrainDir");
	}

	@Scheduled( cron = "30 * * * * *")
	public void startUpdateTestDir() throws Exception {

		log.debug("SCHEDULE : started startUpdateTestDir");
		
		String category = "test";
		schedulerService.readDirectory(category);
		
		log.debug("SCHEDULE : ended startUpdateTestDir");
	}
		
	@Scheduled( cron = "${batch.SoundScheduler.startDeleteWavFile:-}")
	public void startDeleteWavFile() throws Exception {
		
		log.debug("SCHEDULE : started startDeleteWavFile");
		
		// 1. config 설정을 가져 옴
		List<Map<String, Object>> list = schedulerService.getSettingConfigList();
		for( Map<String, Object> map : list ) {
			String configName = (String) map.get("configName");
			if ( "SOUND_DELETE_TERM".equals(configName) ) {
				schedulerService.removeWavFile(map);
			}
		}			
		log.debug("SCHEDULE : ended startDeleteWavFile");
	}
}