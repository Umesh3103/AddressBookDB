package com.bl.addressbookdb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBookDBService {

	private static AddressBookDBService addressBookDBService;
	private PreparedStatement addressBookStatement; 

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

	public List<Details> getAddressBookData(ResultSet resultSet) throws AddressBookException {
		List<Details> addressBookList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				long phNum = resultSet.getLong("phone_number");
				String email = resultSet.getString("email");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				addressBookList.add(new Details(firstName, lastName, phNum, email,start));
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

	public int updateEmail(String name, String email) throws AddressBookException {
		return this.updateEmailUsingStatement(name,email);
	}

	private int updateEmailUsingStatement(String name, String email) throws AddressBookException {
		String sql = String.format("update contacts set email = '%s' where first_name ='%s';", email, name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
		}
	}

	public List<Details> getContactsData(String name) throws AddressBookException {
		List<Details> contactsList = null;
		if (this.addressBookStatement == null) {
			this.prepareStatementForContactData();
		}
		try {
			addressBookStatement.setString(1, name);
			ResultSet resultSet = addressBookStatement.executeQuery();
			contactsList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
		}
		return contactsList;
	}


	private void prepareStatementForContactData() throws AddressBookException {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM contacts WHERE first_name = ?";
			addressBookStatement = connection.prepareStatement(sql);

		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
		}
	}

	public List<Details> getAddressBookDetailsForDateRange(LocalDate startDate, LocalDate endDate) throws AddressBookException {
		String sql = String.format("SELECT * FROM contacts WHERE start BETWEEN '%s' AND '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getAddressBookDataUsingDB(sql);
	}

	public Map<String, Integer> readContactCountByCity() throws AddressBookException {
		String sql ="SELECT state, COUNT(city) as city_count FROM address GROUP BY state;";
		Map<String, Integer> countByCityStateMap= new HashMap<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String state = resultSet.getString("state");
				int cityCount = resultSet.getInt("city_count");
				countByCityStateMap.put(state, cityCount);
			}
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
		}
		return countByCityStateMap;
	}

	public Details addContactToAddressBook(String firstName, String lastName, long phNum, String email,
			LocalDate startDate) throws AddressBookException {
		int contactId=-1;
		Details contactsdata = null;
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
		}

		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO contacts (first_name, last_name, phone_number, email, start) VALUES ('%s', '%s', '%s', '%s', '%s')",
					firstName, lastName, phNum, email, Date.valueOf(startDate));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					contactId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return contactsdata;
			} catch (SQLException e1) {
				throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
			}
		}

		try (Statement statement = connection.createStatement()) {
			String address = "Satna";
			String city = "Katni";
			String state = "MP";
			int zip = 123456;
			String sql = String.format(
					"INSERT INTO address (id, address, city, state, zip) VALUES ('%s', '%s', '%s', '%s', '%s')",
					contactId, address, city, state, zip);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1){
				contactsdata = new Details(firstName,lastName, phNum, email,startDate, contactId, address, city, state, zip);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.DB_PROBLEM);
				}
			}
		}
		return contactsdata;
	}
}
