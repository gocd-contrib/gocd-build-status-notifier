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

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_SERVER_BASE_URL;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_END_POINT;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_USERNAME;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_PASSWORD;


public class GiteaConfigurationViewTest {

    private GiteaConfigurationView view;

    @Before
    public void setUp() {
        view = new GiteaConfigurationView();
    }

    @Test
    public void checkExpectedFields() throws Exception {
        Set<String> expected = new HashSet<String>();
        expected.add(PLUGIN_SETTINGS_SERVER_BASE_URL);
        expected.add(PLUGIN_SETTINGS_END_POINT);
        expected.add(PLUGIN_SETTINGS_USERNAME);
        expected.add(PLUGIN_SETTINGS_PASSWORD);
        assertEquals(expected, view.fields().keySet());
    }

    @Test
    public void checkTemplateName() throws Exception {
        assertEquals("plugin-settings-gitea.template.html", view.templateName());
    }
}
