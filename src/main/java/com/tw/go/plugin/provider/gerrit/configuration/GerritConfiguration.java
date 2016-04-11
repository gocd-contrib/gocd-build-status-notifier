package com.tw.go.plugin.provider.gerrit.configuration;

import com.tw.go.plugin.setting.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultConfiguration.*;
import static com.tw.go.plugin.util.ConfigurationUtils.createField;

public class GerritConfiguration implements Configuration {

    public static final String PLUGIN_SETTINGS_REVIEW_LABEL = "review_label";

    @Override
    public String templateName() {
        return "plugin-settings-gerrit.template.html";
    }

    @Override
    public Map<String, Object> fields() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_END_POINT, createField("End Point", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_USERNAME, createField("Username", null, true, false, "2"));
        response.put(PLUGIN_SETTINGS_PASSWORD, createField("Password", null, true, true, "3"));
        response.put(PLUGIN_SETTINGS_OAUTH_TOKEN, createField("OAuth Token", null, true, true, "4"));
        response.put(PLUGIN_SETTINGS_REVIEW_LABEL, createField("Gerrit Review Label", "Verified", true, false, "5"));
        return response;
    }

}
