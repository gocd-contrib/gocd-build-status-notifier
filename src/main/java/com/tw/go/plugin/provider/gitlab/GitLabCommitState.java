package com.tw.go.plugin.provider.gitlab;

import java.util.Locale;

enum GitLabCommitState {
    pending,
    running,
    success,
    failed,
    canceled;

    static GitLabCommitState getState(final String result) {
        final String lcResult = result == null ? "" : result.toLowerCase(Locale.ENGLISH);
        switch (lcResult) {
            case "passed":
                return success;
            case "failed":
                return failed;
            case "cancelled":
                return canceled;
            default:
                return pending;
        }
    }
}
