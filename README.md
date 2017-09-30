# cucumber-jvm-extentreport
[![Build Status](https://travis-ci.org/sitture/cucumber-jvm-extentreport.svg?branch=master&style=flat-square)](https://travis-ci.org/sitture/cucumber-jvm-extentreport) [![Maven Central](https://img.shields.io/maven-central/v/com.sitture/cucumber-jvm-extentreport.svg?maxAge=300&style=flat-square)](http://search.maven.org/#search|ga|1|g:"com.sitture") [![Dependency Status](https://www.versioneye.com/user/projects/57dadac9bf3e4c004340d4be/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57dadac9bf3e4c004340d4be) [![license](https://img.shields.io/github/license/mashape/apistatus.svg?maxAge=2592000&style=flat-square)](https://raw.githubusercontent.com/sitture/cucumber-jvm-extentreport/master/LICENSE)

A custom `cucumber-jvm` report formatter using [ExtentReports](http://extentreports.relevantcodes.com)

## Preconditions

**Required**
- Java 8

**Optional**
- Maven (If using mvn to build)

## Usage
Add the following to your list of dependencies in `pom.xml`

```
<dependency>
    <groupId>com.sitture</groupId>
    <artifactId>cucumber-jvm-extentsreport</artifactId>
    <version>2.0.0</version>
</dependency>
```

Add the following if you're using gradle to your `build.gradle` file.

```
compile 'com.sitture:cucumber-jvm-extentreport:2.0.0'
```

## Setup - Cucumber  Runner
Add the following to your cucumber runner class:

```java
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources"},
        plugin = {"com.sitture.ExtentFormatter:output/extent-report/index.html", "html:output/html-report"})
public class RunCukesTest {
    @AfterClass
	public static void setup() {
        // Loads the extent config xml to customize on the report.
        ExtentReporter.setConfig("src/test/resources/config.xml");
        // adding system information
        ExtentReporter.setSystemInfo("Browser", "Chrome");
        ExtentReporter.setSystemInfo("Selenium", "v2.53.1");
	}
}
```
### Reports Location

The ExtentFormatter takes the location of reports directory as the parameter.
E.g. `com.sitture.ExtentFormatter:output/extent-report/index.html` will generate the report at `output/extent-report/index.html`.

### Configuration file

Refer here to create the config xml file: [ExtentReports Configuration](http://extentreports.relevantcodes.com/java/#configuration)
To load the config file:

```
ExtentReporter.setConfig(new File("your config xml file path"));
```

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## Building

### Gradle

If using the gradle build system, you can use:

```
./gradlew build
```

To generate the complete set of signed and verified artifacts under the `build/libs` folder