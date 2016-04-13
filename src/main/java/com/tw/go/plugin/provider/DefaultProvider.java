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
