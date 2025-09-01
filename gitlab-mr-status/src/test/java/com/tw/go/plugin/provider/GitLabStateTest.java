package com.tw.go.plugin.provider;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
