package com.example.service;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.HttpTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.http.HttpConnection;
import org.eclipse.jgit.transport.http.HttpConnectionFactory;
import org.eclipse.jgit.transport.http.JDKHttpConnectionFactory;
import org.eclipse.jgit.util.HttpSupport;
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
	public void createCodeBase(String location,String username,String password) throws Exception {
		String gitlocation = "/tmp/repo";
		//check to see if the location exists on the filesystem		
//		gitlocation = location.substring(location.lastIndexOf("/")+1,location.lastIndexOf("."));
		//clean off if exists
//		FileUtils.deleteDirectory(gitlocation);
		//clone the code
		Git git = Git.init().setDirectory(new File("/tmp/repo")).call();
		StoredConfig config = git.getRepository().getConfig();
		config.getBoolean("http", null, "sslVerify",false);
		config.setString("remote", "origin", "url", location);
		config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
		config.save();
		
		//set the connection factory
//		HttpConnectionFactory preservedConnectionFactory = HttpTransport.getConnectionFactory();
		HttpTransport.setConnectionFactory( new InsecureHttpConnectionFactory() );
		
		// clone repository
//		HttpTransport.setConnectionFactory( preservedConnectionFactory );
		
		PullCommand pullCommand = git.pull().setProgressMonitor(new TextProgressMonitor()).setTransportConfigCallback(new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				((HttpTransport)transport).setConnectionFactory(new InsecureHttpConnectionFactory());
			}
		});
		
		if (username != null && password != null) {
			CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username,password);
			pullCommand.setCredentialsProvider(credentialsProvider);
		}//end if
		
//		pullCommand.call();
//		
//		git.pull().setTransportConfigCallback(new TransportConfigCallback() {
//			@Override
//			public void configure(Transport transport) {
//				((HttpTransport)transport).setConnectionFactory(new InsecureHttpConnectionFactory());
//			}
//		}).setCredentialsProvider(credentialsProvider).setProgressMonitor(new TextProgressMonitor()).call();
//		git.checkout().setName("master").call();
//		Git git = Git.cloneRepository().setURI(location).call();
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
		FileUtils.deleteDirectory(new File(location));
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
	
	class InsecureHttpConnectionFactory implements HttpConnectionFactory {

		  @Override
		  public HttpConnection create( URL url ) throws IOException {
		    return create( url, null );
		  }

		  @Override
		  public HttpConnection create( URL url, Proxy proxy ) throws IOException {
		    HttpConnection connection = new JDKHttpConnectionFactory().create( url, proxy );
		    HttpSupport.disableSslVerify( connection );
		    return connection;
		  }
		}	
}
