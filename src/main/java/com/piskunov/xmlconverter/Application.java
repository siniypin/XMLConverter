package com.piskunov.xmlconverter;

import com.piskunov.xmlconverter.categorizer.CategorizingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.aggregator.common.ApplicationUtil.prepareArgs;


@EntityScan(basePackages = {"com.piskunov.xmlconverter.model"})
@EnableTransactionManagement
@SpringBootApplication
public class Application {
    private static final String ENV_KEY = "env";
    private static final String SESSION_KEY = "session";

    private final static Log log = LogFactory.getLog(Application.class);

    //run with allowed session as first parametes, all session will be activated otherwise
    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(Application.class, prepareArgs(args));

        JobLauncher launcher = context.getBean(JobLauncher.class);

        String env = context.getEnvironment().getProperty(ENV_KEY);
        if (env == null) {
            log.error("Missing required parameter: --" + ENV_KEY);
            SpringApplication.exit(context);
            return;
        }

        String session = context.getEnvironment().getProperty(SESSION_KEY);

        context.getBean(CategorizingService.class).rebuildAll();

        for (JobWrapper jobWrapper : context.getBeansOfType(JobWrapper.class).values()) {
            if (session != null && !session.equals(jobWrapper.getSessionName())) {
                continue;
            }

            Job job = jobWrapper.getJob();
            if (job != null) {
                launcher.run(job, new JobParameters());
            }
        }
    }
}
