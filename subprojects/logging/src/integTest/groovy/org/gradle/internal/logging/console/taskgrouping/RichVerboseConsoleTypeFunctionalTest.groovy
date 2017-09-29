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

package org.gradle.internal.logging.console.taskgrouping

import spock.lang.Unroll

import static org.gradle.api.logging.configuration.ConsoleOutput.Rich
import static org.gradle.api.logging.configuration.ConsoleOutput.Verbose

class RichVerboseConsoleTypeFunctionalTest extends AbstractConsoleGroupedTaskFunctionalTest {
    def setup() {
        executer.withConsole(Verbose)
    }

    @Unroll
    def "can have verbose task output according to --console"() {
        given:
        executer.withConsole(mode)
        buildFile << """
            task helloWorld {
                doLast {
                    logger.quiet 'Hello world'
                }
            }
            task byeWorld {
                doLast {
                    logger.quiet 'Bye world'
                }
            }
            
            task silence {}
            
            task all {
                dependsOn helloWorld, byeWorld, silence
            }
        """
        when:
        succeeds('all')

        then:
        result.groupedOutput.task(':helloWorld').output == 'Hello world'
        result.groupedOutput.task(':byeWorld').output == 'Bye world'
        hasSilenceTaskOutput == result.groupedOutput.hasTask(':silence')

        where:
        mode    | hasSilenceTaskOutput
        Rich    | false
        Verbose | true
    }

    def "long running task's output are divided by other output"() {
        given:
        buildFile << '''
def sleepFor(String desc, int times){
    (1..times).each {
         Thread.sleep(1000)
         println "${desc} wake up ${it}"
    }
}

task('longRunning').doLast {
    sleepFor('longRunning', 6)
    assert false
}

Thread.start {
    sleepFor('ungrouped', 6)
}
'''

        when:
        fails('longRunning')

        then:
        result.groupedOutput.task(':longRunning').groupCount == 2
        result.groupedOutput.task(':longRunning').getStatus(0) == ''
        result.groupedOutput.task(':longRunning').getStatus(1) == 'FAILED'
    }

    def 'failed task result can be rendered'() {
        given:
        buildFile << '''
task myFailure {
    doLast {
        assert false
    }
}
'''
        when:
        fails('myFailure')

        then:
        result.groupedOutput.task(':myFailure').getStatus(0) == 'FAILED'
    }

    def 'up-to-date task result can be rendered'() {
        given:
        buildFile << '''
task upToDate{
    outputs.upToDateWhen {true}
    doLast {}
}
'''
        when:
        succeeds('upToDate')

        then:
        result.groupedOutput.task(':upToDate').getStatus(0) == ''

        when:
        executer.withConsole(Verbose)
        succeeds('upToDate')

        then:
        result.groupedOutput.task(':upToDate').getStatus(0) == 'UP-TO-DATE'
    }
}
