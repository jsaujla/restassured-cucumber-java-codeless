package com.spotify.oauth;

import com.spotify.config.ConfigLoader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import commons.restbase.RequestBase;
import commons.restbase.ResponseBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to manage access tokens for API requests.
 */
public class TokenManager {

    /**
     * Logger object for logging purposes. It's declared as final because it's a constant.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenManager.class);

    /**
     * The access token used for API requests.
     */
    private static String accessToken;

    /**
     * The type of the access token.
     */
    private static String accessTokenType;

    /**
     * The expiry time of the access token.
     */
    private static Instant expiryTime;

    /**
     * Returns the access token. If the token is invalid, it renews the token.
     *
     * @return the access token
     */
    public synchronized static String getToken() {
        if (isTokenInvalid()) {
            LOGGER.info("Renewing token");
            renewToken();
        }
        LOGGER.info("Returning token");
        return accessTokenType + " " + accessToken;
    }

    /**
     * Checks if the access token is invalid.
     *
     * @return true if the token is invalid; false otherwise
     */
    private static boolean isTokenInvalid() {
        return accessToken == null || Instant.now().isAfter(expiryTime);
    }

    /**
     * Renews the access token.
     */
    private static void renewToken() {
        Response response = executeTokenRenewalRequest();
        if (response.statusCode() != 200) {
            throw new RuntimeException("Renew token failed");
        }
        accessToken = response.path("access_token");
        accessTokenType = response.path("token_type");
        int expiryDurationInSeconds = response.path("expires_in");
        expiryTime = Instant.now().plusSeconds(expiryDurationInSeconds - 300);
    }

    /**
     * Executes the token renewal request.
     *
     * @return the response of the token renewal request
     */
    private static Response executeTokenRenewalRequest() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", ConfigLoader.getInstance().getClientId());
        formParams.put("client_secret", ConfigLoader.getInstance().getClientSecret());
        formParams.put("refresh_token", ConfigLoader.getInstance().getRefreshToken());
        formParams.put("grant_type", ConfigLoader.getInstance().getGrantType());

        RequestBase requestBase = new RequestBase(ConfigLoader.getInstance().getAccountsBaseUri());
        requestBase.getRequestSpecification().formParams(formParams);
        requestBase.getRequestSpecification().contentType(ContentType.URLENC);

        ResponseBase responseBase = new ResponseBase();

        return requestBase.getRequestSpecification().post("api/token")
                .then().spec(responseBase.getResponseSpecification()).extract().response();
    }
}
