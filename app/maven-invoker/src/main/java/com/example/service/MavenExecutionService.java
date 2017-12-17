package com.example.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



@Service
public class MavenExecutionService implements ExecutionService {

	private static final Log log = LogFactory.getLog(MavenExecutionService.class);
	
	@Autowired
	private MonitorService monitorService;
	
	
	@Async
	@Override
	public void run(String location,String options,final String uuid) throws Exception {
//		String gitlocation = null;
//		try {
//			//clone the location
//			Git git = Git.cloneRepository().setURI(location).call();
//			gitlocation = git.getRepository().getDirectory().getAbsolutePath();
//			gitlocation = gitlocation.substring(0, gitlocation.lastIndexOf("/"));				
			//inStream<T>om
		
		
			//TODO --find the pom
			Stream<Path> matches = Files.find(Paths.get(location), 10, (path,basicFileAttributes) -> String.valueOf(path).endsWith(".xml"));
			matches.map(path -> path.toAbsolutePath()).forEach(System.out::println);
		
		
			InvocationRequest request = new DefaultInvocationRequest();
//			request.setPomFile(new File(location + "/pom.xml"));
//			request.setBaseDirectory(new File(location));
			request.setGoals(Collections.singletonList("test"));
			request.setMavenOpts(options);
			

			Invoker invoker = new DefaultInvoker();
			invoker.setOutputHandler(new InvocationOutputHandler() {
				
				StringBuilder builder = new StringBuilder();
				
				boolean lastLine = false;
				
				@Override
				public void consumeLine(String line) {
					builder.append(line).append("\n");
					//check the line for second last line
					if (line.contains("Final Memory:")) {
						lastLine = true;
					} else if (lastLine) {
						monitorService.update(uuid, builder.toString());
					}//end if
				}
				
			});
			
			monitorService.register(uuid);
			
			//run			
			log.info("starting... " + uuid);
			InvocationResult result = invoker.execute(request);
			log.info("...finished");
			if (result.getExitCode() != 0) {
				log.info("build failed");
			} else {
				log.info("success!");
			}//end if
//		}
//		finally {
//			FileUtils.deleteDirectory(new File(gitlocation));
//		}
	}

	
	
}
