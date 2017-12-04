package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class OtherPageSeleniumTest {

	@Before
	public void before() throws Exception {
		System.out.println("hello from other");
	}

	@Test
	public void test() throws Exception {
		//manually retrieve
		System.out.println("blah from other");
		// fail("not done");
	}

}
