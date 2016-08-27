package com.sitture;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.NetworkMode;

import cucumber.runtime.CucumberException;
import cucumber.runtime.io.URLOutputStream;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;

public class ExtentFormatter implements Reporter, Formatter {

	private static ExtentReports extentReport;
	private static String DEFAULT_REPORT_DIRECTORY = "target/extent-report";
	private static String DEFAULT_FILE_NAME = "index.html";
	private ExtentTest featureTest;
	private ExtentTest scenarioTest;
	private LinkedList<Step> testSteps = new LinkedList<Step>();
	private static File reportDirectory;
	private static Map<String, String> systemInfo;
	private boolean scenarioOutlineTest;

	private static Map<String, String> MIME_TYPES_EXTENSIONS = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			this.put("image/bmp", "bmp");
			this.put("image/gif", "gif");
			this.put("image/jpeg", "jpg");
			this.put("image/png", "png");
			this.put("image/svg+xml", "svg");
			this.put("video/ogg", "ogg");
		}
	};

	public ExtentFormatter() {
	}

	public ExtentFormatter(File filePath) {
		// init report
		reportDirectory = new File(DEFAULT_REPORT_DIRECTORY + File.separator + System.currentTimeMillis());
		extentReport = new ExtentReports(DEFAULT_REPORT_DIRECTORY + File.separator + DEFAULT_FILE_NAME);
		// check if given path exists.
        if (!filePath.getPath().equals("")) {
            String reportPath = filePath.getPath();
			reportDirectory = new File(reportPath);
			extentReport = new ExtentReports(reportPath);
        }
    }

	public static void initiateExtentFormatter(File filePath, Boolean replaceExisting,
			DisplayOrder displayOrder, NetworkMode networkMode, Locale locale) {
		reportDirectory = filePath;
		extentReport = new ExtentReports(filePath.getAbsolutePath(), replaceExisting, displayOrder, networkMode,
				locale);
	}

	public static void initiateExtentFormatter(File filePath, Boolean replaceExisting,
			DisplayOrder displayOrder, NetworkMode networkMode) {
		initiateExtentFormatter(filePath, replaceExisting, displayOrder, networkMode, null);
	}

	public static void initiateExtentFormatter(File filePath, Boolean replaceExisting,
			DisplayOrder displayOrder, Locale locale) {
		initiateExtentFormatter(filePath, replaceExisting, displayOrder, null, locale);
	}

	public static void initiateExtentFormatter(File filePath, Boolean replaceExisting,
			DisplayOrder displayOrder) {
		initiateExtentFormatter(filePath, replaceExisting, displayOrder, null, null);
	}

	public static void initiateExtentFormatter(File filePath, Boolean replaceExisting, NetworkMode networkMode,
			Locale locale) {
		initiateExtentFormatter(filePath, replaceExisting, null, networkMode, locale);
	}

	public static void initiateExtentFormatter(File filePath, Boolean replaceExisting,
			NetworkMode networkMode) {
		initiateExtentFormatter(filePath, replaceExisting, null, networkMode, null);
	}

	public static void initiateExtentFormatter(File filePath, NetworkMode networkMode) {
		initiateExtentFormatter(filePath, null, null, networkMode, null);
	}

	public static void initiateExtentFormatter(File filePath, Boolean replaceExisting, Locale locale) {
		initiateExtentFormatter(filePath, replaceExisting, null, null, locale);
	}

	public static void initiateExtentFormatter(File filePath, Boolean replaceExisting) {
		initiateExtentFormatter(filePath, replaceExisting, null, null, null);
	}

	public static void initiateExtentFormatter(File filePath, Locale locale) {
		initiateExtentFormatter(filePath, null, null, null, locale);
	}

	public static void initiateExtentFormatter(File filePath) {
		initiateExtentFormatter(filePath, null, null, null, null);
	}

	public static void initiateExtentFormatter() {
		String reportFilePath = DEFAULT_REPORT_DIRECTORY + File.separator + System.currentTimeMillis() + File.separator
				+ DEFAULT_FILE_NAME;
		initiateExtentFormatter(new File(reportFilePath));
	}

	public static void loadConfig(File configFile) {
		extentReport.loadConfig(configFile);
	}

	public static void addSystemInfo(String param, String value) {
		if (null == systemInfo) {
			systemInfo = new HashMap<String, String>();
		}
		systemInfo.put(param, value);
	}

	public static void addSystemInfo(Map<String, String> info) {
		if (null == systemInfo) {
			systemInfo = new HashMap<String, String>();
		}
		systemInfo.putAll(info);
	}

	public void before(Match match, Result result) {

	}

	public void result(Result result) {
		if (!scenarioOutlineTest) {
			if ("passed".equals(result.getStatus())) {
				scenarioTest.log(LogStatus.PASS, testSteps.poll().getName(), "PASSED");
			} else if ("failed".equals(result.getStatus())) {
				scenarioTest.log(LogStatus.FAIL, testSteps.poll().getName(), result.getError());
			} else if ("skipped".equals(result.getStatus())) {
				scenarioTest.log(LogStatus.SKIP, testSteps.poll().getName(), "SKIPPED");
			} else if ("undefined".equals(result.getStatus())) {
				scenarioTest.log(LogStatus.UNKNOWN, testSteps.poll().getName(), "UNDEFINED");
			}
		}
	}

	public void after(Match match, Result result) {

	}

	public void match(Match match) {

	}

	public void embedding(String s, byte[] bytes) {
		if (!scenarioOutlineTest) {
			String extension = (String) MIME_TYPES_EXTENSIONS.get(s);
			String fileName = "screenshot-" + System.currentTimeMillis() + "." + extension;
			this.writeBytesAndClose(bytes, this.reportFileOutputStream(fileName));
			scenarioTest.log(LogStatus.INFO, scenarioTest.addScreenCapture(fileName));
		}
	}

	public void write(String s) {
		if (!scenarioOutlineTest)
			scenarioTest.log(LogStatus.INFO, s);
	}

	public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {
	}

	public void uri(String s) {
	}

	public void feature(Feature feature) {
		featureTest = extentReport.startTest("Feature: " + feature.getName());
	}

	public void scenarioOutline(ScenarioOutline scenarioOutline) {
		scenarioOutlineTest = true;
	}

	public void examples(Examples examples) {
	}

	public void startOfScenarioLifeCycle(Scenario scenario) {
		scenarioTest = extentReport.startTest("Scenario: " + scenario.getName());
		for (Tag tag : scenario.getTags()) {
			scenarioTest.assignCategory(tag.getName());
		}
		scenarioOutlineTest = false;
	}

	public void background(Background background) {
	}

	public void scenario(Scenario scenario) {
	}

	public void step(Step step) {
		if (!scenarioOutlineTest)
			testSteps.add(step);
	}

	public void endOfScenarioLifeCycle(Scenario scenario) {
		if (!scenarioOutlineTest) {
			extentReport.endTest(scenarioTest);
			featureTest.appendChild(scenarioTest);
		}
	}

	public void done() {
	}

	public void close() {
		extentReport.addSystemInfo(systemInfo);
		extentReport.close();
	}

	public void eof() {
		extentReport.endTest(featureTest);
		extentReport.flush();
	}

	private OutputStream reportFileOutputStream(String fileName) {
		try {
			return new URLOutputStream(new URL(reportDirectory.toURI().toURL(), fileName));
		} catch (IOException exception) {
			throw new CucumberException(exception);
		}
	}

	private void writeBytesAndClose(byte[] buf, OutputStream out) {
		try {
			out.write(buf);
		} catch (IOException exception) {
			throw new CucumberException("Unable to write to report file item: ", exception);
		}
	}

}
