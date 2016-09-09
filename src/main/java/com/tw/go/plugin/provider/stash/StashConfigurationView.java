package com.tw.go.plugin.provider.stash;

import com.tw.go.plugin.setting.PluginConfigurationView;

import java.util.HashMap;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static com.tw.go.plugin.util.ConfigurationUtils.createField;

public class StashConfigurationView implements PluginConfigurationView {
    
    public static final String PLUGIN_SETTINGS_ALLOW_BUILTIN_GIT = "allow_builtin_git";

    @Override
    public String templateName() {
        return "plugin-settings-stash.template.html";
    }

    @Override
    public Map<String, Object> fields() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_END_POINT, createField("End Point", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_USERNAME, createField("Username", null, true, false, "2"));
        response.put(PLUGIN_SETTINGS_PASSWORD, createField("Password", null, true, true, "3"));
        response.put(PLUGIN_SETTINGS_OAUTH_TOKEN, createField("OAuth Token", null, true, true, "4"));
        response.put(PLUGIN_SETTINGS_ALLOW_BUILTIN_GIT, createField("Allow built-in Git Material", "false", true, false, "5"));
        return response;
    }
}
