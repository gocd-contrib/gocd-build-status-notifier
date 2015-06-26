package com.tw.go.plugin.provider.gerrit;

import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.provider.gerrit.response.ResponseParser;
import com.tw.go.plugin.provider.gerrit.response.model.CommitDetails;
import com.tw.go.plugin.util.AuthenticationType;
import com.tw.go.plugin.util.HTTPUtils;
import com.tw.go.plugin.util.JSONUtils;

import java.util.HashMap;
import java.util.Map;

public class GerritProvider implements Provider {
    public static final String PLUGIN_ID = "gerrit.cs.status";
    public static final String GERRIT_CS_POLLER_PLUGIN_ID = "gerrit.cs";

    public static final int IN_PROGRESS_VALUE = 0;
    public static final int SUCCESS_VALUE = 1;
    public static final int FAILURE_VALUE = -1;

    @Override
    public String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String pollerPluginId() {
        return GERRIT_CS_POLLER_PLUGIN_ID;
    }

    @Override
    public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineInstance,
                             String result, String trackbackURL) throws Exception {
        String commitDetailsURL = String.format("%s/a/changes/?q=commit:%s", pluginSettings.getEndPoint(), revision);
        String commitDetailsResponse = HTTPUtils.getRequest(commitDetailsURL, AuthenticationType.DIGEST, pluginSettings.getUsername(), pluginSettings.getPassword());
        CommitDetails commitDetails = new ResponseParser().parseCommitDetails(commitDetailsResponse);

        Map<String, Object> request = new HashMap<String, Object>();
        request.put("message", String.format("%s: %s", pipelineInstance, trackbackURL));
        Map<String, Object> labels = new HashMap<String, Object>();
        request.put("labels", labels);
        labels.put("Code-Review", getCodeReviewValue(result));
        String updateStatusURL = String.format("%s/a/changes/%s/revisions/%s/review", pluginSettings.getEndPoint(), commitDetails.getId(), revision);
        HTTPUtils.postRequest(updateStatusURL, AuthenticationType.DIGEST, pluginSettings.getUsername(), pluginSettings.getPassword(), JSONUtils.toJSON(request));
    }

    int getCodeReviewValue(String result) {
        result = result == null ? "" : result;
        int value = IN_PROGRESS_VALUE;
        if (result.equalsIgnoreCase("Passed")) {
            value = SUCCESS_VALUE;
        } else if (result.equalsIgnoreCase("Failed")) {
            value = FAILURE_VALUE;
        } else if (result.equalsIgnoreCase("Cancelled")) {
            value = FAILURE_VALUE;
        }
        return value;
    }
}
