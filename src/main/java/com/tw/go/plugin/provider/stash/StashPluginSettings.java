package com.tw.go.plugin.provider.stash;

import com.tw.go.plugin.setting.DefaultPluginSettings;

public class StashPluginSettings extends DefaultPluginSettings {
    
    private String allowBuiltinGit;

    public StashPluginSettings() {
    }

    public StashPluginSettings(String serverBaseURL, String endPoint, String username, String password,
            String oauthToken, String allowBuiltinGit) {
        super(serverBaseURL, endPoint, username, password, oauthToken);
        this.allowBuiltinGit = allowBuiltinGit;
    }

    public String getAllowBuiltinGit() {
        return allowBuiltinGit;
    }

    public void setAllowBuiltinGit(String allowBuiltinGit) {
        this.allowBuiltinGit = allowBuiltinGit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StashPluginSettings that = (StashPluginSettings) o;
        
        return allowBuiltinGit != null ? allowBuiltinGit.equals(that.allowBuiltinGit) : that.allowBuiltinGit == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (allowBuiltinGit != null ? allowBuiltinGit.hashCode() : 0);
        return result;
    }
}
