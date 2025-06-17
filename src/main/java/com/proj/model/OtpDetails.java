package com.proj.model;

public class OtpDetails {
	    private String otp;
	    private long expiryTime;

	    public OtpDetails(String otp, long expiryTime) {
	        this.otp = otp;
	        this.expiryTime = expiryTime;
	    }

	    public String getOtp() {
	        return otp;
	    }

	    public long getExpiryTime() {
	        return expiryTime;
	    }

	    public boolean isExpired() {
	        return System.currentTimeMillis() > expiryTime;
	    }


}
