package com.sitture;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExtentReporter {

    private static Map<String, Boolean> systemInfo = new HashMap<>();

    private ExtentReporter() {
    }

    /**
     * Gets the {@link ExtentHtmlReporter} instance created through listener
     *
     * @return The {@link ExtentHtmlReporter} instance
     */
    public static ExtentHtmlReporter getExtentHtmlReport() {
        return ExtentFormatter.getHtmlReport();
    }

    /**
     * Gets the {@link ExtentReports} instance created through the listener
     *
     * @return The {@link ExtentReports} instance
     */
    public static ExtentReports getExtentReport() {
        return ExtentFormatter.getExtentReport();
    }

    /**
     * Loads the XML config file
     *
     * @param xmlPath The xml path in string
     */
    public static void setConfig(String xmlPath) {
        getExtentHtmlReport().loadXMLConfig(xmlPath);
    }

    /**
     * Loads the XML config file
     *
     * @param file The file path of the XML
     */
    public static void setConfig(File file) {
        getExtentHtmlReport().loadXMLConfig(file);
    }

    /**
     * Adds an info message to the current step
     *
     * @param message The message to be logged to the current step
     */
    public static void addStepLog(String message) {
        getCurrentStep().info(message);
    }

    /**
     * Adds an info message to the current scenario
     *
     * @param message The message to be logged to the current scenario
     */
    public static void addScenarioLog(String message) {
        getCurrentScenario().info(message);
    }

    /**
     * Adds the screenshot from the given path to the current step
     *
     * @param imagePath The image path
     * @throws IOException Exception if imagePath is erroneous
     */
    public static void addScreenCaptureFromPath(String imagePath) throws IOException {
        getCurrentStep().addScreenCaptureFromPath(imagePath);
    }

    /**
     * Adds the screenshot from the given path with the given title to the current step
     *
     * @param imagePath The image path
     * @param title     The title for the image
     * @throws IOException Exception if imagePath is erroneous
     */
    public static void addScreenCaptureFromPath(String imagePath, String title) throws IOException {
        getCurrentStep().addScreenCaptureFromPath(imagePath, title);
    }

    /**
     * Adds the screen cast from the given path to the current step
     *
     * @param screenCastPath The screen cast path
     * @throws IOException Exception if imagePath is erroneous
     */
    public static void addScreenCast(String screenCastPath) throws IOException {
        getCurrentStep().addScreencastFromPath(screenCastPath);
    }

    /**
     * Sets the system information with the given key value pair
     *
     * @param key   The name of the key
     * @param value The value of the given key
     */
    public static void setSystemInfo(String key, String value) {
        if (systemInfo.isEmpty() || !systemInfo.containsKey(key)) {
            systemInfo.put(key, false);
        }
        if (systemInfo.get(key)) {
            return;
        }
        getExtentReport().setSystemInfo(key, value);
        systemInfo.put(key, true);
    }

    private static ExtentTest getCurrentStep() {
        return ExtentFormatter.getStepTestThreadLocal().get();
    }

    private static ExtentTest getCurrentScenario() {
        return ExtentFormatter.getScenarioThreadLocal().get();
    }

}
