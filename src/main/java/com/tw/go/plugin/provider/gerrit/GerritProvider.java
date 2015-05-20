package com.tw.go.plugin.provider.gerrit;

import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.provider.Provider;

public class GerritProvider implements Provider {
    public static final String PLUGIN_ID = "gerrit.cs.status";
    public static final String GERRIT_CS_POLLER_PLUGIN_ID = "gerrit.cs";

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

    }
}
