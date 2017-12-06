package com.example.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

@Service
public class GitCloneService implements CloneService {

	@Override
	public Map<String, String> clone(String location) throws Exception {
		String gitlocation = null;
		
		gitlocation = location.substring(location.lastIndexOf("/")+1,location.lastIndexOf("."));
		//check to see if the location exists on the filesystem
		//clean off if exists
		FileUtils.deleteDirectory("/tmp/" + gitlocation);
		//clone the location
		Git git = Git.cloneRepository().setURI(location).setDirectory(new File("/tmp/" + gitlocation)).call();
		gitlocation = git.getRepository().getDirectory().getAbsolutePath();
		gitlocation = gitlocation.substring(0, gitlocation.lastIndexOf("/"));				
		//build the fragment for it
		Map<String,String> response = new HashMap<String,String>();
		response.put("location",gitlocation);
		response.put("commit", git.getRepository().exactRef(git.getRepository().getFullBranch()).getObjectId().name());
		//close
		git.close();
		//return
		return response;
	}

}
