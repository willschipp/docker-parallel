package com.example.service;

import java.util.List;

import com.example.domain.CodeBase;

public interface CodeService {

	String createCodeBase(String location,String username,String password) throws Exception;
	
	String parseCodeBase(String location) throws Exception;
	
	List<String> getTestBuckets(String location,int hostCount) throws Exception;

	CodeBase getCodeBase(String location) throws Exception;
	
}
