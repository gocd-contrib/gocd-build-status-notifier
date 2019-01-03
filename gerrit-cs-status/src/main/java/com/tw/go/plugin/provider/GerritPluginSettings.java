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
