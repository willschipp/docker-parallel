package com.example.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class InMemoryMonitorService implements MonitorService {

	private static final Log logger = LogFactory.getLog(InMemoryMonitorService.class);
	
	private Map<String,String> map;
	
	@Value("${distributor.runner.url}")
	private String url;
	
	public InMemoryMonitorService() {
		this.map = Collections.synchronizedMap(new HashMap<String,String>());
	}
	
	@Override
	public boolean isFinished(String uuid) throws Exception {
		if (map.containsKey(uuid) && map.get(uuid) != null) {
			return true;
		}//end if
		return false;
	}

	@Override
	public int runningCount() throws Exception {
		int count = 0;
		for (Entry<String,String> entry : map.entrySet()) {
			if (entry.getValue() == null) {
				count++;
			}//end if
		}//end for
		return count;
	}

	@Override
	public void register(String uuid) {
		map.put(uuid, null);
	}

	@Override
	public String getLog(String uuid) {
		return map.get(uuid);
	}

	@Override
	public void update(final String uuid, String log) {
		map.put(uuid, log);
		//TODO - zip the log stream and send
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(baos);
			ZipOutputStream zos = new ZipOutputStream(bos);
			zos.putNextEntry(new ZipEntry("log.txt"));
			IOUtils.copy(new ByteArrayInputStream(log.getBytes()), zos);
			zos.closeEntry();
			zos.finish();
			zos.flush();
			IOUtils.closeQuietly(zos);
			IOUtils.closeQuietly(bos);
			//write to file
//			File temp = File.createTempFile("log", "zip");
//			IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), new FileOutputStream(temp));
			//now have a compressed array --> send
			RestTemplate restTemplate = new RestTemplate();
			LinkedMultiValueMap<String,Object> vmap = new LinkedMultiValueMap<String, Object>();
			ByteArrayResource bar = new ByteArrayResource(baos.toByteArray()) {
				public String getFilename() {
					return uuid + ".log";
				};
			};
			
			
			vmap.add("file", bar);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			HttpEntity<LinkedMultiValueMap<String,Object>> entity = new HttpEntity<LinkedMultiValueMap<String,Object>>(vmap,headers);
			logger.info(url + uuid);
//			ResponseEntity<String> result = restTemplate.exchange(url + "/" + uuid,HttpMethod.POST,entity,String.class);
			String result = restTemplate.postForObject(url + "/" + uuid, entity, String.class);
			logger.info(result);
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public Set<String> getJobs() throws Exception {
		return map.keySet();
	}

}
