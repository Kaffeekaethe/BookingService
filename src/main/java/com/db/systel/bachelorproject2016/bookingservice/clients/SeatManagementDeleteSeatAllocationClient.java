package com.db.systel.bachelorproject2016.bookingservice.clients;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.PartialBooking;

public class SeatManagementDeleteSeatAllocationClient implements Runnable {

	PartialBooking partialBooking;
	
	/*
	 * TODO: Implement Service Discovery when deploying in a cluster
	 */
	
	private static String hostAddress = "http://localhost";
	private static int port = 8085;

	public static RestTemplate restTemplate;

	public SeatManagementDeleteSeatAllocationClient(PartialBooking pb) {
		partialBooking = pb;
	}

	@Override
	public void run() {

		restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");

		HttpEntity<PartialBooking> entity = new HttpEntity<PartialBooking>(partialBooking, headers);

		String URL = String.format("%s:%s/%s", hostAddress, port, "change-seat-allocation?type=delete");
		try {
			ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
			
			System.out.print(response.getBody() + " ");

		} catch (Exception e) {
			System.out.println(e.getMessage());

		}

	}

}
