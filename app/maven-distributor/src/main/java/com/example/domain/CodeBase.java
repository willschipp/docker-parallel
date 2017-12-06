package com.example.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class CodeBase {

	@Id
	private String url;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@OneToMany(fetch=FetchType.EAGER)
	private List<CodeTest> tests;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public List<CodeTest> getTests() {
		return tests;
	}

	public void setTests(List<CodeTest> tests) {
		this.tests = tests;
	}
	
	
	
}
