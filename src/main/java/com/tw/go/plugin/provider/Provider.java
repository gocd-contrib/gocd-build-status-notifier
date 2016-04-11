package com.tw.go.plugin.provider;

import com.tw.go.plugin.setting.Configuration;
import com.tw.go.plugin.setting.PluginSettings;

import java.util.List;
import java.util.Map;

public interface Provider {
    public String pluginId();

    public String pollerPluginId();

    public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineStage,
                             String result, String trackbackURL) throws Exception;

    public List<Map<String, Object>> validateConfig(Map<String, Object> fields);

    public Configuration configuration();

    public PluginSettings pluginSettings(Map<String, String> responseBodyMap);
}
