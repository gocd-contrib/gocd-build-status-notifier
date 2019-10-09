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

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.plugin.setting.DefaultPluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;
import org.apache.commons.lang3.StringUtils;
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
        LOGGER.info("Updating status for '%s' to %s", url, result);

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
        LOGGER.info("Validating configuration");

        return new ArrayList<Map<String, Object>>();
    }

    void updateCommitStatus(String revision, String pipelineStage, String trackbackURL, String repository, GHCommitState state,
                            String usernameToUse, String passwordToUse, String oauthAccessTokenToUse, String endPointToUse) throws Exception {
        LOGGER.info("Updating commit status for '%s' on '%s'", revision, pipelineStage);

        GitHub github = createGitHubClient(usernameToUse, passwordToUse, oauthAccessTokenToUse, endPointToUse);
        GHRepository ghRepository = github.getRepository(repository);
        ghRepository.createCommitStatus(revision, state, trackbackURL, "", pipelineStage);
    }

    GitHub createGitHubClient(String usernameToUse, String passwordToUse, String oauthAccessTokenToUse, String endPointToUse) throws Exception {
        LOGGER.info("Creating GitHub client"); 

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

    public String getRepository(String url) {
        LOGGER.info("Getting repository '%s'", url);

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
