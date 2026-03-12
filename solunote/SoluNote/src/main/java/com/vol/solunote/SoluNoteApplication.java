package com.vol.solunote;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
@EnableScheduling
@MapperScan(value={"com.vol.solunote.**.mapper"})
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.vol.solunote")

public class SoluNoteApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SoluNoteApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SoluNoteApplication.class);
	}
	
	@Bean
	protected TaskScheduler taskScheduler() {
	    return new SimpleAsyncTaskScheduler(); //single threaded by default
	}
}
