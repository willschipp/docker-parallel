package com.example.endpoint;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

@RestController
@RequestMapping("/api/run")
public class RunService {

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
		//get the number of buckets per host
		//get the list of tests in buckets
		//clone the source
		Git git = Git.cloneRepository().setURI(gitUrl).call();
		//scan that directory
		String location = git.getRepository().getDirectory().getAbsolutePath();
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

		//clean up
		Files.delete(Paths.get(location));

		//respond
		response.setStatus(HttpStatus.OK.value());
	}

}
