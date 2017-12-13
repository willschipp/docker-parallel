package com.example.service;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class GitCloneServiceTest {

	@Ignore
	@Test
	public void testCloneString() throws Exception {
		Map<String,String> response = new GitCloneService().clone("https://github.com/willschipp/sample-app.git");
		System.out.println(response);
	}

}
