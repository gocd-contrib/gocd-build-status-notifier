package com.tw.go.plugin.provider.github;

import com.thoughtworks.go.plugin.api.logging.Logger;

import com.tw.go.plugin.provider.DefaultProvider;
import com.tw.go.plugin.setting.DefaultPluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.StringUtils;
import org.kohsuke.github.GHCommitState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GitHubProvider extends DefaultProvider {
    private static Logger LOGGER = Logger.getLoggerFor(GitHubProvider.class);

    public static final String PLUGIN_ID = "github.pr.status";
    public static final String GITHUB_PR_POLLER_PLUGIN_ID = "github.pr";

    public GitHubProvider() {
        super(new DefaultPluginConfigurationView());
    }

    @Override
    public String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String pollerPluginId() {
        return GITHUB_PR_POLLER_PLUGIN_ID;
    }

    @Override
    public void updateStatus(String url, PluginSettings pluginSettings, String prIdStr, String revision, String pipelineStage,
                             String result, String trackbackURL) throws Exception {
        LOGGER.info(String.format("GitHubProvider.updateStatus(): '%s' -> %s", result, url));

        String repository = getRepository(url);
        GHCommitState state = getState(result);

        String endPointToUse = pluginSettings.getEndPoint();
        String usernameToUse = pluginSettings.getUsername();
        String passwordToUse = pluginSettings.getPassword();
        String oauthAccessTokenToUse = pluginSettings.getOauthToken();

        if (StringUtils.isEmpty(endPointToUse)) {
            endPointToUse = System.getProperty("go.plugin.build.status.github.endpoint");
        }
        if (StringUtils.isEmpty(usernameToUse)) {
            usernameToUse = System.getProperty("go.plugin.build.status.github.username");
        }
        if (StringUtils.isEmpty(passwordToUse)) {
            passwordToUse = System.getProperty("go.plugin.build.status.github.password");
        }
        if (StringUtils.isEmpty(oauthAccessTokenToUse)) {
            oauthAccessTokenToUse = System.getProperty("go.plugin.build.status.github.oauth");
        }

        updateCommitStatus(revision, pipelineStage, trackbackURL, repository, state, usernameToUse, passwordToUse, oauthAccessTokenToUse, endPointToUse);
    }

    @Override
    public List<Map<String, Object>> validateConfig(Map<String, Object> fields) {
        LOGGER.info("GitHubProvider.validateConfig()");
        return new ArrayList<Map<String, Object>>();
    }

    void updateCommitStatus(String revision, String pipelineStage, String trackbackURL, String repository, GHCommitState state,
                            String usernameToUse, String passwordToUse, String oauthAccessTokenToUse, String endPointToUse) throws Exception {
        LOGGER.info(String.format("GitHubProvider.updateCommitStatus(): '%s' on %s", revision, pipelineStage));

        GitHub github = createGitHubClient(usernameToUse, passwordToUse, oauthAccessTokenToUse, endPointToUse);
        GHRepository ghRepository = github.getRepository(repository);
        ghRepository.createCommitStatus(revision, state, trackbackURL, "", pipelineStage);
    }

    GitHub createGitHubClient(String usernameToUse, String passwordToUse, String oauthAccessTokenToUse, String endPointToUse) throws Exception {
        LOGGER.info("GitHubProvider.createGitHubClient()");

        GitHub github = null;
        if (usernameAndPasswordIsAvailable(usernameToUse, passwordToUse)) {
            if (endPointIsAvailable(endPointToUse)) {
                github = GitHub.connectToEnterprise(endPointToUse, usernameToUse, passwordToUse);
            } else {
                github = GitHub.connectUsingPassword(usernameToUse, passwordToUse);
            }
        }
        if (oAuthTokenIsAvailable(oauthAccessTokenToUse)) {
            if (endPointIsAvailable(endPointToUse)) {
                github = GitHub.connectUsingOAuth(endPointToUse, oauthAccessTokenToUse);
            } else {
                github = GitHub.connectUsingOAuth(oauthAccessTokenToUse);
            }
        }
        if (github == null) {
            github = GitHub.connect();
        }
        return github;
    }

    String getRepository(String url) {
        LOGGER.info(String.format("GitHubProvider.getRepository(): on %s", url));

        String[] urlParts = url.split("/");
        String repo = urlParts[urlParts.length - 1];
        String usernameWithSSHPrefix = urlParts[urlParts.length - 2];
        int positionOfColon = usernameWithSSHPrefix.lastIndexOf(":");
        if (positionOfColon > 0) {
            usernameWithSSHPrefix = usernameWithSSHPrefix.substring(positionOfColon + 1);
        }

        String urlWithoutPrefix = String.format("%s/%s", usernameWithSSHPrefix, repo);
        if (urlWithoutPrefix.endsWith(".git")) return urlWithoutPrefix.substring(0, urlWithoutPrefix.length() - 4);
        else return urlWithoutPrefix;
    }

    GHCommitState getState(String result) {
        LOGGER.info(String.format("GitHubProvider.getState(): on %s", result));

        result = result == null ? "" : result;
        GHCommitState state = GHCommitState.PENDING;
        if (result.equalsIgnoreCase("Passed")) {
            state = GHCommitState.SUCCESS;
        } else if (result.equalsIgnoreCase("Failed")) {
            state = GHCommitState.FAILURE;
        } else if (result.equalsIgnoreCase("Cancelled")) {
            state = GHCommitState.ERROR;
        }
        return state;
    }

    private boolean usernameAndPasswordIsAvailable(String usernameToUse, String passwordToUse) {
        return !isEmpty(usernameToUse) && !isEmpty(passwordToUse);
    }

    private boolean oAuthTokenIsAvailable(String oauthAccessTokenToUse) {
        return !isEmpty(oauthAccessTokenToUse);
    }

    private boolean endPointIsAvailable(String endPointToUse) {
        return !isEmpty(endPointToUse);
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
