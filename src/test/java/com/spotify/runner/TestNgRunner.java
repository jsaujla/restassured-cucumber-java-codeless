package com.spotify.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * This class is used to configure Cucumber options and run the test(s) with TestNG.
 *
 * @author Jaspal Aujla
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.spotify.steps"},
        monochrome = true,
        dryRun = false,
        publish = false,
        tags="@regression",
        plugin = {"pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber.json",
                "junit:target/junit-cucumber-results.xml"
        }
)
public class TestNgRunner extends AbstractTestNGCucumberTests {

}
