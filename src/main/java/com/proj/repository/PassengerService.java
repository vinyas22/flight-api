package com.proj.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {

	private final JdbcTemplate jdbcTemplate;

    public PassengerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addPassengerToBooking(int bookingId, String fullName, int age, String gender, String passportNumber, String seatNumber) {
        String sql = """
            INSERT INTO passengers (booking_id, full_name, age, gender, passport_number, seat_number)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql, bookingId, fullName, age, gender, passportNumber, seatNumber);
    }
}
