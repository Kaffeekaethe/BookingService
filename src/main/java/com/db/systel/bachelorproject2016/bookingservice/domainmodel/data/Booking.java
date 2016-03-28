package com.db.systel.bachelorproject2016.bookingservice.domainmodel.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Booking {

	private Integer bookingID = null;
	private String paymentMethod = null;
	private List<PartialBooking> partialBookings = null;

	public Booking(@JsonProperty("bookingID") int bookingID, @JsonProperty("paymentMethod") String paymentMethod,
			@JsonProperty("partialBookings") List<PartialBooking> partialBookings) {
		setBookingID(bookingID);
		setPaymentMethod(paymentMethod);
		setPartialBookings(partialBookings);
	}

	public Integer getBookingID() {
		return bookingID;
	}

	@JsonProperty("bookingID")
	public void setBookingID(Integer bookingID) {
		this.bookingID = bookingID;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	@JsonProperty("paymentMethod")
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public List<PartialBooking> getPartialBookings() {
		return partialBookings;
	}

	@JsonProperty("partialBookings")
	public void setPartialBookings(List<PartialBooking> partialBookings) {
		this.partialBookings = partialBookings;
	}

}
