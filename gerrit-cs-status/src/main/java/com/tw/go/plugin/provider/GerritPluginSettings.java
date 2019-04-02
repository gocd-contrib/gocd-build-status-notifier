/*
 * Copyright 2019 ThoughtWorks, Inc.
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

package com.tw.go.plugin.provider;

import com.tw.go.plugin.setting.DefaultPluginSettings;

public class GerritPluginSettings extends DefaultPluginSettings {

    private String reviewLabel;

    public GerritPluginSettings() {
    }

    public GerritPluginSettings(String serverBaseURL, String endPoint, String username, String password, String oauthToken, String reviewLabel) {
        super(serverBaseURL, endPoint, username, password, oauthToken);
        this.reviewLabel = reviewLabel;
    }

    public String getReviewLabel() {
        return reviewLabel;
    }

    public void setReviewLabel(String reviewLabel) {
        this.reviewLabel = reviewLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GerritPluginSettings that = (GerritPluginSettings) o;

        return reviewLabel != null ? reviewLabel.equals(that.reviewLabel) : that.reviewLabel == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (reviewLabel != null ? reviewLabel.hashCode() : 0);
        return result;
    }
}
