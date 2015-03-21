package com.tw.go.plugin;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.provider.Provider;

import java.lang.reflect.Constructor;
import java.util.*;

import static java.util.Arrays.asList;

@Extension
public class BuildStatusNotifierPlugin implements GoPlugin {
    public static final String EXTENSION_NAME = "notification";
    public static final String REQUEST_NOTIFICATIONS_INTERESTED_IN = "notifications-interested-in";
    public static final String REQUEST_STAGE_STATUS = "stage-status";
    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int INTERNAL_ERROR_RESPONSE_CODE = 500;
    private static final List<String> goSupportedVersions = asList("1.0");
    private static Logger LOGGER = Logger.getLoggerFor(BuildStatusNotifierPlugin.class);

    private Provider provider;

    public BuildStatusNotifierPlugin() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/defaults.properties"));
            Class<?> providerClass = Class.forName(properties.getProperty("provider"));
            Constructor<?> constructor = providerClass.getConstructor();
            provider = (Provider) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("could not create provider", e);
        }
    }

    void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        // ignore
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) {
        if (goPluginApiRequest.requestName().equals(REQUEST_NOTIFICATIONS_INTERESTED_IN)) {
            return handleNotificationsInterestedIn();
        } else if (goPluginApiRequest.requestName().equals(REQUEST_STAGE_STATUS)) {
            return handleStageNotification(goPluginApiRequest);
        }
        return null;
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier(EXTENSION_NAME, goSupportedVersions);
    }

    GoPluginApiResponse handleNotificationsInterestedIn() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("notifications", Arrays.asList(REQUEST_STAGE_STATUS));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    GoPluginApiResponse handleStageNotification(GoPluginApiRequest goPluginApiRequest) {
        Map<String, Object> dataMap = getMapFor(goPluginApiRequest);

        int responseCode = SUCCESS_RESPONSE_CODE;
        Map<String, Object> response = new HashMap<String, Object>();
        List<String> messages = new ArrayList<String>();
        try {
            String pipelineInstance = String.format("%s/%s/%s/%s", dataMap.get("pipeline-name"), dataMap.get("pipeline-counter"), dataMap.get("stage-name"), dataMap.get("stage-counter"));
            String trackbackHostAndPort = System.getProperty("go.plugin.build.status.go-server", "localhost:8153");
            String trackbackURL = String.format("https://%s/go/pipelines/%s", trackbackHostAndPort, pipelineInstance);
            String result = (String) dataMap.get("stage-result");

            Map pipeline = (Map) dataMap.get("pipeline");
            List<Map> materialRevisions = (List<Map>) pipeline.get("build-cause");
            for (Map materialRevision : materialRevisions) {
                Map material = (Map) materialRevision.get("material");
                if (isMaterialOfType(material, provider.pollerPluginId())) {
                    Map materialConfiguration = (Map) material.get("scm-configuration");
                    String url = (String) materialConfiguration.get("url");
                    String username = (String) materialConfiguration.get("username");

                    List<Map> modifications = (List<Map>) materialRevision.get("modifications");
                    String revision = (String) modifications.get(0).get("revision");
                    Map modificationData = (Map) modifications.get(0).get("data");
                    String prId = (String) modificationData.get("PR_ID");

                    provider.updateStatus(url, username, prId, revision, pipelineInstance, result, trackbackURL);
                }
            }

            response.put("status", "success");
            messages.add("Could connect to URL successfully");
        } catch (Exception e) {
            responseCode = INTERNAL_ERROR_RESPONSE_CODE;
            response.put("status", "failure");
            messages.add(e.getMessage());
        }

        response.put("messages", messages);
        return renderJSON(responseCode, response);
    }

    private boolean isMaterialOfType(Map material, String pollerPluginId) {
        return material.get("type").equals("scm") && material.get("plugin-id").equals(pollerPluginId);
    }

    private Map<String, Object> getMapFor(GoPluginApiRequest goPluginApiRequest) {
        return (Map<String, Object>) new GsonBuilder().create().fromJson(goPluginApiRequest.requestBody(), Object.class);
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
