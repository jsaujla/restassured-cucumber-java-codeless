package com.spotify.codeless.support;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class manages data stores of different types.
 */
public class DataStoreManager {

    /**
     * Logger object for logging purposes. It's declared as final because it's a constant.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStoreManager.class);

    Map<String, String> stringDataStore = new HashMap<>();
    Map<String, Integer> integerDataStore = new HashMap<>();
    Map<String, Boolean> booleanDataStore = new HashMap<>();
    Map<String, BigDecimal> decimalDataStore = new HashMap<>();

    /**
     * Stores the response body value in the appropriate data store based on the variable type.
     *
     * @param row      The row of data-table to be stored.
     * @param response The response from which the data is extracted.
     */
    public void storeResponseBodyValue(Map<String, String> row, Response response) {
        LOGGER.info("Storing response body value");
        String variableName = row.get("variableName");
        String variableType = row.get("variableType");
        String responsePath = row.get("responsePath");

        if(variableType.equalsIgnoreCase("Boolean")) {
            booleanDataStore.put(variableName, response.getBody().path(responsePath));
        } else if (variableType.equalsIgnoreCase("Integer")) {
            integerDataStore.put(variableName, response.getBody().path(responsePath));
        } else if (variableType.equalsIgnoreCase("Decimal")) {
            decimalDataStore.put(variableName, response.getBody().path(responsePath));
        } else if (variableType.equalsIgnoreCase("String")) {
            stringDataStore.put(variableName, response.getBody().path(responsePath));
        } else {
            throw new IllegalArgumentException("variableType should be Boolean, Integer, Decimal or String");
        }
    }

    /**
     * Converts or retrieves the expected value based on its type.
     *
     * @param expectedValue     The expected value to be converted or retrieved.
     * @param expectedValueType The type of the expected value.
     * @return The converted or retrieved expected value.
     */
    public Object convertOrRetrieveExpectedValue(Object expectedValue, String expectedValueType) {
        LOGGER.info("Converting or retrieving expected value with expectedValue: '{}', expectedValueType: '{}'", expectedValue, expectedValueType);
        if (expectedValue.toString().startsWith("{") && expectedValue.toString().endsWith("}")) {
            expectedValue = getStoredValue(expectedValue.toString().replace("{", "").replace("}", ""), expectedValueType);
        } else if (expectedValueType.equalsIgnoreCase("Boolean")) {
            expectedValue = Boolean.parseBoolean(expectedValue.toString());
        } else if (expectedValueType.equalsIgnoreCase("Integer")) {
            expectedValue = Integer.parseInt(expectedValue.toString());
        } else if (expectedValueType.equalsIgnoreCase("Decimal")) {
            expectedValue = new BigDecimal(expectedValue.toString());
        }
        return expectedValue;
    }

    /**
     * Retrieves the stored value based on the variable name and type.
     *
     * @param variableName The name of the variable.
     * @param variableType The type of the variable.
     * @return The stored value.
     */

    private Object getStoredValue(String variableName, String variableType) {
        LOGGER.info("Returning stored value with variableName: '{}', variableType: '{}'", variableName, variableType);
        if(variableType.equalsIgnoreCase("Boolean")) {
            return booleanDataStore.get(variableName);
        } else if (variableType.equalsIgnoreCase("Integer")) {
            return integerDataStore.get(variableName);
        } else if (variableType.equalsIgnoreCase("Decimal")) {
            return decimalDataStore.get(variableName);
        } else if (variableType.equalsIgnoreCase("String")) {
            return stringDataStore.get(variableName);
        } else {
            throw new IllegalArgumentException("variableType should be Boolean, Integer, Decimal or String");
        }
    }

    /**
     * Replaces placeholders in a string with data from the data stores.
     *
     * @param str The string with placeholders.
     * @return The string with placeholders replaced with data.
     */
    public String resolvePlaceholdersWithData(String str) {
        LOGGER.info("Resolving placeholders with data: '{}'", str);
        List<Map<String, ?>> replacementsList = Arrays.asList(stringDataStore, integerDataStore, booleanDataStore, decimalDataStore);
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(str);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object replacement = findReplacement(key, replacementsList);
            if (replacement != null) {
                matcher.appendReplacement(buffer, replacement.toString());
            } else {
                throw new IllegalArgumentException("No replacement found for key: " + key);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * Finds a replacement for a key in a list of replacements.
     *
     * @param key              The key to be replaced.
     * @param replacementsList The list of replacements.
     * @return The replacement for the key.
     */
    private Object findReplacement(String key, List<Map<String, ?>> replacementsList) {
        LOGGER.info("Find replacement with key: '{}', replacementsList: '{}'", key, replacementsList);
        for (Map<String, ?> replacements : replacementsList) {
            if (replacements.containsKey(key)) {
                return replacements.get(key);
            }
        }
        return null;
    }

}
