package com.sap.businessobjects.amuller.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {

	private String id;
	private String firstName;
	private String lastName;
	
	public User() {
		// Nothing
	}
	
	public User(String id, String firstName, String lastName) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
}
