package com.tw.go.plugin.provider.gerrit.response;

import com.google.gson.GsonBuilder;
import com.tw.go.plugin.provider.gerrit.response.model.CommitDetails;

public class ResponseParser {
    public CommitDetails parseCommitDetails(String response) {
        CommitDetails[] commitDetails = new GsonBuilder().create().fromJson(response, CommitDetails[].class);
        return commitDetails == null || commitDetails.length == 0 ? null : commitDetails[0];
    }
}
