package com.proj.model;

public class OtpRecord {
	
	 private String email;
	    private String otp;
	    private long expirationTime;
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getOtp() {
			return otp;
		}
		public void setOtp(String otp) {
			this.otp = otp;
		}
		public long getExpirationTime() {
			return expirationTime;
		}
		public void setExpirationTime(long expirationTime) {
			this.expirationTime = expirationTime;
		}


}
