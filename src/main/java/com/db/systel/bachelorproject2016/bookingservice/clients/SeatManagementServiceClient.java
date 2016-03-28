package com.db.systel.bachelorproject2016.bookingservice.clients;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.PartialBooking;

public class SeatManagementServiceClient {

	// TODO: Service Discovery für BuchungsDienste --> URL und Port
	private static String hostAddress = "http://localhost";
	private static int port = 8085;

	public static RestTemplate restTemplate;

	public static String requestDatabaseUpdate(List<PartialBooking> partialBookings) {

		String resp = "";

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
				resp += response.getBody() + " ";

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
		return resp;
	}

	/*
	 * Inform SeatManagement about chaning bookings
	 */
	public static String changeSeatAllocation(PartialBooking origin, PartialBooking newInformation) {

		String resp = "";

		restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");

		HttpEntity<PartialBooking> entity = new HttpEntity<PartialBooking>(origin, headers);

		String URL = String.format("%s:%s/%s", hostAddress, port,
				"change-seat-allocation?type=update&" + "trainConnectionID=" + newInformation.getTrainConnectionID()
						+ "&seatID=" + newInformation.getSeatID() + "&departureTime="
						+ newInformation.getDepartureTime() + "&arrivalTime=" + newInformation.getArrivalTime());
		try {
			ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
			// Alle Antworten aneinander hängen
			resp += response.getBody() + " ";

		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
		return resp;

	}

	/*
	 * Inform seat management that a booking has been cancelled
	 */
	public static String deleteSeatAllocation(PartialBooking partialBooking) {
		String resp = "";

		restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");

		HttpEntity<PartialBooking> entity = new HttpEntity<PartialBooking>(partialBooking, headers);

		// TODO: Param richtig übergeben
		String URL = String.format("%s:%s/%s", hostAddress, port, "change-seat-allocation?type=delete");
		try {
			ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
			// Alle Antworten aneinander hängen
			resp += response.getBody() + " ";

		} catch (Exception e) {
			System.out.println(e.getMessage());

		}

		return resp;

	}

}