package com.vol.solunote.batch.scheduler.demo;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.vol.solunote.batch.service.demo.DemoSchedulerService;


@Configuration
public class DemoScheduler implements SchedulingConfigurer {
	@Autowired
	Environment env;
	
	@Autowired
	DemoSchedulerService schedulerService;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		
		setSecheduleSTT(taskRegistrar, "fixed.delay.string.sendrest");
		setSecheduleReport(taskRegistrar, "fixed.delay.string.report");
		setSecheduleStatus(taskRegistrar, "fixed.delay.string.status");
	}

	private void setSecheduleStatus(ScheduledTaskRegistrar taskRegistrar, String delayname) {
		
		if(getNewExecutionTime(delayname) <= 0) return;
		
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// Do not put @Scheduled annotation above this method, we don't need it anymore.
				try {
					schedulerService.getServerStatus();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				Calendar nextExecutionTime = new GregorianCalendar();
				@SuppressWarnings("deprecation")
				Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
				nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
				nextExecutionTime.add(Calendar.MILLISECOND, getNewExecutionTime(delayname));
				return nextExecutionTime.getTime();
			}

			@Override
			public @Nullable Instant nextExecution(TriggerContext triggerContext) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
	private void setSecheduleReport(ScheduledTaskRegistrar taskRegistrar, String delayname) {
		
		if(getNewExecutionTime(delayname) <= 0) return;
		
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// Do not put @Scheduled annotation above this method, we don't need it anymore.
				try {
					schedulerService.setReport();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				Calendar nextExecutionTime = new GregorianCalendar();
				@SuppressWarnings("deprecation")
				Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
				nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
				nextExecutionTime.add(Calendar.MILLISECOND, getNewExecutionTime(delayname));
				return nextExecutionTime.getTime();
			}

			@Override
			public @Nullable Instant nextExecution(TriggerContext triggerContext) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
	
	private void setSecheduleSTT(ScheduledTaskRegistrar taskRegistrar, String delayname) {
		
		if(getNewExecutionTime(delayname) <= 0) return;
		
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// Do not put @Scheduled annotation above this method, we don't need it anymore.
				try {
					schedulerService.sendPOSTResultSTT();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				Calendar nextExecutionTime = new GregorianCalendar();
				@SuppressWarnings("deprecation")
				Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
				nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
				nextExecutionTime.add(Calendar.MILLISECOND, getNewExecutionTime(delayname));
				return nextExecutionTime.getTime();
			}

			@Override
			public @Nullable Instant nextExecution(TriggerContext triggerContext) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	private int getNewExecutionTime(String name) {
		// Load Your execution time from database or property file
		return Integer.parseInt(env.getProperty(name));
	}
}

