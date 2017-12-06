package com.example.service;

import java.util.List;

import com.example.domain.Host;
import com.example.domain.JobInstance;

public interface JobService {

	void createJobInstance(Host host,String uuid);
	
	List<JobInstance> getAll();
	
	void updateLog(String uuid,byte[] log);

	JobInstance get(String uuid);
	
}
