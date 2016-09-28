package com.sap.businessobjects.amuller.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Users {

	@XmlElement(name = "user")
	public List<User> userList = new ArrayList<>();
}
