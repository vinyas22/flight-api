package com.proj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.proj.model.Passenger;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP for Payment Confirmation");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 1 minute.");
        javaMailSender.send(message);
    }

    public void sendPassengerDetailsEmail(String toEmail, List<Passenger> passengers, String flightName, String source,
            String destination, String departureTime, String arrivalTime, double price) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("✈️ Booking Confirmed!\n\n");
        emailContent.append("📅 Flight Details:\n");
        emailContent.append("Flight: ").append(flightName).append("\n");
        emailContent.append("From: ").append(source).append("\n");
        emailContent.append("To: ").append(destination).append("\n");
        emailContent.append("Departure: ").append(departureTime).append("\n");
        emailContent.append("Arrival: ").append(arrivalTime).append("\n");
        emailContent.append("Price: ₹").append(price).append("\n\n");

        emailContent.append("👤 Passenger Details:\n");
        for (Passenger p : passengers) {
            emailContent.append("- Name: ").append(p.getFullName()).append(", Age: ").append(p.getAge())
                    .append(", Gender: ").append(p.getGender()).append(", Passport: ").append(p.getPassportNumber())
                    .append(", Seat: ").append(p.getSeatNumber()).append("\n");
        }

        emailContent.append("\nThank you for booking with SkyFly!");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("✅ Your Flight Booking Confirmation");
        message.setText(emailContent.toString());

        javaMailSender.send(message);
    }

    // Add this method if you want to send generic emails with subject and body
    public void sendSimpleEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
