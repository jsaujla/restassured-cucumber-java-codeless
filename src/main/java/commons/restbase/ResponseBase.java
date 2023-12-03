package commons.restbase;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a base for handling REST responses.
 */
public class ResponseBase {

    /**
     * Logger object for logging purposes. It's declared as final because it's a constant.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseBase.class);

    /**
     * ThreadLocal variable to hold the ResponseSpecification for each thread.
     */
    private final ThreadLocal<ResponseSpecification> responseSpecification = new ThreadLocal<>();

    /**
     * Constructor to initialize the ResponseSpecification.
     */
    public ResponseBase() {
        LOGGER.info("Constructing ResponseBase");
        createResponseSpecification();
    }

    /**
     * Getter method for the ResponseSpecification.
     * @return ResponseSpecification for the current thread.
     */
    public ResponseSpecification getResponseSpecification() {
        return responseSpecification.get();
    }

    /**
     * Method to create and set the ResponseSpecification for the current thread.
     */
    private void createResponseSpecification() {
        LOGGER.info("Creating ResponseSpecBuilder");
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder().log(LogDetail.ALL);
        LOGGER.info("Returning ResponseSpecification");
        responseSpecification.set(responseSpecBuilder.build());
    }
}
