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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GiteaStateTest {
    @Test
    public void checkStates() throws Exception {
        assertEquals("pending", GiteaState.stateFor("unknown"));
        assertEquals("pending", GiteaState.stateFor(null));
        assertEquals("success", GiteaState.stateFor("Passed"));
        assertEquals("failure", GiteaState.stateFor("Failed"));
        assertEquals("error", GiteaState.stateFor("Cancelled"));
    }

    @Test
    public void checkDescriptions() throws Exception {
        assertEquals("Build is pending", GiteaState.descriptionFor("unknown"));
        assertEquals("Build is pending", GiteaState.descriptionFor(null));
        assertEquals("Build is passing", GiteaState.descriptionFor("Passed"));
        assertEquals("Build is failing", GiteaState.descriptionFor("Failed"));
        assertEquals("Build encountered an error", GiteaState.descriptionFor("Cancelled"));
    }
}
