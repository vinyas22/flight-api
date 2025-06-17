package com.proj.model;

public class AvailableSeatsDTO {
    private int flightId;
    private int totalSeats;
    private int bookedSeats;
    private int availableSeats;

    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
