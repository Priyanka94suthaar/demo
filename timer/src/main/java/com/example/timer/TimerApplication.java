
package com.example.timer;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.jobs.FileScanJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@RestController
public class TimerApplication {
	private static final Logger LOG = LoggerFactory.getLogger(TimerApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(TimerApplication.class, args);
		fileScan("F://Priyanka/18-Feb/PaymentCompleteTimeAlertAdvOutput.txt");



	}
	@GetMapping(value = "/readData")
	public List<String> readData(){

		//List l = readFileInList("F://Priyanka/18-Feb/PaymentCompleteTimeAdvOutput.txt");
		List l = readFileInList("F://Priyanka/18-Feb/PaymentCompleteTimeAlertAdvOutput.txt");
		//l.addAll(l1);
		return l;

	}

	public static List<String> readFileInList(String fileName)
	{
		List<String> lines = Collections.emptyList();
		try
		{
			lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
		return lines;
	}

	private static void fileScan(String filename) {
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("fileScanTriggerName", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule("0/20- * * * * ?")).build();
		Scheduler scheduler;
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();

			JobKey jobKey = new JobKey("fileScanJobName", "group1");
			JobDetail job =
					JobBuilder.newJob(FileScanJob.class).withIdentity(jobKey).build();
			job.getJobDataMap().put(FileScanJob.FILE_NAME, filename);
			job.getJobDataMap().put(FileScanJob.FILE_SCAN_LISTENER_NAME, FileScanListenerDemo.LISTENER_NAME);
			scheduler.getContext().put(FileScanListenerDemo.LISTENER_NAME, new FileScanListenerDemo());
			scheduler.scheduleJob(job, trigger);

		} catch (SchedulerException e) {
			LOG.error(e.getMessage());
		}
	}

}
