package com.bl.addressbookdb;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bl.addressbookdb.AddressBookService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class AddressBookServiceTest {
	
	@Test
	public void givenParticularAddressBookDB_WhenRetrieved_ShouldMatchRecordsCount() {
		AddressBookService addressBookService = new AddressBookService();
		int result;
		try {
			result = addressBookService.readAddressBookDB(IOService.DB_IO);
			Assert.assertEquals(11, result);
		} catch (AddressBookException e) {
		}
	}
	
	@Test
	public void givenAddressBookDB_WhenUpdated_ShouldSyncWithDB(){
		AddressBookService addressBookService = new AddressBookService();
		try {
			addressBookService.readAddressBookDB(IOService.DB_IO);
			addressBookService.updateEmail("Umesh", "umesh.deora@gmail.com");
			boolean result = addressBookService.checkContactInfoSyncWithDB("Umesh");
			Assert.assertTrue(result);
		} catch (AddressBookException e) {
		}
	}
	
	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchContactCount() {
		AddressBookService addressBookService = new AddressBookService();
		try {
			addressBookService.readAddressBookDB(IOService.DB_IO);
			LocalDate startDate = LocalDate.of(2018, 07, 01);
			LocalDate endDate = LocalDate.now();
			List<Details> addressBookList = addressBookService
					.readAddressBookForDateRange(IOService.DB_IO, startDate, endDate);
			Assert.assertEquals(9, addressBookList.size());
		} catch (AddressBookException e) {
		}
	}
	
	@Test
	public void givenAddressBookDB_WhenRetrievedByCityCount_ShouldReturnProperValue(){
		AddressBookService addressBookService = new AddressBookService();
		try {
			addressBookService.readAddressBookDB(IOService.DB_IO);
			Map<String, Integer> countOfCityByState;
			countOfCityByState = addressBookService.readContactCountByCity(IOService.DB_IO);
			Assert.assertTrue(countOfCityByState.get("Rajasthan").equals(4) && countOfCityByState.get("UP").equals(1) && countOfCityByState.get("Bihar").equals(1));
		} catch (AddressBookException e) {
		}
	}
	
	@Test 
	public void givenNewContact_WhenAdded_ShouldSyncWithDB(){
		AddressBookService addressBookService = new AddressBookService();
		try {
			addressBookService.readAddressBookDB(IOService.DB_IO);
			addressBookService.addContactsToAddressBook("Abhinav","Singh", 9983917564L, "abh@gmail.com", LocalDate.now());
			boolean result = addressBookService.checkContactInfoSyncWithDB("Abhinav"); 
			Assert.assertTrue(result);
		} catch (AddressBookException e) {
		}
	}
	
	@Test
	public void givenMultipleContacts_WhenAdded_ShouldMatchAddressBookEntries(){
		Details[] arrayOfContacts = {
			new Details("Jofra", "Archer", 7538964120L, "jfh@gmail.com", LocalDate.now()),
			new Details("Rahul", "Tewatiya", 1234567890L, "rtw@gmail.com", LocalDate.now()),
			new Details("Jos", "Buttler", 7894561230L, "jst@gmail.com", LocalDate.now()),
			new Details("Ben", "Stokes", 4569512378L, "bst@gmail.com", LocalDate.now()),
		};
		AddressBookService addressBookService = new AddressBookService();
		try {
			addressBookService.readAddressBookDB(IOService.DB_IO);
			Instant start = Instant.now();
			addressBookService.addContactsToAddressBookWithThread(Arrays.asList(arrayOfContacts));
			Instant end = Instant.now();
			System.out.println("Duration with thread: " + Duration.between(start, end));
			Assert.assertEquals(11,addressBookService.countEntries());
		} catch (AddressBookException e) {
		}
	}
	
	@Before 
	public void setup(){
		RestAssured.baseURI="http://localhost";
		RestAssured.port=3000;
	}
	public Details[] getContactsList() {
		Response response = RestAssured.get("/contacts");
		System.out.println("ADDRESS BOOK ENTRIES IN JSONServer:\n"+ response.asString());
		Details[] arrayOfContacts = new Gson().fromJson(response.asString(), Details[].class);
		return arrayOfContacts;
	}
	
	private Response addContactsToJsonServer(Details contactDetails) {
		String contactJson = new Gson().toJson(contactDetails);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/contacts");
	}
	
	@Test
	public void givenAddressBookDataInJSONServer_WhenRetrieved_ShouldMatchTheCount(){
		Details[] arrayOfContacts = getContactsList();
		AddressBookService addressBookService;
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		long entries = addressBookService.countEntries();
		Assert.assertEquals(2, entries);
	}
	
	@Test 
	public void givenContact_WhenAdded_ShouldGive201Response(){
		Details[] arrayOfContacts = getContactsList();
		AddressBookService addressBookService;
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		Details contactDetails=null;
		contactDetails = new Details(3,"Hitesh","Paliwal",1237896540L,"hts@gmail.com",LocalDate.now());
		Response response = addContactsToJsonServer(contactDetails);
		int result = response.getStatusCode();
		Assert.assertEquals(201, result);
	}
}
