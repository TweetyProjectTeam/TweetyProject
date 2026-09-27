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
import org.tweetyproject.logics.commons.syntax.interfaces.Atom;

/**
 * Parses an Assumption-Based Argumentation (ABA) System from an input text.
 * Each line is empty, a comment starting with '%', or one of:
 * <br>&lt;assumptions&gt; ::= '{' &lt;atom&gt; (',' &lt;atom&gt;)* '}'
 * <br>&lt;rule&gt; ::= &lt;atom&gt; '&lt;-' &lt;body&gt;?
 * <br>&lt;body&gt; ::= 'true' | &lt;atom&gt; (',' &lt;atom&gt;)*
 * <br>&lt;contrary&gt; ::= 'not' &lt;atom&gt; '=' &lt;atom&gt;
 * <br>where &lt;atom&gt; is an atom of the theory's language. Any other line
 * is a {@link ParserException}.
 *
 * @param <T> the type of formulas (language) that the ABA theory ranges over
 *
 * @author Nils Geilen
 * @author Lars Bengel
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
        int lineNumber = 0;
        while (true) {
            String line = br.readLine();
            if (line == null) break;
            lineNumber++;

            // Skip comments and empty lines
            if (EMPTY.matcher(line).matches() || COMMENT.matcher(line).matches()) continue;

            try {
                Matcher matcher = ASSUMPTIONS.matcher(line);
                if (matcher.matches()) {
                    for (String ass : matcher.group(1).split(Pattern.quote(symbolComma)))
                        abat.add(new Assumption<>(parseAtom(ass)));
                    continue;
                }
                if (!rulePattern().matcher(line).matches() && !negationPattern().matcher(line).matches())
                    throw new ParserException("expected '{...}', a rule or 'not a = c'");
                abat.add(parseFormula(line));
            } catch (RuntimeException e) {
                throw new ParserException("Line " + lineNumber + " '" + line.trim() + "': " + e.getMessage());
            }
        }

        return abat.ground();
    }

    /*
     * (non-Javadoc)
     * @see org.tweetyproject.commons.Parser#parseFormula(java.io.Reader)
     */
    @Override
    public Formula parseFormula(Reader reader) throws IOException, ParserException {
        final Pattern TRUE = Pattern.compile("^\\s*(" + Pattern.quote(symbolTrue) + ")?\\s*$");

        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        if (line == null) return null;

        // Parse inference rules
        Matcher m = rulePattern().matcher(line);
        if (m.matches()) {
            InferenceRule<T> rule = new InferenceRule<>();
            rule.setConclusion(parseAtom(m.group(1)));
            if (!TRUE.matcher(m.group(2)).matches())
                for (String pre : m.group(2).split(Pattern.quote(symbolComma)))
                    rule.addPremise(parseAtom(pre));
            return rule;
        }

        // Parse negations
        m = negationPattern().matcher(line);
        if (m.matches())
            return new Negation<T>(parseAtom(m.group(1)), parseAtom(m.group(2)));

        // Parse assumptions
        return new Assumption<>(parseAtom(line));
    }

    private Pattern rulePattern() {
        return Pattern.compile("(.+)" + Pattern.quote(symbolArrow) + "(.*)");
    }

    private Pattern negationPattern() {
        return Pattern.compile("^\\s*not\\s+(.+?)\\s*=\\s*(.+?)\\s*$");
    }

    /**
     * Parses a single atom of the underlying language
     */
    @SuppressWarnings("unchecked")
    private T parseAtom(String text) throws IOException, ParserException {
        if (text.isBlank())
            throw new ParserException("missing atom");
        Formula formula = formulaparser.parseFormula(text.trim());
        if (!(formula instanceof Atom))
            throw new ParserException("'" + text.trim() + "' is not an atom");
        return (T) formula;
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
