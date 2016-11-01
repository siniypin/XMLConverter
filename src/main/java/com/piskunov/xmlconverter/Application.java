package com.piskunov.xmlconverter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static com.aggregator.common.ApplicationUtil.prepareArgs;

@SpringBootApplication
public class Application {
    //run with allowed session as first parametes, all session will be activated otherwise
    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(Application.class, prepareArgs(args));

        JobLauncher launcher = context.getBean(JobLauncher.class);

        for (JobWrapper jobWrapper : context.getBeansOfType(JobWrapper.class).values()) {
            Job job = jobWrapper.getJob();
            if (job != null) {
                launcher.run(job, new JobParameters());
            }
        }
    }
}
