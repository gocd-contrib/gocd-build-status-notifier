package com.tw.go.plugin.setting;

import java.util.Map;

public interface PluginConfigurationView {

    public String templateName();

    public Map<String, Object> fields();

}
