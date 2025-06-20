package com.proj.controller;

import com.proj.model.*;
import com.proj.repository.BookingRepository;
import com.proj.repository.BookingRepository.Flight;
import com.proj.repository.SeatRepository;
import com.proj.repository.SeatRepository.Seat;
import com.proj.repository.*; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "https://flight-ui.onrender.com")
public class TestController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    // 1. Search available flights
    @GetMapping("/search")
    public List<Flight> searchFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String date) {
        return bookingRepository.searchFlights(origin, destination, date);
    }

    // 2. Book a flight with multiple passengers
//    @PostMapping("/book")
//    public String bookFlight(@RequestBody BookingRequest request) {
//        bookingRepository.saveBooking(request);
//        return "Booking successful!";
//    }

    // 3. View user's past bookings
    @GetMapping("/past/{userId}")
    public List<BookingDetailsDTO> getPastBookings(@PathVariable int userId) {
        return bookingRepository.getPastBookings(userId);
    }

    // 4. Get available seats
    @GetMapping("/seats/available/{flightId}")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable int flightId) {
        List<Seat> seats = seatRepository.findAvailableSeatsByFlightId(flightId);
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("SeatController is working");
    }

    // 5. Get all origins and destinations
    @GetMapping("/origins")
    public List<String> getAllOrigins() {
        return jdbcTemplate.queryForList("SELECT DISTINCT origin FROM flights", String.class);
    }

    @GetMapping("/destinations")
    public List<String> getAllDestinations() {
        return jdbcTemplate.queryForList("SELECT DISTINCT destination FROM flights", String.class);
    }

    // 6. Send booking email (test/manual)
    @PostMapping("/send")
    public String sendBookingEmail(@RequestBody PassengerBookingRequest request) {
    	 bookingRepository.addPassengersAndSendEmail(request);
    	    return "Booking email sent!";
    }

    
    @PostMapping("/passengers/add")
    public ResponseEntity<String> addPassengers(@RequestBody PassengerBookingRequest request) {
        try {
            bookingRepository.addPassengersAndSendEmail(request);
            return ResponseEntity.ok("Passengers added and email sent successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 7. Add passengers and send email
//    @PostMapping(value = "/passengers/add", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> addPassengers(@RequestBody PassengerBookingRequest request) {
//        List<Passenger> passengers = request.getPassengers();
//        String userEmail = request.getEmail();
//
//        if (passengers == null || passengers.isEmpty()) {
//            return ResponseEntity.badRequest().body("Passenger list is empty.");
//        }
//
//        String sql = "INSERT INTO defaultdb.passengers (full_name, age, gender, passport_number, seat_number, user_id, flight_id, booking_date) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
//
//        for (Passenger p : passengers) {
//            jdbcTemplate.update(sql,
//                p.getFullName(),
//                p.getAge(),
//                p.getGender(),
//                p.getPassportNumber(),
//                p.getSeatNumber(),
//                p.getUserId(),
//                p.getFlightId()
//            );
//        }
//
//        // Send confirmation email to manually entered email
//        emailService.sendPassengerDetailsEmail(userEmail, passengers);
//
//        return ResponseEntity.ok("Passengers added and email sent successfully!");
//    }

//    @PostMapping("/generate-otp")
//    public ResponseEntity<String> generateOtp(@RequestBody String email) {
//        String otp = otpService.generateOtp(email); // generate and store OTP mapped to email
//        emailService.sendOtpEmail(email, otp);
//        return ResponseEntity.ok("OTP sent successfully");
//    }

    // 8. Generate OTP for payment
    @CrossOrigin(origins = "https://flight-ui.onrender.com")
    @PostMapping("/payment/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        // Generate OTP
        String otp = otpService.generateOtp(email);

        // Send OTP email
        try {
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok("OTP sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to send OTP email");
        }
    }


    // 9. Confirm payment using OTP and save booking
    @CrossOrigin(origins = "https://flight-ui.onrender.com")

    @PostMapping("/payment/confirm")
    public ResponseEntity<String> confirmPaymentAndBooking(@RequestBody PaymentConfirmationRequest request) {
        boolean valid = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }

        BookingRequest br = request.getBookingRequest();

        PassengerBookingRequest passengerRequest = new PassengerBookingRequest();
        passengerRequest.setEmail(request.getEmail());
        passengerRequest.setPassengers(br.getPassengers());
        passengerRequest.setFlightId(br.getFlightId());
        passengerRequest.setFlightName(br.getFlightName());
        passengerRequest.setDepartureTime(br.getDepartureTime());
        passengerRequest.setArrivalTime(br.getArrivalTime());

        bookingRepository.addPassengersAndSendEmail(passengerRequest);

        return ResponseEntity.ok("Payment confirmed and booking successful!");
    }
    
//    @GetMapping("/test-email")
//    public ResponseEntity<String> testEmail() {
//        try {
//            emailService.sendOtpEmail("vinyasdc22@gmail.com", "123456");
//            return ResponseEntity.ok("Test email sent");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send test email");
//        }
//    }


}
