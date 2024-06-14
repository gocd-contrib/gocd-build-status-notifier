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

import com.tw.go.plugin.setting.DefaultPluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DefaultProviderTest {

    private Provider provider;

    static class TestProvider extends DefaultProvider {

        public TestProvider() {
            super(new DefaultPluginConfigurationView());
        }

        @Override
        public String pluginId() {
            return null;
        }

        @Override
        public List<String> pollerPluginIds() {
            return Collections.emptyList();
        }

        @Override
        public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineStage, String result, String trackbackURL) throws Exception {

        }

        @Override
        public List<Map<String, Object>> validateConfig(Map<String, Object> fields) {
            return null;
        }

    }

    @BeforeEach
    public void setUp() {
        provider = new TestProvider();
    }

    @Test
    public void shouldReturnSettingsObject() {
        Map<String, String> responseBodyMap = new HashMap<>();

        responseBodyMap.put(PLUGIN_SETTINGS_SERVER_BASE_URL, "url");
        responseBodyMap.put(PLUGIN_SETTINGS_END_POINT, "endpoint");
        responseBodyMap.put(PLUGIN_SETTINGS_USERNAME, "username");
        responseBodyMap.put(PLUGIN_SETTINGS_PASSWORD, "password");
        responseBodyMap.put(PLUGIN_SETTINGS_OAUTH_TOKEN, "token");

        PluginSettings pluginSettings = provider.pluginSettings(responseBodyMap);

        assertThat(pluginSettings.getServerBaseURL()).isEqualTo("url");
        assertThat(pluginSettings.getEndPoint()).isEqualTo("endpoint");
        assertThat(pluginSettings.getUsername()).isEqualTo("username");
        assertThat(pluginSettings.getPassword()).isEqualTo("password");
        assertThat(pluginSettings.getOauthToken()).isEqualTo("token");
    }

}
