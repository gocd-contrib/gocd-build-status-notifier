package com.tw.go.plugin.provider.stash;

import com.google.gson.GsonBuilder;
import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.HashMap;
import java.util.Map;

public class StashProvider implements Provider {
    public static final String PLUGIN_ID = "stash.pr.status";
    public static final String STASH_PR_POLLER_PLUGIN_ID = "stash.pr";

    @Override
    public String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String pollerPluginId() {
        return STASH_PR_POLLER_PLUGIN_ID;
    }

    @Override
    public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineStage,
                             String result, String trackbackURL) throws Exception {
        String endPointToUse = pluginSettings.getEndPoint();
        String usernameToUse = pluginSettings.getUsername();
        String passwordToUse = pluginSettings.getPassword();

        if (StringUtils.isEmpty(endPointToUse)) {
            endPointToUse = System.getProperty("go.plugin.build.status.stash.endpoint");
        }
        if (StringUtils.isEmpty(usernameToUse)) {
            usernameToUse = System.getProperty("go.plugin.build.status.stash.username");
        }
        if (StringUtils.isEmpty(passwordToUse)) {
            passwordToUse = System.getProperty("go.plugin.build.status.stash.password");
        }

        String updateURL = String.format("%s/rest/build-status/1.0/commits/%s", endPointToUse, revision);

        Map<String, String> params = new HashMap<String, String>();
        params.put("state", getState(result));
        params.put("key", pipelineStage);
        params.put("name", pipelineStage);
        params.put("url", trackbackURL);
        params.put("description", "");
        String requestBody = new GsonBuilder().create().toJson(params);

        updateStatus(updateURL, usernameToUse, passwordToUse, requestBody);
    }

    private void updateStatus(String updateURL, String usernameToUse, String passwordToUse, String requestBody) throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {
            HttpPost request = new HttpPost(updateURL);
            byte[] authorizationData = (usernameToUse + ":" + passwordToUse).getBytes();
            request.setHeader("Authorization", "Basic " + Base64.encodeBase64String(authorizationData));
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(requestBody));

            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode > 204) {
                throw new RuntimeException("Error occurred. Status Code: " + statusCode);
            }
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    String getState(String result) {
        result = result == null ? "" : result;
        String state = "INPROGRESS";
        if (result.equalsIgnoreCase("Passed")) {
            state = "SUCCESSFUL";
        } else if (result.equalsIgnoreCase("Failed")) {
            state = "FAILED";
        } else if (result.equalsIgnoreCase("Cancelled")) {
            state = "FAILED";
        }
        return state;
    }
}
