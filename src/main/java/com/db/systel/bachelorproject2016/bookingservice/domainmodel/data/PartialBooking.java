package com.db.systel.bachelorproject2016.bookingservice.domainmodel.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PartialBooking {

	private Integer lockEventID = null;
	private Integer partialBookingID = null;
	private Integer seatID = null;
	private Integer trainConnectionID = null;
	/*
	 * Name der Haltestelle
	 * 
	 * Fremdschlüsselbeziehung ist an dieser Stelle ja sowieso nicht möglich
	 */
	private String start = null;
	private String destination = null;
	/*
	 * Format: dd-MM-yyy HH:mm
	 */
	private String departureTime = null;
	private String arrivalTime = null;
	private Double price = null;
	private String state = null;

	public PartialBooking(@JsonProperty("lockEventID") Integer lockEventID,
			@JsonProperty("partialBookingID") Integer partialBookingID, @JsonProperty("seatID") Integer seatID,
			@JsonProperty("trainConnectionID") Integer trainConnectionID, @JsonProperty("start") String start,
			@JsonProperty("destination") String destination, @JsonProperty("departureTime") String departureTime,
			@JsonProperty("arrivalTime") String arrivalTime,
			@JsonProperty("price") Double price, @JsonProperty("state") String state) {

		this.setLockEventID(lockEventID);
		this.setPartialBookingID(partialBookingID);
		this.setSeatID(seatID);
		this.setTrainConnectionID(trainConnectionID);
		this.setStart(start);
		this.setDestination(destination);
		this.setPrice(price);
		this.setDepartureTime(departureTime);
		this.setArrivalTime(arrivalTime);
		this.setState(state);
	}

	public Integer getPartialBookingID() {
		return partialBookingID;
	}

	@JsonProperty("partialBookingID")
	public void setPartialBookingID(Integer partialBookingID) {
		this.partialBookingID = partialBookingID;
	}

	public Integer getSeatID() {
		return seatID;
	}

	@JsonProperty("seatID")
	public void setSeatID(Integer seatID) {
		this.seatID = seatID;
	}

	public Integer getTrainConnectionID() {
		return trainConnectionID;
	}

	@JsonProperty("trainConnectionID")
	public void setTrainConnectionID(Integer trainConnectionID) {
		this.trainConnectionID = trainConnectionID;
	}

	public String getStart() {
		return start;
	}

	@JsonProperty("start")
	public void setStart(String start) {
		this.start = start;
	}

	public String getDestination() {
		return destination;
	}

	@JsonProperty("destination")
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Double getPrice() {
		return price;
	}

	@JsonProperty("price")
	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getLockEventID() {
		return lockEventID;
	}

	@JsonProperty("lockEventID")
	public void setLockEventID(Integer lockEventID) {
		this.lockEventID = lockEventID;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	@JsonProperty("departureTime")
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	@JsonProperty("arrivalTime")
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	public String getState() {
		return state;
	}

	@JsonProperty("state")
	public void setState(String state) {
		this.state = state;
	}

}
