package com.example.endpoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;

@RestController
@RequestMapping("/api/build")
public class BuildService {

	
	
//	@Autowired
//	DockerClient docker;
	
	@RequestMapping(method=RequestMethod.POST)
	public void build(@RequestBody Map<String,Object> request,HttpServletResponse response) throws Exception {

		//make a new directory
		Path dirpath = Paths.get("/tmp/build");
		dirpath = Files.createDirectory(dirpath);
		
		//write the local file to the same directory
		InputStream is = this.getClass().getResourceAsStream("/Dockerfile");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		StringBuilder sb = new StringBuilder();
		
		while (line != null) {
			if (line.contains("${url}")) {
				line = line.replace("${url}",request.get("git-url").toString());
			}//end if
			sb.append(line).append("\n");
			line = reader.readLine();
		}//end while
		
		//now write to file
		FileWriter fileWriter = new FileWriter("/tmp/build/Dockerfile", false);
		fileWriter.write(sb.toString());
		fileWriter.flush();
		fileWriter.close();
		is.close();
		
		
		BuildImageResultCallback callback = new BuildImageResultCallback() {
			public void onNext(com.github.dockerjava.api.model.BuildResponseItem item) {
				super.onNext(item);
			};
		};
		
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(request.get("docker-host").toString()).build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
		
		File dockerfile = new File("/tmp/build/Dockerfile");
		
		Set<String> tags = new HashSet<String>();
		tags.add(request.get("tag").toString());
		
		String imageId = dockerClient.buildImageCmd(dockerfile)
				.withTags(tags)
				.exec(callback)
				.awaitImageId();
		
		System.out.println(imageId);
		
		
		Files.deleteIfExists(dockerfile.toPath());
		Files.delete(dirpath);
		
		response.setStatus(HttpStatus.OK.value());
	}
	
	
}
