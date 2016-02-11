package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.util.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kohsuke.github.GHCommitState;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GitHubProviderTest {
    PluginSettings pluginSettings;
    GitHubProvider provider;

    @Before
    public void setUp() throws Exception {
        pluginSettings = new PluginSettings();
        provider = new GitHubProvider();
    }

    @Test
    public void shouldGetStateFromResult() {
        assertThat(provider.getState("Unknown"), is(GHCommitState.PENDING));
        assertThat(provider.getState("Passed"), is(GHCommitState.SUCCESS));
        assertThat(provider.getState("Failed"), is(GHCommitState.FAILURE));
        assertThat(provider.getState("Cancelled"), is(GHCommitState.ERROR));
    }

    @Ignore("for local runs")
    @Test
    public void shouldUpdateStatusForPR() throws Exception {
        provider.updateStatus("https://github.com/srinivasupadhya/sample-repo", pluginSettings, "1", "6d4627a71fa6dc1610a321feee8e76d3e5fe997c", "pipeline-name/stage-name", "Passed", "http://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1");
    }
}
