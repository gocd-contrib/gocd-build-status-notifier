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

import com.tw.go.plugin.setting.DefaultPluginSettings;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kohsuke.github.GHCommitState;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GitHubProviderTest {
    DefaultPluginSettings pluginSettings;
    GitHubProvider provider;

    @Before
    public void setUp() throws Exception {
        pluginSettings = new DefaultPluginSettings();
        provider = new GitHubProvider();
    }

    @Test
    public void shouldGetRepositoryFromURL() {
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo/"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("http://github.com/srinivasupadhya/sample-repo.git/"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("https://github.com/srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("https://github.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(provider.getRepository("git@github.com:srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
    }

    @Test
    public void shouldGetStateFromResult() {
        assertThat(provider.getState("Unknown"), is(GHCommitState.PENDING));
        assertThat(provider.getState("Passed"), is(GHCommitState.SUCCESS));
        assertThat(provider.getState("Failed"), is(GHCommitState.FAILURE));
        assertThat(provider.getState("Cancelled"), is(GHCommitState.ERROR));
    }

    @Ignore("for local runs")
    @Test
    public void shouldUpdateStatusForPR() throws Exception {
        provider.updateStatus("https://github.com/srinivasupadhya/sample-repo", pluginSettings, "1", "6d4627a71fa6dc1610a321feee8e76d3e5fe997c", "pipeline-name/stage-name", "Passed", "http://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1");
    }

    @Test
    public void shouldReturnCorrectTemplate() {
        assertThat(provider.configurationView().templateName(), is("plugin-settings.template.html"));
    }

    @Test
    public void shouldReturnCorrectConfigFields() throws Exception {
        Map<String, Object> configuration = provider.configurationView().fields();

        assertThat(configuration.containsKey("server_base_url"), Is.is(true));
        assertThat(configuration.containsKey("end_point"), Is.is(true));
        assertThat(configuration.containsKey("username"), Is.is(true));
        assertThat(configuration.containsKey("password"), Is.is(true));
        assertThat(configuration.containsKey("oauth_token"), Is.is(true));
    }
}
