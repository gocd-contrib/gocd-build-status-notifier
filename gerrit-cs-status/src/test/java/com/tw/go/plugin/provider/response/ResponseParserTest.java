/*
 * Copyright 2022 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tw.go.plugin.provider.response;

import com.tw.go.plugin.provider.response.model.CommitDetails;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(commitDetails.getId()).isEqualTo("test-gerrit~master~I028339c9d6aa81e9b0a876a6421b96d2d3fadabb");
        assertThat(commitDetails.getProject()).isEqualTo("test-gerrit");
        assertThat(commitDetails.getBranch()).isEqualTo("master");
        assertThat(commitDetails.getChangeId()).isEqualTo("I028339c9d6aa81e9b0a876a6421b96d2d3fadabb");
    }
}
