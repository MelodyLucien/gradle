/*
 * Copyright 2013 the original author or authors.
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
package org.gradle.api.internal.artifacts.component

import org.gradle.api.artifacts.component.BuildComponentIdentifier
import org.gradle.api.artifacts.component.BuildComponentSelector
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.util.Matchers.strictlyEquals

class DefaultBuildComponentSelectorTest extends Specification {
    def "is instantiated with non-null constructor parameter values"() {
        when:
        BuildComponentSelector defaultBuildComponentSelector = new DefaultBuildComponentSelector(':myPath')

        then:
        defaultBuildComponentSelector.projectPath == ':myPath'
        defaultBuildComponentSelector.displayName == 'project :myPath'
        defaultBuildComponentSelector.toString() == 'project :myPath'
    }

    @Unroll
    def "is instantiated with null constructor parameter value"() {
        when:
        new DefaultBuildComponentSelector(null)

        then:
        Throwable t = thrown(AssertionError)
        t.message == 'project path cannot be null'
    }

    @Unroll
    def "can compare with other instance (#projectPath)"() {
        expect:
        BuildComponentSelector defaultBuildComponentSelector1 = new DefaultBuildComponentSelector(':myProjectPath1')
        BuildComponentSelector defaultBuildComponentSelector2 = new DefaultBuildComponentSelector(projectPath)
        strictlyEquals(defaultBuildComponentSelector1, defaultBuildComponentSelector2) == equality
        (defaultBuildComponentSelector1.hashCode() == defaultBuildComponentSelector2.hashCode()) == hashCode
        (defaultBuildComponentSelector1.toString() == defaultBuildComponentSelector2.toString()) == stringRepresentation

        where:
        projectPath       | equality | hashCode | stringRepresentation
        ':myProjectPath1' | true     | true     | true
        ':myProjectPath2' | false    | false    | false
    }

    def "prevents matching of null id"() {
        when:
        BuildComponentSelector defaultBuildComponentSelector = new DefaultBuildComponentSelector(':myPath')
        defaultBuildComponentSelector.matchesStrictly(null)

        then:
        Throwable t = thrown(AssertionError)
        assert t.message == 'identifier cannot be null'
    }

    @Unroll
    def "matches id (#projectPath)"() {
        expect:
        BuildComponentSelector defaultBuildComponentSelector = new DefaultBuildComponentSelector(':myProjectPath1')
        BuildComponentIdentifier defaultBuildComponentIdentifier = new DefaultBuildComponentIdentifier(projectPath)
        defaultBuildComponentSelector.matchesStrictly(defaultBuildComponentIdentifier) == matchesId

        where:
        projectPath       | matchesId
        ':myProjectPath1' | true
        ':myProjectPath2' | false
    }
}
