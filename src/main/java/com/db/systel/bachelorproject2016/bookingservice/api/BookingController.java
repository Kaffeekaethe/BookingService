package com.db.systel.bachelorproject2016.bookingservice.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.db.systel.bachelorproject2016.bookingservice.BookingService;
import com.db.systel.bachelorproject2016.bookingservice.clients.SeatManagementDeleteSeatAllocationClient;
import com.db.systel.bachelorproject2016.bookingservice.clients.SeatManagementInsertSeatAllocationClient;
import com.db.systel.bachelorproject2016.bookingservice.clients.SeatManagementUpdateSeatAllocationClient;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.Booking;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.CustomerBooking;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.PartialBooking;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.PartialBookingResult;

@EnableAutoConfiguration
@Controller
public class BookingController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<String> home() {
		return new ResponseEntity<String>("I am a BookingService.", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/get-customer-bookings")
	public ResponseEntity<List<Booking>> customerBookings(@RequestParam int customerID) {

		System.out.println("Received request for bookings of customer " + customerID);
		List<Booking> bookings = BookingService.bookingDAO.getBookingsForCustomer(customerID);
		return new ResponseEntity<List<Booking>>(bookings, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/initiate-partial-bookings")
	public ResponseEntity<?> partialBookingResults(@RequestBody List<PartialBooking> partialBookings) {

		List<Integer> failedBookings = new ArrayList<Integer>();
		// TODO: Logik mit Redis
		for (PartialBooking pb : partialBookings) {
			if (BookingService.bookingDAO.findCollidingBooking(pb.getTrainConnectionID(), pb.getSeatID(),
					pb.getDepartureTime(), pb.getArrivalTime())) {
				failedBookings.add(pb.getSeatID());

			}
		}

		if (failedBookings.size() == 0) {
			return new ResponseEntity<List<PartialBookingResult>>(HttpStatus.OK);
		}
		return new ResponseEntity<List<Integer>>(failedBookings, HttpStatus.CONFLICT);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/confirm-partial-bookings")
	public ResponseEntity<?> customerBookings(@RequestParam int customerID, @RequestParam String paymentMethod,
			@RequestBody List<PartialBooking> partialBookings) {

		ResponseEntity<?> initiation = this.partialBookingResults(partialBookings);

		if (initiation.getStatusCode() == HttpStatus.CONFLICT) {

			/*
			 * Check if bookings is okay
			 */
			List<Integer> failedSeats = (List<Integer>) initiation.getBody();
			String response = "Booking collides on seats: ";
			for (Integer seat : failedSeats) {
				response += seat + " ";
			}
			return new ResponseEntity<String>(response, HttpStatus.CONFLICT);
		}

		System.out.println(
				"Customer with ID " + customerID + " confirmed " + partialBookings.size() + " partial Bookings");
		;

		Booking booking = BookingService.bookingDAO.insertBooking(customerID, paymentMethod, partialBookings);

		Thread requestNewAllocation = new Thread(new SeatManagementInsertSeatAllocationClient(partialBookings));
		requestNewAllocation.start();

		return new ResponseEntity<Booking>(booking, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/cancel-partial-bookings")
	public ResponseEntity<Void> cancelBookings(@RequestBody List<Integer> guardEventIDs) {
		// TODO: Use Event Store here
		System.out.println("Cancelled partial bookings in Event Store");
		for (int ID : guardEventIDs) {
			try {
				BookingService.bookingDAO.deletePartialBooking(ID);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/change-partial-booking")
	public ResponseEntity<?> changeBookings(@RequestParam int partialBookingID,
			@RequestBody PartialBooking newInformation) {

		System.out.println("Updating a partial Booking");

		try {
			PartialBooking origin = BookingService.bookingDAO.changePartialBooking(partialBookingID, newInformation);

			// SeatManagementServiceClient.changeSeatAllocation(origin,
			// newInformation);

			Thread requestUpdate = new Thread(new SeatManagementUpdateSeatAllocationClient(origin, newInformation));
			requestUpdate.start();

			return new ResponseEntity<PartialBooking>(origin, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/delete-partial-booking")
	public ResponseEntity<String> deleteBookings(@RequestParam int partialBookingID) {

		System.out.println("Deleting a partial Booking");

		try {
			PartialBooking partialBooking = BookingService.bookingDAO.deletePartialBooking(partialBookingID);

			if (partialBooking != null) {
				// SeatManagementServiceClient.deleteSeatAllocation(partialBooking);

				Thread requestDelete = new Thread(new SeatManagementDeleteSeatAllocationClient(partialBooking));
				requestDelete.start();
				return new ResponseEntity<String>("Successfully deleted", HttpStatus.OK);
			}

			return new ResponseEntity<String>("Could not delete", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(method = RequestMethod.POST, value = "/disable-partial-bookings")
	public ResponseEntity<List<CustomerBooking>> disableBookings(@RequestBody List<Integer> seatIDs) {

		List<CustomerBooking> customerBookings = BookingService.bookingDAO.updateDisabledBookings(seatIDs);
		return new ResponseEntity<List<CustomerBooking>>(customerBookings, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/cancel-train-connection")
	public ResponseEntity<List<CustomerBooking>> cancelTrainConnection(@RequestParam int trainConnectionID,
			String day) {

		List<CustomerBooking> customerBookings = BookingService.bookingDAO
				.updateCancelledTrainConnection(trainConnectionID, day);
		return new ResponseEntity<List<CustomerBooking>>(customerBookings, HttpStatus.OK);

	}
}