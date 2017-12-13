package com.example.endpoint;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	private static final Log logger = LogFactory.getLog(RunEndpoint.class);
	
	
	@Value("${root.directory:/tmp}")
	private String rootDirectory;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private HostService hostService;
	
	@Autowired
	private JobService jobService;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	//clone endpoint
	@RequestMapping(method=RequestMethod.POST)
	public void run(@RequestBody Map<String,String> request,HttpServletResponse response) throws Exception {
		final String gitUrl = request.get("git-url").toString();
		final String username = request.get("username") != null ? request.get("username").toString() : null;
		final String password = request.get("password") != null ? request.get("password").toString() : null;
		
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					String fileId = codeService.createCodeBase(gitUrl,username,password);
					//scan for hosts
					List<Host> hosts = hostService.getAll();
					if (hosts == null) {
						throw new Exception("no hosts");
					}//end if
					//partition and send --> url, list of tests
					List<String> buckets = codeService.getTestBuckets(request.get("git-url"), hosts.size());
					//send
					for (int i=0;i<buckets.size();i++) {
						hostService.run(hosts.get(i), fileId + ".zip", buckets.get(i));
					}//end for					
				}
				catch (Exception e) {
					logger.error(e);
				}
			}
			
		});
		response.setStatus(HttpStatus.CREATED.value());
	}
	
	//file upload endpoint
	@RequestMapping(value="/file",method=RequestMethod.POST)
	public void runFile(@RequestParam("file") MultipartFile file,@RequestParam("cucumber") boolean cucumber,@RequestParam(value="tag",required=false) String tag,HttpServletRequest request,HttpServletResponse response) throws Exception {
		//upload the file to temp
		String filename = UUID.randomUUID().toString() + ".zip";
		String location = rootDirectory + File.separator + filename;
		Files.copy(file.getInputStream(), Paths.get(location));
		//unzip
		//process
		location = codeService.parseCodeBase(location,cucumber,tag);
		//scan for hosts
		List<Host> hosts = hostService.getAll();
		if (hosts == null) {
			throw new Exception("no hosts");
		}//end if
		//partition and send --> url, list of tests
		List<String> buckets = null;
		if (cucumber) {
			buckets = codeService.getFeatureBuckets(location, hosts.size());
		} else {
			buckets = codeService.getTestBuckets(location, hosts.size());
		}//end if
		
		String[] parameters = null;
		Collection<String> values = new ArrayList<String>();
		//check for additional parameters
		if (!request.getParameterMap().isEmpty()) {
			for (Entry<String,String[]> entry : request.getParameterMap().entrySet()) {
				if (entry.getKey().contains("parameter")) {
					values.add(entry.getValue()[0]);
				}//end if
			}//end for
		}//end if
		
		//send
		for (int i=0;i<buckets.size();i++) {

			if (cucumber) {
				//build the cucumber parameters
				StringBuilder cucumberParameter = new StringBuilder("-Dcucumber.options=\"");
				if (tag != null) {
					cucumberParameter.append("--tags ").append(tag).append(" ");
				}//end if
				cucumberParameter.append(buckets.get(i));
				cucumberParameter.append("\"");
				//add
				values.add(cucumberParameter.toString());
			}//end if			
			
			parameters = values.toArray(new String[values.size()]);
			//execute
			hostService.run(hosts.get(i), filename, buckets.get(i),parameters);
		}//end for					
		//parse for content
		response.setStatus(HttpStatus.CREATED.value());
		//signal
	}
	
	//file validation endpoint
	@RequestMapping(value="/file/validate",method=RequestMethod.POST)
	public Map<String,Object> runFileValidate(@RequestParam("file") MultipartFile file,@RequestParam(value="cucumber",required=false) boolean cucumber) throws Exception {
		//upload the file to temp
		Map<String,Object> response = new HashMap<String,Object>();
		String filename = UUID.randomUUID().toString() + ".zip";
		String location = rootDirectory + File.separator + filename;
		Files.copy(file.getInputStream(), Paths.get(location));
		//unzip
		
		//process
		List<Host> hosts = hostService.getAll();
		if (hosts == null) {
			response.put("buckets",codeService.getTestBuckets(location, hosts.size()).size());
//			throw new Exception("no hosts");
			logger.error("no hosts");
		}//end if
		if (cucumber) {
			response.put("codebase", codeService.getCodeBase(location,true));
		} else {
			response.put("codebase", codeService.getCodeBase(location));
		}//end if
		
		//return
		return response;
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
	
	@RequestMapping(value="/{uuid}/log",method=RequestMethod.GET)
	public ResponseEntity<Resource> getLog(@PathVariable("uuid") String uuid,HttpServletResponse response) throws Exception {
		//get the job instance
		JobInstance instance = jobService.get(uuid);
		Resource file = new ByteArrayResource(instance.getLog());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uuid + ".zip\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(file);
	}
}
