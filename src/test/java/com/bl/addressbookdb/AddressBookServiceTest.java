package com.bl.addressbookdb;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.bl.addressbookdb.AddressBookService.IOService;


public class AddressBookServiceTest {
	
	@Test
	public void givenParticularAddressBookDB_WhenRetrieved_ShouldMatchRecordsCount() {
		AddressBookService addressBookService = new AddressBookService();
		int result;
		try {
			result = addressBookService.readAddressBookDB(IOService.DB_IO);
			Assert.assertEquals(7, result);
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
			Assert.assertEquals(5, addressBookList.size());
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
}
