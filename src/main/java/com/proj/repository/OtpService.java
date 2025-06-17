package com.proj.repository;


import com.proj.model.OtpDetails;
import com.proj.model.OtpRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private ConcurrentHashMap<String, OtpDetails> otpStorage = new ConcurrentHashMap<>();
    private final int OTP_EXPIRY_MINUTES = 2;

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        long expiryTime = System.currentTimeMillis() + (OTP_EXPIRY_MINUTES * 60 * 1000);
        otpStorage.put(email, new OtpDetails(otp, expiryTime));
        return otp;
    }

    public boolean validateOtp(String email, String inputOtp) {
        OtpDetails details = otpStorage.get(email);
        if (details == null || details.isExpired()) {
            otpStorage.remove(email); // Remove expired OTP
            return false;
        }
        if (details.getOtp().equals(inputOtp)) {
            otpStorage.remove(email); // OTP used, remove it
            return true;
        }
        return false;
    }
}
