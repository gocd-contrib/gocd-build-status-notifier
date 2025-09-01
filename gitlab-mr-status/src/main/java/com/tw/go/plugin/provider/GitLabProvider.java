package com.tw.go.plugin.provider;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.ValidationUtils;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static com.tw.go.plugin.util.ValidationUtils.getValidationError;
import static java.util.Collections.unmodifiableList;

public class GitLabProvider extends DefaultProvider {
    private static Logger LOGGER = Logger.getLoggerFor(GitLabProvider.class);

    private static final String PLUGIN_ID = "gitlab.mr.status";
    private static final String GITLAB_FB_POLLER_PLUGIN_ID = "git.fb";
    private static final String GITLAB_POLLER_PLUGIN_ID = "gitlab.pr";
    private static final List<String> POLLER_PLUGINS = unmodifiableList(new ArrayList<String>() {{
        add(GITLAB_FB_POLLER_PLUGIN_ID);
        add(GITLAB_POLLER_PLUGIN_ID);
    }});

    public GitLabProvider() {
        super(new GitlLabConfigurationView());
    }

    @Override
    public String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    public List<String> pollerPluginIds() {
        return POLLER_PLUGINS;
    }

    @Override
    public List<Map<String, Object>> validateConfig(Map<String, Object> fields) {
        List<Map<String, Object>> errors = new ArrayList<>();

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
        if (getValueFromPluginSettings(fields.get(PLUGIN_SETTINGS_OAUTH_TOKEN)).isEmpty()) {
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
        LOGGER.info(String.format("Updating commit status for %s on %s", revision, pipelineStage));

        String endPointToUse = pluginSettings.getEndPoint();
        String oauthAccessTokenToUse = pluginSettings.getOauthToken();

        if (ValidationUtils.isEmpty(endPointToUse)) {
            endPointToUse = System.getProperty("go.plugin.build.status.gitlab.endpoint");
        }
        if (ValidationUtils.isEmpty(oauthAccessTokenToUse)) {
            oauthAccessTokenToUse = System.getProperty("go.plugin.build.status.gitlab.oauth");
        }

        GitlabAPI api = GitlabAPI.connect(endPointToUse, oauthAccessTokenToUse);
        GitlabProject project = api.getProject(getRepository(url));
        String state = GitLabState.stateFor(result);
        api.createCommitStatus(project, revision, state, prIdStr, "GoCD", trackbackURL, "");
    }

    @SuppressWarnings("unchecked")
    private String getValueFromPluginSettings(Object values) {
        if (values == null) {
            return "";
        }
        Map<String, String> vals = (Map<String, String>) values;
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

    public String getRepository(String url) {
        String repoPath = null;

        String sshProtocolString = "(.*)@(.*):(.*?)(/*)$";
        Pattern sshPattern = Pattern.compile(sshProtocolString);
        Matcher sshMatcher = sshPattern.matcher(url);
        if (sshMatcher.find()) {
            repoPath = sshMatcher.group(3);
        }

        String httpProtocolString = "http(.?)://(.*?)/(.*?)(/*)$";
        Pattern httpPattern = Pattern.compile(httpProtocolString);
        Matcher httpMatcher = httpPattern.matcher(url);
        if (httpMatcher.find()) {
            repoPath = httpMatcher.group(3);
        }

        if (!ValidationUtils.isEmpty(repoPath) && repoPath.endsWith(".git")) {
            repoPath = repoPath.substring(0, repoPath.length() - 4);
        }
        return repoPath;
    }
}
