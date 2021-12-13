/*
 * Copyright 2019 ThoughtWorks, Inc.
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

package com.tw.go.plugin.provider;

public class GiteaState {
    private static final String PENDING = "pending";
    private static final String PENDING_DESCRIPTION = "Build is pending";
    private static final String SUCCESS = "success";
    private static final String SUCCESS_DESCRIPTION = "Build is passing";
    private static final String FAILURE = "failure";
    private static final String FAILURE_DESCRIPTION = "Build is failing";
    private static final String ERROR = "error";
    private static final String ERROR_DESCRIPTION = "Build encountered an error";

    public GiteaState() {
    }

    static String stateFor(String result) {
        result = result == null ? "" : result;
        String state = PENDING;
        if (result.equalsIgnoreCase("Passed")) {
            state = SUCCESS;
        } else if (result.equalsIgnoreCase("Failed")) {
            state = FAILURE;
        } else if (result.equalsIgnoreCase("Cancelled")) {
            state = ERROR;
        }
        return state;
    }

    static String descriptionFor(String result) {
        result = result == null ? "" : result;
        String description = PENDING_DESCRIPTION;
        if (result.equalsIgnoreCase("Passed")) {
            description = SUCCESS_DESCRIPTION;
        } else if (result.equalsIgnoreCase("Failed")) {
            description = FAILURE_DESCRIPTION;
        } else if (result.equalsIgnoreCase("Cancelled")) {
            description = ERROR_DESCRIPTION;
        }
        return description;
    }
}
