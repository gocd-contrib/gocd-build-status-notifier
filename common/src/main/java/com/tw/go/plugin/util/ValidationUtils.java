/*
 * Copyright 2022 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tw.go.plugin.util;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static Map<String, Object> getValidationError(String fieldName, String message) {
        Map<String, Object> validationError = new HashMap<>();
        validationError.put("key", fieldName);
        validationError.put("message", message);
        return validationError;
    }

}
