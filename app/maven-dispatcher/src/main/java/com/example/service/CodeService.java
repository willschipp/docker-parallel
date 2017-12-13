package com.example.service;

import java.util.List;

import com.example.domain.CodeBase;

public interface CodeService {

	String createCodeBase(String location,String username,String password) throws Exception;
	
	String parseCodeBase(String location,boolean cucumber,String tag) throws Exception;
	
	List<String> getTestBuckets(String location,int hostCount) throws Exception;
	
	List<String> getFeatureBuckets(String location,int hostCount) throws Exception;

	CodeBase getCodeBase(String location) throws Exception;
	
	CodeBase getCodeBase(String location,boolean cucumber) throws Exception;
	
}
