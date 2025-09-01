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

package com.tw.go.plugin;

import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.setting.PluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.JSONUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.*;

import static com.tw.go.plugin.BuildStatusNotifierPlugin.PLUGIN_SETTINGS_GET_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class GerritBuildStatusNotifierPluginTest {
    public static final String PLUGIN_ID = "gerrit-plugin-id";
    public static final String POLLER_PLUGIN_ID = "gerrit-repo-poller-plugin-id";
    @Mock
    private GoApplicationAccessor goApplicationAccessor;
    @Mock
    private Provider provider;

    private BuildStatusNotifierPlugin plugin;

    @BeforeEach
    public void setUp() {
        openMocks(this);

        plugin = new GerritBuildStatusNotifierPlugin();

        DefaultGoApiResponse pluginSettingsResponse = new DefaultGoApiResponse(200);
        pluginSettingsResponse.setResponseBody(JSONUtils.toJSON(new HashMap<String, String>()));
        when(goApplicationAccessor.submit(any(GoApiRequest.class))).thenReturn(pluginSettingsResponse);
        when(provider.pluginId()).thenReturn(PLUGIN_ID);
        when(provider.pollerPluginIds()).thenReturn(Collections.singletonList(POLLER_PLUGIN_ID));

        plugin.initializeGoApplicationAccessor(goApplicationAccessor);
        plugin.setProvider(provider);
    }

    @Test
    public void shouldRegisterForStageStatusChange() {
        assertThat(plugin.handleNotificationsInterestedIn().responseBody()).isEqualTo("{\"notifications\":[\"stage-status\"]}");
    }

    @Test
    public void shouldDelegateUpdateStatusToProviderWithCorrectParameters() throws Exception {
        PluginSettings mockSettings = mock(PluginSettings.class);
        when(plugin.getPluginSettings()).thenReturn(mockSettings);

        String expectedURL = "url";
        String expectedUsername = "username";
        String expectedRevision = "sha-1";
        String expectedPRId = "1";
        String pipelineName = "pipeline";
        String pipelineCounter = "1";
        String stageName = "stage";
        String stageCounter = "1";
        String expectedPipelineStage = String.format("%s/%s", pipelineName, stageName);
        String expectedPipelineInstance = String.format("%s/%s/%s/%s", pipelineName, pipelineCounter, stageName, stageCounter);
        String expectedStageResult = "Passed";

        Map requestBody = createRequestBodyMap(expectedURL, expectedUsername, expectedRevision, expectedPRId, pipelineName, pipelineCounter, stageName, stageCounter, expectedStageResult);
        plugin.handleStageNotification(createGoPluginAPIRequest(requestBody));

        verify(provider).updateStatus(eq(expectedURL), any(PluginSettings.class), eq("1"), eq(expectedRevision), eq(expectedPipelineStage), eq(expectedStageResult), eq("http://localhost:8153/go/pipelines/" + expectedPipelineInstance));
    }

    @Test
    public void shouldReturnPluginSettings() throws Exception {
        Provider mockProvider = mock(Provider.class);
        PluginConfigurationView mockConfigView = mock(PluginConfigurationView.class);
        when(mockProvider.configurationView()).thenReturn(mockConfigView);
        Map<String, Object> fields = new HashMap<>();
        when(mockConfigView.fields()).thenReturn(fields);

        plugin.setProvider(mockProvider);

        Map<String, Object> configuration = new Gson().fromJson(
                plugin.handle(createRequest(PLUGIN_SETTINGS_GET_CONFIGURATION)
                ).responseBody(), Map.class);

        assertThat(configuration).isEqualTo(fields);
    }

    private GoPluginApiRequest createRequest(final String name) {
        return new GoPluginApiRequest() {
            @Override
            public String extension() {
                return null;
            }

            @Override
            public String extensionVersion() {
                return null;
            }

            @Override
            public String requestName() {
                return name;
            }

            @Override
            public Map<String, String> requestParameters() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                return null;
            }

            @Override
            public String requestBody() {
                return null;
            }
        };
    }

    private Map createRequestBodyMap(String url, String username, String revision, String prId, String pipelineName, String pipelineCounter, String stageName, String stageCounter, String stageResult) {
        Map materialRevisionMap = new HashMap();
        Map materialMap = new HashMap();
        materialMap.put("type", "scm");
        materialMap.put("plugin-id", POLLER_PLUGIN_ID);
        Map configurationMap = new HashMap();
        configurationMap.put("url", url);
        configurationMap.put("username", username);
        materialMap.put("scm-configuration", configurationMap);
        materialRevisionMap.put("material", materialMap);

        List modifications = new ArrayList();
        Map modificationMap = new HashMap();
        modificationMap.put("revision", revision);
        Map modificationDataMap = new HashMap();
        modificationDataMap.put("PR_ID", prId);
        modificationMap.put("data", modificationDataMap);
        modifications.add(modificationMap);
        materialRevisionMap.put("modifications", modifications);

        Map pipelineMap = new HashMap();
        List buildCause = new ArrayList();
        buildCause.add(materialRevisionMap);
        pipelineMap.put("build-cause", buildCause);

        Map stageMap = new HashMap();
        stageMap.put("name", stageName);
        stageMap.put("counter", stageCounter);
        stageMap.put("result", stageResult);
        pipelineMap.put("stage", stageMap);

        pipelineMap.put("name", pipelineName);
        pipelineMap.put("counter", pipelineCounter);

        Map requestBody = new HashMap();
        requestBody.put("pipeline", pipelineMap);
        return requestBody;
    }

    private DefaultGoPluginApiRequest createGoPluginAPIRequest(Map requestBody) {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest(BuildStatusNotifierPlugin.EXTENSION_NAME, "1.0", BuildStatusNotifierPlugin.REQUEST_STAGE_STATUS);
        request.setRequestBody(JSONUtils.toJSON(requestBody));
        return request;
    }
}
