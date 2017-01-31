package com.tw.go.plugin.provider.gitlab;

public class GitLabState {
    public static final String PENDING  = "pending";
    public static final String RUNNING = "running";
    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";
    public static final String CANCELED = "canceled";

    public GitLabState() {}

    public static String stateFor(String result) {
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
