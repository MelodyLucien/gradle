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

package org.gradle.integtests.fixtures.logging;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.gradle.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupedTaskFixture {

    private final String taskName;

    private final List<SingleGroupResult> groups = new ArrayList<SingleGroupResult>(1);

    GroupedTaskFixture(String taskName) {
        this.taskName = taskName;
    }

    void addGroup(String status, String outpuLines) {
        groups.add(new SingleGroupResult(status, outpuLines));
    }

    public String getName() {
        return taskName;
    }

    public String getStatus(int index) {
        return groups.get(index).status;
    }

    public int getGroupCount() {
        return groups.size();
    }

    public String getOutput() {
        return CollectionUtils.join("\n", getOutputs());
    }

    public List<String> getOutputs() {
        return Lists.newArrayList(Iterables.transform(groups, new Function<SingleGroupResult, String>() {
            public String apply(SingleGroupResult input) {
                return input.outputLines;
            }
        }));
    }

    private static class SingleGroupResult {
        private final String status;
        private final String outputLines;

        private SingleGroupResult(String status, String outputLines) {
            this.status = status == null ? "" : status;
            this.outputLines = outputLines;
        }
    }
}
