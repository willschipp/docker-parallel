package com.example.service;

public interface FileService {

	String getFile(String url) throws Exception;
	
	String extractFile(String location) throws Exception;
}
