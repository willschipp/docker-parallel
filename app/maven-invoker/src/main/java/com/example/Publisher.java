package com.example;

import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Publisher implements ApplicationListener<ContextRefreshedEvent> {

	@Value("${distributor.publisher.url}")
	private String url;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Value("${server.port}")
	private int port;
	
	private static final Log logger = LogFactory.getLog(Publisher.class);
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
//		int port = ((TomcatEmbeddedServletContainer)((AnnotationConfigEmbeddedWebApplicationContext)event.getApplicationContext()).getEmbeddedServletContainer()).getPort();
		
		try {
			//create the json object
			Map<String,String> requestObject = new HashMap<String,String>();
			requestObject.put("address",InetAddress.getLocalHost().getHostAddress());
			requestObject.put("port",Integer.toString(port));
			//send
			HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(requestObject),headers);
			ResponseEntity<String> response = restTemplate.postForEntity(new URI(url), entity,String.class);
			if (!response.getStatusCode().is2xxSuccessful()) {
				logger.error("bad response code");
			}//end if
		}
		catch (Exception e) {
			logger.error(e);
		}
		
		
	}

}
