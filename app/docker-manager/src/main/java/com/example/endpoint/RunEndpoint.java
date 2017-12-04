package com.example.endpoint;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Container;
import com.example.domain.ContainerRepository;
import com.example.domain.Host;
import com.example.domain.HostRepository;
import com.example.util.TestNameUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;

@RestController
@RequestMapping("/api/run")
public class RunEndpoint {

//	@Autowired
	private DockerClient dockerClient;

	private String location;

	@Autowired
	private ContainerRepository containerRepository;

	@Autowired
	private HostRepository hostRepository;

	@RequestMapping(method=RequestMethod.POST)
	public void run(@RequestParam("name") String name,@RequestParam("url") String gitUrl,@RequestParam("instances") int instanceCount,HttpServletResponse response) throws Exception {
		//get the list of hosts
		List<Host> hosts = hostRepository.findAll();
		if (hosts == null || hosts.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}//end if
		String location = null;
		try {
			//clone the source
			Git git = Git.cloneRepository().setURI(gitUrl).call();
			//scan that directory
			location = git.getRepository().getDirectory().getAbsolutePath();
			location = location.substring(0, location.lastIndexOf("/"));
			List<String> buckets = TestNameUtil.getListBuckets(location, hosts.size() * instanceCount);
			//iterate over the hosts and send the tests
			// for (int i=0;i<buckets.size();i++) {
			Iterator<String> bucketIterator = buckets.iterator();
			for (int i=0;i<hosts.size();i++) {
				Host host = hosts.get(i);

				// String bucket = buckets.get(i);

				DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host.getAddress()).build();
				dockerClient = DockerClientBuilder.getInstance(config).build();

				//loop over the instance size
				for (int j=0;j<instanceCount;j++) {
					String bucket = bucketIterator.next();//get the next bucket
					System.out.println(bucket);
					String id = dockerClient.createContainerCmd(name).withEnv(TestNameUtil.getEnvironment(bucket)).exec().getId();
					Container container = new Container();
					container.setId(id);
					//save
					containerRepository.save(container);
					//start
					dockerClient.startContainerCmd(id).exec();
					container.setStartTime(new Date());
					//save
					containerRepository.save(container);
				}//end for
			}//end for			
		}
		finally {
			//clean up
//			Files.delete(Paths.get(location));
			FileUtils.deleteDirectory(new File(location));
		}
		//respond
		response.setStatus(HttpStatus.OK.value());
	}

	@RequestMapping(method=RequestMethod.GET)
	public void updateContainer(@RequestParam("id") String id,HttpServletResponse response) throws Exception {
		List<Host> hosts = hostRepository.findAll();
		for (Host host : hosts) {
			DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(host.getAddress()).build();
			dockerClient = DockerClientBuilder.getInstance(config).build();
			InspectContainerResponse reply = dockerClient.inspectContainerCmd(id).exec();
			if (reply != null) {
				System.out.println(reply.getState().toString());
				//this is the host
				if (reply.getState().getRunning()) {
					response.setStatus(HttpStatus.PROCESSING.value());
					//update the container id
				} else if (reply.getState().getStatus().equals("exited")) {
					response.setStatus(HttpStatus.OK.value());
					//now get the container log
					dockerClient.logContainerCmd(id).withStdOut(true).exec(new LogContainerResultCallback() {
						
						StringBuffer buffer = new StringBuffer();
						
						@Override
						public void onNext(Frame item) {
							buffer.append(item.toString()).append("\n");
							super.onNext(item);
						}
						
						@Override
						public void onComplete() {
							
							Container container = containerRepository.findOne(id);
							if (container != null) {
								//parse the log for status
								if (buffer.toString().contains("BUILD SUCCESS")) {
									container.setPassed(true);
								} else {
									container.setPassed(false);
								}//end if
								//TODO - parse the date --> 2017-12-04T18:24:29.580992247Z
								
								
								container.setLog(buffer.toString().getBytes());
								containerRepository.save(container);								
							}//end if
							//done
							super.onComplete();
						}
					});					
					
				} else {
					response.setStatus(HttpStatus.NOT_FOUND.value());
				}//end if
			}//end if
		}
	}
}
