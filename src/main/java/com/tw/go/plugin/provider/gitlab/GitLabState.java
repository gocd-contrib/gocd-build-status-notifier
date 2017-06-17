package com.tw.go.plugin.provider.gitlab;

public class GitLabState {
    private static final String PENDING  = "pending";
    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";
    private static final String CANCELED = "canceled";

    public GitLabState() {}

    static String stateFor(String result) {
        result = result == null ? "" : result;
        String state = PENDING;
        if (result.equalsIgnoreCase("Passed")) {
            state = SUCCESS;
        } else if (result.equalsIgnoreCase("Failed")) {
            state = FAILED;
        } else if (result.equalsIgnoreCase("Cancelled")) {
            state = CANCELED;
        }
        return state;
    }
}
