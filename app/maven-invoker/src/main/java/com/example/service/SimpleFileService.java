package com.example.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

@Service
public class SimpleFileService implements FileService {
	
	private static final Log logger = LogFactory.getLog(SimpleFileService.class);

	@Value("${root.directory}")
	private String rootDirectory;
	
	@Override
	public String getFile(String url) throws Exception {
		String filename = url.substring(url.lastIndexOf("=")+1);
		
		System.out.println("url: " + url);
		System.out.println("filename: " + filename);
		
		RestTemplate restTemplate = new RestTemplate();

		RequestCallback requestCallBack = request -> request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM,MediaType.ALL));

		ResponseExtractor<Void> responseExtractor = response -> {
			Path path = Paths.get(rootDirectory + "/" + filename);	
			Files.copy(response.getBody(), path);
			return null;
		};
		
		restTemplate.execute(URI.create(url),HttpMethod.GET,requestCallBack,responseExtractor);
		
		return rootDirectory + "/" + filename;
	}

	@Override
	public String extractFile(String location) throws Exception {
		//create the target
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
		return directory.getAbsolutePath();
	}

}
