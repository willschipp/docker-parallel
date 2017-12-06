package com.example.service;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.domain.Host;
import com.example.domain.HostRepository;
import com.example.domain.JobInstance;
import com.example.domain.JobInstanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SimpleHostService implements HostService {

	@Autowired
	private HostRepository hostRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private JobInstanceRepository jobInstanceRepository;
	
	@Override
	public int count() {
		return new Long(hostRepository.count()).intValue();
	}

	@Override
	public void register(String ipAddress, String port) {
		Host host = hostRepository.findOne(ipAddress);
		if (host == null) {
			host = new Host();
			host.setAddress(ipAddress);
		}//end if
		host.setUpdatedDate(new Date());
		host.setPort(port);
		//save
		hostRepository.save(host);
	}

	@Override
	public List<Host> getAll() {
		return hostRepository.findAll();
	}

	@Override
	public void run(Host host, String location, String parameters) throws Exception {
		//create the url
		String url = "http://" + host.getAddress() + ":" + host.getPort() + "/api/run";
		//init
		RestTemplate restTemplate = new RestTemplate();
		
		//sort out the parameters
		if (parameters != null && parameters.length() > 0) {
			parameters = "-Dtest=" + parameters;
		}//end if
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		//create the json object
		Map<String,String> requestObject = new HashMap<String,String>();
		requestObject.put("git-url",location);
		requestObject.put("options",parameters);
		//send
		HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(requestObject),headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(new URI(url), entity, Map.class);
		
		JobInstance jobInstance = new JobInstance();
		jobInstance.setCreatedDate(new Date());
		jobInstance.setHost(host);
		jobInstance.setUuid(response.getBody().get("uuid").toString());
		//save
		jobInstanceRepository.save(jobInstance);
	}

	@Override
	public void processLog(String uuid, byte[] bytes) {
		//byte array will be a zip
		//need to 'save' it
		JobInstance jobInstance = jobInstanceRepository.findByUuid(uuid);
		//check
		if (jobInstance == null) {
			//TODO handle
		}//end if
		//update
		jobInstance.setLog(bytes);
		//save
		jobInstanceRepository.save(jobInstance);
	}

}
