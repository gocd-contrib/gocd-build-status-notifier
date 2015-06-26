package com.tw.go.plugin.provider.gerrit;

import com.tw.go.plugin.PluginSettings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GerritProviderTest {
    PluginSettings pluginSettings;
    GerritProvider provider;

    @Before
    public void setUp() throws Exception {
        pluginSettings = new PluginSettings();
        pluginSettings.setEndPoint("http://localhost:8080");
        pluginSettings.setUsername("srinivas");
        pluginSettings.setPassword("VMDiHOBuXPlBU9jQQmy+2/HMZGnW5ey3JY3cthAGXw");

        provider = new GerritProvider();
    }

    @Test
    public void shouldGetStateFromResult() {
        assertThat(provider.getCodeReviewValue("Unknown"), is(GerritProvider.IN_PROGRESS_VALUE));
        assertThat(provider.getCodeReviewValue("Passed"), is(GerritProvider.SUCCESS_VALUE));
        assertThat(provider.getCodeReviewValue("Failed"), is(GerritProvider.FAILURE_VALUE));
        assertThat(provider.getCodeReviewValue("Cancelled"), is(GerritProvider.FAILURE_VALUE));
    }

    @Ignore("for local runs")
    @Test
    public void shouldUpdateStatus() throws Exception {
        provider.updateStatus(null, pluginSettings, null, "64bdf589adda32a0652f8b3335b15bb8f53fe2cf", "pipeline-name/stage-name", "Passed", "https://localhost:8153/go/pipelines/pipeline-name/1/stage-name/1");
    }
}
