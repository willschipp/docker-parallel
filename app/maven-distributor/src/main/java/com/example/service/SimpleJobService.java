package com.example.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.example.domain.Host;
import com.example.domain.JobInstance;
import com.example.domain.JobInstanceRepository;

@Service
public class SimpleJobService implements JobService {

	@Autowired
	private JobInstanceRepository jobInstanceRepository;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	@Override
	public void createJobInstance(Host host, String uuid) {
		//create
		JobInstance jobInstance = new JobInstance();
		jobInstance.setCreatedDate(new Date());
		jobInstance.setHost(host);
		jobInstance.setUuid(uuid);
		//save
		jobInstanceRepository.save(jobInstance);

	}

	@Override
	public List<JobInstance> getAll() {
		return jobInstanceRepository.findAll();
	}

	@Override
	public void updateLog(String uuid, byte[] log) {
		//need to 'save' it
		JobInstance jobInstance = jobInstanceRepository.findByUuid(uuid);
		//check
		if (jobInstance == null) {
			//TODO handle
		}//end if
		//update
		jobInstance.setLog(log);
		//set finished
		jobInstance.setFinished(true);
		//save
		jobInstanceRepository.save(jobInstance);
		//hand off to process log
		taskExecutor.execute(new LogProcess(jobInstanceRepository,uuid));
	}

	@Override
	public JobInstance get(String uuid) {
		return jobInstanceRepository.findByUuid(uuid);
	}
	
	
	class LogProcess implements Runnable {

		private JobInstanceRepository jobInstanceRepository;
		
		private String uuid;
		
		public LogProcess(JobInstanceRepository jobInstanceRepository,String uuid) {
			this.jobInstanceRepository = jobInstanceRepository;
			this.uuid = uuid;
		}
		
		@Override
		public void run() {
			//get the uuid
			JobInstance jobInstance = jobInstanceRepository.findByUuid(uuid);
			//retrieve the log
			if (jobInstance.getLog() != null && jobInstance.getLog().length > 0) {
				//unzip the stream
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ByteArrayInputStream bis = new ByteArrayInputStream(jobInstance.getLog());
				ZipInputStream zis = new ZipInputStream(bis);
				try {					
					ZipEntry entry = zis.getNextEntry();
					while (entry != null) {
						int i = zis.read();
						while (i != -1) {
							bos.write(i);
							i = zis.read();
						}//end while
						entry = zis.getNextEntry();
					}//end while
					
					
					//now convert the outputstream to a string
					String log = new String(bos.toByteArray());
					if (log.contains("BUILD SUCCESS")) {
						jobInstance.setSuccessful(true);
					}//end if
					//save
					jobInstanceRepository.save(jobInstance);

				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}//end if
		}
		
	}

}
