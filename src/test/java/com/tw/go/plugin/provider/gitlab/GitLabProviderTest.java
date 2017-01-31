package com.tw.go.plugin.provider.gitlab;

import com.google.gson.internal.LinkedHashTreeMap;
import com.tw.go.plugin.setting.DefaultPluginSettings;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_END_POINT;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_OAUTH_TOKEN;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_SERVER_BASE_URL;

public class GitLabProviderTest {
    DefaultPluginSettings pluginSettings;
    GitLabProvider provider;

    @Before
    public void setUp() throws Exception {
        pluginSettings = new DefaultPluginSettings();
        provider = new GitLabProvider();
    }

    @Test
    public void checkIdsMatch () throws Exception {
        assertEquals("gitlab.fb.status", provider.pluginId());
        assertEquals("git.fb", provider.pollerPluginId());
    }

    @Test
    public void checkValidationWithValidValues () throws Exception {
        Map<String, Object> config = new LinkedHashTreeMap<String, Object>();
        List<Map<String, Object>> errors = new ArrayList<Map<String, Object>>();

        Map<String, String> dummyUrl = new LinkedHashTreeMap<String, String>();
        dummyUrl.put("value", "http://localhost:8153");

        Map<String, String> dummyToken = new LinkedHashTreeMap<String, String>();
        dummyToken.put("value", "abcdef");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, (Object)dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, (Object)dummyUrl);
        config.put(PLUGIN_SETTINGS_OAUTH_TOKEN, (Object)dummyToken);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(errors, returnedErrors);
    }

    @Test
    public void checkValidationWithInvalidValues () throws Exception {
        Map<String, Object> config = new LinkedHashTreeMap<String, Object>();

        Map<String, String> dummyUrl = new LinkedHashTreeMap<String, String>();
        dummyUrl.put("value", "localhost:8153");

        Map<String, String> dummyToken = new LinkedHashTreeMap<String, String>();
        dummyToken.put("value", "");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, (Object)dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, (Object)dummyUrl);
        config.put(PLUGIN_SETTINGS_OAUTH_TOKEN, (Object)dummyToken);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(3, returnedErrors.size());
    }
}
