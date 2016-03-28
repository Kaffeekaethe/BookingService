package com.db.systel.bachelorproject2016.bookingservice.domainmodel.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;

import com.db.systel.bachelorproject2016.bookingservice.BookingService;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.Booking;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.CustomerBooking;
import com.db.systel.bachelorproject2016.bookingservice.domainmodel.data.PartialBooking;
import com.mysql.jdbc.Statement;

public class JdbcBookingDAO implements BookingDAO {

	private DataSource dataSource;

	private ClassLoader classLoader = getClass().getClassLoader();

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Booking insertBooking(int customerID, String paymentMethod, List<PartialBooking> partialBookings) {

		int bookingID = 0;

		String insertBooking = "INSERT INTO booking " + "(customer_id, payment_method) VALUES (?, ?)";

		String insertPartialBooking = "INSERT INTO partial_booking "
				+ "(booking_id, seat_id, train_connection_id, start, destination, departure_time, arrival_time, price, state)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'valid')";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			PreparedStatement insertBookingStmt = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS);

			insertBookingStmt.setInt(1, customerID);
			insertBookingStmt.setString(2, paymentMethod);

			insertBookingStmt.executeUpdate();

			// Get the newly inserted booking
			ResultSet keyset = insertBookingStmt.getGeneratedKeys();

			keyset.next();
			bookingID = keyset.getInt(1);
			// = keyset.getInt("ID");

