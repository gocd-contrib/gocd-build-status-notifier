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

import com.google.gson.internal.LinkedHashTreeMap;
import com.tw.go.plugin.setting.DefaultPluginSettings;
import com.tw.go.plugin.util.AuthenticationType;
import com.tw.go.plugin.util.HTTPClient;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_SERVER_BASE_URL;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_END_POINT;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_USERNAME;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_PASSWORD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GiteaProviderTest {
    DefaultPluginSettings pluginSettings;
    HTTPClient mockHttpClient;
    GiteaProvider provider;

    private static final String serverBaseURL = "https://gocd-server.com";
    private static final String endpoint = "https://www.test-gitea.com";
    private static final String testUser = "testUser";
    private static final String testPassword = "testPassword";

    @Before
    public void setUp() throws Exception {
        pluginSettings = new DefaultPluginSettings();
        pluginSettings.setServerBaseURL(serverBaseURL);
        pluginSettings.setEndPoint(endpoint);
        pluginSettings.setUsername(testUser);
        pluginSettings.setPassword(testPassword);
        mockHttpClient = mock(HTTPClient.class);

        provider = new GiteaProvider(mockHttpClient);
    }

    @Test
    public void checkIdsMatch() throws Exception {
        assertEquals("gitea.pr.status", provider.pluginId());
        assertEquals("git.fb", provider.pollerPluginId());
    }

    @Test
    public void checkValidationWithValidValues() throws Exception {
        Map<String, Object> config = new LinkedHashTreeMap<String, Object>();
        List<Map<String, Object>> errors = new ArrayList<Map<String, Object>>();

        Map<String, String> dummyUrl = new LinkedHashTreeMap<String, String>();
        dummyUrl.put("value", "http://localhost:8153");

        Map<String, String> dummyUsername = new LinkedHashTreeMap<String, String>();
        dummyUsername.put("value", "testUser");

        Map<String, String> dummyPassword = new LinkedHashTreeMap<String, String>();
        dummyPassword.put("value", "testPassword");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, dummyUrl);
        config.put(PLUGIN_SETTINGS_USERNAME, dummyUsername);
        config.put(PLUGIN_SETTINGS_PASSWORD, dummyPassword);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(errors, returnedErrors);
    }

    @Test
    public void checkValidationWithInvalidValues() throws Exception {
        Map<String, Object> config = new LinkedHashTreeMap<String, Object>();

        Map<String, String> dummyUrl = new LinkedHashTreeMap<String, String>();
        dummyUrl.put("value", "invalid");

        Map<String, String> dummyUsername = new LinkedHashTreeMap<String, String>();
        dummyUsername.put("value", "");

        Map<String, String> dummyPassword = new LinkedHashTreeMap<String, String>();
        dummyPassword.put("value", "");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, dummyUrl);
        config.put(PLUGIN_SETTINGS_USERNAME, dummyUsername);
        config.put(PLUGIN_SETTINGS_PASSWORD, dummyPassword);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(4, returnedErrors.size());
    }

    @Test
    public void shouldGetRepositoryFromURL() {
        assertThat(provider.getRepository("http://gitea.com/group/sample-repo"), is("group/sample-repo"));
        assertThat(provider.getRepository("http://gitea.com/group/sample-repo.git"), is("group/sample-repo"));
        assertThat(provider.getRepository("http://gitea.com/group/sample-repo/"), is("group/sample-repo"));
        assertThat(provider.getRepository("http://gitea.com/group/sample-repo.git/"), is("group/sample-repo"));
        assertThat(provider.getRepository("https://gitea.com/group/sample-repo"), is("group/sample-repo"));
        assertThat(provider.getRepository("https://gitea.com/group/sample-repo.git"), is("group/sample-repo"));
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:group/sample-repo"), is("group/sample-repo"));
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:group/sample-repo.git"), is("group/sample-repo"));
        assertThat(provider.getRepository("git@gitea.com:group/sample-repo.git"), is("group/sample-repo"));
        assertThat(provider.getRepository("http://gitea.com/group/sub-group/sample-repo.git/"), is("group/sub-group/sample-repo"));
        assertThat(provider.getRepository("https://gitea.com/group/sub-group/another-sub-group/sample-repo"), is("group/sub-group/another-sub-group/sample-repo"));
    }

    @Test
    public void checkFormUpdateURL() throws Exception {
        String gitServerBaseURL = "https://www.test-gitea.com";
        String gitServerBaseURLWithEndingSlash = "https://www.test-gitea.com/";
        String repositoryURL = "https://www.test-gitea.com/test-owner/test-repo";
        String commitRevision = "revValue";

        assertThat(provider.formUpdateURL(gitServerBaseURL, repositoryURL, commitRevision), is("https://www.test-gitea.com/api/v1/repos/test-owner/test-repo/statuses/revValue"));
        assertThat(provider.formUpdateURL(gitServerBaseURLWithEndingSlash, repositoryURL, commitRevision), is("https://www.test-gitea.com/api/v1/repos/test-owner/test-repo/statuses/revValue"));
    }

    @Test
    public void shouldUpdateStatusForPR() throws Exception {
        provider.updateStatus("https://www.test-gitea.com/test-owner/test-repo",
                pluginSettings,
                "test-branch",
                "revValue",
                "pipeline-name/stage-name",
                "Passed",
                "http://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1");

        verify(mockHttpClient).postRequest(
                "https://www.test-gitea.com/api/v1/repos/test-owner/test-repo/statuses/revValue",
                AuthenticationType.BASIC,
                testUser,
                testPassword,
                "{" +
                        "\"target_url\":\"http://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1\"," +
                        "\"context\":\"GoCD\"," +
                        "\"description\":\"Build is passing\"," +
                        "\"state\":\"success\"" +
                        "}"
        );
    }
}
