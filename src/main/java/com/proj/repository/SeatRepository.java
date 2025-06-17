package com.proj.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class SeatRepository {

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public List<Seat> findSeatsByFlightId(int flightId) {
		String sql = "SELECT * FROM seats WHERE flight_id = ?";
        return jdbcTemplate.query(sql, new SeatRowMapper(), flightId);
	}

	public List<Seat> findAvailableSeatsByFlightId(int flightId) {
	    String sql = "SELECT * FROM seats WHERE flight_id = ? AND is_booked = false";
	    return jdbcTemplate.query(sql, new SeatRowMapper(), flightId);
	}




	// Seat class and RowMapper
	public static class Seat {
	    private int seatId;
	    private int flightId;
	    private String seatNumber;
	    private boolean isBooked;
		public int getSeatId() {
			return seatId;
		}
		public void setSeatId(int seatId) {
			this.seatId = seatId;
		}
		public int getFlightId() {
			return flightId;
		}
		public void setFlightId(int flightId) {
			this.flightId = flightId;
		}
		public String getSeatNumber() {
			return seatNumber;
		}
		public void setSeatNumber(String seatNumber) {
			this.seatNumber = seatNumber;
		}
		public boolean isBooked() {
			return isBooked;
		}
		public void setBooked(boolean isBooked) {
			this.isBooked = isBooked;
		}

	   }

	public class SeatRowMapper implements RowMapper<Seat> {
	    @Override
	    public Seat mapRow(ResultSet rs, int rowNum) throws SQLException {
	        Seat seat = new Seat();
	        seat.setSeatId(rs.getInt("seat_id"));
	        seat.setFlightId(rs.getInt("flight_id"));
	        seat.setSeatNumber(rs.getString("seat_number"));
	        seat.setBooked(rs.getBoolean("is_booked"));
	        return seat;
	    }
	}
	
}
