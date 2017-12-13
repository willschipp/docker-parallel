package com.example.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.ExecutionService;
import com.example.service.FileService;

@RestController
@RequestMapping("/api/run")
public class ExecutionEndpoint {

	@Autowired
	private ExecutionService executionService;
	
	@Autowired
	private FileService fileService;
	
	@Value("${distributor.publisher.url}")
	private String url;
	
	@RequestMapping(method=RequestMethod.POST)
	public Map<String,String> run(@RequestBody Map<String,Object> request,HttpServletResponse response) throws Exception {
		//get the download
		String downloadedFile = fileService.getFile(url + "/file?fileName=" + request.get("git-url").toString());
		String downloadedLocation = fileService.extractFile(downloadedFile);
		//generate id
		final String uuid = UUID.randomUUID().toString();
		Map<String,String> variables = new HashMap<String,String>();
		variables.put("uuid", uuid);
		//execute
		executionService.run(downloadedLocation, request.get("options").toString(),uuid);
		//send back
		response.setStatus(HttpStatus.CREATED.value());
		//return variables
		return variables;
	}
	
}
