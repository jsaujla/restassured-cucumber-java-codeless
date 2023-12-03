# README #

### About the repository ###
* The repository contains the implementation to demonstrate an effective way to use 'restassured and cucumber with java' to design and develop a REST API codeless test automation framework
* The website under test is 'api.spotify'
* There are 3 test scenarios automated. Few test scenarios execute with different set of test data, which makes the total test count 5

### GitHub Actions (CI) test execution result ###
[![CI with Maven](https://github.com/jsaujla/restassured-cucumber-java-codeless/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/jsaujla/restassured-cucumber-java-codeless/actions/workflows/maven.yml)  

* Access cucumber result report online  
  https://jsaujla.github.io/restassured-cucumber-java-codeless/cucumber-reports.html
* Access execution logs online  
  https://jsaujla.github.io/restassured-cucumber-java-codeless/log/testlog.html

## Local setup ##

### Prerequisite for execution ###
* Java 11 or higher installed
* Maven installed
* JAVA_HOME environment variable configured
* MAVEN_HOME environment variable configured

### Prerequisite for development ###
* Java 11 or higher installed
* Maven installed
* JAVA_HOME environment variable configured
* MAVEN_HOME environment variable configured
* Java IDE installed (IntelliJ IDEA or Eclipse)
* TestNG plugin installed in IDE
* Cucumber plugin installed in IDE

### Technologies used ###
* Java
* Maven
* TestNG
* RestAssured
* Cucumber

### How do I get set up ###
* Download the repository into system
* Unzip the repository

### How to execute tests ###
* Open Command Prompt
* Go to project directory
* Execute below command to run tests with default properties file, default browser and default tag(s):
  * Default config properties file is QA '\src\test\resources\config-qa.properties'. Note: 'config-qa.properties' includes PROD URL because there is no access to QA Env
  * Default tag(s) is as per configured in '\src\test\java\com\spotify\runner\TestNgRunner.class'
```
mvn clean verify
```
* Execute tests on specific environment (dev, qa, uat, prod):
  * Currently, 'config-dev.properties' and 'config-uat.properties' files are empty
```
mvn clean verify -Dconfig.file=config-dev
mvn clean verify -Dconfig.file=config-qa
mvn clean verify -Dconfig.file=config-uat
mvn clean verify -Dconfig.file=config-prod
```
* Execute tests with specific tag(s):
```
mvn clean verify -Dcucumber.filter.tags="@smoke"
mvn clean verify -Dcucumber.filter.tags="@regression"
```
* Above mvn command parameters can also be used together. For example:
```
mvn clean verify -Dconfig.file=config-qa -Dcucumber.filter.tags="@smoke"
```

### Parallel test execution ###
* Execute tests in parallel mode:
  * Default thread count is as per configured in testng-parallel.xml file. Currently, it is '5'
```
mvn clean verify -Dsurefire.suiteXmlFiles=testng-parallel.xml
```

### Test execution results ###
* Cucumber default HTML report 'cucumber-reports.html' will be available under directory 'target' after test execution finished
* The test execution logs will be available under directory 'target\log' after test execution finished

### Project packages/structure ###
* BDD test scenarios: Refer feature files under directory '\src\test\resources\features'
* Test script implementation: Refer packages under directory '\src\test\java\com\spotify'
* Generic utils: Refer packages under directory '\src\main\java\commons'

### Who do I talk to ###
* For more information contact: Jaspal Aujla at [jaspal.qa@outlook.com](mailto:jaspal.qa@outlook.com) or [jsaujla1@gmail.com](mailto:jsaujla1@gmail.com)