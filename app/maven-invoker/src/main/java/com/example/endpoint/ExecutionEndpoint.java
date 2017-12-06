package com.example.endpoint;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.CloneService;
import com.example.service.ExecutionService;

@RestController
@RequestMapping("/api/run")
public class ExecutionEndpoint {

	@Autowired
	private ExecutionService executionService;
	
	@Autowired
	private CloneService cloneService;
	
	@RequestMapping(method=RequestMethod.POST)
	public Map<String,String> run(@RequestBody Map<String,Object> request,HttpServletResponse response) throws Exception {
		//clone
		Map<String,String> variables = cloneService.clone(request.get("git-url").toString());
		//generate id
		final String uuid = UUID.randomUUID().toString();
		variables.put("uuid", uuid);
		//execute
		executionService.run(variables.get("location"), request.get("options").toString(),uuid);
		//send back
		response.setStatus(HttpStatus.CREATED.value());
		//return variables
		return variables;
	}
	
}
