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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.domain.Host;
import com.example.domain.HostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SimpleHostService implements HostService {

	@Autowired
	private HostRepository hostRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private JobService jobService;
	
	@Override
	public int count() {
		return new Long(hostRepository.count()).intValue();
	}

	@Override
	public void register(String ipAddress, String port) {
		Host host = hostRepository.findByAddressAndPort(ipAddress,port);
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
	@Async
	public void run(Host host, String location, String parameters) throws Exception {
		run(host,location,parameters,new String[0]);
	}
	
	@Override
	public void run(Host host, String location, String parameters, String... options) throws Exception {
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
		
		//build the options
		StringBuffer buffer = new StringBuffer();
		for (String option : options) {
			buffer.append(" ").append(option);
		}//end for
		
		//create the json object
		Map<String,String> requestObject = new HashMap<String,String>();
		requestObject.put("git-url",location);
		requestObject.put("options",parameters + buffer.toString());
		
		String json = mapper.writeValueAsString(requestObject);
		
		System.out.println(json);
		
		//send
		HttpEntity<String> entity = new HttpEntity<String>(json,headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(new URI(url), entity, Map.class);
		//save
		jobService.createJobInstance(host, response.getBody().get("uuid").toString());		
	}	

	@Override
	public void processLog(String uuid, byte[] bytes) {
		//byte array will be a zip
		jobService.updateLog(uuid, bytes);
	}



}
