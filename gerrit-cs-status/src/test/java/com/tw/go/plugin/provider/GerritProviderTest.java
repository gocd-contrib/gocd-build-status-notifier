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

import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.AuthenticationType;
import com.tw.go.plugin.util.HTTPClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.provider.GerritConfigurationView.PLUGIN_SETTINGS_REVIEW_LABEL;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GerritProviderTest {
    public static final String USERNAME = "srinivas";
    public static final String PASSWORD = "VMDiHOBuXPlBU9jQQmy+2/HMZGnW5ey3JY3cthAGXw";
    public static final String COMMIT_DETAILS_RESPONSE = "[{\n" +
            "    \"id\": \"1\",\n" +
            "    \"project\": \"foo\",\n" +
            "    \"branch\": \"branch\",\n" +
            "    \"change_id\": \"abcd\"\n" +
            "}]";
    GerritPluginSettings pluginSettings;
    GerritProvider provider;
    HTTPClient mockHttpClient;

    @BeforeEach
    public void setUp() throws Exception {
        pluginSettings = new GerritPluginSettings();
        pluginSettings.setEndPoint("http://localhost:8080");
        pluginSettings.setUsername(USERNAME);
        pluginSettings.setPassword(PASSWORD);
        pluginSettings.setReviewLabel("Verified");

        mockHttpClient = mock(HTTPClient.class);
        provider = new GerritProvider(mockHttpClient);
    }

    @Test
    public void shouldGetStateFromResult() {
        assertThat(provider.getCodeReviewValue("Unknown")).isEqualTo(GerritProvider.IN_PROGRESS_VALUE);
        assertThat(provider.getCodeReviewValue("Passed")).isEqualTo(GerritProvider.SUCCESS_VALUE);
        assertThat(provider.getCodeReviewValue("Failed")).isEqualTo(GerritProvider.FAILURE_VALUE);
        assertThat(provider.getCodeReviewValue("Cancelled")).isEqualTo(GerritProvider.FAILURE_VALUE);
    }

    @Test
    public void shouldUpdateStatus() throws Exception {
        when(mockHttpClient.getRequest(
                eq("http://localhost:8080/a/changes/?q=commit:64bdf589adda32a0652f8b3335b15bb8f53fe2cf"),
                any(AuthenticationType.class),
                eq(USERNAME),
                eq(PASSWORD))
        ).thenReturn(COMMIT_DETAILS_RESPONSE);

        provider.updateStatus(null, pluginSettings, null, "64bdf589adda32a0652f8b3335b15bb8f53fe2cf", "pipeline-name/stage-name", "Passed", "https://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1");

        verify(mockHttpClient).postRequest(
                "http://localhost:8080/a/changes/1/revisions/64bdf589adda32a0652f8b3335b15bb8f53fe2cf/review",
                AuthenticationType.DIGEST,
                USERNAME,
                PASSWORD,
                "{" +
                        "\"message\":\"pipeline-name/stage-name: https://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1\"," +
                        "\"labels\":{\"Verified\":1}" +
                        "}"
        );
    }

    @Test
    public void shouldValidateConfigurationSuccessfully() {
        Map<String, Object> settings = new HashMap<String, Object>();
        settings.put("review_label", getSettingValue());

        List<Map<String, Object>> validationErrors = provider.validateConfig(settings);

        assertThat(validationErrors.size()).isEqualTo(0);
    }

    private Map<String, Object> getSettingValue() {
        Map<String, Object> value = new HashMap<String, Object>();
        value.put("value", "Verified");
        return value;
    }

    @Test
    public void shouldValidateMissingReviewField() {
        Map<String, Object> config = new HashMap<String, Object>();
        List<Map<String, Object>> validationErrors = provider.validateConfig(config);

        assertThat(validationErrors.size()).isEqualTo(1);
        assertThat((String) validationErrors.get(0).get("key")).isEqualTo("review_label");
        assertThat((String) validationErrors.get(0).get("message")).isEqualTo("Review field must be set");
    }

    @Test
    public void shouldReturnGerritSettingsObject() {
        Map<String, String> responseBodyMap = new HashMap<String, String>();

        responseBodyMap.put(PLUGIN_SETTINGS_SERVER_BASE_URL, "url");
        responseBodyMap.put(PLUGIN_SETTINGS_END_POINT, "endpoint");
        responseBodyMap.put(PLUGIN_SETTINGS_USERNAME, "username");
        responseBodyMap.put(PLUGIN_SETTINGS_PASSWORD, "password");
        responseBodyMap.put(PLUGIN_SETTINGS_OAUTH_TOKEN, "token");
        responseBodyMap.put(PLUGIN_SETTINGS_REVIEW_LABEL, "label");

        PluginSettings pluginSettings = provider.pluginSettings(responseBodyMap);

        assertThat(pluginSettings instanceof GerritPluginSettings).isEqualTo(true);
        GerritPluginSettings gerritPluginSettings = (GerritPluginSettings) pluginSettings;

        assertThat(gerritPluginSettings.getServerBaseURL()).isEqualTo("url");
        assertThat(gerritPluginSettings.getEndPoint()).isEqualTo("endpoint");
        assertThat(gerritPluginSettings.getUsername()).isEqualTo("username");
        assertThat(gerritPluginSettings.getPassword()).isEqualTo("password");
        assertThat(gerritPluginSettings.getOauthToken()).isEqualTo("token");
        assertThat(gerritPluginSettings.getReviewLabel()).isEqualTo("label");
    }

    @Test
    public void shouldReturnCorrectTemplate() {
        assertThat(provider.configurationView().templateName()).isEqualTo("plugin-settings-gerrit.template.html");
    }

    @Test
    public void shouldReturnCorrectConfigFields() throws Exception {
        Map<String, Object> configuration = provider.configurationView().fields();

        assertThat(configuration.containsKey("server_base_url")).isEqualTo(true);
        assertThat(configuration.containsKey("end_point")).isEqualTo(true);
        assertThat(configuration.containsKey("username")).isEqualTo(true);
        assertThat(configuration.containsKey("password")).isEqualTo(true);
        assertThat(configuration.containsKey("oauth_token")).isEqualTo(true);
        assertThat(configuration.containsKey("review_label")).isEqualTo(true);
    }
}
