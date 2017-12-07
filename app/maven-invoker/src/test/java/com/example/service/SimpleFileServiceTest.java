package com.example.service;

import org.junit.Ignore;
import org.junit.Test;

public class SimpleFileServiceTest {

	@Ignore
	@Test
	public void testExtractFile() throws Exception {
		new SimpleFileService().extractFile("/tmp/e2e360dd-d9ad-4a81-8c9a-dcd610c46716.zip");
	}

}
