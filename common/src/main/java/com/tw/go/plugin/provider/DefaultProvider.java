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
import com.tw.go.plugin.setting.PluginConfigurationView;
import com.tw.go.plugin.setting.PluginSettings;

import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;

public abstract class DefaultProvider implements Provider {

    private PluginConfigurationView pluginConfigurationView;

    public DefaultProvider(PluginConfigurationView pluginConfigurationView) {
        this.pluginConfigurationView = pluginConfigurationView;
    }

    @Override
    public PluginConfigurationView configurationView() {
        return pluginConfigurationView;
    }

    @Override
    public PluginSettings pluginSettings(Map<String, String> responseBodyMap) {
        return new DefaultPluginSettings(
                responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL),
                responseBodyMap.get(PLUGIN_SETTINGS_END_POINT),
                responseBodyMap.get(PLUGIN_SETTINGS_USERNAME),
                responseBodyMap.get(PLUGIN_SETTINGS_PASSWORD),
                responseBodyMap.get(PLUGIN_SETTINGS_OAUTH_TOKEN)
        );
    }
}
