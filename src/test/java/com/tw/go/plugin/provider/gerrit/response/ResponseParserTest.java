package com.tw.go.plugin.provider.gerrit.response;

import com.tw.go.plugin.provider.gerrit.response.model.CommitDetails;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ResponseParserTest {
    @Test
    public void shouldParseCommitDetailsResponse() {
        String response = ")]}'\n" +
                "[\n" +
                "  {\n" +
                "    \"id\": \"test-gerrit~master~I028339c9d6aa81e9b0a876a6421b96d2d3fadabb\",\n" +
                "    \"project\": \"test-gerrit\",\n" +
                "    \"branch\": \"master\",\n" +
                "    \"change_id\": \"I028339c9d6aa81e9b0a876a6421b96d2d3fadabb\",\n" +
                "    \"subject\": \"5\",\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"created\": \"2015-03-16 16:45:49.728000000\",\n" +
                "    \"updated\": \"2015-03-16 16:45:49.728000000\",\n" +
                "    \"mergeable\": true,\n" +
                "    \"insertions\": 1,\n" +
                "    \"deletions\": 0,\n" +
                "    \"_sortkey\": \"0033ce0d00000004\",\n" +
                "    \"_number\": 4,\n" +
                "    \"owner\": {\n" +
                "      \"name\": \"srinivas\"\n" +
                "    }\n" +
                "  }\n" +
                "]\n";

        CommitDetails commitDetails = new ResponseParser().parseCommitDetails(response);

        assertThat(commitDetails.getId(), is("test-gerrit~master~I028339c9d6aa81e9b0a876a6421b96d2d3fadabb"));
        assertThat(commitDetails.getProject(), is("test-gerrit"));
        assertThat(commitDetails.getBranch(), is("master"));
        assertThat(commitDetails.getChangeId(), is("I028339c9d6aa81e9b0a876a6421b96d2d3fadabb"));
    }
}
