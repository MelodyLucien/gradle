/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.api.plugins.antlr

import spock.lang.Ignore
import spock.lang.Issue

class IncrementalAntlrTaskIntegrationTest extends AbstractAntlrIntegrationTest {
    String antlrDependency = "org.antlr:antlr:3.5.2"

    def test1TokenFile = file("build/generated-src/antlr/main/Test1.tokens")
    def test1LexerFile = file("build/generated-src/antlr/main/Test1Lexer.java")
    def test1ParserFile = file("build/generated-src/antlr/main/Test1Parser.java")

    def test2TokenFile = file("build/generated-src/antlr/main/Test2.tokens")
    def test2LexerFile = file("build/generated-src/antlr/main/Test2Lexer.java")
    def test2ParserFile = file("build/generated-src/antlr/main/Test2Parser.java")

    def "changed task inputs handled incrementally"() {
        when:
        grammar("Test1", "Test2")
        then:
        succeeds("generateGrammarSource")

        when:
        def test1TokensFileSnapshot = test1TokenFile.snapshot()
        def test1LexerFileSnapshot  = test1LexerFile.snapshot()
        def test1ParserFileSnapshot = test1ParserFile.snapshot()

        def test2TokensFileSnapshot = test2TokenFile.snapshot()
        def test2LexerFileSnapshot  = test2LexerFile.snapshot()
        def test2ParserFileSnapshot = test2ParserFile.snapshot()

        changedGrammar("Test2")

        then:
        succeeds("generateGrammarSource")
        test1TokenFile.assertHasNotChangedSince(test1TokensFileSnapshot);
        test1LexerFile.assertHasNotChangedSince(test1LexerFileSnapshot);
        test1ParserFile.assertHasNotChangedSince(test1ParserFileSnapshot);

        test2TokenFile.assertHasChangedSince(test2TokensFileSnapshot);
        test2LexerFile.assertHasChangedSince(test2LexerFileSnapshot);
        test2ParserFile.assertHasChangedSince(test2ParserFileSnapshot);
    }

    def "added grammar is handled incrementally"() {
        when:
        grammar("Test1")
        then:
        succeeds("generateGrammarSource")

        when:
        def test1TokensFileSnapshot = test1TokenFile.snapshot()
        def test1LexerFileSnapshot = test1LexerFile.snapshot()
        def test1ParserFileSnapshot = test1ParserFile.snapshot()

        !test2TokenFile.exists()
        !test2LexerFile.exists()
        !test2ParserFile.exists()

        grammar("Test2")

        then:
        succeeds("generateGrammarSource")
        test1TokenFile.assertHasNotChangedSince(test1TokensFileSnapshot);
        test1LexerFile.assertHasNotChangedSince(test1LexerFileSnapshot);
        test1ParserFile.assertHasNotChangedSince(test1ParserFileSnapshot);

        test2TokenFile.exists()
        test2LexerFile.exists()
        test2ParserFile.exists()

    }

    @Ignore
    //Somehow the exposed issues seems related to:
    @Issue("https://issues.gradle.org/browse/GRADLE-2440")
    def "output for removed grammar file is not handled correctly"() {
        when:
        grammar("Test1", "Test2")
        then:
        succeeds("generateGrammarSource")

        test1TokenFile.exists()
        test1LexerFile.exists()
        test1ParserFile.exists()

        test2TokenFile.exists()
        test2LexerFile.exists()
        test2ParserFile.exists()

        removedGrammar("Test1")

        then:
        succeeds("generateGrammarSource")
        !test1TokenFile.exists();
        !test1LexerFile.exists();
        !test1ParserFile.exists();
    }

    def grammar(String... ids) {
        ids.each{ id ->
            file("src/main/antlr/${id}.g") << """grammar ${id};
            list    :   item (item)*
                    ;

            item    :
                ID
                | INT
                ;

            ID  :   ('a'..'z'|'_') ('a'..'z'|'0'..'9'|'_')*
                ;

            INT :   '0'..'9'+
                ;
        """
        }
    }

    def changedGrammar(String... ids) {
        ids.each{ id ->
            file("src/main/antlr/${id}.g").text = """grammar ${id};
             list    :   item (item)*
                    ;

            item    :
                ID
                | INT
                ;

            ID  :   ('A'..'Z'|'_') ('A'..'Z'|'0'..'9'|'_')*
                ;

            INT :   '0'..'9'+
                ;
        """
        }
    }

    def removedGrammar(String... ids) {
        ids.each{ id ->
            file("src/main/antlr/${id}.g").delete()
        }
    }
}
