package com.bl.addressbookdb;

import java.time.LocalDate;
import java.util.*;

public class Details {
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String state;
	private int zip;
	private String email;
	private Long phNum;
	private int id;
	public LocalDate start;

	public Details() {

	}

	public Details(String firstName, String lastName, Long phNum,String email, LocalDate start) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phNum = phNum;
		this.email = email;
		this.start=start;
	}
	
	public Details(String firstName, String lastName, Long phNum,String email, LocalDate start, String city, String state){
		this(firstName, lastName, phNum, email,start);
		this.city=city;
		this.state=state;
	}

	public Details(String firstName, String lastName, Long phNum,String email,LocalDate start,int contactId, String address, String city, String state, int zip) {
		this(firstName, lastName, phNum, email,start);
		this.id=contactId;
		this.address=address;
		this.city=city;
		this.state=state;
		this.zip=zip;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public Long getMobNum() {
		return phNum;
	}

	public void setMobNum(Long mobNum) {
		this.phNum = mobNum;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Details [firstName=" + firstName + ", lastName=" + lastName + "]";
	}

	@Override
	public int hashCode(){
		return Objects.hash(firstName, lastName, phNum, email, start);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Details other = (Details) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}
	
	
}
