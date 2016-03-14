package com.piskunov.xmlconverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.logging.Logger;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = XmlConverterApplication.class)
public class XmlConverterApplicationTests {

	@Autowired
	ApplicationContext context;

	@Test
	public void contextLoads() {

		SessionParameters sp = (SessionParameters) context.getBean("sessionParmeters");
		Logger.getLogger(getClass().getName()).info(sp.getPartnerID());

	}

}
