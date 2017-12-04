package com.example.util;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class TestNameUtilTest {

	@Ignore
	@Test
	public void test() throws Exception {
		List<String> buckets = TestNameUtil.getListBuckets("/Users/will/workspace-demo/ni-selenium-grid-docker/app/docker-manager/sample-app", 1*3);
		System.out.println(buckets);
	}

}
