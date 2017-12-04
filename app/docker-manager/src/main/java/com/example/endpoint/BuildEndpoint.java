package com.example.endpoint;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.BuildService;

@RestController
@RequestMapping("/api/build")
public class BuildEndpoint {

	@Autowired
	private BuildService builder;

	@RequestMapping(method=RequestMethod.POST)
	public void build(@RequestBody Map<String,Object> request,HttpServletResponse response) throws Exception {
		//start the build
		builder.build(request);
		//respond
		response.setStatus(HttpStatus.OK.value());
	}

}
