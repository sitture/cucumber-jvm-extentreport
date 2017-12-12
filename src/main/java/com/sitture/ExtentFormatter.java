package com.sitture;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.gherkin.model.Scenario;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import cucumber.api.Result;
import cucumber.api.TestCase;
import cucumber.api.TestStep;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestCaseStarted;
import cucumber.api.event.TestRunFinished;
import cucumber.api.event.TestSourceRead;
import cucumber.api.event.TestStepFinished;
import cucumber.api.event.TestStepStarted;
import cucumber.api.formatter.Formatter;
import gherkin.ast.Background;
import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import gherkin.ast.TableCell;
import gherkin.ast.TableRow;
import gherkin.ast.Tag;

import java.io.File;
import java.util.List;

/**
 * Cucumber formatter for formatting Extent Reports.
 * See <a href="http://extentreports.com/">Extent reports</a> for more information.
 */
public class ExtentFormatter implements Formatter {

    private static final String DEFAULT_REPORT_DIRECTORY = "extent-report";
    private static final String DEFAULT_FILE_NAME = "index.html";

    private static ExtentReports extentReport;
    private static ExtentHtmlReporter htmlReport;

    private static ThreadLocal<ExtentTest> featureTestThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<ExtentTest> backgroundThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<ExtentTest> scenarioOutlineThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<ExtentTest> scenarioThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<ExtentTest> stepTestThreadLocal = new InheritableThreadLocal<>();

    private String currentFeatureFile;
    private boolean scenarioOutlineTest;
    private final TestSourcesModel testSources = new TestSourcesModel();

    private EventHandler<TestSourceRead> testSourceReadHandler = new EventHandler<TestSourceRead>() {
        @Override
        public void receive(TestSourceRead event) {
            testSources.addTestSourceReadEvent(event.uri, event);
        }
    };
    private EventHandler<TestCaseStarted> caseStartedHandler = new EventHandler<TestCaseStarted>() {
        @Override
        public void receive(TestCaseStarted event) {
            handleTestCaseStarted(event);
        }
    };
    private EventHandler<TestStepStarted> stepStartedHandler = new EventHandler<TestStepStarted>() {
        @Override
        public void receive(TestStepStarted event) {
            handleTestStepStarted(event);
        }
    };
    private EventHandler<TestStepFinished> stepFinishedHandler = new EventHandler<TestStepFinished>() {
        @Override
        public void receive(TestStepFinished event) {
            handleTestStepFinished(event);
        }
    };

    private EventHandler<TestRunFinished> runFinishedHandler = new EventHandler<TestRunFinished>() {
        @Override
        public void receive(TestRunFinished event) {
            handleTestRunFinished();
        }
    };

    public ExtentFormatter(File report) {
        scenarioOutlineTest = false;
        if (htmlReport == null) {
            htmlReport = new ExtentHtmlReporter(getReportsDir(report));
        }

        if (extentReport == null) {
            extentReport = new ExtentReports();
            extentReport.attachReporter(getHtmlReport());
        }
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, testSourceReadHandler);
        publisher.registerHandlerFor(TestCaseStarted.class, caseStartedHandler);
        publisher.registerHandlerFor(TestStepStarted.class, stepStartedHandler);
        publisher.registerHandlerFor(TestStepFinished.class, stepFinishedHandler);
        publisher.registerHandlerFor(TestRunFinished.class, runFinishedHandler);
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        scenarioOutlineTest = false;

