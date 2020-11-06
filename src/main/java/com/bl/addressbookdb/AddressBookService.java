package com.bl.addressbookdb;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bl.addressbookdb.AddressBookService.IOService;

public class AddressBookService {

	public enum IOService {
		DB_IO, REST_IO
	}

	private static AddressBookDBService addressBookDBService;
	private List<Details> addressBookList;

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public AddressBookService(List<Details> addressBookList){
		this();
		this.addressBookList=addressBookList;
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

	public List<Details> readAddressBookForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) throws AddressBookException {
		if (ioService.equals(IOService.DB_IO)) {
			return addressBookDBService.getAddressBookDetailsForDateRange(startDate, endDate);
		}
		return null;
	}

	public Map<String, Integer> readContactCountByCity(IOService ioService) throws AddressBookException {
		if (ioService.equals(IOService.DB_IO)) {
			return addressBookDBService.readContactCountByCity();
		}
		return null;
	}

	public void addContactsToAddressBook(String firstName, String lastName, long phNum, String email, LocalDate start) throws AddressBookException {
		addressBookList.add(addressBookDBService.addContactToAddressBook(firstName, lastName, phNum, email, start));
	}

	public int countEntries() {
		return addressBookList.size();
	}

	public void addContactsToAddressBookWithThread(List<Details> addressBookDataList) {
		Map<Integer, Boolean> addressBookAdditionStatus = new HashMap<>();
		addressBookDataList.forEach(addressBookData -> {
			Runnable task = () -> {
				addressBookAdditionStatus.put(addressBookData.hashCode(), false);
				System.out.println("Contact Being Added: "+Thread.currentThread().getName());
				try {
					this.addContactsToAddressBook(addressBookData.getFirstName(), addressBookData.getLastName(), addressBookData.getMobNum(), addressBookData.getEmail(), addressBookData.start);
				} catch (AddressBookException e) {
				}
				addressBookAdditionStatus.put(addressBookData.hashCode(), true);
				System.out.println("Contact Added: "+Thread.currentThread().getName());
			};
			Thread thread = new Thread(task,addressBookData.getFirstName());
			thread.start();
		});
		while(addressBookAdditionStatus.containsValue(false)){
			try{ Thread.sleep(10);
			} catch (InterruptedException e){	
			}
		}
	}
}
