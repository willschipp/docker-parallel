package com.example.endpoint;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.MonitorService;

@RestController
@RequestMapping("/api/monitor")
public class MonitorEndpoint {

	@Autowired
	private MonitorService monitorService;
	
	@RequestMapping(value="/{uuid}",method=RequestMethod.GET)
	public boolean isFinished(@PathVariable("uuid") String uuid) throws Exception {
		return monitorService.isFinished(uuid);
	}
	
	@RequestMapping(value="/count",method=RequestMethod.GET)
	public int runningCount() throws Exception {
		return monitorService.runningCount();
	}
	
	@RequestMapping(value="/{uuid}/log",method=RequestMethod.GET)
	public String getLog(@PathVariable("uuid") String uuid) {
		return monitorService.getLog(uuid);
	}
	
	@RequestMapping(value="/",method=RequestMethod.GET)
	public Set<String> getRunning() throws Exception {
		return monitorService.getJobs();
	}
}
