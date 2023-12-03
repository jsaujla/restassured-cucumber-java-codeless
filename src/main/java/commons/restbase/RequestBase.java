package commons.restbase;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Class for managing request specifications in RestAssured.
 * Each thread gets its own instance of RequestSpecification.
 */
public class RequestBase {

    /**
     * Logger object for logging purposes. It's declared as final because it's a constant.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBase.class);

    /**
     * Base URI for the requests.
     */
    private final String baseUri;

    /**
     * Builder for the request specifications.
     */
    private static RequestSpecBuilder requestSpecBuilder;

    /**
     * ThreadLocal variable to hold the RequestSpecification for each thread.
     */
    private final ThreadLocal<RequestSpecification> requestSpecification = new ThreadLocal<>();

    /**
     * Constructor to initialize the RequestSpecification and its builder.
     * @param baseUri Base URI for the requests.
     */
    public RequestBase(String baseUri) {
        LOGGER.info("Constructing RequestBase with baseUri: '{}'", baseUri);
        this.baseUri = baseUri;
        if (requestSpecBuilder == null) {
            createRequestBuilder();
        }
        createRequestSpecification();
    }

    /**
     * Getter method for the RequestSpecification.
     * @return RequestSpecification for the current thread.
     */
    public RequestSpecification getRequestSpecification() {
        LOGGER.info("Returning RequestSpecification");
        return requestSpecification.get();
    }

    /**
     * Method to reset the RequestSpecification for the current thread.
     */
    public void resetRequestSpecification() {
        LOGGER.info("Reset RequestSpecification");
        createRequestSpecification();
    }

    /**
     * Method to create and set the RequestSpecification for the current thread.
     */
    private void createRequestSpecification() {
        requestSpecification.set(RestAssured.given().spec(requestSpecBuilder.build()).baseUri(baseUri));
    }

    /**
     * Method to create and set the RequestSpecBuilder with logging filters.
     */
    private void createRequestBuilder() {
        LOGGER.info("Creating RequestBuilder");
        PrintStream logFile = null;
        PrintStream errorLogFile = null;
        try {
            String threadName = Thread.currentThread().getName();
            logFile = new PrintStream("target/restassured-logs/request-response-all-" + threadName + "log");
            errorLogFile = new PrintStream("target/restassured-logs/error-" + threadName + "log");
        } catch (FileNotFoundException e) {
            LOGGER.error("Failed to create restassured log files under target folder", e);
            e.printStackTrace();
        }

        assert logFile != null;
        assert errorLogFile != null;
        requestSpecBuilder = new RequestSpecBuilder().log(LogDetail.ALL).
                addFilter(new RequestLoggingFilter(LogDetail.ALL, logFile)).
                addFilter(new ResponseLoggingFilter(LogDetail.ALL, logFile)).
                addFilter(new ErrorLoggingFilter(errorLogFile));
    }
}
