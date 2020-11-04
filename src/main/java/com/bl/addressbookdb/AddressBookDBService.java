package com.bl.addressbookdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {

	private static AddressBookDBService addressBookDBService;

	private AddressBookDBService() {

	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null) {
			addressBookDBService = new AddressBookDBService();
		}
		return addressBookDBService;
	}

	public List<Details> readData() throws AddressBookException {
		String sql = "SELECT * FROM contacts";
		return this.getAddressBookDataUsingDB(sql);
	}

	private List<Details> getAddressBookDataUsingDB(String sql) throws AddressBookException {
		List<Details> addressBookList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			addressBookList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
		}
		return addressBookList;
	}

	private List<Details> getAddressBookData(ResultSet resultSet) throws AddressBookException {
		List<Details> addressBookList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				long phNum = resultSet.getLong("phone_number");
				String email = resultSet.getString("email");
				addressBookList.add(new Details(firstName, lastName, phNum, email));
			}
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
		}
		return addressBookList;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/address_book_service?useSSL=false";
		String userName = "root";
		String password = "root";
		Connection con;
		System.out.println("Connecting to database:" + jdbcURL);
		con = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection is successful!!!" + con);
		return con;
	}
}
