package com.example.endpoint;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Host;
import com.example.service.HostService;

@RestController
@RequestMapping("/api/host")
public class HostEndpoint {

	@Autowired
	private HostService hostService;
	
	@RequestMapping(method=RequestMethod.POST)
	public void register(@RequestBody Map<String,Object> request,HttpServletResponse response) throws Exception {
		hostService.register(request.get("address").toString(), request.get("port").toString());
		response.setStatus(HttpStatus.CREATED.value());
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Host> getAll() throws Exception {
		return hostService.getAll();
	}
	
	@RequestMapping(value="/file",method=RequestMethod.GET)
	public ResponseEntity<Resource> getFile(@RequestParam("fileName") String fileName) throws Exception {
		//get the file
		Resource file = new PathResource(fileName);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(file);				
	}
}