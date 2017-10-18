package com.sitture;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources"},
        tags = {"@successful"},
        snippets = SnippetType.CAMELCASE,
        plugin = {"html:output/html-report", "com.sitture.ExtentFormatter:output/extent-report/index.html"}
)
public class RunCukesTest {

    @AfterClass
    public static void setup() {
        ExtentReporter.setConfig("src/test/resources/config.xml");
        ExtentReporter.setSystemInfo("Browser", "Chrome");
        ExtentReporter.setSystemInfo("Selenium", "v2.53.1");
    }

}
