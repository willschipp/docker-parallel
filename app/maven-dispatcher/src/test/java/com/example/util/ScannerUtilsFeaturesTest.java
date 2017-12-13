package com.example.util;

import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Test;

public class ScannerUtilsFeaturesTest {

	@Test
	public void testFeature() throws Exception {
		List<String> names = null;
		names = ScannerUtils.getFeatures("/Users/will_schipp/Documents/java-code/neo/neo-at-data", "@happy_flow");
		int namesCount = names.size();
		for (String name : names) {
			System.out.println(name);
		}//end for
		
		names = ScannerUtils.getFeatures("/Users/will_schipp/Documents/java-code/neo/neo-at-data",null);
		assertNotEquals(namesCount,names.size());
	}
	
}
