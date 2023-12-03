package com.spotify.runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * This class is used to configure Cucumber options and run the test(s) with JUnit.
 *
 * @author Jaspal Aujla
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features="src/test/resources/features",
        glue={"com.spotify.steps"},
        monochrome = true,
        tags="@regression",
        plugin={"pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber.json",
        }
)
public class JUnitRunner {

}
