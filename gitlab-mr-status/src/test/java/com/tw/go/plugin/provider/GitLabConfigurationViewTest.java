package com.tw.go.plugin.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_END_POINT;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_OAUTH_TOKEN;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_SERVER_BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitLabConfigurationViewTest {

    private GitlLabConfigurationView view;

    @BeforeEach
    public void setUp() {
        view = new GitlLabConfigurationView();
    }

    @Test
    public void checkExpectedFields() throws Exception {
        Set<String> expected = new HashSet<String>();
        expected.add(PLUGIN_SETTINGS_END_POINT);
        expected.add(PLUGIN_SETTINGS_OAUTH_TOKEN);
        expected.add(PLUGIN_SETTINGS_SERVER_BASE_URL);
        assertEquals(expected, view.fields().keySet());
    }

    @Test
    public void checkTemplateName() throws Exception {
        assertEquals("plugin-settings-gitlab.template.html", view.templateName());
    }
}
