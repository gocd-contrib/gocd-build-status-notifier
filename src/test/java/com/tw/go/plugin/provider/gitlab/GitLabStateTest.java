package com.tw.go.plugin.provider.gitlab;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GitLabStateTest {
    @Test
    public void checkStates() throws Exception {
        assertEquals("pending", GitLabState.stateFor("unknown"));
        assertEquals("pending", GitLabState.stateFor(null));
        assertEquals("success", GitLabState.stateFor("Passed"));
        assertEquals("failed", GitLabState.stateFor("Failed"));
        assertEquals("canceled", GitLabState.stateFor("Cancelled"));
    }
}
