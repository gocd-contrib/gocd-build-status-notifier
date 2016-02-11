package com.tw.go.plugin.util;

import org.junit.Test;

import static com.tw.go.plugin.util.StringUtils.getRepository;
import static com.tw.go.plugin.util.StringUtils.isEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(isEmpty(null));
        assertTrue(isEmpty(""));
        assertFalse(isEmpty("Hello"));
    }

    @Test
    public void shouldGetRepositoryFromURL() {
        assertThat(getRepository("http://github.com/srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("http://github.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("http://github.com/srinivasupadhya/sample-repo/"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("http://github.com/srinivasupadhya/sample-repo.git/"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("https://github.com/srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("https://github.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("git@code.corp.yourcompany.com:srinivasupadhya/sample-repo"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("git@code.corp.yourcompany.com:srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("git@github.com:srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
        assertThat(getRepository("ssh://git@gitlab.com/srinivasupadhya/sample-repo.git"), is("srinivasupadhya/sample-repo"));
    }

}