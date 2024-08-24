Here is a README template tailored to your API automation framework:

---

# API Automation Framework

## Overview

This API automation framework is designed to test and verify the functionalities of the "JointSharing" feature in an API. The framework uses Cucumber for behavior-driven development (BDD), WireMock for stubbing HTTP services, REST Assured for HTTP requests, and Allure for generating detailed test reports. The framework is configured to run in a mocked environment, providing a reliable and isolated testing experience.

## Features

- **Cucumber**: Implements BDD using feature files and step definitions.
- **WireMock**: Mocks external HTTP services to simulate real-world scenarios.
- **REST Assured**: Handles HTTP requests and responses for API testing.
- **Allure**: Generates detailed and visually appealing test reports.

## Project Structure

```
/src
  /main
    /java
      /api            # API client classes
      /builder        # Request builders
  /test
    /java
      /steps          # Step definitions for Cucumber
      /cucumberConfig # Cucumber configuration and setup
    /resources
      /features       # Cucumber feature files
  /build              # Build artifacts and reports
```

## Prerequisites

- Java 17 or higher
- Gradle 7.5 or higher
- IntelliJ IDEA or any other Java IDE

## How to Execute Tests

1. **Clone the Repository**:
   ```bash
   git clone https://your-repo-url.git
   cd poc-api-test-framework
   ```

2. **Run Tests**:  
   To execute tests in the mock environment:
   ```bash
   ./gradlew clean test -Denv=mock
   ```

3. **View Reports**:  
   Allure reports will be generated in the `build/allure-results` directory. To view the report:
   ```bash
   allure serve build/allure-results
   ```
   This command will start a local server and open the Allure report in your default web browser.

## Troubleshooting

- **SLF4J Warnings**:  
  You may see warnings about SLF4J failing to load. These are generally safe to ignore but can be resolved by adding a compatible SLF4J binding to your dependencies.

- **AspectJ Errors**:  
  If you encounter errors related to AspectJ, ensure that your project is using the correct Java version (Java 17 in this case). Updating AspectJ or your Gradle configuration might be necessary.

- **Test Failures**:  
  In case of test failures, detailed reports will be available in the `build/reports/tests/test/index.html` file. Review this file to diagnose and resolve issues.

- **Allure Not Generating Reports**:  
  Ensure that the `@CucumberOptions` in the `RunCucumberTests` class includes the Allure plugin:
  ```java
  plugin = {"pretty", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
  ```

## Adding New Tests

1. **Create a Feature File**:  
   Add a new `.feature` file in the `src/test/resources/features` directory.

2. **Implement Step Definitions**:  
   Add the corresponding step definitions in the `steps` package.

3. **Run Tests and Generate Reports**:  
   Execute tests as described above to validate your new test cases.

---
