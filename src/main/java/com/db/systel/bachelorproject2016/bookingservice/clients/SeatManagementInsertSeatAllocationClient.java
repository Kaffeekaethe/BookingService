package com.db.systel.bachelorproject2016.bookingservice.clients;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.PartialBooking;

public class SeatManagementInsertSeatAllocationClient implements Runnable{
	List<PartialBooking> partialBookings;
	
	private static String hostAddress = "http://localhost";
	private static int port = 8085;

	public static RestTemplate restTemplate;

	public SeatManagementInsertSeatAllocationClient(List<PartialBooking> pb) {
		partialBookings = pb;
	}

	@Override
	public void run() {

		restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");

		for (PartialBooking partialBooking : partialBookings) {
			HttpEntity<PartialBooking> entity = new HttpEntity<PartialBooking>(partialBooking, headers);

			// TODO: Param richtig übergeben
			String URL = String.format("%s:%s/%s", hostAddress, port, "insert?type=seat_allocation");
			try {
				ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
				// Alle Antworten aneinander hängen
				System.out.println(response.getBody() + " ");

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}

	}
}
