package com.piskunov.xmlconverter;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {

		ApplicationContext context = SpringApplication.run(Application.class, args);

		JobLauncher launcher = context.getBean(JobLauncher.class);

		Map m = context.getBeansOfType(JobWrapper.class);

		for(JobWrapper jobWrapper : context.getBeansOfType(JobWrapper.class).values()) {
			launcher.run(jobWrapper.getJob(), new JobParameters());
		}

	}
}
