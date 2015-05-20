package com.tw.go.plugin.provider.stash;

import com.tw.go.plugin.PluginSettings;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StashProviderTest {
    @Test
    public void shouldGetStateFromResult() {
        StashProvider provider = new StashProvider();
        assertThat(provider.getState("Unknown"), is("INPROGRESS"));
        assertThat(provider.getState("Passed"), is("SUCCESSFUL"));
        assertThat(provider.getState("Failed"), is("FAILED"));
        assertThat(provider.getState("Cancelled"), is("FAILED"));
    }

    @Ignore("for local runs")
    @Test
    public void shouldUpdateStatusForPR() throws Exception {
        StashProvider provider = new StashProvider();
        provider.updateStatus("http://localhost:7990/scm/test", new PluginSettings(), "1", "a0029a9049f5ad3f8492b830bec8f9d35463a2b5", "pipeline-name/stage-name", "Passed", "http://localhost:8153/go/pipelines/pipeline/1/stage/1");
    }
}
