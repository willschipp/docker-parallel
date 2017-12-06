package com.example.service;

import java.util.List;

public interface CodeService {

	void createCodeBase(String location) throws Exception;
	
	List<String> getTestBuckets(String location,int hostCount) throws Exception;
	
}
