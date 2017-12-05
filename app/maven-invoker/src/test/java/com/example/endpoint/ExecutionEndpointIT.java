package com.example.endpoint;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.example.Application;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class ExecutionEndpointIT {

	@Autowired
	WebApplicationContext context;
	
	@Autowired
	ObjectMapper mapper;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		System.setProperty("maven.home", "/Users/will/Documents/tools/maven");
	}
	
	@Test
	public void test() throws Exception {
		MockMvc mock = webAppContextSetup(context).build();
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("git-url","https://github.com/willschipp/sample-app.git");
		map.put("options","-Dtest=First*");
		
		mock.perform(post("/api/run").content(mapper.writeValueAsBytes(map)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
	}

}
