package com.example;

import java.net.InetAddress;

import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


public class ParallelTestRunnerIT {

	@Value("${test.selenium.url}")
	private String url;
	
	@LocalServerPort
	private int port;
	
	private String host;
	
//	WebDriver driver;

	@Test
	public void execute() throws Exception {
		//get the host
		host = InetAddress.getLocalHost().getHostAddress();

		//setup
		Class<?>[] classes = {HomePageSeleniumTest.class};
		JUnitCore.runClasses(new ParallelComputer(true, true),classes);
//		DesiredCapabilities capability = DesiredCapabilities.chrome();
//		System.out.println(url);
//		driver = new RemoteWebDriver(new URL(url),capability);
		
	}
	


	
}
