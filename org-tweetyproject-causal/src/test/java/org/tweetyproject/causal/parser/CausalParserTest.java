/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.causal.parser;

import org.junit.jupiter.api.Test;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.tweetyproject.causal.syntax.StructuralCausalModel.*;

/**
 * @author Oleksandr Dzhychko
 */
public class CausalParserTest {

    CausalParser parser = new CausalParser();

    @Test
    public void parseBeliefBase() throws IOException {
        var a = new Proposition("a");
        var b = new Proposition("b");
        var c = new Proposition("c");
        var d = new Proposition("d");
        var input = """
                a <=> b
                c <=> d
                { d, !b }
                """;

        var knowledgeBase = parser.parseBeliefBase(input);

        assertEquals(Set.of(new Equivalence(a, b), new Equivalence(c, d)), knowledgeBase.getBeliefs());
        assertEquals(Set.of(new Negation(b), d), knowledgeBase.getAssumptions());
    }

    @Test
    public void parseBeliefBaseWithMultipleAssumptionLines() throws IOException {
        var a = new Proposition("a");
        var b = new Proposition("b");
        var c = new Proposition("c");
        var d = new Proposition("d");
        // Being able to break up assumptions to different lines
        // allows to freely structure the knowledgeable.
        var input = """
                { !b }
                a <=> b
                c <=> d
                { d }
                """;

        var knowledgeBase = parser.parseBeliefBase(input);

        assertEquals(Set.of(new Equivalence(a, b), new Equivalence(c, d)), knowledgeBase.getBeliefs());
        assertEquals(Set.of(new Negation(b), d), knowledgeBase.getAssumptions());
    }

    @Test
    public void parseBeliefBaseWithEmptyAssumptions() throws IOException {
        var a = new Proposition("a");
        var input = """
                a <=> +
                {}
                """;

        var knowledgeBase = parser.parseBeliefBase(input);

        assertEquals(Set.of(new Equivalence(a, new Tautology())), knowledgeBase.getBeliefs());
    }

    @Test
    public void throwsParserExceptionForInvalidSyntax() {
        var input = """
                (a
                """;

        assertThrows(ParserException.class, () -> parser.parseBeliefBase(input));
    }

    @Test
    public void throwsParserExceptionForFormulaThatIsNotAnEquivalence() {
        var input = """
                a
                """;

        assertThrows(ParserException.class, () -> parser.parseBeliefBase(input));
    }

    @Test
    public void throwsParserExceptionForCyclicDependency() {
        var input = """
                a <=> a
                """;

        var exception = assertThrows(ParserException.class, () -> parser.parseBeliefBase(input));
        assertInstanceOf(CyclicDependencyException.class, exception.getCause());
    }


    @Test
    public void parseObservations() throws IOException {
        var a = new Proposition("a");
        var b = new Proposition("b");
        var input = "a, !b";

        var observations = parser.parseListOfFormulae(input, ",");

        assertEquals(List.of(a, new Negation(b)), observations);
    }
}