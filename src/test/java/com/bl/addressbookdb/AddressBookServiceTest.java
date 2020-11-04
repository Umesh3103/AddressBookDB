package com.bl.addressbookdb;

import org.junit.Assert;
import org.junit.Test;

import com.bl.addressbookdb.AddressBookService.IOService;

public class AddressBookServiceTest {

	@Test
	public void givenAddressBookDB_WhenRetrieved_ShouldMatchRecordsCount(){
		AddressBookService addressBookService = new AddressBookService();
		int result = addressBookService.readAddressBookDB(IOService.DB_IO);
		Assert.assertEquals(6, result);
	}
}
