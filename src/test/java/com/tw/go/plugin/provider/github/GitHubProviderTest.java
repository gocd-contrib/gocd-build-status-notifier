package com.tw.go.plugin.provider.github;

import org.junit.Test;
import org.kohsuke.github.GHCommitState;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GitHubProviderTest {
    @Test
    public void shouldGetRepositoryFromURL() {
        GitHubProvider provider = new GitHubProvider();
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo/"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo.git/"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("https://github.com/srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("https://github.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
    }

    @Test
    public void shouldGetStateFromResult() {
        GitHubProvider provider = new GitHubProvider();
        assertThat(provider.getState("Unknown"), is(GHCommitState.PENDING));
        assertThat(provider.getState("Passed"), is(GHCommitState.SUCCESS));
        assertThat(provider.getState("Failed"), is(GHCommitState.FAILURE));
        assertThat(provider.getState("Cancelled"), is(GHCommitState.ERROR));
    }

    @Test
    public void shouldUpdateStatusForPR() {
        GitHubProvider provider = new GitHubProvider();
        provider.updateStatus("https://github.com/srinivasupadhya/sample-repo", "", "", "1", "6d4627a71fa6dc1610a321feee8e76d3e5fe997c", "pipeline/1/stage/1", "Passed", "http://localhost:8153");
    }
}
