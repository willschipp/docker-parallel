package com.example.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Host {
	
	@Id
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
		
}