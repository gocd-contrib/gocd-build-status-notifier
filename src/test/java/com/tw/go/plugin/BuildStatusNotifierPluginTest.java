package com.tw.go.plugin;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.tw.go.plugin.provider.Provider;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BuildStatusNotifierPluginTest {
    private Provider provider;
    private BuildStatusNotifierPlugin plugin;

    @Before
    public void setUp() {
        provider = mock(Provider.class);

        plugin = new BuildStatusNotifierPlugin();
        plugin.setProvider(provider);
    }

    @Test
    public void shouldRegisterForStageStatusChange() {
        assertThat(plugin.handleNotificationsInterestedIn().responseBody(), is("{\"notifications\":[\"stage-status\"]}"));
    }

    @Test
    public void shouldDelegateUpdateStatusToProviderWithCorrectParameters() throws Exception {
        when(provider.pollerPluginId()).thenReturn("github.pr");

        String expectedURL = "url";
        String expectedUsername = "username";
        String expectedRevision = "sha-1";
        String expectedPRId = "1";
        String pipelineName = "pipeline";
        String pipelineCounter = "1";
        String stageName = "stage";
        String stageCounter = "1";
        String expectedPipelineInstance = String.format("%s/%s/%s/%s", pipelineName, pipelineCounter, stageName, stageCounter);
        String expectedStageResult = "Passed";

        Map requestBody = createRequestBodyMap(expectedURL, expectedUsername, expectedRevision, expectedPRId, pipelineName, pipelineCounter, stageName, stageCounter, expectedStageResult);
        plugin.handleStageNotification(mockGoPluginAPIRequest(new GsonBuilder().create().toJson(requestBody)));

        verify(provider).updateStatus(eq(expectedURL), eq(expectedUsername), eq("1"), eq(expectedRevision), eq(expectedPipelineInstance), eq(expectedStageResult), eq("https://localhost:8153/go/pipelines/" + expectedPipelineInstance));
    }

    private Map createRequestBodyMap(String url, String username, String revision, String prId, String pipelineName, String pipelineCounter, String stageName, String stageCounter, String stageResult) {
        Map materialRevisionMap = new HashMap();
        Map materialMap = new HashMap();
        materialMap.put("type", "scm");
        materialMap.put("plugin-id", "github.pr");
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

        Map requestBody = new HashMap();
        requestBody.put("pipeline-name", pipelineName);
        requestBody.put("pipeline-counter", pipelineCounter);
        requestBody.put("stage-name", stageName);
        requestBody.put("stage-counter", stageCounter);
        requestBody.put("stage-result", stageResult);
        requestBody.put("pipeline", pipelineMap);
        return requestBody;
    }

    private GoPluginApiRequest mockGoPluginAPIRequest(final String requestBody) {
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
                return null;
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
                return requestBody;
            }
        };
    }
}
