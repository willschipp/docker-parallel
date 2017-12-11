package com.example.service;

import java.util.List;

public interface CodeService {

	String createCodeBase(String location,String username,String password) throws Exception;
	
	String parseCodeBase(String location) throws Exception;
	
	List<String> getTestBuckets(String location,int hostCount) throws Exception;
	
}
