package com.tw.go.plugin.provider.stash;

import com.tw.go.plugin.provider.Provider;

public class StashProvider implements Provider {
    public static final String STASH_PR_PLUGIN_ID = "stash.pr";

    @Override
    public String pollerPluginId() {
        return STASH_PR_PLUGIN_ID;
    }

    @Override
    public void updateStatus(String url, String username, String password, String branch, String revision, String pipelineInstance, String result, String trackbackURL) {

    }
}
