package com.tw.go.plugin.provider;

public interface Provider {
    public String pollerPluginId();

    public void updateStatus(String url, String username, String branch, String revision, String pipelineInstance,
                             String result, String trackbackURL) throws Exception;
}
