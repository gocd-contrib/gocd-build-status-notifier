package com.tw.go.plugin.setting;

import java.util.HashMap;
import java.util.Map;

import static com.tw.go.plugin.util.ConfigurationUtils.createField;

public class DefaultConfiguration implements Configuration {

    public static final String PLUGIN_SETTINGS_SERVER_BASE_URL = "server_base_url";
    public static final String PLUGIN_SETTINGS_END_POINT = "end_point";
    public static final String PLUGIN_SETTINGS_USERNAME = "username";
    public static final String PLUGIN_SETTINGS_PASSWORD = "password";
    public static final String PLUGIN_SETTINGS_OAUTH_TOKEN = "oauth_token";

    @Override
    public String templateName() {
        return "plugin-settings.template.html";
    }

    @Override
    public Map<String, Object> fields() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_END_POINT, createField("End Point", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_USERNAME, createField("Username", null, true, false, "2"));
        response.put(PLUGIN_SETTINGS_PASSWORD, createField("Password", null, true, true, "3"));
        response.put(PLUGIN_SETTINGS_OAUTH_TOKEN, createField("OAuth Token", null, true, true, "4"));
        return response;
    }
}
