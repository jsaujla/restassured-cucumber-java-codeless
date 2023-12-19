package com.spotify.codeless.support;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * This class manages the request body.
 */
public class RequestBodyManager {

    /**
     * Logger object for logging purposes. It's declared as final because it's a constant.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBodyManager.class);

    /**
     * The configuration object for the JsonPath library.
     * This configuration uses the default settings and adds the DEFAULT_PATH_LEAF_TO_NULL option.
     * The DEFAULT_PATH_LEAF_TO_NULL option means that if the leaf of a path is missing in the document,
     * null will be returned instead of throwing an exception.
     */
    private static final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    /**
     * Validates the size of the table.
     *
     * @param table The table to be validated.
     * @throws IllegalArgumentException If the table does not have two rows.
     */
    public void validateTableSize(List<List<String>> table) {
        if (table.size() != 2) {
            throw new IllegalArgumentException("DataTable must have two rows");
        }
    }

    /**
     * Reads a JSON document from a file.
     *
     * @param jsonFilePath The path of the JSON file.
     * @return The JSON document.
     */
    public Object readJsonFromFile(String jsonFilePath) {
        LOGGER.info("Reading JSON file: '{}'", jsonFilePath);
        Object document = null;
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            document = conf.defaultConfiguration().jsonProvider().parse(jsonString);
        } catch (Exception e) {
            LOGGER.error("Failed to read JSON file: '{}'", jsonFilePath, e);
        }
        return document;
    }

    /**
     * Updates a JSON obj with a data table.
     *
     * @param obj The JSON obj to be updated.
     * @param table    The data table.
     */
    public void updateJsonWithDataTable(Object obj, List<List<String>> table) {
        LOGGER.info("Updating JSON: '{}' with DataTable", obj.toString());
        for (int i = 0; i < table.get(0).size(); i++) {
            String key = table.get(0).get(i);
            String value = table.get(1).get(i);
            try {
                Object existingValue = JsonPath.read(obj, key);
                Object newValue = determineNewValue(existingValue, value);
                JsonPath.parse(obj).set(key, newValue);
            } catch (Exception e) {
                LOGGER.error("Failed to update JSON", e);
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("Updated JSON: '{}'", obj);
    }

    /**
     * Determines the new value based on the existing value and the new value.
     *
     * @param existingValue The existing value.
     * @param value         The new value.
     * @return The determined new value.
     */
    private Object determineNewValue(Object existingValue, String value) {
        LOGGER.info("Determine new value");
        Object newValue = value;
        if (existingValue != null) {
            newValue = parseValueBasedOnExistingType(existingValue, value);
        } else {
            newValue = parseValueBasedOnValue(value);
        }
        return newValue;
    }

    /**
     * Parses a value based on the existing type of a key in a JSON object.
     *
     * @param existingValue The existing value.
     * @param value         The value to be parsed.
     * @return The parsed value.
     */
    private Object parseValueBasedOnExistingType(Object existingValue, String value) {
        LOGGER.info("Parse value based on existing type");
        Object newValue = value;
        if (existingValue instanceof Boolean) {
            newValue = Boolean.parseBoolean(value);
        } else if (existingValue instanceof Integer) {
            newValue = Integer.parseInt(value);
        } else if (existingValue instanceof Double) {
            newValue = Double.parseDouble(value);
        }
        return newValue;
    }

    /**
     * Parses a value based on the value itself.
     *
     * @param value The value to be parsed.
     * @return The parsed value.
     */
    private Object parseValueBasedOnValue(String value) {
        LOGGER.info("Parse value based on value: '{}'", value);
        Object newValue = value;
        if ("true".equals(value) || "false".equals(value)) {
            newValue = Boolean.parseBoolean(value);
        } else if (value.matches("\\d+")) {
            newValue = Integer.parseInt(value);
        } else if (value.matches("\\d+\\.\\d+")) {
            newValue = Double.parseDouble(value);
        } else if (value.equals("null")) {
            newValue = null;
        }
        return newValue;
    }
}
