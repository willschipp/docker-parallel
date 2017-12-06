package com.example.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.CodeBase;
import com.example.domain.CodeBaseRepository;
import com.example.domain.CodeTest;
import com.example.domain.CodeTestRepository;
import com.example.util.ScannerUtils;

@Service
public class SimpleCodeService implements CodeService {

	@Autowired
	private CodeBaseRepository codeBaseRepository;
	
	@Autowired
	private CodeTestRepository codeTestRepository;
	
	@Override
	public void createCodeBase(String location) throws Exception {
		String gitlocation = null;
		//check to see if the location exists on the filesystem		
		gitlocation = location.substring(location.lastIndexOf("/")+1,location.lastIndexOf("."));
		//clean off if exists
		FileUtils.deleteDirectory(gitlocation);
		//clone the code
		Git git = Git.cloneRepository().setURI(location).call();
		gitlocation = git.getRepository().getDirectory().getAbsolutePath();
		gitlocation = gitlocation.substring(0, gitlocation.lastIndexOf("/"));						
		//scan for tests
		List<String> names = ScannerUtils.getListOfTestNames(gitlocation);
		List<CodeTest> tests = new ArrayList<CodeTest>();
		//loop
		for (String name : names) {
			CodeTest codeTest = new CodeTest();
			codeTest.setName(name);
			codeTest.setCreatedDate(new Date());
			//add
			tests.add(codeTest);
		}//end for
		//save
		codeTestRepository.save(tests);
		//create codebase
		CodeBase codeBase = new CodeBase();
		codeBase.setUrl(location);
		codeBase.setTests(tests);
		//save
		codeBaseRepository.save(codeBase);
		//clean up
		FileUtils.deleteDirectory(gitlocation);
	}

	@Override
	public List<String> getTestBuckets(String location,int hostCount) throws Exception {
		if (hostCount <= 0) {
			throw new Exception("no hosts");
		}//end if
		//retrieve the code base
		CodeBase codeBase = codeBaseRepository.findOne(location);
		if (codeBase == null) {
			throw new Exception("no known code base");
		}//end if
		//get the tests
		List<CodeTest> tests = codeBase.getTests();
		//check
		if (hostCount == 1) {
			List<String> names = getNames(tests);
			//flatten
			String name = ScannerUtils.listAsString(names.toArray(new String[names.size()]));
			return Arrays.asList(name);
		} else {
			List<String> buckets = new ArrayList<String>();
			//split
			int bucketSize = (int) Math.ceil((double)tests.size() / hostCount);
			int position = 0;
			for (int j=0;j<hostCount;j++) {
				List<String> nameCollection = new ArrayList<String>();
				for (int i=position;i<(position + bucketSize);i++) {
					if (i < tests.size()) {
						nameCollection.add(tests.get(i).getName());
					}//end if
				}//end for
				//add
				buckets.add(ScannerUtils.listAsString(nameCollection.toArray(new String[nameCollection.size()])));
				//increment position
				position += bucketSize;
			}//end for
			//return
			return buckets;
		}//end if
		
	}

	
	private List<String> getNames(List<CodeTest> tests) {
		List<String> names = new ArrayList<String>();
		for (CodeTest test : tests) {
			names.add(test.getName());
		}//end for
		return names;
	}
}
