package com.iokays.joda;

import java.util.Calendar;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JodaTest {
	
	private static final Logger logger = LoggerFactory.getLogger(JodaTest.class);
	
	public static void main(String[] args) {
		final DateTime dateTime = new DateTime();
		logger.info("dateTime: {}", dateTime);
		for (int i = 0; i < 21; i++) {
			logger.info(new DateTime(Calendar.getInstance()).plusDays(i).dayOfWeek().withMinimumValue().toString("yyyy-MM-dd HH:mm:ss"));
		}
	}
}
