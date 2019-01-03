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

import com.tw.go.plugin.setting.PluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;

import java.util.List;
import java.util.Map;

public interface Provider {
    public String pluginId();

    public String pollerPluginId();

    public void updateStatus(String url, PluginSettings pluginSettings, String branch, String revision, String pipelineStage,
                             String result, String trackbackURL) throws Exception;

    public List<Map<String, Object>> validateConfig(Map<String, Object> fields);

    public PluginConfigurationView configurationView();

    public PluginSettings pluginSettings(Map<String, String> responseBodyMap);
}
