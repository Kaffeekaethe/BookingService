package com.db.systel.bachelorproject2016.bookingservice.domainmodel.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartialBookingResult {

	private int seatID;
	private boolean bookingIsPossible;
	private int lockEventID;
	private int guardEventID;

	public PartialBookingResult(@JsonProperty("seatID") int seatID,
			@JsonProperty("bookingIsPossible") boolean bookingIsPossible, @JsonProperty("lockEventID") int lockEventID,
			@JsonProperty("guardEventID") int guardEventID) {

		setSeatID(seatID);
		setBookingIsPossible(bookingIsPossible);
		setLockEventID(lockEventID);
		setGuardEventID(guardEventID);
	}

	public Integer getSeatID() {
		return seatID;
	}

	@JsonProperty("seatID")
	public void setSeatID(Integer seatID) {
		this.seatID = seatID;
	}

	public Boolean getBookingIsPossible() {
		return bookingIsPossible;
	}

	@JsonProperty("bookingIsPossible")
	public void setBookingIsPossible(Boolean bookingIsPossible) {
		this.bookingIsPossible = bookingIsPossible;
	}

	public Integer getLockEventID() {
		return lockEventID;
	}

	@JsonProperty("lockEventID")
	public void setLockEventID(Integer lockEventID) {
		this.lockEventID = lockEventID;
	}

	public Integer getGuardEventID() {
		return guardEventID;
	}

	@JsonProperty("guardEventID")
	public void setGuardEventID(Integer guardEventID) {
		this.guardEventID = guardEventID;
	}
}
