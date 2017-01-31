package com.tw.go.plugin.provider.gitlab;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_END_POINT;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_OAUTH_TOKEN;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_SERVER_BASE_URL;

public class GitLabConfigurationViewTest {

    private GitlLabConfigurationView view;

    @Before
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
