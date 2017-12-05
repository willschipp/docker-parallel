package com.example.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class MavenExecutionServiceTest {

	@Autowired
	private ExecutionService executionService;
	
	@Test
	public void testRun() throws Exception {
		System.setProperty("maven.home", "/Users/will/Documents/tools/maven");
		executionService.run("https://github.com/willschipp/sample-app.git","-Dtest=Second*");
	}

}
