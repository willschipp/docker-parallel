package com.example;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class HomePageSeleniumTest extends RemoteDriverManager {

	private String testUrl;
	
	private String remoteUrl;
	
	@Before
	public void before() throws Exception {
		
	}
	
	@Test
	public void test() throws Exception {
		//manually retrieve
		driver = new RemoteDriverManager().getDriver("chrome", remoteUrl);
		driver.get(testUrl);
		WebElement element = driver.findElement(By.tagName("h1"));
		assertEquals(element.getText(),"hello world");
	}	
	
}
