package com.tw.go.plugin.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_END_POINT;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_OAUTH_TOKEN;
import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.PLUGIN_SETTINGS_SERVER_BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitLabProviderTest {
    private GitLabProvider provider;

    @BeforeEach
    public void setUp() throws Exception {
        provider = new GitLabProvider();
    }

    @Test
    public void checkIdsMatch () throws Exception {
        assertEquals("gitlab.mr.status", provider.pluginId());
        assertThat(provider.pollerPluginIds()).containsExactly("git.fb", "gitlab.pr");
    }

    @Test
    public void shouldGetRepositoryFromURL() {
        assertThat(provider.getRepository("http://github.com/group/sample-repo")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("http://github.com/group/sample-repo.git")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("http://github.com/group/sample-repo/")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("http://github.com/group/sample-repo.git/")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("https://github.com/group/sample-repo")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("https://github.com/group/sample-repo.git")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:group/sample-repo")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:group/sample-repo.git")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("git@github.com:group/sample-repo.git")).isEqualTo("group/sample-repo");
        assertThat(provider.getRepository("http://github.com/group/sub-group/sample-repo.git/")).isEqualTo("group/sub-group/sample-repo");
        assertThat(provider.getRepository("https://github.com/group/sub-group/another-sub-group/sample-repo")).isEqualTo("group/sub-group/another-sub-group/sample-repo");
    }

    @Test
    public void checkValidationWithValidValues () throws Exception {
        Map<String, Object> config = new LinkedHashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        Map<String, String> dummyUrl = new LinkedHashMap<>();
        dummyUrl.put("value", "http://localhost:8153");

        Map<String, String> dummyToken = new LinkedHashMap<>();
        dummyToken.put("value", "abcdef");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, dummyUrl);
        config.put(PLUGIN_SETTINGS_OAUTH_TOKEN, dummyToken);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(errors, returnedErrors);
    }

    @Test
    public void checkValidationWithInvalidValues () throws Exception {
        Map<String, Object> config = new LinkedHashMap<>();

        Map<String, String> dummyUrl = new LinkedHashMap<>();
        dummyUrl.put("value", "localhost:8153");

        Map<String, String> dummyToken = new LinkedHashMap<>();
        dummyToken.put("value", "");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, dummyUrl);
        config.put(PLUGIN_SETTINGS_OAUTH_TOKEN, dummyToken);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(3, returnedErrors.size());
    }
}
