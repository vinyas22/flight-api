package com.proj.model;

import java.util.List;

public class BookingRequest {
    private int flightId;
    private String flightName;
    private String departureTime;
    private String arrivalTime;
    private List<Passenger> passengers;
	public int getFlightId() {
		return flightId;
	}
	public void setFlightId(int flightId) {
		this.flightId = flightId;
	}
	public String getFlightName() {
		return flightName;
	}
	public void setFlightName(String flightName) {
		this.flightName = flightName;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public List<Passenger> getPassengers() {
		return passengers;
	}
	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}

}
