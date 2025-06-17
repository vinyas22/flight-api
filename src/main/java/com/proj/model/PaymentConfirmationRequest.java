package com.proj.model;

public class PaymentConfirmationRequest {
    private String email;
    private String otp;
    private BookingRequest bookingRequest;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public BookingRequest getBookingRequest() { return bookingRequest; }
    public void setBookingRequest(BookingRequest bookingRequest) { this.bookingRequest = bookingRequest; }
}
