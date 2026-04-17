package com.vol.solunote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.vol.solunote.batch.scheduler.train.TrainScheduler;

@Configuration
@EnableScheduling
public class ScheduledJobs {

    // @Bean
    //TrainScheduler scheduledTrainJob() {
	//	return new TrainScheduler();
	//}
	
////	@Profile({ "pc"})
//	@Profile({ "devel", "dev", "prod", "demo"})
//	@Bean
//	public SoundScheduler scheduledSoundJob() {
//		return new SoundScheduler();
//	}
//	
////	@Profile({ "pc"})
//	@Profile({ "devel", "dev", "prod", "demo"})
//	@Bean(initMethod = "initSoundLauncher")   // MeetingLauncher.java 안에 initMeetingLauncher() 을 실행한다
//	public SoundLauncher callSoundInit() {
//	    return new SoundLauncher();
//	}
}