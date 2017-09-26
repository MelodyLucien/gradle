/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.vcs.internal;

import com.google.common.base.Preconditions;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.artifacts.component.ComponentSelector;
import org.gradle.vcs.VersionControlSpec;

public class DefaultVcsMapping implements VcsMappingInternal {
    private final ComponentSelector requested;
    private final ModuleVersionSelector oldRequested;
    private VersionControlSpec versionControlSpec;

    public DefaultVcsMapping(ComponentSelector requested, ModuleVersionSelector oldRequested) {
        this.requested = requested;
        this.oldRequested = oldRequested;
    }

    @Override
    public ComponentSelector getRequested() {
        return requested;
    }

    @Override
    public ModuleVersionSelector getOldRequested() {
        return oldRequested;
    }

    @Override
    public void from(VersionControlSpec versionControlSpec) {
        Preconditions.checkNotNull(versionControlSpec, "VCS repository cannot be null");
        this.versionControlSpec = versionControlSpec;
    }

    @Override
    public VersionControlSpec getRepository() {
        return versionControlSpec;
    }

    @Override
    public boolean hasRepository() {
        return versionControlSpec != null;
    }
}
