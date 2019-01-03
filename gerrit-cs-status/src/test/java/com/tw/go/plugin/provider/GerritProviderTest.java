package com.tw.go.plugin.provider;

import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.AuthenticationType;
import com.tw.go.plugin.util.HTTPClient;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.provider.GerritConfigurationView.PLUGIN_SETTINGS_REVIEW_LABEL;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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

    @Before
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
        assertThat(provider.getCodeReviewValue("Unknown"), is(GerritProvider.IN_PROGRESS_VALUE));
        assertThat(provider.getCodeReviewValue("Passed"), is(GerritProvider.SUCCESS_VALUE));
        assertThat(provider.getCodeReviewValue("Failed"), is(GerritProvider.FAILURE_VALUE));
        assertThat(provider.getCodeReviewValue("Cancelled"), is(GerritProvider.FAILURE_VALUE));
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

        assertThat(validationErrors.size(), is(0));
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

        assertThat(validationErrors.size(), is(1));
        assertThat((String) validationErrors.get(0).get("key"), is("review_label"));
        assertThat((String) validationErrors.get(0).get("message"), is("Review field must be set"));
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

        assertThat(pluginSettings instanceof GerritPluginSettings, is(true));
        GerritPluginSettings gerritPluginSettings = (GerritPluginSettings) pluginSettings;

        assertThat(gerritPluginSettings.getServerBaseURL(), is("url"));
        assertThat(gerritPluginSettings.getEndPoint(), is("endpoint"));
        assertThat(gerritPluginSettings.getUsername(), is("username"));
        assertThat(gerritPluginSettings.getPassword(), is("password"));
        assertThat(gerritPluginSettings.getOauthToken(), is("token"));
        assertThat(gerritPluginSettings.getReviewLabel(), is("label"));
    }

    @Test
    public void shouldReturnCorrectTemplate() {
        assertThat(provider.configurationView().templateName(), is("plugin-settings-gerrit.template.html"));
    }

    @Test
    public void shouldReturnCorrectConfigFields() throws Exception {
        Map<String, Object> configuration = provider.configurationView().fields();

        assertThat(configuration.containsKey("server_base_url"), Is.is(true));
        assertThat(configuration.containsKey("end_point"), Is.is(true));
        assertThat(configuration.containsKey("username"), Is.is(true));
        assertThat(configuration.containsKey("password"), Is.is(true));
        assertThat(configuration.containsKey("oauth_token"), Is.is(true));
        assertThat(configuration.containsKey("review_label"), Is.is(true));
    }
}
