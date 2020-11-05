package com.bl.addressbookdb;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bl.addressbookdb.AddressBookService.IOService;

public class AddressBookServiceTest {

	@Test
	public void givenAddressBookDB_WhenRetrieved_ShouldMatchRecordsCount() {
		AddressBookService addressBookService = new AddressBookService();
		int result;
		try {
			result = addressBookService.readAddressBookDB(IOService.DB_IO);
			Assert.assertEquals(6, result);
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
			Assert.assertEquals(4, addressBookList.size());
		} catch (AddressBookException e) {
		}
	}
}
