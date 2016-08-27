package com.sitture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = { "src/test/resources" }, plugin = {
		"com.sitture.ExtentFormatter" }, snippets = SnippetType.CAMELCASE)
public class RunCukesTest {

	@BeforeClass
	public static void setup() {
		ExtentFormatter.initiateExtentFormatter();
		ExtentFormatter.loadConfig(new File("src/test/resources/config.xml"));

		ExtentFormatter.addSystemInfo("Browser", "Chrome");
		ExtentFormatter.addSystemInfo("Selenium", "v2.53.1");

		Map<String, String> systemInfo = new HashMap<String, String>();
		systemInfo.put("Cucumber", "v1.2.4");
		systemInfo.put("Extent Reports", "v2.41.1");
		ExtentFormatter.addSystemInfo(systemInfo);
	}

}

