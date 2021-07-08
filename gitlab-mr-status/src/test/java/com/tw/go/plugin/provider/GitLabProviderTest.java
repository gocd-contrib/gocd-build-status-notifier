package com.tw.go.plugin.provider;

import com.google.gson.internal.LinkedTreeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tw.go.plugin.setting.DefaultPluginConfigurationView.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class GitLabProviderTest {
    private GitLabProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new GitLabProvider();
    }

    @Test
    public void checkIdsMatch () throws Exception {
        assertEquals("gitlab.mr.status", provider.pluginId());
        assertThat(provider.pollerPluginIds(), hasItem("git.fb"));
        assertThat(provider.pollerPluginIds(), hasItem("gitlab.pr"));
    }

    @Test
    public void shouldGetRepositoryFromURL() {
        assertThat(provider.getRepository("http://github.com/group/sample-repo"), is("group/sample-repo"));
        assertThat(provider.getRepository("http://github.com/group/sample-repo.git"), is("group/sample-repo"));
        assertThat(provider.getRepository("http://github.com/group/sample-repo/"), is("group/sample-repo"));
        assertThat(provider.getRepository("http://github.com/group/sample-repo.git/"), is("group/sample-repo"));
        assertThat(provider.getRepository("https://github.com/group/sample-repo"), is("group/sample-repo"));
        assertThat(provider.getRepository("https://github.com/group/sample-repo.git"), is("group/sample-repo"));
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:group/sample-repo"), is("group/sample-repo"));
        assertThat(provider.getRepository("git@code.corp.yourcompany.com:group/sample-repo.git"), is("group/sample-repo"));
        assertThat(provider.getRepository("git@github.com:group/sample-repo.git"), is("group/sample-repo"));
        assertThat(provider.getRepository("http://github.com/group/sub-group/sample-repo.git/"), is("group/sub-group/sample-repo"));
        assertThat(provider.getRepository("https://github.com/group/sub-group/another-sub-group/sample-repo"), is("group/sub-group/another-sub-group/sample-repo"));
    }

    @Test
    public void checkValidationWithValidValues () throws Exception {
        Map<String, Object> config = new LinkedTreeMap<String, Object>();
        List<Map<String, Object>> errors = new ArrayList<Map<String, Object>>();

        Map<String, String> dummyUrl = new LinkedTreeMap<String, String>();
        dummyUrl.put("value", "http://localhost:8153");

        Map<String, String> dummyToken = new LinkedTreeMap<String, String>();
        dummyToken.put("value", "abcdef");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, dummyUrl);
        config.put(PLUGIN_SETTINGS_OAUTH_TOKEN, dummyToken);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(errors, returnedErrors);
    }

    @Test
    public void checkValidationWithInvalidValues () throws Exception {
        Map<String, Object> config = new LinkedTreeMap<String, Object>();

        Map<String, String> dummyUrl = new LinkedTreeMap<String, String>();
        dummyUrl.put("value", "localhost:8153");

        Map<String, String> dummyToken = new LinkedTreeMap<String, String>();
        dummyToken.put("value", "");

        config.put(PLUGIN_SETTINGS_SERVER_BASE_URL, dummyUrl);
        config.put(PLUGIN_SETTINGS_END_POINT, dummyUrl);
        config.put(PLUGIN_SETTINGS_OAUTH_TOKEN, dummyToken);

        List<Map<String, Object>> returnedErrors = provider.validateConfig(config);
        assertEquals(3, returnedErrors.size());
    }
}
