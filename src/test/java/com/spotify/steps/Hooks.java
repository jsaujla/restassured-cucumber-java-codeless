package com.spotify.steps;

import com.spotify.config.ConfigLoader;
import com.spotify.codeless.support.RequestBodyManager;
import com.spotify.codeless.support.DataStoreManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import commons.restbase.RequestBase;
import commons.restbase.ResponseBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages common steps of test scenarios that need to be performed before and after each test.
 */
public class Hooks {

    /**
     * Logger object for logging purposes. It's declared as final because it's a constant.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);

    /**
     * The instance of DependencyContainer class used to manage shared object instances.
     */
    private final DependencyContainer dependencyContainer;

    /**
     * Constructor for Hooks class that takes a DependencyContainer object as a parameter.
     *
     * @param dependencyContainer the DependencyContainer object to be used in the Hooks class
     */
    public Hooks(DependencyContainer dependencyContainer) {
        this.dependencyContainer = dependencyContainer;
    }

    /**
     * This method sets up the environment before executing a test scenario.
     *
     * @param scenario the scenario object that represents the current test scenario being executed
     */
    @Before
    public void setUp(Scenario scenario) {
        LOGGER.info("XXXXXXXXXX" + " START TEST SCENARIO " + "XXXXXXXXXX");
        LOGGER.info("Scenario: " + scenario.getName());

        dependencyContainer.requestBase = new RequestBase(ConfigLoader.getInstance().getApiBaseUri());
        dependencyContainer.responseBase = new ResponseBase();
        dependencyContainer.requestBodyManager = new RequestBodyManager();
        dependencyContainer.dataStoreManager = new DataStoreManager();
    }

    /**
     * This method is called after each scenario and performs necessary cleanup tasks.
     */
    @After()
    public void tearDown() {
        LOGGER.info("XXXXXXXXXX" + " END TEST SCENARIO " + "XXXXXXXXXX");
    }

}
