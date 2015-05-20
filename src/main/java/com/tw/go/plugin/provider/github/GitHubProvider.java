package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.StringUtils;
import org.kohsuke.github.GHCommitState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GitHubProvider implements Provider {
    public static final String PLUGIN_ID = "github.pr.status";
    public static final String GITHUB_PR_POLLER_PLUGIN_ID = "github.pr";

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

    void updateCommitStatus(String revision, String pipelineStage, String trackbackURL, String repository, GHCommitState state,
                            String usernameToUse, String passwordToUse, String oauthAccessTokenToUse, String endPointToUse) throws Exception {
        GitHub github = createGitHubClient(usernameToUse, passwordToUse, oauthAccessTokenToUse, endPointToUse);
        GHRepository ghRepository = github.getRepository(repository);
        ghRepository.createCommitStatus(revision, state, trackbackURL, "", pipelineStage);
    }

    GitHub createGitHubClient(String usernameToUse, String passwordToUse, String oauthAccessTokenToUse, String endPointToUse) throws Exception {
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
        url = url.trim();
        String[] parts = url.split("://");
        parts = parts[1].split("/");
        String user = parts[1];
        String repository = parts[2];
        repository = repository.contains(".") ? repository.split("\\.")[0] : repository;
        return user + "/" + repository;
    }

    GHCommitState getState(String result) {
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
