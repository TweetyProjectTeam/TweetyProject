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
 * Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.causal.parser;

import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.causal.syntax.StructuralCausalModel;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParserFactory;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for {@link CausalKnowledgeBase} and observation as consumed by {@link org.tweetyproject.causal.reasoner.AbstractCausalReasoner}.
 * A causal knowledge base can be parsed with {@link #parseBeliefBase(Reader)}.
 * Observations can be parsed with {@link #parseListOfFormulae(String, String)}.
 *
 * @author Lars Bengel
 * @author Oleksandr Dzhychko
 */
public class CausalParser extends Parser<CausalKnowledgeBase, PlFormula> {
    static final Pattern ASSUMPTIONS_PATTERN = Pattern.compile("^\\s*\\{(.*)\\}\\s*$");
    static final String SYMBOL_COMMA = ",";
    static Parser<PlBeliefSet, PlFormula> plParser = PlParserFactory.getParserForFormat(PlParserFactory.Format.TWEETY);

    /**
     * Parses data from the reader into a {@link CausalKnowledgeBase}.
     * Each line must contain either assumptions or are an equation.
     * Assumptions and equations are defined as following:
     * <br>equation ::= formula '&lt;=&gt;' formula
     * <br>assumptions ::= '{' assumption (',' assumption)* '}'
     * <br>assumption ::= formula
     * <br>formula ::= a propositional formula as parsable by {@link org.tweetyproject.logics.pl.parser.PlParser#parseFormula(String)}
     *
     * @param reader a reader
     * @return the parsed causal knowledge base
     * @throws IOException     if some IO issue occurred.
     * @throws ParserException some parsing exceptions may be added here.
     */
    @Override
    public CausalKnowledgeBase parseBeliefBase(Reader reader) throws IOException, ParserException {
        // Implementation similar to AbaParser.parseBeliefBase.
        // But it simplified by not allowing empty lines or comments.
        // If needed, it can be added later.

        List<PlFormula> assumptions = new ArrayList<>();
        List<PlFormula> equations = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);
        while (true) {
            String line = br.readLine();
            if (line == null) break;
            Matcher matcher = ASSUMPTIONS_PATTERN.matcher(line);
            if (matcher.matches()) {
                String[] assumptionStrings = matcher.group(1).split(SYMBOL_COMMA);
                for (String assumptionString : assumptionStrings)
                    if (!assumptionString.isBlank()) {
                        assumptions.add(parseFormula(assumptionString));
                    }
            } else {
                equations.add(parseFormula(line));
            }
        }

        StructuralCausalModel model;
        try {
            model = new StructuralCausalModel(equations);
        } catch (IllegalArgumentException | StructuralCausalModel.CyclicDependencyException e) {
            throw new ParserException(e);
        }
        return new CausalKnowledgeBase(model, assumptions);
    }

    @Override
    public PlFormula parseFormula(Reader reader) throws IOException, ParserException {
        return plParser.parseFormula(reader);
    }
}