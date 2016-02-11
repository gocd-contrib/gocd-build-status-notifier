package com.tw.go.plugin.provider.gitlab;

import com.tw.go.plugin.PluginSettings;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class GitLabProviderTest {
    final PluginSettings pluginSettings = new PluginSettings();
    final GitLabProvider provider = new GitLabProvider();

    @Test
    @Ignore("local run only")
    public void testUpdateStatus() throws Exception {
        pluginSettings.setEndPoint("https://gitlab.com/");
        provider.updateStatus("ssh://git@gitlab.com/mfriedenhagen/gocd-notifier-test.git", pluginSettings, "master", "73a655c60fd56b761334fe77d1814aa3344b4621", "pipeline-name/stage-name", "Passed", "http://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1");
        provider.updateStatus("ssh://git@gitlab.com/mfriedenhagen/gocd-notifier-test.git", pluginSettings, "feature/1234", "f9c399ef9e7f0d242106db323a9afe00c0cb89a0", "pipeline-name/stage-name", "Passed", "http://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1");
    }
}