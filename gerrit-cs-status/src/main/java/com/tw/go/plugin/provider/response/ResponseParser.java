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

import com.google.gson.GsonBuilder;
import com.tw.go.plugin.provider.response.model.CommitDetails;

public class ResponseParser {
    public CommitDetails parseCommitDetails(String response) {
        CommitDetails[] commitDetails = new GsonBuilder().create().fromJson(response, CommitDetails[].class);
        return commitDetails == null || commitDetails.length == 0 ? null : commitDetails[0];
    }
}
