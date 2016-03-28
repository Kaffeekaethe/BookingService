package com.db.systel.bachelorproject2016.bookingservice;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.db.systel.bachelorproject2016.bookingservice.domainmodel.logic.BookingDAO;

@SpringBootApplication
public class BookingService {

	public static BookingDAO bookingDAO;
	
	public static SimpleDateFormat dateFormat;
	
	public static void main(String args[]) {
		
		dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");

		bookingDAO = (BookingDAO) context.getBean("bookingDAO");
		
		new SpringApplicationBuilder(BookingService.class).web(true).run(args);
		
	}

}