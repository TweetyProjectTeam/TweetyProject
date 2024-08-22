/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.aba.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.aba.syntax.InferenceRule;
import org.tweetyproject.arg.aba.syntax.Negation;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;

/**
 * Parses an Assumption-Based Argumentation (ABA) System from an input text.
 * The standard symbols used in the syntax are:
 * <br>&lt;rule&gt; ::= &lt;head&gt; '&lt;-' &lt;body&gt;?
 * <br>&lt;head&gt; ::= &lt;word&gt;
 * <br>&lt;body&gt; ::= 'true' | &lt;word&gt; (',' &lt;word&gt;)*
 * <br>&lt;assumption&gt; ::= &lt;word&gt;
 * <br>&lt;assumptions&gt; ::= '{' &lt;assumption&gt; (',' &lt;assumption&gt;)* '}'
 * <br>where &lt;word&gt; is a term in the theory's language.
 *
 * @param <T> the type of formulas (language) that the ABA theory ranges over
 *
 * @author Nils Geilen
 */
public class AbaParser<T extends Formula> extends Parser<AbaTheory<T>, Formula> {

    /**
     * The parser used to parse the individual formulae.
     */
    private final Parser<? extends BeliefBase, ? extends Formula> formulaparser;

    /**
     * Symbol used for representing 'true' in rules.
     */
    private String symbolTrue = "true";

    /**
     * Symbol used for separating the head and body in rules.
     */
    private String symbolArrow = "<-";

    /**
     * Symbol used for separating assumptions.
     */
    private String symbolComma = ",";

    /**
     * Creates a new ABA parser.
     *
     * @param formulaparser the parser for parsing individual formulae
     */
    public AbaParser(Parser<? extends BeliefBase, ? extends Formula> formulaparser) {
        super();
        this.formulaparser = formulaparser;
    }

    /*
     * (non-Javadoc)
     * @see org.tweetyproject.commons.Parser#parseBeliefBase(java.io.Reader)
     */
    @Override
    public AbaTheory<T> parseBeliefBase(Reader reader) throws IOException, ParserException {
        final Pattern COMMENT = Pattern.compile("^%.*"),
                      EMPTY = Pattern.compile("^\\s*$"),
                      ASSUMPTIONS = Pattern.compile("^\\s*\\{(.*)\\}\\s*$");

        AbaTheory<T> abat = new AbaTheory<>();
        BufferedReader br = new BufferedReader(reader);

        while (true) {
            String line = br.readLine();
            if (line == null) break;

            // Skip comments and empty lines
            if (EMPTY.matcher(line).matches() || COMMENT.matcher(line).matches()) continue;

            // Parse assumptions
            Matcher matcher = ASSUMPTIONS.matcher(line);
            if (matcher.matches()) {
                String[] asss = matcher.group(1).split(symbolComma);
                for (String ass : asss)
                    abat.add(parseFormula(ass));
            } else {
                abat.add(parseFormula(line));
            }
        }

        return abat;
    }

    /*
     * (non-Javadoc)
     * @see org.tweetyproject.commons.Parser#parseFormula(java.io.Reader)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Formula parseFormula(Reader reader) throws IOException, ParserException {
        final Pattern RULE = Pattern.compile("(.+)" + symbolArrow + "(.*)"),
                      TRUE = Pattern.compile("^\\s*$"),
                      NEGATION = Pattern.compile("not(.+)=(.+)");

        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        if (line == null) return null;

        // Parse inference rules
        Matcher m = RULE.matcher(line);
        if (m.matches()) {
            InferenceRule<T> rule = new InferenceRule<>();
            String head = m.group(1), tail = m.group(2);
            rule.setConclusion((T) formulaparser.parseFormula(head));

            if (!TRUE.matcher(tail).matches()) {
                String[] pres = tail.split(symbolComma);
                for (String pre : pres)
                    rule.addPremise((T) formulaparser.parseFormula(pre));
            }
            return rule;
        }

        // Parse negations
        m = NEGATION.matcher(line);
        if (m.matches()) {
            return new Negation<Formula>(formulaparser.parseFormula(m.group(1)), formulaparser.parseFormula(m.group(2)));
        }

        // Parse assumptions
        return new Assumption<>((T) formulaparser.parseFormula(line));
    }

    /**
     * Gets the symbol representing 'true' in rules.
     * This is the symbol used to represent a 'true' condition in rule bodies.
     *
     * @return the symbol representing 'true' in rules
     */
    public String getSymbolTrue() {
        return symbolTrue;
    }

    /**
     * Sets the symbol representing 'true' in rules.
     * This allows setting a custom symbol to represent 'true' in rule bodies.
     *
     * @param symbolTrue the symbol to set for 'true'
     */
    public void setSymbolTrue(String symbolTrue) {
        this.symbolTrue = symbolTrue;
    }

    /**
     * Gets the symbol used to separate the head and body in rules.
     * This is the arrow symbol used to define rules in the form of head <- body.
     *
     * @return the symbol separating the head and body in rules
     */
    public String getSymbolArrow() {
        return symbolArrow;
    }

    /**
     * Sets the symbol used to separate the head and body in rules.
     * This allows setting a custom symbol to represent the arrow in the rule definitions.
     *
     * @param symbolArrow the symbol to set for the arrow
     */
    public void setSymbolArrow(String symbolArrow) {
        this.symbolArrow = symbolArrow;
    }

    /**
     * Gets the symbol used for separating assumptions.
     * This is the symbol used to separate different assumptions in the assumption set.
     *
     * @return the symbol used for separating assumptions
     */
    public String getSymbolComma() {
        return symbolComma;
    }

    /**
     * Sets the symbol used for separating assumptions.
     * This allows setting a custom symbol to separate assumptions in the assumption set.
     *
     * @param symbolComma the symbol to set for separating assumptions
     */
    public void setSymbolComma(String symbolComma) {
        this.symbolComma = symbolComma;
    }

}
