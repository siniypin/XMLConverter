package com.piskunov.xmlconverter;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.piskunov.xmlconverter.mapping.MappingStatistics;

/**
 * Created by Vladimir Piskunov on 3/19/16.
 */
public class JobCleanUpListener implements JobExecutionListener {

    @Autowired
    MappingStatistics mappingStatistics;


    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        mappingStatistics.printStatistics();
        mappingStatistics.resetStatistics();
    }
}
