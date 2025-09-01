package com.tw.go.plugin.provider;

import com.tw.go.plugin.setting.PluginConfigurationView;

import java.util.HashMap;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static com.tw.go.plugin.util.ConfigurationUtils.createField;

public class GitlLabConfigurationView implements PluginConfigurationView {

    @Override
    public String templateName() {
        return "plugin-settings-gitlab.template.html";
    }

    @Override
    public Map<String, Object> fields() {
        Map<String, Object> response = new HashMap<>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_END_POINT, createField("End Point", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_OAUTH_TOKEN, createField("OAuth Token", null, true, true, "4"));
        return response;
    }
}
