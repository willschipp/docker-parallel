package com.example.endpoint;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.domain.Host;
import com.example.domain.JobInstance;
import com.example.service.CodeService;
import com.example.service.HostService;
import com.example.service.JobService;

@RestController
@RequestMapping("/api/runner")
public class RunEndpoint {

	@Autowired
	private CodeService codeService;
	
	@Autowired
	private HostService hostService;
	
	@Autowired
	private JobService jobService;
	
	@RequestMapping(method=RequestMethod.POST)
	public void run(@RequestBody Map<String,String> request,HttpServletResponse response) throws Exception {
		codeService.createCodeBase(request.get("git-url").toString());
		//scan for hosts
		List<Host> hosts = hostService.getAll();
		if (hosts == null) {
			throw new Exception("no hosts");
		}//end if
		//partition and send --> url, list of tests
		List<String> buckets = codeService.getTestBuckets(request.get("git-url"), hosts.size());
		//send
		for (int i=0;i<buckets.size();i++) {
			hostService.run(hosts.get(i), request.get("git-url").toString(), buckets.get(i));
		}//end for
	}
	
	@RequestMapping(value="/{uuid}",method=RequestMethod.POST)
	public void receiveLog(@PathVariable("uuid") String uuid,@RequestParam("file") MultipartFile file,HttpServletResponse response) throws Exception {
		hostService.processLog(uuid,file.getBytes());
		response.setStatus(HttpStatus.OK.value());
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<JobInstance> get() throws Exception {
		return jobService.getAll();
	}
	
	@RequestMapping(value="/{uuid}",method=RequestMethod.GET)
	public JobInstance get(@PathVariable("uuid") String uuid) throws Exception {
		return jobService.get(uuid);
	}
	
	//TODO get endpoint to get the file for uid
}
