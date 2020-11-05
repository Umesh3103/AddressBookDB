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

	public void updateEmail(String name, String email) throws AddressBookException {
		int result = addressBookDBService.updateEmail(name,email);
		if (result == 0)
			return;
		Details contactDetails = this.getContactsData(name);
		if (contactDetails != null)
			contactDetails.setEmail(email);
	}

	private Details getContactsData(String name) {
		return this.addressBookList.stream()
				.filter(addressBookDataItem -> addressBookDataItem.getFirstName().equals(name)).findFirst().orElse(null);
	}

	public boolean checkContactInfoSyncWithDB(String name) throws AddressBookException {
		List<Details> contactsDataList = addressBookDBService.getContactsData(name);
		return contactsDataList.get(0).equals(getContactsData(name));
	}
}
