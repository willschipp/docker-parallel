package com.example.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.EmptyProgressMonitor;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.domain.CodeBase;
import com.example.domain.CodeBaseRepository;
import com.example.domain.CodeTest;
import com.example.domain.CodeTestRepository;
import com.example.util.ScannerUtils;

@Service
public class SimpleCodeService implements CodeService {

	private static final Log logger = LogFactory.getLog(SimpleCodeService.class);
	
	@Value("${root.directory:/tmp}")
	private String rootDirectory;
	
	@Autowired
	private CodeBaseRepository codeBaseRepository;

	@Autowired
	private CodeTestRepository codeTestRepository;

	@Override
	public String createCodeBase(String location,String username,String password) throws Exception {
		String uuid = UUID.randomUUID().toString();
		String gitlocation = "/tmp/repo";
		//clone the code
		Git git = Git.init().setDirectory(new File(gitlocation)).call();
		StoredConfig config = git.getRepository().getConfig();
		config.getBoolean("http", null, "sslVerify",false);
		config.setString("remote", "origin", "url", location);
		config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
		config.save();

		//set the connection factory
		HttpTransport.setConnectionFactory( new InsecureHttpConnectionFactory() );

		//setup pull
		PullCommand pullCommand = git.pull().setProgressMonitor(new DirtyProgressMonitor()).setTransportConfigCallback(new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				((HttpTransport)transport).setConnectionFactory(new InsecureHttpConnectionFactory());
			}
		});

		if (username != null && password != null) {
			CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username,password);
			pullCommand.setCredentialsProvider(credentialsProvider);
		}//end if

		pullCommand.call();

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
		//zip the package
		zipLocation(gitlocation,"/tmp/" + uuid + ".zip");
		//clean up
		FileUtils.deleteDirectory(new File(location));
		
		return uuid;
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

	@Override
	public String parseCodeBase(String location) throws Exception {
		//unzip the location
		String targetDirectory = location.substring(0,location.lastIndexOf("."));
		File directory = new File(targetDirectory);
		if (!directory.exists()) {
			directory.mkdir();
		}//end if
		//unzip the file
		ZipInputStream zis = new ZipInputStream(new FileInputStream(location));
		ZipEntry entry = zis.getNextEntry();
		while (entry != null) {
			String fileName = entry.getName();
			File f = new File(directory.getAbsolutePath() + File.separator + fileName);
			new File(f.getParent()).mkdirs();
			FileOutputStream fos = new FileOutputStream(f);
			int i = zis.read();
			while (i != -1) {
				fos.write(i);
				i = zis.read();
			}//end while
			fos.flush();
			fos.close();
			//create target
			entry = zis.getNextEntry();
		}//end while
		zis.close();
		String gitlocation = directory.getAbsolutePath();		
		
		//run the test scanner on it
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
		FileUtils.deleteDirectory(new File(gitlocation));		
		
		return location;
	}
	
	

	private void zipLocation(String input,String output) throws Exception {
		Path path = Files.createFile(Paths.get(output));
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(path))) {
			Path sourcePath = Paths.get(input);
			Files.walk(sourcePath)
				.filter(p -> !Files.isDirectory(p))
				.forEach(p -> {
					ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(p).toString());
					try {
						zos.putNextEntry(zipEntry);
						Files.copy(p, zos);
						zos.closeEntry();
					} catch (IOException e) {
						logger.error(e);
					}
				});
		}
	}
	
	
	
	
	private List<String> getNames(List<CodeTest> tests) {
		List<String> names = new ArrayList<String>();
		for (CodeTest test : tests) {
			names.add(test.getName());
		}//end for
		return names;
	}
	
	
	class DirtyProgressMonitor extends EmptyProgressMonitor {

		@Override
		public void beginTask(String title, int totalWork) {
			System.out.println("started task... " + title);
			super.beginTask(title, totalWork);
		}

		@Override
		public void update(int completed) {
			System.out.println("update... " + completed);
			super.update(completed);
		}

		@Override
		public void start(int totalTasks) {
			System.out.println("start called...");
			super.start(totalTasks);
		}

		@Override
		public void endTask() {
			System.out.println("end task");
			super.endTask();
		}
		
		
		
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