        handleStartOfFeature(event.testCase);
        handleScenarioOutline(event.testCase);
        handleScenario(event.testCase);
        handleBackground(event.testCase);
    }

    private void handleTestStepStarted(TestStepStarted event) {
        TestStep testStep = event.testStep;
        if (TestSourcesModel.isBackgroundStep(testSources.getAstNode(currentFeatureFile, testStep.getStepLine()))) {
            String keywordFromSource = testSources.getKeywordFromSource(currentFeatureFile, testStep.getStepLine());
            try {
                ExtentTest node = backgroundThreadLocal.get().createNode(new GherkinKeyword(keywordFromSource), testStep.getStepText());
                stepTestThreadLocal.set(node);
            } catch (ClassNotFoundException e) {
                System.out.println("Something went wrong while trying to parse the Gherkin keyword for this step: " + e);
            }
        }
        if (!scenarioOutlineTest) {
            ExtentTest scenarioTest = scenarioThreadLocal.get();
            if (scenarioTest != null) {
                String keywordFromSource = testSources.getKeywordFromSource(currentFeatureFile, testStep.getStepLine());
                ExtentTest testNode;
                try {
                    if (!TestSourcesModel.isBackgroundStep(testSources.getAstNode(currentFeatureFile, testStep.getStepLine()))) {
                        testNode = scenarioTest.createNode(new GherkinKeyword(keywordFromSource), testStep.getStepText());
                        stepTestThreadLocal.set(testNode);
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("Something went wrong while trying to parse the Gherkin keyword for this step: " + e);
                }
            }
        }
    }

    private void handleTestStepFinished(TestStepFinished event) {
        if (!scenarioOutlineTest) {
            if (Result.Type.PASSED.equals(event.result.getStatus())) {
                getStepTestThreadLocal().get().pass(Result.Type.PASSED.name());
            } else if (Result.Type.FAILED.equals(event.result.getStatus())) {
                getStepTestThreadLocal().get().fail(event.result.getError());
            } else if (Result.Type.SKIPPED.equals(event.result.getStatus())) {
                getStepTestThreadLocal().get().skip(Result.Type.SKIPPED.name());
            } else if (Result.Type.UNDEFINED.equals(event.result.getStatus())) {
                getStepTestThreadLocal().get().warning(Result.Type.UNDEFINED.name());
            }
        }
    }

    private void handleStartOfFeature(TestCase testCase) {
        if (currentFeatureFile == null || !currentFeatureFile.equals(testCase.getUri())) {
            currentFeatureFile = testCase.getUri();

            Feature cucumberFeature = getFeature(testCase);
            ExtentTest feature = getExtentReport().createTest(cucumberFeature.getName(), cucumberFeature.getDescription());

            for (Tag tag : cucumberFeature.getTags()) {
                feature.assignCategory(tag.getName());
            }
            featureTestThreadLocal.set(feature);
        }
    }

    private void handleBackground(TestCase testCase) {
        Background background = TestSourcesModel.getBackgoundForTestCase(testSources.getAstNode(currentFeatureFile, testCase.getLine()));
        if (background != null) {
            if (scenarioOutlineTest) {
                ExtentTest backgroundNode = scenarioOutlineThreadLocal.get()
                        .createNode(com.aventstack.extentreports.gherkin.model.Background.class, background.getName());
                backgroundThreadLocal.set(backgroundNode);
            } else {
                ExtentTest backgroundNode = scenarioThreadLocal.get()
                        .createNode(com.aventstack.extentreports.gherkin.model.Background.class, background.getName());
                backgroundThreadLocal.set(backgroundNode);
            }
        }
    }

    private void handleScenarioOutline(TestCase testCase) {
        if (TestSourcesModel.isScenarioOutlineScenario(testSources.getAstNode(currentFeatureFile, testCase.getLine()))) {
            ScenarioOutline scenarioOutline = (ScenarioOutline) TestSourcesModel.getScenarioDefinition(testSources.getAstNode(currentFeatureFile, testCase.getLine()));
            scenarioOutlineTest = true;

            ExtentTest node = featureTestThreadLocal.get()
                    .createNode(com.aventstack.extentreports.gherkin.model.ScenarioOutline.class, scenarioOutline.getName(), scenarioOutline.getDescription());
            scenarioOutlineThreadLocal.set(node);

            List<Examples> examples = scenarioOutline.getExamples();
            for (Examples example : examples) {
                examples(example);
            }

            for (Tag tag : scenarioOutline.getTags()) {
                node.assignCategory(tag.getName());
            }
        }
    }

    private void handleScenario(TestCase testCase) {
        ExtentTest scenario;
        if (scenarioOutlineTest) {
            scenarioOutlineTest = false;
            ScenarioDefinition scenarioDefinition = TestSourcesModel.getScenarioDefinition(testSources.getAstNode(currentFeatureFile, testCase.getLine()));
            scenario = scenarioOutlineThreadLocal.get().createNode(Scenario.class, scenarioDefinition.getName());
        } else {
            gherkin.ast.Scenario scenarioDefinition = (gherkin.ast.Scenario) TestSourcesModel.getScenarioDefinition(testSources.getAstNode(currentFeatureFile, testCase.getLine()));
            scenario = featureTestThreadLocal.get().createNode(Scenario.class, scenarioDefinition.getName(), scenarioDefinition.getDescription());
            for (Tag tag : scenarioDefinition.getTags()) {
                scenario.assignCategory(tag.getName());
            }
        }
        scenarioThreadLocal.set(scenario);
    }

    private void handleTestRunFinished() {
        getExtentReport().flush();
    }

    private Feature getFeature(TestCase testCase) {
        return testSources.getFeature(testCase.getUri());
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

    private void examples(Examples examples) {
        ExtentTest test = scenarioOutlineThreadLocal.get();

        String[][] data = null;
        List<TableRow> rows = examples.getTableBody();
        int rowSize = rows.size();
        for (int i = 0; i < rowSize; i++) {
            TableRow examplesTableRow = rows.get(i);
            List<TableCell> cells = examplesTableRow.getCells();
            int cellSize = cells.size();
            if (data == null) {
                data = new String[rowSize][cellSize];
            }
            for (int j = 0; j < cellSize; j++) {
                data[i][j] = cells.get(j).getValue();
            }
        }
        test.info(MarkupHelper.createTable(data));
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
