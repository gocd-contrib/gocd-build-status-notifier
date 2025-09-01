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

package com.tw.go.plugin.provider;

import com.tw.go.plugin.setting.PluginConfigurationView;

import java.util.HashMap;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static com.tw.go.plugin.util.ConfigurationUtils.createField;

public class GerritConfigurationView implements PluginConfigurationView {

    public static final String PLUGIN_SETTINGS_REVIEW_LABEL = "review_label";

    @Override
    public String templateName() {
        return "plugin-settings-gerrit.template.html";
    }

    @Override
    public Map<String, Object> fields() {
        Map<String, Object> response = new HashMap<>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_END_POINT, createField("End Point", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_USERNAME, createField("Username", null, true, false, "2"));
        response.put(PLUGIN_SETTINGS_PASSWORD, createField("Password", null, true, true, "3"));
        response.put(PLUGIN_SETTINGS_OAUTH_TOKEN, createField("OAuth Token", null, true, true, "4"));
        response.put(PLUGIN_SETTINGS_REVIEW_LABEL, createField("Gerrit Review Label", "Verified", true, false, "5"));
        return response;
    }

}
