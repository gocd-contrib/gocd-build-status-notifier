package com.tw.go.plugin.provider.gitlab;


import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.StringUtils;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabCommitStatus;
import org.gitlab.api.models.GitlabProject;

public class GitLabProvider implements Provider {
    public static final String PLUGIN_ID = "gitlab.mr.status";
    public static final String GITLAB_MR_POLLER_PLUGIN_ID = "gitlab.mr";

    @Override
    public String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String pollerPluginId() {
        return GITLAB_MR_POLLER_PLUGIN_ID;
    }

    @Override
    public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineStage, String result, String trackbackURL) throws Exception {
        final String groupAndProject = StringUtils.getRepository(url);
        String endPointToUse = pluginSettings.getEndPoint();
        String oauthAccessTokenToUse = pluginSettings.getOauthToken();

        if (StringUtils.isEmpty(endPointToUse)) {
            endPointToUse = System.getProperty("go.plugin.build.status.gitlab.endpoint");
        }
        if (StringUtils.isEmpty(oauthAccessTokenToUse)) {
            oauthAccessTokenToUse = System.getProperty("go.plugin.build.status.gitlab.oauth");
        }
        final GitlabAPI api = GitlabAPI.connect(endPointToUse, oauthAccessTokenToUse);
        final GitlabProject project = api.getProject(groupAndProject);
        final String state = String.valueOf(GitLabCommitState.getState(result));
        final GitlabCommitStatus commitStatus = api.createCommitStatus(project, revision, state, branch, pipelineStage, trackbackURL, "");
    }
}
