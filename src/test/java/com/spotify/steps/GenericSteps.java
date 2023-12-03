package com.spotify.steps;

import com.spotify.config.ConfigLoader;
import com.spotify.oauth.TokenManager;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import com.spotify.codeless.support.DataStoreManager;
import com.spotify.codeless.support.RequestBodyManager;
import commons.restbase.RequestBase;
import commons.restbase.ResponseBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class contains the implementation of step definitions that correspond to feature files.
 */
public class GenericSteps {

    /**
     * Logger object for logging purposes. It's declared as final because it's a constant.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericSteps.class);

    /**
     * The instance of RequestBase class used to manage request specifications.
     */
    protected final RequestBase requestBase;

    /**
     * The instance of ResponseBase class used to manage response specifications.
     */
    protected final ResponseBase responseBase;

    /**
     * The instance of Response class used to store the response of the HTTP request.
     */
    protected Response response;

    /**
     * The instance of RequestBodyManager class used to manage and manipulate the body of the HTTP request.
     */
    private  final RequestBodyManager requestBodyManager;

    /**
     * The instance of DataStoreManager class used to manage and manipulate the data store.
     */
    private final DataStoreManager dataStoreManager;

    /**
     * Constructor to initialize the RegisterSteps class.
     *
     * @param dependencyContainer An instance of the DependencyContainer class
     */
    public GenericSteps(DependencyContainer dependencyContainer) {
        LOGGER.info("Constructing GenericSteps");
        requestBase = dependencyContainer.requestBase;
        responseBase = dependencyContainer.responseBase;
        response = dependencyContainer.response;
        requestBodyManager = dependencyContainer.requestBodyManager;
        dataStoreManager = dependencyContainer.dataStoreManager;
    }

    //********** STEP DEFINITION METHODS **********

    @When("With request headers")
    public void with_request_headers(DataTable dataTable) {
        List<List<String>> table = dataTable.asLists(String.class);
        if (table.size() != 2) {
            throw new IllegalArgumentException("DataTable must have two rows");
        }
        Map<String, String> header = new HashMap<>();
        for(int i=0; i<table.get(0).size(); i++) {
            String resolvedRowTwoDataWithConfigFile = ConfigLoader.getInstance().replacePlaceholdersWithProperties(table.get(1).get(i));
            String resolvedRowTwoDataWithConfigFileAndAccessToken = resolvedRowTwoDataWithConfigFile;
            if (resolvedRowTwoDataWithConfigFile.contains("{access_token}")) {
                resolvedRowTwoDataWithConfigFileAndAccessToken = resolvedRowTwoDataWithConfigFile.replace("{access_token}", TokenManager.getToken());
            }
            header.put(table.get(0).get(i), resolvedRowTwoDataWithConfigFileAndAccessToken);
        }
        requestBase.getRequestSpecification().headers(header);
    }

    @And("With request body: {string}")
    public void with_request_body(String jsonFilePath, DataTable dataTable) {
        List<List<String>> table = dataTable.asLists(String.class);
        requestBodyManager.validateTableSize(table);
        JSONObject json = requestBodyManager.readJsonFromFile(jsonFilePath);
        requestBodyManager.updateJsonWithDataTable(json, table);
        requestBase.getRequestSpecification().body(json.toString());
    }

    @When("User makes a POST request to endpoint: {string}")
    public void user_makes_a_post_request_to_endpoint(String endpoint) {
        String resolvedEndpointWithConfigFile = ConfigLoader.getInstance().replacePlaceholdersWithProperties(endpoint);
        String resolvedEndpointWithConfigFileAndDataStore = dataStoreManager.resolvePlaceholdersWithData(resolvedEndpointWithConfigFile);
        response = requestBase.getRequestSpecification().post(resolvedEndpointWithConfigFileAndDataStore).
                then().spec(responseBase.getResponseSpecification()).extract().response();
        requestBase.resetRequestSpecification();

    }

    @When("User makes a GET request to endpoint: {string}")
    public void user_makes_a_get_request_to_endpoint(String endpoint) {
        String resolvedEndpointWithConfigFile = ConfigLoader.getInstance().replacePlaceholdersWithProperties(endpoint);
        String resolvedEndpointWithConfigFileAndDataStore = dataStoreManager.resolvePlaceholdersWithData(resolvedEndpointWithConfigFile);
        response = requestBase.getRequestSpecification().get(resolvedEndpointWithConfigFileAndDataStore).
                then().spec(responseBase.getResponseSpecification()).extract().response();
        requestBase.resetRequestSpecification();
    }

    @When("User makes a PUT request to endpoint: {string}")
    public void user_makes_a_put_request_to_endpoint(String endpoint) {
        String resolvedEndpointWithConfigFile = ConfigLoader.getInstance().replacePlaceholdersWithProperties(endpoint);
        String resolvedEndpointWithConfigFileAndDataStore = dataStoreManager.resolvePlaceholdersWithData(resolvedEndpointWithConfigFile);
        response = requestBase.getRequestSpecification().put(resolvedEndpointWithConfigFileAndDataStore).
                then().spec(responseBase.getResponseSpecification()).extract().response();
        requestBase.resetRequestSpecification();
    }

    @When("User makes a DELETE request to endpoint: {string}")
    public void user_makes_a_delete_request_to_endpoint(String endpoint) {
        String resolvedEndpointWithConfigFile = ConfigLoader.getInstance().replacePlaceholdersWithProperties(endpoint);
        String resolvedEndpointWithConfigFileAndDataStore = dataStoreManager.resolvePlaceholdersWithData(resolvedEndpointWithConfigFile);
        response = requestBase.getRequestSpecification().delete(resolvedEndpointWithConfigFileAndDataStore).
                then().spec(responseBase.getResponseSpecification()).extract().response();
        requestBase.resetRequestSpecification();
    }

    @Then("Response status code should be: {int}")
    public void response_status_code_should_be(int statusCode) {
        assertThat(response.getStatusCode(), CoreMatchers.equalTo(statusCode));
    }

    @And("Response body should contains fields")
    public void response_body_should_contains_fields(DataTable dataTable) {
        List<List<String>> rows = dataTable.asLists(String.class);
        if (rows.size() != 3) {
            throw new IllegalArgumentException("DataTable must have three rows");
        }
        for(int i=0; i<rows.get(0).size(); i++) {
            String jsonPath = rows.get(0).get(i); // First Row
            Object expectedValue = rows.get(1).get(i); // Second Row
            String expectedValueType = rows.get(2).get(i); // Third Row

            if (expectedValue.equals("NOT_NULL")) {
                assertThat(response.getBody().path(jsonPath), CoreMatchers.notNullValue());
            } else if (expectedValue.equals("NULL") || expectedValue.equals("null")) {
                assertThat(response.getBody().path(jsonPath), CoreMatchers.nullValue());
            } else {
                expectedValue = dataStoreManager.convertOrRetrieveExpectedValue(expectedValue, expectedValueType);
                assertThat(response.getBody().path(jsonPath), CoreMatchers.equalTo(expectedValue));
            }
        }
    }

    @And("Store response body value in variable")
    public void store_response_body_value_in_variable(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        if (rows.get(0).size() != 3) {
            throw new IllegalArgumentException("DataTable must have three columns. variableName | variableType | responsePath");
        }
        for(int i=0; i<rows.size(); i++) {
            dataStoreManager.storeResponseBodyValue(rows.get(i), response);
        }
    }

}
