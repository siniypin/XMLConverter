package com.piskunov.xmlconverter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.System;
import java.util.Arrays;
import java.util.logging.Logger;

@SpringBootApplication
public class XmlConverterApplication {


	public static void main(String[] args) {

		SpringApplication.run(XmlConverterApplication.class, args);

	}
}
