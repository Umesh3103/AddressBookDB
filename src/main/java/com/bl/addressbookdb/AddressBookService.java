package com.bl.addressbookdb;

import java.util.List;

public class AddressBookService {

	public enum IOService {
		DB_IO, REST_IO
	}

	private static AddressBookDBService addressBookDBService;
	private List<Details> addressBookList;

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public int readAddressBookDB(IOService ioService) throws AddressBookException {
		if (ioService.equals(IOService.DB_IO)) {
			this.addressBookList = addressBookDBService.readData();
		}
		return this.addressBookList.size();
	}

}
