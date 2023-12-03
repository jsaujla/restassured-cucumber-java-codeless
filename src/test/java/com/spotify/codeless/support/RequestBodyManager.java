package com.spotify.codeless.support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
     * Validates the size of the table.
     *
     * @param table The table to be validated.
     */
    public void validateTableSize(List<List<String>> table) {
        if (table.size() != 2) {
            throw new IllegalArgumentException("DataTable must have two rows");
        }
    }

    /**
     * Reads a JSON object from a file.
     *
     * @param jsonFilePath The path to the file.
     * @return The JSON object read from the file.
     */
    public JSONObject readJsonFromFile(String jsonFilePath) {
        LOGGER.info("Reading JSON file: '{}'", jsonFilePath);
        JSONObject json = null;
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            json = new JSONObject(jsonString);
        } catch (Exception e) {
            LOGGER.error("Failed to read JSON file: '{}'", jsonFilePath, e);
        }
        return json;
    }

    /**
     * Updates a JSON object with data from a table.
     *
     * @param json  The JSON object to be updated.
     * @param table The table containing the data.
     */
    public void updateJsonWithDataTable(JSONObject json, List<List<String>> table) {
        LOGGER.info("Updating JSON: '{}' with DataTable", json.toString());
        for (int i = 0; i < table.get(0).size(); i++) {
            String key = table.get(0).get(i);
            String value = table.get(1).get(i);
            try {
                updateJsonValue(json, key, value);
            } catch (JSONException e) {
                LOGGER.error("Failed to update JSON", e);
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("Updated JSON: '{}'", json);
    }

    /**
     * Updates a value in a JSON object.
     *
     * @param json  The JSON object to be updated.
     * @param key   The key of the value to be updated.
     * @param value The new value.
     * @throws JSONException If the key is not found in the JSON object.
     */
    private void updateJsonValue(JSONObject json, String key, String value) throws JSONException {
        LOGGER.info("Updating JSON value");
        if (key.contains(".")) {
            updateNestedJsonValue(json, key, value);
        } else {
            putValueInJson(json, key, value);
        }
    }

    /**
     * Updates a nested value in a JSON object.
     *
     * @param json  The JSON object to be updated.
     * @param key   The key of the value to be updated.
     * @param value The new value.
     * @throws JSONException If the key is not found in the JSON object.
     */
    private void updateNestedJsonValue(JSONObject json, String key, String value) throws JSONException {
        LOGGER.info("Updating nested JSON value");
        String[] parts = key.split("\\.");
        JSONObject lastNestedJson = json;
        for (int j = 0; j < parts.length - 1; j++) {
            lastNestedJson = getNestedJson(lastNestedJson, parts[j]);
        }
        putValueInJson(lastNestedJson, parts[parts.length - 1], value);
    }

    /**
     * Retrieves a nested JSON object from a JSON object.
     *
     * @param json The JSON object.
     * @param key  The key of the nested JSON object.
     * @return The nested JSON object.
     * @throws JSONException If the key is not found in the JSON object.
     */
    private JSONObject getNestedJson(JSONObject json, String key) throws JSONException {
        LOGGER.info("Returning nested JSON object");
        if (key.matches(".+\\[\\d+\\]$")) {
            return getJsonFromJsonArray(json, key);
        } else {
            return json.has(key) ? json.getJSONObject(key) : json.put(key, new JSONObject()).getJSONObject(key);
        }
    }

    /**
     * Retrieves a JSON object from a JSON array within a JSON object.
     *
     * @param json The JSON object.
     * @param key  The key of the JSON array.
     * @return The JSON object from the JSON array.
     * @throws JSONException If the key is not found in the JSON object.
     */
    private JSONObject getJsonFromJsonArray(JSONObject json, String key) throws JSONException {
        LOGGER.info("Returning JSON object from JSONArray");
        String arrayKey = key.substring(0, key.indexOf('['));
        int index = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
        if (!json.has(arrayKey)) {
            json.put(arrayKey, new JSONArray());
        }
        JSONArray jsonArray = json.getJSONArray(arrayKey);
        while (jsonArray.length() <= index) {
            jsonArray.put(new JSONObject());
        }
        return jsonArray.getJSONObject(index);
    }

    /**
     * Puts a new value in a JSON object.
     *
     * @param json  The JSON object.
     * @param key   The key of the value to be put.
     * @param value The new value.
     * @throws JSONException If the key is not found in the JSON object.
     */
    private void putValueInJson(JSONObject json, String key, String value) throws JSONException {
        LOGGER.info("Put value in JSON");
        Object newValue = determineNewValue(json, key, value);
        json.put(key, newValue);
    }

    /**
     * Determines the new value to be put in a JSON object.
     *
     * @param json  The JSON object.
     * @param key   The key of the value to be put.
     * @param value The new value.
     * @return The determined new value.
     * @throws JSONException If the key is not found in the JSON object.
     */
    private Object determineNewValue(JSONObject json, String key, String value) throws JSONException {
        LOGGER.info("Determine new value");
        Object newValue = value;
        if (json.has(key)) {
            newValue = parseValueBasedOnExistingType(json, key, value);
        } else {
            newValue = parseValueBasedOnValue(value);
        }
        return newValue;
    }

    /**
     * Parses a value based on the existing type of a key in a JSON object.
     *
     * @param json  The JSON object.
     * @param key   The key of the value to be parsed.
     * @param value The value to be parsed.
     * @return The parsed value.
     * @throws JSONException If the key is not found in the JSON object.
     */
    private Object parseValueBasedOnExistingType(JSONObject json, String key, String value) throws JSONException {
        LOGGER.info("Parse value based on existing type. Key: '{}', Value: '{}'", key, value);
        Object newValue = value;
        if (json.get(key) instanceof Boolean) {
            newValue = Boolean.parseBoolean(value);
        } else if (json.get(key) instanceof Integer) {
            newValue = Integer.parseInt(value);
        } else if (json.get(key) instanceof Double) {
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
