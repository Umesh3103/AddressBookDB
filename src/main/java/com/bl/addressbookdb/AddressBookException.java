package com.bl.addressbookdb;

public class AddressBookException extends Exception{
	
	public enum ExceptionType{
		DB_PROBLEM
	}

	ExceptionType type;
	
	public AddressBookException(String message, ExceptionType type){
		super(message);
		this.type=type;
	}
}
