package com.tw.go.plugin.provider;

import com.tw.go.plugin.setting.PluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultProviderTest {

    private Provider provider;

    static class TestProvider extends DefaultProvider {
        @Override
        public String pluginId() {
            return null;
        }

        @Override
        public String pollerPluginId() {
            return null;
        }

        @Override
        public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineStage, String result, String trackbackURL) throws Exception {

        }

        @Override
        public List<Map<String, Object>> validateConfig(Map<String, Object> fields) {
            return null;
        }

        @Override
        public PluginConfigurationView configurationView() {
            return null;
        }
    }

    @Before
    public void setUp() {
        provider = new TestProvider();
    }

    @Test
    public void shouldReturnSettingsObject() {
        Map<String, String> responseBodyMap = new HashMap<String, String>();

        responseBodyMap.put(PLUGIN_SETTINGS_SERVER_BASE_URL, "url");
        responseBodyMap.put(PLUGIN_SETTINGS_END_POINT, "endpoint");
        responseBodyMap.put(PLUGIN_SETTINGS_USERNAME, "username");
        responseBodyMap.put(PLUGIN_SETTINGS_PASSWORD, "password");
        responseBodyMap.put(PLUGIN_SETTINGS_OAUTH_TOKEN, "token");

        PluginSettings pluginSettings = provider.pluginSettings(responseBodyMap);

        assertThat(pluginSettings.getServerBaseURL(), is("url"));
        assertThat(pluginSettings.getEndPoint(), is("endpoint"));
        assertThat(pluginSettings.getUsername(), is("username"));
        assertThat(pluginSettings.getPassword(), is("password"));
        assertThat(pluginSettings.getOauthToken(), is("token"));
    }

}
