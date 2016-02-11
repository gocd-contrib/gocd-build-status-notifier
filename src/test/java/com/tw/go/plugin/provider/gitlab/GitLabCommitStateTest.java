package com.tw.go.plugin.provider.gitlab;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class GitLabCommitStateTest {

    @Test
    public void shouldGetStateFromResult() {
        assertThat(GitLabCommitState.getState("Passed"), is(GitLabCommitState.success));
        assertThat(GitLabCommitState.getState("fAiled"), is(GitLabCommitState.failed));
        assertThat(GitLabCommitState.getState("Cancelled"), is(GitLabCommitState.canceled));
        assertThat(GitLabCommitState.getState(""), is(GitLabCommitState.pending));
        assertThat(GitLabCommitState.getState(null), is(GitLabCommitState.pending));
    }

    @Test
    public void shouldLowerCaseState() {
        assertThat(String.valueOf(GitLabCommitState.getState(null)), is("pending"));
    }
}