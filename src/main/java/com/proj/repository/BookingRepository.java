package com.proj.repository;

import com.proj.controller.EmailService;
import com.proj.model.BookingDetailsDTO;
import com.proj.model.BookingRequest;
import com.proj.model.PastBookingDTO;
import com.proj.model.Passenger;
import com.proj.model.PassengerBookingRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class BookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EmailService emailService; 

    // Search flights by origin, destination, and date
    public List<Flight> searchFlights(String origin, String destination, String date) {
        String sql = """
            SELECT flight_id, flight_name, origin, destination, departure_time, available_seats, price, arrival_time 
            FROM updation.flights 
            WHERE origin = ? AND destination = ? AND DATE(departure_time) = ?
        """;
        return jdbcTemplate.query(sql, new FlightRowMapper(), origin, destination, date);
    }
    
    @Transactional
    public void addPassengersAndSendEmail(PassengerBookingRequest request) {
        List<Passenger> passengers = request.getPassengers();
        String userEmail = request.getEmail();
        int flightId = request.getFlightId();

        if (passengers == null || passengers.isEmpty()) {
            throw new IllegalArgumentException("Passenger list is empty.");
        }

        // Fetch full flight details
        String flightSql = "SELECT flight_name, origin, destination, departure_time, arrival_time, price FROM updation.flights WHERE flight_id = ?";

        List<Map<String, Object>> flightList = jdbcTemplate.queryForList(flightSql, flightId);

        if (flightList.isEmpty()) {
            throw new IllegalArgumentException("No flight found with id " + flightId);
        }

        Map<String, Object> flightDetails = flightList.get(0);

        String flightName = (String) flightDetails.get("flight_name");
        String source = (String) flightDetails.get("origin");
        String destination = (String) flightDetails.get("destination");
        String departureTime = flightDetails.get("departure_time").toString();
        String arrivalTime = flightDetails.get("arrival_time").toString();
        Double price = ((Number) flightDetails.get("price")).doubleValue();


        // Insert passengers
        String sql = "INSERT INTO updation.passengers " +
                     "(full_name, age, gender, passport_number, seat_number, user_id, flight_id, booking_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Passenger p = passengers.get(i);
                ps.setString(1, p.getFullName());
                ps.setInt(2, p.getAge());
                ps.setString(3, p.getGender());
                ps.setString(4, p.getPassportNumber());
                ps.setString(5, p.getSeatNumber());
                ps.setInt(6, p.getUserId());
                ps.setInt(7, flightId);
            }

            public int getBatchSize() {
                return passengers.size();
            }
        });

        // Sends email with full flight details
        emailService.sendPassengerDetailsEmail(
            userEmail,
            passengers,
            flightName,
            source,
            destination,
            departureTime,
            arrivalTime,
            price
     
        );
    }





    // Save booking and passengers (transactional)
    @Transactional
//    public void saveBooking(BookingRequest request) {
//        String insertBookingSql = "INSERT INTO bookings (flight_id, user_id, booking_date) VALUES (?, ?, NOW())";
//        jdbcTemplate.update(insertBookingSql, request.getFlightId(), request.getUserId());
//
//        Integer bookingId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
//
//        String insertPassengerSql = "INSERT INTO passengers (booking_id, name, age, gender, passport_number, seat_number) VALUES (?, ?, ?, ?, ?, ?)";
//        for (Passenger p : request.getPassengers()) {
//            jdbcTemplate.update(insertPassengerSql,
//                bookingId,
//                p.getFullName(),
//                p.getAge(),
//                p.getGender(),
//                p.getPassportNumber(),
//                p.getPassportNumber());  // Seat number is assumed to be passport number
//        }
//    }

    // Get past bookings for user
    public List<BookingDetailsDTO> getPastBookings(int user_id) {
        String sql = """
            SELECT  
                p.user_id,
                f.flight_id,
                f.origin,
                f.destination,
                f.departure_time,
                f.arrival_time,
                p.passenger_id,
                p.full_name AS passenger_name,
                p.age,
                p.gender,
                p.passport_number,
                p.seat_number,
                p.booking_date
            FROM passengers p
            JOIN flights f ON p.flight_id = f.flight_id
            WHERE p.user_id = ?
        """;

        return jdbcTemplate.query(sql, new PastBookingRowMapper(), user_id);
    }

    // RowMapper for past bookings
    public class PastBookingRowMapper implements RowMapper<BookingDetailsDTO> {
        @Override
        public BookingDetailsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            BookingDetailsDTO dto = new BookingDetailsDTO();
            dto.setUserId(rs.getLong("user_id"));
            dto.setFlightId(rs.getLong("flight_id"));
            dto.setOrigin(rs.getString("origin"));
            dto.setDestination(rs.getString("destination"));
            dto.setDepartureTime(rs.getTimestamp("departure_time"));
            dto.setArrivalTime(rs.getTimestamp("arrival_time"));
            dto.setPassengerId(rs.getLong("passenger_id"));
            dto.setPassengerName(rs.getString("passenger_name"));
            dto.setAge(rs.getInt("age"));
            dto.setGender(rs.getString("gender"));
            dto.setPassportNumber(rs.getString("passport_number"));
            dto.setSeatNumber(rs.getString("seat_number"));
            dto.setBookingDate(rs.getDate("booking_date"));
            return dto;
        }
    }

    // Flight class
    public static class Flight {
        private int id;
        private String airline;
        private String origin;
        private String destination;
        private String departureTime;
        private int availableSeats;
        private double price;
        private String arrivalTime;
        public String getArrivalTime() {
			return arrivalTime;
		}
		public void setArrivalTime(String arrivalTime) {
			this.arrivalTime = arrivalTime;
		}
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getAirline() { return airline; }
        public void setAirline(String airline) { this.airline = airline; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getDepartureTime() { return departureTime; }
        public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    //Row mapper
    private static class FlightRowMapper implements RowMapper<Flight> {
        @Override
        public Flight mapRow(ResultSet rs, int rowNum) throws SQLException {
            Flight f = new Flight();
            f.setId(rs.getInt("flight_id"));
            f.setAirline(rs.getString("flight_name"));
            f.setOrigin(rs.getString("origin"));
            f.setDestination(rs.getString("destination"));
            f.setDepartureTime(rs.getString("departure_time"));
            f.setArrivalTime(rs.getString("arrival_time"));
            f.setAvailableSeats(rs.getInt("available_seats"));  
            f.setPrice(rs.getDouble("price"));
            return f;
        }
    }
}
