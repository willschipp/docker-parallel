package com.example.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class InMemoryMonitorService implements MonitorService {

	private Map<String,String> map;
	
	public InMemoryMonitorService() {
		this.map = Collections.synchronizedMap(new HashMap<String,String>());
	}
	
	@Override
	public boolean isFinished(String uuid) throws Exception {
		if (map.containsKey(uuid) && map.get(uuid) != null) {
			return true;
		}//end if
		return false;
	}

	@Override
	public int runningCount() throws Exception {
		int count = 0;
		for (Entry<String,String> entry : map.entrySet()) {
			if (entry.getValue() == null) {
				count++;
			}//end if
		}//end for
		return count;
	}

	@Override
	public void register(String uuid) {
		map.put(uuid, null);
	}

	@Override
	public String getLog(String uuid) {
		return map.get(uuid);
	}

	@Override
	public void update(String uuid, String log) {
		map.put(uuid, log);
	}

	@Override
	public Set<String> getJobs() throws Exception {
		return map.keySet();
	}

}
