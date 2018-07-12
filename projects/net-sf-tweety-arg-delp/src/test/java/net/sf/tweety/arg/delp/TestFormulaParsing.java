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
 package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testing DeLP formula parsing.
 *
 * @author Linda.Briesemeister
 */
public final class TestFormulaParsing {

    private final static DelpParser PARSER_BIRDS = new DelpParser();
    private final static DelpParser PARSER_STOCKS = new DelpParser();

    @BeforeClass
    public static void initParsers() {
        try {
            PARSER_BIRDS.parseBeliefBase(Utilities.getKB("/birds.txt"));
            PARSER_STOCKS.parseBeliefBase(Utilities.getKB("/stocks.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseSimple() throws IOException {
        FolFormula fol;
        fol = (FolFormula) PARSER_BIRDS.parseFormula("Bird(tweety)");
        assertTrue("Formula '"+fol+"' is ground", fol.isGround());
        assertTrue("Formula '"+fol+"' is literal", fol.isLiteral());

        fol = (FolFormula) PARSER_BIRDS.parseFormula("tweety");
        assertTrue("Formula '"+fol+"' is ground", fol.isGround());
        assertTrue("Formula '"+fol+"' is literal", fol.isLiteral());
        assertEquals("First predicate has arity 0", 0, fol.getPredicates().iterator().next().getArity());

        fol = (FolFormula) PARSER_BIRDS.parseFormula(" \tPenguin(tina) \n");
        assertTrue("Formula '"+fol+"' is ground", fol.isGround());
        assertTrue("Formula '"+fol+"' is literal", fol.isLiteral());

        fol = (FolFormula) PARSER_BIRDS.parseFormula("  Scared (  tweety )");
        assertTrue("Formula '"+fol+"' is ground", fol.isGround());
        assertTrue("Formula '"+fol+"' is literal", fol.isLiteral());
        assertEquals("First predicate has arity 1", 1, fol.getPredicates().iterator().next().getArity());

        fol = (FolFormula) PARSER_BIRDS.parseFormula("~  Bird (\ntweety)");
        assertTrue("Formula '"+fol+"' is ground", fol.isGround());
        assertTrue("Formula '"+fol+"' is literal", fol.isLiteral());
        assertTrue("Formula '"+fol+"' is negation", fol instanceof Negation);

        fol = (FolFormula) PARSER_BIRDS.parseFormula(" ~Bird (Ydog)");
        assertFalse("Formula '"+fol+"' is NOT ground", fol.isGround());
        assertTrue("Formula '"+fol+"' is literal", fol.isLiteral());
        assertTrue("Formula '"+fol+"' is negation", fol instanceof Negation);

        fol = (FolFormula) PARSER_STOCKS.parseFormula(" in_fusion (A, B)");
        assertFalse("Formula '"+fol+"' is NOT ground", fol.isGround());
        assertTrue("Formula '"+fol+"' is literal", fol.isLiteral());
        assertEquals("First predicate has arity 2", 2, fol.getPredicates().iterator().next().getArity());

        fol = (FolFormula) PARSER_STOCKS.parseFormula(" ~ in_fusion (A, steel)");
        assertFalse("Formula '"+fol+"' is NOT ground", fol.isGround());
        assertTrue("Formula '"+fol+"' is literal", fol.isLiteral());
    }

    // parsing exceptions: formula too long, unknown predicates or facts, symbols (!%& and -<. in formula)
    @Test(expected = ParserException.class)
    public void parseTooMuch() throws IOException {
       PARSER_STOCKS.parseFormula(" strong(acme) , B");
    }
    @Test(expected = ParserException.class)
    public void parseTooMuch2() throws IOException {
        PARSER_STOCKS.parseFormula(" strong(acme) . ");
    }
    @Test(expected = ParserException.class)
    public void parseTooMuch3() throws IOException {
        PARSER_STOCKS.parseFormula(" strong(acme) <- Foo(B) ");
    }
    @Test(expected = ParserException.class)
    public void parseUnknownPred() throws IOException {
        PARSER_STOCKS.parseFormula(" ~IPO(acme) ");
    }
    @Test(expected = ParserException.class)
    public void parseUnknownConstantAsPredicate() throws IOException {
        PARSER_STOCKS.parseFormula(" ~ google  ");
    }
    @Test(expected = ParserException.class)
    public void parseUnknownVariableAsPredicate() throws IOException {
        PARSER_STOCKS.parseFormula(" A  ");
    }
    @Test(expected = ParserException.class)
    public void parseUnknownConstant() throws IOException {
        PARSER_STOCKS.parseFormula(" ~in_fusion (A, google)");
    }
    @Test(expected = ParserException.class)
    public void parseDoubleNegation() throws IOException {
        PARSER_STOCKS.parseFormula(" ~ ~ strong (acme)");
    }
}
