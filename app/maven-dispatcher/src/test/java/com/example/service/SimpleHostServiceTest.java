package com.example.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.Application;
import com.example.domain.Host;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class SimpleHostServiceTest {

	@Autowired
	private HostService hostService;
	
	@Ignore
	@Test
	public void test() throws Exception {
		Host host = new Host();
		host.setAddress("127.0.0.1");
		host.setPort("8888");
		hostService.run(host, "https://github.com/willschipp/sample-app.git", "");
	}

}
