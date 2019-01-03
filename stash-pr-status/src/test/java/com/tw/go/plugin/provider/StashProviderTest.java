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

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StashProviderTest {
    DefaultPluginSettings pluginSettings;
    StashProvider provider;

    @Before
    public void setUp() throws Exception {
        pluginSettings = new DefaultPluginSettings();
        pluginSettings.setEndPoint("http://localhost:7990");
        pluginSettings.setUsername("test");
        pluginSettings.setPassword("Stash");

        provider = new StashProvider();
    }

    @Test
    public void shouldGetStateFromResult() {
        assertThat(provider.getState("Unknown"), is(StashProvider.IN_PROGRESS_STATE));
        assertThat(provider.getState("Passed"), is(StashProvider.SUCCESSFUL_STATE));
        assertThat(provider.getState("Failed"), is(StashProvider.FAILED_STATE));
        assertThat(provider.getState("Cancelled"), is(StashProvider.FAILED_STATE));
    }

    @Ignore("for local runs")
    @Test
    public void shouldUpdateStatusForPR() throws Exception {
        provider.updateStatus("http://localhost:7990/scm/test", pluginSettings, "1", "a0029a9049f5ad3f8492b830bec8f9d35463a2b5", "pipeline-name/stage-name", "Passed", "http://localhost:8153/go/pipelines/pipeline/1/stage/1");
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
