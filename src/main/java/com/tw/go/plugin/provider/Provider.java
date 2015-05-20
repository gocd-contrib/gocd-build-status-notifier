package com.tw.go.plugin.provider;

import com.tw.go.plugin.PluginSettings;

public interface Provider {
    public String pluginId();

    public String pollerPluginId();

    public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineStage,
                             String result, String trackbackURL) throws Exception;
}
