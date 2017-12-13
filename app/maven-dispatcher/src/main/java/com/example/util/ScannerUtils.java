package com.example.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScannerUtils {

	private static final String DELIMITER = ",";

	public static List<String> getListOfTestNames(String location) throws Exception {
		List<Path> paths = Files.walk(Paths.get(location)).filter(Files::isRegularFile).filter(value -> value.toUri().toString().contains("Test")).collect(Collectors.toList());

		List<String> names = new ArrayList<String>();
		for (Path path : paths) {
			names.add(cleanUpName(path.getFileName().toString()));
		}//end for

		return names;
	}

	public static List<String> getListBuckets(String location,int bucketCount) throws Exception {
		List<String> buckets = new ArrayList<String>();
		//get the list of names
		List<String> names = getListOfTestNames(location);
		//split count by buckets
		if (bucketCount <= 0) {
			throw new RuntimeException("no buckets");
		} else if (bucketCount == 1) {
			buckets.add(listAsString(names.toArray(new String[names.size()])));
		} else {
			//split
			int bucketSize = (int) Math.ceil((double)names.size() / bucketCount);
			int position = 0;
			for (int j=0;j<bucketCount;j++) {
				List<String> nameCollection = new ArrayList<String>();
				for (int i=position;i<(position + bucketSize);i++) {
					if (i < names.size()) {
						nameCollection.add(names.get(i));
					}//end if
				}//end for
				//add
				buckets.add(listAsString(nameCollection.toArray(new String[nameCollection.size()])));
				//increment position
				position += bucketSize;
			}//end for
		}//end if

		return buckets;
	}

	public static String getEnvironment(String testList) throws Exception {
		return "-Dtest=" + testList;
	}

	public static String listAsString(String... names) throws Exception {
		StringBuilder builder = new StringBuilder();
		for (String name :names) {
			if (builder.length() > 0) {
				builder.append(DELIMITER);
			}//end if
			builder.append(name);
		}//end for
		return builder.toString();
	}
	
	public static String listAsStringFeatures(String... names) throws Exception {
		StringBuilder builder = new StringBuilder();
		for (String name :names) {
			if (builder.length() > 0) {
				builder.append(" ");
			}//end if
			builder.append(name);
		}//end for
		return builder.toString();
	}	

	private static String cleanUpName(String name) throws Exception {
		return name.substring(0, name.lastIndexOf("."));
	}
	
	private static String getCucumberString(String feature) throws Exception {
		return "-n \"" + feature + "\"";
	}

	public static List<String> getFeatures(String location,String tag) throws Exception {
		List<String> features = new ArrayList<String>();
		//open up the location
		//crawl for *.feature files
		List<Path> paths = Files.walk(Paths.get(location))
				.filter(Files::isRegularFile)
				.filter(value -> value.toUri().toString().contains(".feature"))
				.collect(Collectors.toList());
		//for each *.feature file, pull all the "scenarios"
		for (Path path : paths) {
			//read the file
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile())));
			String line = reader.readLine();
			boolean nextLine = false;
			while (line != null) {
				if (tag != null && line.contains(tag)) {
					//now we need the NEXT line too
					nextLine = true;
				}//end if
				if (tag != null && nextLine == true && line.contains("Scenario")) {
					//gotcha
					features.add(getCucumberString(line.substring(line.lastIndexOf(":")+2)));
					nextLine = false;//reset
				} else if (tag == null && line.contains("Scenario")) {
					features.add(getCucumberString(line.substring(line.lastIndexOf(":")+2)));
				}//end if

				line = reader.readLine();
			}//end while
			reader.close();
		}//end for
		//filter by tags

		return features;
	}


}
