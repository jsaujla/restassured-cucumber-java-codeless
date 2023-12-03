package com.spotify.constant;

/**
 * This class contains constants for different environment configurations and their corresponding property file paths.
 */
public final class Constants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Constants() {}

    public static final String CONFIG_QA = "config-qa";
    public static final String CONFIG_DEV = "config-dev";
    public static final String CONFIG_UAT = "config-uat";
    public static final String CONFIG_PROD = "config-prod";

    public static final String QA_CONFIG_PROPERTIES_PATH = System.getProperty("user.dir") + "/src/test/resources/config-qa.properties";
    public static final String DEV_CONFIG_PROPERTIES_PATH = System.getProperty("user.dir") + "/src/test/resources/config-dev.properties";
    public static final String UAT_CONFIG_PROPERTIES_PATH = System.getProperty("user.dir") + "/src/test/resources/config-uat.properties";
    public static final String PROD_CONFIG_PROPERTIES_PATH = System.getProperty("user.dir") + "/src/test/resources/config-prod.properties";

}
