package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.provider.Provider;
import org.kohsuke.github.GHCommitState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GitHubProvider implements Provider {
    public static final String GITHUB_PR_PLUGIN_ID = "github.pr";
    public static final String PUBLIC_GITHUB_ENDPOINT = "https://api.github.com";

    @Override
    public String pollerPluginId() {
        return GITHUB_PR_PLUGIN_ID;
    }

    @Override
    public void updateStatus(String url, String username, String password, String prIdStr, String revision, String pipelineInstance, String result, String trackbackURL) {
        try {
            String repository = getRepository(url);
            GHCommitState state = getState(result);

            GitHub github = GitHub.connectUsingPassword(username, password);
            GHRepository ghRepository = github.getRepository(repository);
            ghRepository.createCommitStatus(revision, state, trackbackURL, "", pipelineInstance);
        } catch (Exception e) {
            // ignore
        }
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
}
