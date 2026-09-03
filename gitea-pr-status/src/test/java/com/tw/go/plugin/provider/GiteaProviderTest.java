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

import com.tw.go.plugin.setting.DefaultPluginSettings;
import com.tw.go.plugin.util.AuthenticationType;
import com.tw.go.plugin.util.HTTPClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @BeforeEach
    public void setUp() {
        pluginSettings = new DefaultPluginSettings();
        pluginSettings.setServerBaseURL(serverBaseURL);
        pluginSettings.setEndPoint(endpoint);
        pluginSettings.setUsername(testUser);
        pluginSettings.setPassword(testPassword);
        mockHttpClient = mock(HTTPClient.class);

        provider = new GiteaProvider(mockHttpClient);
    }

    @Test
    public void checkIdsMatch() {
        assertEquals("gitea.pr.status", provider.pluginId());
        assertEquals("git.fb", provider.pollerPluginId());
    }

    @Test
    public void checkValidationWithValidValues() {
        Map<String, Object> config = new LinkedHashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        Map<String, String> dummyUrl = new LinkedHashMap<>();
        dummyUrl.put("value", "http://localhost:8153");

        Map<String, String> dummyUsername = new LinkedHashMap<>();
        dummyUsername.put("value", "testUser");

        Map<String, String> dummyPassword = new LinkedHashMap<>();
        dummyPassword.put("value", "testPassword");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, dummyUrl);
        config.put(PLUGIN_SETTINGS_USERNAME, dummyUsername);
        config.put(PLUGIN_SETTINGS_PASSWORD, dummyPassword);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(errors, returnedErrors);
    }

    @Test
    public void checkValidationWithInvalidValues() {
        Map<String, Object> config = new LinkedHashMap<>();

        Map<String, String> dummyUrl = new LinkedHashMap<>();
        dummyUrl.put("value", "invalid");

        Map<String, String> dummyUsername = new LinkedHashMap<>();
        dummyUsername.put("value", "");

        Map<String, String> dummyPassword = new LinkedHashMap<>();
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
        assertThat(provider.getRepository("http://gitea.com/group/sample-repo")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("http://gitea.com/group/sample-repo.git")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("http://gitea.com/group/sample-repo/")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("http://gitea.com/group/sample-repo.git/")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("https://gitea.com/group/sample-repo")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("https://gitea.com/group/sample-repo.git")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:group/sample-repo")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:group/sample-repo.git")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("git@gitea.com:group/sample-repo.git")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("http://gitea.com/group/sub-group/sample-repo.git/")).isEqualTo("group/sub-group/sample-repo");
        assertThat(provider.getRepository("https://gitea.com/group/sub-group/another-sub-group/sample-repo")).isEqualTo("group/sub-group/another-sub-group/sample-repo");
    }

    @Test
    public void checkFormUpdateURL() {
        String gitServerBaseURL = "https://www.test-gitea.com";
        String gitServerBaseURLWithEndingSlash = "https://www.test-gitea.com/";
        String repositoryURL = "https://www.test-gitea.com/test-owner/test-repo";
        String commitRevision = "revValue";

        assertThat(provider.formUpdateURL(gitServerBaseURL, repositoryURL, commitRevision)).isEqualTo("https://www.test-gitea.com/api/v1/repos/test-owner/test-repo/statuses/revValue");
        assertThat(provider.formUpdateURL(gitServerBaseURLWithEndingSlash, repositoryURL, commitRevision)).isEqualTo("https://www.test-gitea.com/api/v1/repos/test-owner/test-repo/statuses/revValue");
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
