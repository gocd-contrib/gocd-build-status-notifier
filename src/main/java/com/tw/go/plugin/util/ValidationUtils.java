package com.tw.go.plugin.util;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtils {

    public static Map<String, Object> getValidationError(String fieldName, String message) {
        Map<String, Object> validationError = new HashMap<String, Object>();
        validationError.put("key", fieldName);
        validationError.put("message", message);
        return validationError;
    }

}
