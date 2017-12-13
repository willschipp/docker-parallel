package com.example.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.Application;
import com.example.domain.CodeBaseRepository;
import com.example.domain.CodeTestRepository;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)
public class SimpleCodeServiceTest {

	@Autowired
	private CodeService codeService;
	
	@Autowired
	private CodeBaseRepository codeBaseRepository;
	
	@Autowired
	private CodeTestRepository codeTestRepository;
	
	@Before
	public void after() throws Exception {
		codeBaseRepository.deleteAllInBatch();
		codeTestRepository.deleteAllInBatch();
	}
	
	@Test
	public void testCreateCodeBase() throws Exception {
		codeService.createCodeBase("https://github.com/willschipp/sample-app.git",null,null);
		assertTrue(codeBaseRepository.count() > 0);
		assertTrue(codeTestRepository.count() == 3);
	}
	
	@Test
	public void testGetTestBuckets() throws Exception {
		codeService.createCodeBase("https://github.com/willschipp/sample-app.git",null,null);
		List<String> results = null;
		results = codeService.getTestBuckets("https://github.com/willschipp/sample-app.git",1);
		assertTrue(results.size() == 1);
		printArray(results);
		results = codeService.getTestBuckets("https://github.com/willschipp/sample-app.git",3);
		assertTrue(results.size() == 3);
		printArray(results);
		results = codeService.getTestBuckets("https://github.com/willschipp/sample-app.git",2);
		assertTrue(results.size() == 2);
		printArray(results);
		
	}
	
	void printArray(List<String> results) {
		System.out.println("<----------------");
		for (String result: results) {
			System.out.println(result);
		}//end for
		System.out.println("---------------->");
	}

}
