package com.spotify.config;

import com.spotify.constant.Constants;
import commons.properties.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for loading configurations from a properties file and replacing placeholders in strings with their corresponding property values.
 */
public class ConfigLoader {

    /**
     * Logger object for logging purposes. It's declared as final because it's a constant.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

    /**
     * The PropertiesManager instance used to fetch property values.
     */
    private static PropertiesManager propertiesManager;

    /**
     * The singleton instance of the ConfigLoader class.
     */
    private static ConfigLoader configLoader;

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private ConfigLoader(){}

    /**
     * Returns the singleton instance of the ConfigLoader class, creating it if necessary.
     *
     * @return the singleton instance of the ConfigLoader class
     */
    public static synchronized ConfigLoader getInstance(){
        if(configLoader == null){
            LOGGER.info("Creating instance of ConfigLoader");
            setConfig();
            configLoader = new ConfigLoader();
        }
        LOGGER.info("Returning instance of ConfigLoader");
        return configLoader;
    }

    /**
     * Replaces placeholders in the given string with their corresponding property values.
     * If a placeholder's corresponding property is not found, an IllegalArgumentException is thrown.
     *
     * @param str the string in which to replace placeholders
     * @return the updated string with placeholders replaced by property values
     * @throws IllegalArgumentException if a property for a placeholder is not found
     */
    public String replacePlaceholdersWithProperties(String str) {
        LOGGER.info("Replacing placeholders with properties: '{}'", str );
        int startIndex = str.indexOf("{{");
        int endIndex = str.indexOf("}}");

        while (startIndex != -1 && endIndex != -1) {
            String key = str.substring(startIndex + 2, endIndex);
            String value = propertiesManager.getProperty(key);

            if (value != null) {
                str = str.replace("{{" + key + "}}", value);
            } else {
                throw new IllegalArgumentException("Property not found: " + key);
            }

            startIndex = str.indexOf("{{");
            endIndex = str.indexOf("}}");
        }

        System.out.println("Updated endpoint: " + str);
        return str;
    }

    /**
     * Returns the base URI for the API from the properties file.
     *
     * @return the base URI for the API
     * @throws RuntimeException if the property "api.base.uri" is not specified in the config.properties file
     */
    public String getApiBaseUri(){
        String prop = propertiesManager.getProperty("api.base.uri");
        if(prop != null) return prop;
        else throw new RuntimeException("property api.base.uri is not specified in the config.properties file");
    }

    /**
     * Returns the base URI for the accounts from the properties file.
     *
     * @return the base URI for the accounts
     * @throws RuntimeException if the property "accounts.base.uri" is not specified in the config.properties file
     */
    public String getAccountsBaseUri(){
        String prop = propertiesManager.getProperty("accounts.base.uri");
        if(prop != null) return prop;
        else throw new RuntimeException("property accounts.base.uri is not specified in the config.properties file");
    }

    /**
     * Returns the client ID from the properties file.
     *
     * @return the client ID
     * @throws RuntimeException if the property "client_id" is not specified in the config.properties file
     */
    public String getClientId(){
        String prop = propertiesManager.getProperty("client_id");
        if(prop != null) return prop;
        else throw new RuntimeException("property client_id is not specified in the config.properties file");
    }

    /**
     * Returns the client secret from the properties file.
     *
     * @return the client secret
     * @throws RuntimeException if the property "client_secret" is not specified in the config.properties file
     */
    public String getClientSecret(){
        String prop = propertiesManager.getProperty("client_secret");
        if(prop != null) return prop;
        else throw new RuntimeException("property client_secret is not specified in the config.properties file");
    }

    /**
     * Returns the grant type from the properties file.
     *
     * @return the grant type
     * @throws RuntimeException if the property "grant_type" is not specified in the config.properties file
     */
    public String getGrantType(){
        String prop = propertiesManager.getProperty("grant_type");
        if(prop != null) return prop;
        else throw new RuntimeException("property grant_type is not specified in the config.properties file");
    }

    /**
     * Returns the refresh token from the properties file.
     *
     * @return the refresh token
     * @throws RuntimeException if the property "refresh_token" is not specified in the config.properties file
     */
    public String getRefreshToken(){
        String prop = propertiesManager.getProperty("refresh_token");
        if(prop != null) return prop;
        else throw new RuntimeException("property refresh_token is not specified in the config.properties file");
    }

    /**
     * Returns the user ID from the properties file.
     *
     * @return the user ID
     * @throws RuntimeException if the property "user_id" is not specified in the config.properties file
     */
    public String getUserId(){
        String prop = propertiesManager.getProperty("user_id");
        if(prop != null) return prop;
        else throw new RuntimeException("property user_id is not specified in the config.properties file");
    }

    /**
     * Reads the configuration properties file based on the environment type provided by command-line execution.
     * If environment type not provided by command-line execution, the default value 'config-qa' will be used.
     */
    private static void setConfig() {
        String environmentType = System.getProperty("config.file", Constants.CONFIG_QA);
        String configFilePath = null;
        switch (environmentType) {
            case Constants.CONFIG_DEV:
                configFilePath = Constants.DEV_CONFIG_PROPERTIES_PATH;
                break;
            case Constants.CONFIG_QA:
                configFilePath = Constants.QA_CONFIG_PROPERTIES_PATH;
                break;
            case Constants.CONFIG_UAT:
                configFilePath = Constants.UAT_CONFIG_PROPERTIES_PATH;
                break;
            case Constants.CONFIG_PROD:
                configFilePath = Constants.PROD_CONFIG_PROPERTIES_PATH;
                break;
            default:
                throw new IllegalArgumentException("Invalid environment type: " + environmentType);
        }
        propertiesManager = new PropertiesManager(configFilePath);
    }

}
