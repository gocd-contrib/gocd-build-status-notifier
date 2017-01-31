package com.tw.go.plugin.provider.gitlab;

import com.google.gson.internal.LinkedHashTreeMap;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.plugin.provider.DefaultProvider;
import com.tw.go.plugin.setting.PluginSettings;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_END_POINT;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_OAUTH_TOKEN;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_SERVER_BASE_URL;
import static com.tw.go.plugin.util.ValidationUtils.getValidationError;

public class GitLabProvider extends DefaultProvider {

    private static Logger LOG = Logger.getLoggerFor(GitLabProvider.class);
    private static final String PLUGIN_ID = "gitlab.fb.status";
    private static final String GITLAB_FB_POLLER_PLUGIN_ID = "git.fb";

    public GitLabProvider() {
        super(new GitlLabConfigurationView());
    }

    @Override
    public String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String pollerPluginId() {
        return GITLAB_FB_POLLER_PLUGIN_ID;
    }

    @Override
    public List<Map<String, Object>> validateConfig(Map<String, Object> fields) {
        List<Map<String, Object>> errors = new ArrayList<Map<String, Object>>();

        if (!validateUrl(getValueFromPluginSettings(fields.get(PLUGIN_SETTINGS_END_POINT)))) {
            errors.add(getValidationError(
                    PLUGIN_SETTINGS_END_POINT,
                    "GitLab Endpoint not set correctly"
            ));
        }
        if (!validateUrl(getValueFromPluginSettings(fields.get(PLUGIN_SETTINGS_SERVER_BASE_URL)))) {
            errors.add(getValidationError(
                    PLUGIN_SETTINGS_SERVER_BASE_URL,
                    "GoCD Server URL not set correctly"
            ));
        }
        if (getValueFromPluginSettings(fields.get(PLUGIN_SETTINGS_OAUTH_TOKEN)).equals("")) {
            errors.add(getValidationError(
                    PLUGIN_SETTINGS_OAUTH_TOKEN,
                    "GitLab OAuth Token not set"
            ));
        }

        return errors;
    }

    @Override
    public void updateStatus(String url, PluginSettings pluginSettings, String prIdStr, String revision, String pipelineStage,
                             String result, String trackbackURL) throws Exception {

        GitlabAPI api = GitlabAPI.connect(pluginSettings.getEndPoint(), pluginSettings.getOauthToken());
        GitlabProject project = api.getProject(getRepository(url));
        api.createCommitStatus(project, revision, GitLabState.stateFor(result), prIdStr,"GoCD",trackbackURL, "");
    }

    private String getValueFromPluginSettings(Object values) {
        if (values == null) {
            return "";
        }
        Map<String, String> vals = (LinkedHashTreeMap<String, String>)values;
        String ret = vals.get("value");
        if (ret == null) {
            return "";
        }
        return ret;
    }

    private boolean validateUrl(String uri) {
        try {
            new URL(uri);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    private String getRepository(String url) {
        String[] urlParts = url.split("/");
        String repo = urlParts[urlParts.length - 1];
        if (repo.endsWith(".git")) {
            repo = repo.substring(0, repo.length() - 4);
        }
        String usernameWithSSHPrefix = urlParts[urlParts.length - 2];

        int positionOfColon = usernameWithSSHPrefix.lastIndexOf(":");
        if (positionOfColon > 0) {
            usernameWithSSHPrefix = usernameWithSSHPrefix.substring(positionOfColon + 1);
        }
        return String.format("%s/%s", usernameWithSSHPrefix, repo);
    }
}
