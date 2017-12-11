package com.example.service;

import java.util.List;

import com.example.domain.Host;

public interface HostService {

	int count();
	
	void register(String ipAddress,String port);

	List<Host> getAll();
	
	void run(Host host,String location,String parameters) throws Exception;

	void processLog(String uuid, byte[] bytes);
	
}