			// Add each partial booking
			PreparedStatement insertPtBookingStmt = conn.prepareStatement(insertPartialBooking);
			for (PartialBooking pb : partialBookings) {

				/*
				 * Die Daten kommen für Menschen gut lesbar rein und müssen für
				 * die Datenbank in Longs umgewandelt werden
				 */
				Date departure = BookingService.dateFormat.parse(pb.getDepartureTime());
				Date arrival = BookingService.dateFormat.parse(pb.getArrivalTime());

				insertPtBookingStmt.setInt(1, bookingID);
				insertPtBookingStmt.setInt(2, pb.getSeatID());
				insertPtBookingStmt.setInt(3, pb.getTrainConnectionID());
				insertPtBookingStmt.setString(4, pb.getStart());
				insertPtBookingStmt.setString(5, pb.getDestination());
				insertPtBookingStmt.setLong(6, departure.getTime());
				insertPtBookingStmt.setLong(7, arrival.getTime());
				insertPtBookingStmt.setDouble(8, pb.getPrice());
				insertPtBookingStmt.executeUpdate();
			}
			insertBookingStmt.close();
			insertPtBookingStmt.close();

		} catch (SQLException | ParseException e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}

		}
		return this.getBooking(bookingID);
	}

	public PartialBooking changePartialBooking(int partialBookingID, PartialBooking partialBooking) {

		PartialBooking oldData = null;
		String select = "SELECT * FROM partial_booking WHERE id = ?";

		String sql = "UPDATE partial_booking SET ";

		

		PreparedStatement ps = null;
		PreparedStatement selectStmt = null;
		Connection conn = null;

		try {
			if (partialBooking.getSeatID() != null) {
				sql += " seat_id =" + partialBooking.getSeatID() + ", ";
			}
			if (partialBooking.getTrainConnectionID() != null) {
				sql += " train_connection_id =" + partialBooking.getTrainConnectionID() + ", ";
			}
			if (partialBooking.getStart() != null) {
				sql += " start = '" + partialBooking.getStart() + "', ";
			}
			if (partialBooking.getDestination() != null) {
				sql += " destination = '" + partialBooking.getDestination() + "', ";
			}
			if (partialBooking.getDepartureTime() != null) {
				sql += " departure_time =" + BookingService.dateFormat.parse(partialBooking.getDepartureTime()).getTime() + ", ";
			}
			if (partialBooking.getArrivalTime() != null) {
				sql += " arrival_time =" + BookingService.dateFormat.parse(partialBooking.getArrivalTime()).getTime() + ", ";
			}
			if (partialBooking.getPrice() != null) {
				sql += " price =" + partialBooking.getPrice() + ", ";
			}

			sql = sql.substring(0, sql.length() - 2);
			sql += " WHERE id = ? ;";

			conn = dataSource.getConnection();

			selectStmt = conn.prepareStatement(select);
			selectStmt.setInt(1, partialBookingID);
			ResultSet partialBookingRS = selectStmt.executeQuery();

			if (partialBookingRS.next()) {
				oldData = parsePartialBooking(partialBookingRS);
				ps = conn.prepareStatement(sql);
				ps.setInt(1, partialBookingID);
				ps.executeUpdate();
				ps.close();
			}
			else{
				return null;
			}

		} catch (SQLException | ParseException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return oldData;

	}

	public PartialBooking deletePartialBooking(int partialBookingID) {

		PartialBooking partialBooking = null;

		String select = "SELECT * FROM partial_booking WHERE id = ?";
		String delete = "DELETE FROM partial_booking WHERE id = ?";
		PreparedStatement deleteStmt = null;
		PreparedStatement selectStmt = null;

		Connection conn = null;

		try {
			conn = dataSource.getConnection();

			selectStmt = conn.prepareStatement(select);
			selectStmt.setInt(1, partialBookingID);
			ResultSet partialBookingRS = selectStmt.executeQuery();

			if (partialBookingRS.next()) {
				partialBooking = parsePartialBooking(partialBookingRS);
				selectStmt.close();

				deleteStmt = conn.prepareStatement(delete);
				deleteStmt.setInt(1, partialBookingID);
				deleteStmt.executeUpdate();
				deleteStmt.close();
			} else {
				return null;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		return partialBooking;

	}

	public Booking getBooking(int bookingID) {
		Booking booking = null;

		String selectBookings = "SELECT * FROM booking WHERE id = ?";
		String selectPartialBookings = "SELECT * FROM partial_booking WHERE booking_id = ?";

		PreparedStatement selectBookingsStmt = null;
		PreparedStatement selectPartialBookingsStmt = null;
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			selectBookingsStmt = conn.prepareStatement(selectBookings);
			selectPartialBookingsStmt = conn.prepareStatement(selectPartialBookings);

			selectBookingsStmt.setInt(1, bookingID);
			ResultSet bookingsRS = selectBookingsStmt.executeQuery();

			if (bookingsRS.next()) {

				List<PartialBooking> partialBookings = new ArrayList<PartialBooking>();

				selectPartialBookingsStmt.setInt(1, bookingID);
				ResultSet partialBookingsRS = selectPartialBookingsStmt.executeQuery();

				// Dann für die einzelnen Buchungen die Teilbuchungne finden
				while (partialBookingsRS.next()) {

					partialBookings.add(parsePartialBooking(partialBookingsRS));
				}

				booking = new Booking(bookingsRS.getInt("id"), bookingsRS.getString("payment_method"), partialBookings);

			}

			selectBookingsStmt.close();
			selectPartialBookingsStmt.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return booking;

	}

	public List<Booking> getBookingsForCustomer(int customerID) {

		List<Booking> bookings = new ArrayList<Booking>();

		String selectBookings = "SELECT * FROM booking WHERE booking.customer_id = ?";
		String selectPartialBookings = "SELECT * FROM partial_booking WHERE booking_id = ?";

		PreparedStatement selectBookingsStmt = null;
		PreparedStatement selectPartialBookingsStmt = null;
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			selectBookingsStmt = conn.prepareStatement(selectBookings);
			selectPartialBookingsStmt = conn.prepareStatement(selectPartialBookings);

			selectBookingsStmt.setInt(1, customerID);
			ResultSet bookingsRS = selectBookingsStmt.executeQuery();

			// Erst die Buchungen auslesen, die zum Kunden gehören
			while (bookingsRS.next()) {

				List<PartialBooking> partialBookings = new ArrayList<PartialBooking>();

				selectPartialBookingsStmt.setInt(1, bookingsRS.getInt("id"));
				ResultSet partialBookingsRS = selectPartialBookingsStmt.executeQuery();

				// Dann für die einzelnen Buchungen die Teilbuchungne finden
				while (partialBookingsRS.next()) {

					partialBookings.add(parsePartialBooking(partialBookingsRS));

				}

				bookings.add(
						new Booking(bookingsRS.getInt("id"), bookingsRS.getString("payment_method"), partialBookings));

			}

			selectBookingsStmt.close();
			selectPartialBookingsStmt.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return bookings;
	}

	// TODO: Diese Methode könnte man eventuell splitten
	public List<CustomerBooking> updateDisabledBookings(List<Integer> seatIDs) {

		List<CustomerBooking> customerBookings = new ArrayList<CustomerBooking>();

		// Für die Gruppierung der Teilbuchungen und Kunden
		Map<Integer, List<PartialBooking>> dict = new HashMap<Integer, List<PartialBooking>>();

		String arrayAsString = seatIDs.get(0).toString();

		for (int seatID : seatIDs.subList(1, seatIDs.size())) {
			arrayAsString += ", " + seatID;
		}

		String updateBookings = "UPDATE partial_booking SET state = \"invalid\" WHERE seat_id in (" + arrayAsString
				+ ")";
		PreparedStatement updateBookingsStmt = null;

		String selectBookings = "SELECT * FROM partial_booking JOIN booking "
				+ " WHERE partial_booking.booking_id = booking.id " + " AND seat_id in (" + arrayAsString + ")";
		PreparedStatement selectBookingsStmt = null;

		Connection conn = null;

		try {
			conn = dataSource.getConnection();

			// Updated zuerst alle betroffenen Teilbuchungen
			updateBookingsStmt = conn.prepareStatement(updateBookings);

			// Liste der betroffenen Plätze in SQL-Format umwandeln

			updateBookingsStmt.executeUpdate();

			updateBookingsStmt.close();

			// alle Buchungen wählen
			selectBookingsStmt = conn.prepareStatement(selectBookings);

			ResultSet partialBookingsRS = selectBookingsStmt.executeQuery();

			// PartialBooking-Objekte erstellen
			while (partialBookingsRS.next()) {

				int customerID = partialBookingsRS.getInt("customer_id");

				PartialBooking pb = parsePartialBooking(partialBookingsRS);

				// Wenn der Kunde schon existiert, wird ihm die neue Teilbuchung
				// zugewiesen
				if (dict.containsKey(customerID)) {
					List<PartialBooking> currentBookings = dict.get(customerID);
					currentBookings.add(pb);
					dict.replace(customerID, currentBookings);
				}
				// Ansonsten wird ein neuer Eintrag im Dictionary angelegt
				else {
					List<PartialBooking> currentBookings = new ArrayList<PartialBooking>();
					currentBookings.add(pb);
					dict.put(customerID, currentBookings);
				}
			}

			selectBookingsStmt.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		// Dictionary in ArrayList tranferieren
		for (int key : dict.keySet()) {
			customerBookings.add(new CustomerBooking(key, dict.get(key)));
		}
		return customerBookings;
	}

	public boolean findCollidingBooking(int trainConnectionID, int seatID, String departureTime, String arrivalTime) {

		Connection conn = null;

		BookingService.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		try {
			String findBooking = IOUtils.toString(classLoader.getResourceAsStream("Queries/find-colliding-booking"),
					"UTF-8");

			conn = dataSource.getConnection();
			PreparedStatement findBookingStmt = conn.prepareStatement(findBooking);

			findBookingStmt.setInt(1, trainConnectionID);
			findBookingStmt.setInt(2, seatID);
			findBookingStmt.setLong(3, BookingService.dateFormat.parse(departureTime).getTime());
			findBookingStmt.setLong(4, BookingService.dateFormat.parse(arrivalTime).getTime());
			findBookingStmt.setLong(5, BookingService.dateFormat.parse(departureTime).getTime());
			findBookingStmt.setLong(6, BookingService.dateFormat.parse(arrivalTime).getTime());
			findBookingStmt.setLong(7, BookingService.dateFormat.parse(departureTime).getTime());
			findBookingStmt.setLong(8, BookingService.dateFormat.parse(arrivalTime).getTime());
			
			ResultSet rs = findBookingStmt.executeQuery();

			if (rs.next()) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException | IOException | ParseException e) {
			throw new RuntimeException(e);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}

		}

	}

	@Override
	public List<CustomerBooking> updateCancelledTrainConnection(int trainConnectionID, String day) {
		List<CustomerBooking> customerBookings = new ArrayList<CustomerBooking>();

		// Für die Gruppierung der Teilbuchungen und Kunden
		Map<Integer, List<PartialBooking>> dict = new HashMap<Integer, List<PartialBooking>>();

		Connection conn = null;

		try {
			String updateBookings = IOUtils
					.toString(classLoader.getResourceAsStream("Queries/update-cancelled-train-connection"), "UTF-8");

			String selectBookings = IOUtils
					.toString(classLoader.getResourceAsStream("Queries/select-cancelled-train-connection"), "UTF-8");

			conn = dataSource.getConnection();

			// Updated zuerst alle betroffenen Teilbuchungen
			PreparedStatement updateBookingsStmt = conn.prepareStatement(updateBookings);

			updateBookingsStmt.setInt(1, trainConnectionID);

			/*
			 * Buchungen, bei denen die Zeiten irgendwo an diesem Tag liegt
			 */
			updateBookingsStmt.setLong(2, BookingService.dateFormat.parse(day + " 00:00:00").getTime());

			updateBookingsStmt.setLong(3, BookingService.dateFormat.parse(day + " 00:00:00").getTime() + 86400000);
			// Liste der betroffenen Plätze in SQL-Format umwandeln

			updateBookingsStmt.executeUpdate();

			updateBookingsStmt.close();

			// alle Buchungen wählen
			PreparedStatement selectBookingsStmt = conn.prepareStatement(selectBookings);

			selectBookingsStmt.setInt(1, trainConnectionID);
			selectBookingsStmt.setLong(2, BookingService.dateFormat.parse(day + " 00:00").getTime());
			selectBookingsStmt.setLong(3, BookingService.dateFormat.parse(day + " 00:00").getTime() + 86400000);

			ResultSet partialBookingsRS = selectBookingsStmt.executeQuery();

			// PartialBooking-Objekte erstellen
			while (partialBookingsRS.next()) {

				int customerID = partialBookingsRS.getInt("customer_id");

				PartialBooking pb = parsePartialBooking(partialBookingsRS);

				// Wenn der Kunde schon existiert, wird ihm die neue Teilbuchung
				// zugewiesen
				if (dict.containsKey(customerID)) {
					List<PartialBooking> currentBookings = dict.get(customerID);
					currentBookings.add(pb);
					dict.replace(customerID, currentBookings);
				}
				// Ansonsten wird ein neuer Eintrag im Dictionary angelegt
				else {
					List<PartialBooking> currentBookings = new ArrayList<PartialBooking>();
					currentBookings.add(pb);
					dict.put(customerID, currentBookings);
				}
			}

			selectBookingsStmt.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		// Dictionary in ArrayList tranferieren
		for (int key : dict.keySet()) {
			customerBookings.add(new CustomerBooking(key, dict.get(key)));
		}
		return customerBookings;
	}

	private PartialBooking parsePartialBooking(ResultSet partialBookingsRS) {
		
		PartialBooking partialBooking = null;
		try {
			Date departure = new Date(partialBookingsRS.getLong("departure_time"));
			Date arrival = new Date(partialBookingsRS.getLong("arrival_time"));

			partialBooking = new PartialBooking(null, partialBookingsRS.getInt("id"),
					partialBookingsRS.getInt("seat_id"), partialBookingsRS.getInt("train_connection_id"),
					partialBookingsRS.getString("start"), partialBookingsRS.getString("destination"),
					BookingService.dateFormat.format(departure), BookingService.dateFormat.format(arrival), partialBookingsRS.getDouble("price"),
					partialBookingsRS.getString("state"));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return partialBooking;

	}

}
