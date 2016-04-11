package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.setting.DefaultPluginSettings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kohsuke.github.GHCommitState;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GitHubProviderTest {
    DefaultPluginSettings pluginSettings;
    GitHubProvider provider;

    @Before
    public void setUp() throws Exception {
        pluginSettings = new DefaultPluginSettings();
        provider = new GitHubProvider();
    }

    @Test
    public void shouldGetRepositoryFromURL() {
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo/"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo.git/"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("https://github.com/srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("https://github.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("git@github.com:srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
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
