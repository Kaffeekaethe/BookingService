package com.db.systel.bachelorproject2016.bookingservice.domainmodel.logic;

import java.util.List;

import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.Booking;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.CustomerBooking;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.PartialBooking;

public interface BookingDAO {

	public Booking insertBooking(int customerID, String paymentMethod, List<PartialBooking> partialBookings);

	/*
	 * Returns overriden database values
	 */
	public PartialBooking changePartialBooking(int partialBookingID, PartialBooking partialBooking);

	public PartialBooking deletePartialBooking(int partialBookingID);

	public List<Booking> getBookingsForCustomer(int customerID);

	public List<CustomerBooking> updateDisabledBookings(List<Integer> platzIDs);

	public List<CustomerBooking> updateCancelledTrainConnection(int trainConnectionID, String day);

	public boolean findCollidingBooking(int trainConnectionID, int seatID, String departureTime, String arrivalTime);
}
