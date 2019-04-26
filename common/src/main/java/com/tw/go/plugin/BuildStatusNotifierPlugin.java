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

package com.tw.go.plugin;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

public abstract class BuildStatusNotifierPlugin implements GoPlugin {
    private static Logger LOGGER = Logger.getLoggerFor(BuildStatusNotifierPlugin.class);

    public static final String EXTENSION_NAME = "notification";
    public static final List<String> goSupportedVersions = asList("1.0");

    public static final String PLUGIN_SETTINGS_GET_CONFIGURATION = "go.plugin-settings.get-configuration";
    public static final String PLUGIN_SETTINGS_GET_VIEW = "go.plugin-settings.get-view";
    public static final String PLUGIN_SETTINGS_VALIDATE_CONFIGURATION = "go.plugin-settings.validate-configuration";
    public static final String REQUEST_NOTIFICATIONS_INTERESTED_IN = "notifications-interested-in";
    public static final String REQUEST_STAGE_STATUS = "stage-status";

    public static final String GET_PLUGIN_SETTINGS = "go.processor.plugin-settings.get";

    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int NOT_FOUND_RESPONSE_CODE = 404;
    public static final int INTERNAL_ERROR_RESPONSE_CODE = 500;

    private Provider provider;
    private GoApplicationAccessor goApplicationAccessor;

    public BuildStatusNotifierPlugin() {
        this.provider = loadProvider();
    }

    protected abstract Provider loadProvider();

    void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        this.goApplicationAccessor = goApplicationAccessor;
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) {
        String requestName = goPluginApiRequest.requestName();
        if (requestName.equals(PLUGIN_SETTINGS_GET_CONFIGURATION)) {
            return handleGetPluginSettingsConfiguration();
        } else if (requestName.equals(PLUGIN_SETTINGS_GET_VIEW)) {
            try {
                return handleGetPluginSettingsView();
            } catch (IOException e) {
                return renderJSON(500, String.format("Failed to find template: %s", e.getMessage()));
            }
        } else if (requestName.equals(PLUGIN_SETTINGS_VALIDATE_CONFIGURATION)) {
            return handleValidatePluginSettingsConfiguration(goPluginApiRequest);
        } else if (requestName.equals(REQUEST_NOTIFICATIONS_INTERESTED_IN)) {
            return handleNotificationsInterestedIn();
        } else if (requestName.equals(REQUEST_STAGE_STATUS)) {
            return handleStageNotification(goPluginApiRequest);
        }
        return renderJSON(NOT_FOUND_RESPONSE_CODE, null);
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return getGoPluginIdentifier();
    }

    private GoPluginApiResponse handleGetPluginSettingsConfiguration() {
        return renderJSON(SUCCESS_RESPONSE_CODE, provider.configurationView().fields());
    }

    private GoPluginApiResponse handleGetPluginSettingsView() throws IOException {
        Map<String, Object> response = new HashMap<String, Object>();

        response.put("template", IOUtils.toString(getClass().getResourceAsStream("/" + provider.configurationView().templateName()), "UTF-8"));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleValidatePluginSettingsConfiguration(GoPluginApiRequest goPluginApiRequest) {
        Map<String, Object> fields = (Map<String, Object>) JSONUtils.fromJSON(goPluginApiRequest.requestBody());
        List<Map<String, Object>> response = provider.validateConfig((Map<String, Object>) fields.get("plugin-settings"));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }


    public PluginSettings getPluginSettings() {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("plugin-id", provider.pluginId());
        GoApiResponse response = goApplicationAccessor.submit(createGoApiRequest(GET_PLUGIN_SETTINGS, JSONUtils.toJSON(requestMap)));
        Map<String, String> responseBodyMap = response.responseBody() == null ? new HashMap<String, String>() : (Map<String, String>) JSONUtils.fromJSON(response.responseBody());
        return provider.pluginSettings(responseBodyMap);
    }

    GoPluginApiResponse handleNotificationsInterestedIn() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("notifications", Arrays.asList(REQUEST_STAGE_STATUS));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    GoPluginApiResponse handleStageNotification(GoPluginApiRequest goPluginApiRequest) {
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.fromJSON(goPluginApiRequest.requestBody());

        int responseCode = SUCCESS_RESPONSE_CODE;
        Map<String, Object> response = new HashMap<String, Object>();
        List<String> messages = new ArrayList<String>();
        try {
            PluginSettings pluginSettings = getPluginSettings();
            String serverBaseURLToUse = pluginSettings.getServerBaseURL();
            if (StringUtils.isEmpty(serverBaseURLToUse)) {
                serverBaseURLToUse = System.getProperty("go.plugin.build.status.go-server", "http://localhost:8153");
            }

            Map pipeline = (Map) dataMap.get("pipeline");
            Map stage = (Map) pipeline.get("stage");

            String pipelineStage = String.format("%s/%s", pipeline.get("name"), stage.get("name"));
            String pipelineInstance = String.format("%s/%s/%s/%s", pipeline.get("name"), pipeline.get("counter"), stage.get("name"), stage.get("counter"));
            String trackbackURL = String.format("%s/go/pipelines/%s", serverBaseURLToUse, pipelineInstance);
            String result = (String) stage.get("result");

            List<Map> materialRevisions = (List<Map>) pipeline.get("build-cause");
            for (Map materialRevision : materialRevisions) {
                Map material = (Map) materialRevision.get("material");
                if (isMaterialOfType(material, provider.pollerPluginId())) {
                    Map materialConfiguration = (Map) material.get("scm-configuration");
                    String url = (String) materialConfiguration.get("url");

                    List<Map> modifications = (List<Map>) materialRevision.get("modifications");
                    String revision = (String) modifications.get(0).get("revision");
                    Map modificationData = (Map) modifications.get(0).get("data");
                    String prId = (String) modificationData.get("PR_ID");

                    if (StringUtils.isEmpty(prId)) {
                        prId = (String) modificationData.get("CURRENT_BRANCH");
                    }

                    try {
                        provider.updateStatus(url, pluginSettings, prId, revision, pipelineStage, result, trackbackURL);
                    } catch (Exception e) {
                        LOGGER.error(String.format("Error occurred. Could not update build status - URL: %s Revision: %s Build: %s Result: %s", url, revision, pipelineInstance, result), e);
                    }
                }
            }

            response.put("status", "success");
            messages.add("Could connect to URL successfully");
        } catch (Exception e) {
            responseCode = INTERNAL_ERROR_RESPONSE_CODE;
            response.put("status", "failure");
            messages.add(e.getMessage());
            LOGGER.warn("Error occurred.", e);
        }

        response.put("messages", messages);
        return renderJSON(responseCode, response);
    }

    private boolean isMaterialOfType(Map material, String pollerPluginId) {
        return ((String) material.get("type")).equalsIgnoreCase("scm") && ((String) material.get("plugin-id")).equalsIgnoreCase(pollerPluginId);
    }

    private GoPluginIdentifier getGoPluginIdentifier() {
        return new GoPluginIdentifier(EXTENSION_NAME, goSupportedVersions);
    }

    private GoApiRequest createGoApiRequest(final String api, final String responseBody) {
        return new GoApiRequest() {
            @Override
            public String api() {
                return api;
            }

            @Override
            public String apiVersion() {
                return "1.0";
            }

            @Override
            public GoPluginIdentifier pluginIdentifier() {
                return getGoPluginIdentifier();
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
                return responseBody;
            }
        };
    }

    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        final String json = response == null ? null : new GsonBuilder().create().toJson(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }
}
