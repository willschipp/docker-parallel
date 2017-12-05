package com.example;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteDriverManager {
	
	WebDriver driver;
	
	public WebDriver getDriver(String browser,String url) throws Exception {
		DesiredCapabilities capability = null;
		if (browser == "chrome") {
			capability = DesiredCapabilities.chrome();
		} else {
			capability = DesiredCapabilities.firefox();
		}//end if
		//return 
		return driver = new RemoteWebDriver(new URL(url),capability);
	}
	
}
