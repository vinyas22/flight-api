package com.proj.model;

import java.sql.Date;

import com.google.protobuf.Timestamp;


public class BookingDetailsDTO {
	
	 private Long userId;
	 private Long flightId;
	    private String username;
	    private Long bookingId;
	    private String flightName;
	    private String origin;
	    private String destination;
	    private java.sql.Timestamp departureTime;
	    private java.sql.Timestamp arrivalTime;
	    private Long passengerId;
	    private String passengerName;
	    private int age;
	    private String gender;
	    private String passportNumber;
	    private String seatNumber;
	    private Date bookingDate;

		public Long getFlightId() {
			return flightId;
		}
		public void setFlightId(Long flightId) {
			this.flightId = flightId;
		}
		public Date getBookingDate() {
			return bookingDate;
		}
		public void setBookingDate(Date bookingDate) {
			this.bookingDate = bookingDate;
		}
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public Long getBookingId() {
			return bookingId;
		}
		public void setBookingId(Long bookingId) {
			this.bookingId = bookingId;
		}
		public String getFlightName() {
			return flightName;
		}
		public void setFlightName(String flightName) {
			this.flightName = flightName;
		}
		public String getOrigin() {
			return origin;
		}
		public void setOrigin(String origin) {
			this.origin = origin;
		}
		public String getDestination() {
			return destination;
		}
		public void setDestination(String destination) {
			this.destination = destination;
		}
		public java.sql.Timestamp getDepartureTime() {
		    return departureTime;
		}

		public void setDepartureTime(java.sql.Timestamp departureTime) {
		    this.departureTime = departureTime;
		}

		public java.sql.Timestamp getArrivalTime() {
		    return arrivalTime;
		}

		public void setArrivalTime(java.sql.Timestamp arrivalTime) {
		    this.arrivalTime = arrivalTime;
		}

		public Long getPassengerId() {
			return passengerId;
		}
		public void setPassengerId(Long passengerId) {
			this.passengerId = passengerId;
		}
		public String getPassengerName() {
			return passengerName;
		}
		public void setPassengerName(String passengerName) {
			this.passengerName = passengerName;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public String getPassportNumber() {
			return passportNumber;
		}
		public void setPassportNumber(String passportNumber) {
			this.passportNumber = passportNumber;
		}
		public String getSeatNumber() {
			return seatNumber;
		}
		public void setSeatNumber(String seatNumber) {
			this.seatNumber = seatNumber;
		}
	    
	    

}
