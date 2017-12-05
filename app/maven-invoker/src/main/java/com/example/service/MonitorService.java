package com.example.service;

import java.util.Set;

public interface MonitorService {

	boolean isFinished(String uuid) throws Exception;
	
	int runningCount() throws Exception;
	
	void register(String uuid);
	
	String getLog(String uuid);
	
	void update(String uuid,String log);
	
	Set<String> getJobs() throws Exception;
}
