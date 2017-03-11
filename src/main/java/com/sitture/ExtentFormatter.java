package com.sitture;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import gherkin.formatter.Formatter;
import gherkin.formatter.NiceAppendable;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ExtentFormatter implements Reporter, Formatter {

    private NiceAppendable out;
    private static ExtentReports extentReport;
    private static ExtentHtmlReporter htmlReport;
	private static String DEFAULT_REPORT_DIRECTORY = "extent-report";
	private static String DEFAULT_FILE_NAME = "index.html";
    private static ThreadLocal<ExtentTest> featureTestThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<ExtentTest> scenarioOutlineThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<ExtentTest> scenarioThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<LinkedList<Step>> stepListThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<ExtentTest> stepTestThreadLocal = new InheritableThreadLocal<>();
	private boolean scenarioOutlineTest;

	public ExtentFormatter(File report) {
        scenarioOutlineTest = false;
		if (null == getHtmlReport()) {
            // init report
            htmlReport = new ExtentHtmlReporter(getReportsDir(report));
        }

        if (null == getExtentReport()) {
		    // init extent reporter
            extentReport = new ExtentReports();
            extentReport.attachReporter(getHtmlReport());
        }
        stepListThreadLocal.set(new LinkedList<Step>());
    }

    private static File getReportsDir(File file) {
        File reportsDir = new File(
                DEFAULT_REPORT_DIRECTORY + File.separator + System.currentTimeMillis() + File.separator + DEFAULT_FILE_NAME);
	    if (file != null) {
	        reportsDir = file;
        }
        if (!reportsDir.exists()) {
            reportsDir.getParentFile().mkdirs();
        }
        return reportsDir;
    }

	public void before(Match match, Result result) {
	}

	public void result(Result result) {
		if (!scenarioOutlineTest) {
            if (Result.PASSED.equals(result.getStatus())) {
                getStepTestThreadLocal().get().pass(Result.PASSED);
            } else if (Result.FAILED.equals(result.getStatus())) {
                getStepTestThreadLocal().get().fail(result.getError());
            } else if (Result.SKIPPED.equals(result)) {
                getStepTestThreadLocal().get().skip(Result.SKIPPED.getStatus());
            } else if (Result.UNDEFINED.equals(result)) {
                getStepTestThreadLocal().get().skip(Result.UNDEFINED.getStatus());
            }
		}
	}

	public void after(Match match, Result result) {
	}

	public void match(Match match) {
        Step step = stepListThreadLocal.get().poll();
        String data[][] = null;
        if (step.getRows() != null) {
            List<DataTableRow> rows = step.getRows();
            int rowSize = rows.size();
            for (int i = 0; i < rowSize; i++) {
                DataTableRow dataTableRow = rows.get(i);
                List<String> cells = dataTableRow.getCells();
                int cellSize = cells.size();
                if (data == null) {
                    data = new String[rowSize][cellSize];
                }
                for (int j = 0; j < cellSize; j++) {
                    data[i][j] = cells.get(j);
                }
            }
        }

        ExtentTest scenarioTest = scenarioThreadLocal.get();
        ExtentTest stepTest = scenarioTest.createNode(step.getKeyword() + step.getName());

        if (data != null) {
            Markup table = MarkupHelper.createTable(data);
            stepTest.info(table);
        }
        stepTestThreadLocal.set(stepTest);
	}

	public void embedding(String s, byte[] bytes) {
	}

	public void write(String s) {
	}

	public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {
	}

	public void uri(String s) {
	}

	public void feature(Feature feature) {
        featureTestThreadLocal.set(getExtentReport().createTest(feature.getName()));
        ExtentTest test = featureTestThreadLocal.get();
        for (Tag tag : feature.getTags()) {
            test.assignCategory(tag.getName());
        }
	}

	public void scenarioOutline(ScenarioOutline scenarioOutline) {
		scenarioOutlineTest = true;
        ExtentTest node = featureTestThreadLocal.get()
                .createNode(scenarioOutline.getKeyword() + ": " + scenarioOutline.getName());
        // set the node
        scenarioOutlineThreadLocal.set(node);
	}

	public void examples(Examples examples) {
        ExtentTest test = scenarioOutlineThreadLocal.get();

        String[][] data = null;
        List<ExamplesTableRow> rows = examples.getRows();
        int rowSize = rows.size();
        for (int i = 0; i < rowSize; i++) {
            ExamplesTableRow examplesTableRow = rows.get(i);
            List<String> cells = examplesTableRow.getCells();
            int cellSize = cells.size();
            if (data == null) {
                data = new String[rowSize][cellSize];
            }
            for (int j = 0; j < cellSize; j++) {
                data[i][j] = cells.get(j);
            }
        }
        test.info(MarkupHelper.createTable(data));
	}

	public void startOfScenarioLifeCycle(Scenario scenario) {
	    // set scenario outline to be false
        scenarioOutlineTest = false;
        ExtentTest scenarioNode;
        if (scenarioOutlineThreadLocal.get() != null && scenario.getKeyword().trim()
                .equalsIgnoreCase("Scenario Outline")) {
            scenarioNode =
                    scenarioOutlineThreadLocal.get().createNode("Scenario: " + scenario.getName());
        } else {
            scenarioNode =
                    featureTestThreadLocal.get().createNode("Scenario: " + scenario.getName());
        }
        for (Tag tag : scenario.getTags()) {
            scenarioNode.assignCategory(tag.getName());
        }
        scenarioThreadLocal.set(scenarioNode);
	}

	public void background(Background background) {
	}

	public void scenario(Scenario scenario) {
	}

	public void step(Step step) {
		if (!scenarioOutlineTest) {
            System.out.println(step.getName());
            stepListThreadLocal.get().add(step);
        }
	}

	public void endOfScenarioLifeCycle(Scenario scenario) {
	}

	public void done() {
        getExtentReport().flush();
	}

	public void close() {
	}

	public void eof() {
	}

    public static ExtentReports getExtentReport() {
        return extentReport;
    }

    public static ExtentHtmlReporter getHtmlReport() {
        return htmlReport;
    }

    public static ThreadLocal<ExtentTest> getStepTestThreadLocal() {
        return stepTestThreadLocal;
    }

    public static ThreadLocal<ExtentTest> getScenarioThreadLocal() {
        return scenarioThreadLocal;
    }

}
