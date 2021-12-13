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

import com.tw.go.plugin.setting.PluginSettings;
import com.tw.go.plugin.util.AuthenticationType;
import com.tw.go.plugin.util.HTTPClient;
import com.tw.go.plugin.util.JSONUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_SERVER_BASE_URL;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_END_POINT;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_USERNAME;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_PASSWORD;
import static com.tw.go.plugin.util.ValidationUtils.getValidationError;

public class GiteaProvider extends DefaultProvider {
    public static final String PLUGIN_ID = "gitea.pr.status";
    public static final String GIT_FB_POLLER_PLUGIN_ID = "git.fb";
    public static final String STATUS_CONTEXT = "GoCD";

    private final HTTPClient httpClient;

    public GiteaProvider() {
        super(new GiteaConfigurationView());
        httpClient = new HTTPClient();
    }

    public GiteaProvider(HTTPClient httpClient) {
        super(new GiteaConfigurationView());
        this.httpClient = httpClient;
    }

    @Override
    public String pluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String pollerPluginId() {
        return GIT_FB_POLLER_PLUGIN_ID;
    }

    @Override
    public List<Map<String, Object>> validateConfig(Map<String, Object> fields) {
        List<Map<String, Object>> errors = new ArrayList<>();

        if (!validateUrl(getValueFromPluginSettings(fields.get(PLUGIN_SETTINGS_SERVER_BASE_URL)))) {
            errors.add(getValidationError(
                    PLUGIN_SETTINGS_SERVER_BASE_URL,
                    "GoCD base url not set correctly"
            ));
        }
        if (!validateUrl(getValueFromPluginSettings(fields.get(PLUGIN_SETTINGS_END_POINT)))) {
            errors.add(getValidationError(
                    PLUGIN_SETTINGS_END_POINT,
                    "Gitea base url not set correctly"
            ));
        }
        if (getValueFromPluginSettings(fields.get(PLUGIN_SETTINGS_USERNAME)).equals("")) {
            errors.add(getValidationError(
                    PLUGIN_SETTINGS_USERNAME,
                    "Gitea Username not set correctly"
            ));
        }
        if (getValueFromPluginSettings(fields.get(PLUGIN_SETTINGS_PASSWORD)).equals("")) {
            errors.add(getValidationError(
                    PLUGIN_SETTINGS_PASSWORD,
                    "Gitea Password not set correctly"
            ));
        }

        return errors;
    }

    @Override
    public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineStage,
                             String result, String trackbackURL) throws Exception {
        String giteaBaseURL = pluginSettings.getEndPoint();
        String username = pluginSettings.getUsername();
        String password = pluginSettings.getPassword();

        if (StringUtils.isEmpty(giteaBaseURL)) {
            giteaBaseURL = System.getProperty("go.plugin.build.status.gitea.endpoint");
        }
        if (StringUtils.isEmpty(username)) {
            username = System.getProperty("go.plugin.build.status.gitea.username");
        }
        if (StringUtils.isEmpty(password)) {
            password = System.getProperty("go.plugin.build.status.gitea.password");
        }

        String updateURL = formUpdateURL(giteaBaseURL, url, revision);
        Map<String, String> request = new HashMap<>();
        request.put("context", STATUS_CONTEXT);
        request.put("description", GiteaState.descriptionFor(result));
        request.put("state", GiteaState.stateFor(result));
        request.put("target_url", trackbackURL);
        String requestBody = JSONUtils.toJSON(request);

        httpClient.postRequest(updateURL, AuthenticationType.BASIC, username, password, requestBody);
    }

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

    public String formUpdateURL(String gitServerBaseURL, String url, String revision) {
        if (gitServerBaseURL.endsWith("/")) {
            gitServerBaseURL = gitServerBaseURL.substring(0, gitServerBaseURL.length() - 1);
        }
        return String.format("%s/api/v1/repos/%s/statuses/%s", gitServerBaseURL, getRepository(url), revision);
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

        if (!StringUtils.isEmpty(repoPath) && repoPath.endsWith(".git")) {
            repoPath = repoPath.substring(0, repoPath.length() - 4);
        }
        return repoPath;
    }
}
