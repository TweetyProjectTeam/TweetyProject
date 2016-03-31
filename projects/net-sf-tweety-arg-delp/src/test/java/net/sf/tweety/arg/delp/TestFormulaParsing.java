package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.parser.ParseException;
import net.sf.tweety.commons.Formula;
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
        assertTrue("Formula is ground", fol.isGround());
        assertTrue("Formula is literal", fol.isLiteral());

        fol = (FolFormula) PARSER_BIRDS.parseFormula("tweety");
        assertTrue("Formula is ground", fol.isGround());
        assertTrue("Formula is literal", fol.isLiteral());
        assertEquals("First predicate has arity 0", 0, fol.getPredicates().iterator().next().getArity());

        fol = (FolFormula) PARSER_BIRDS.parseFormula(" \tPenguin(tina) \n");
        assertTrue("Formula is ground", fol.isGround());
        assertTrue("Formula is literal", fol.isLiteral());

        fol = (FolFormula) PARSER_BIRDS.parseFormula("  Scared (  tweety )");
        assertTrue("Formula is ground", fol.isGround());
        assertTrue("Formula is literal", fol.isLiteral());
        assertEquals("First predicate has arity 1", 1, fol.getPredicates().iterator().next().getArity());

        fol = (FolFormula) PARSER_BIRDS.parseFormula("~  Bird (\ntweety)");
        assertTrue("Formula is ground", fol.isGround());
        assertTrue("Formula is literal", fol.isLiteral());
        assertTrue("Formula is negation", fol instanceof Negation);

        fol = (FolFormula) PARSER_BIRDS.parseFormula(" ~Bird (Ydog)");
        assertFalse("Formula is NOT ground", fol.isGround());
        assertTrue("Formula is literal", fol.isLiteral());
        assertTrue("Formula is negation", fol instanceof Negation);

        fol = (FolFormula) PARSER_STOCKS.parseFormula(" In_fusion (A, B)");
        assertFalse("Formula is NOT ground", fol.isGround());
        assertTrue("Formula is literal", fol.isLiteral());
        assertEquals("First predicate has arity 2", 2, fol.getPredicates().iterator().next().getArity());

        fol = (FolFormula) PARSER_STOCKS.parseFormula(" A  ");
        assertTrue("Formula '"+fol+"' is ground", fol.isGround());
        assertTrue("Formula is literal", fol.isLiteral());
        assertEquals("First predicate has arity 0", 0, fol.getPredicates().iterator().next().getArity());

//        fol = (FolFormula) PARSER_STOCKS.parseFormula("A , B");
//        assertFalse("Formula '"+fol+"' is NOT ground", fol.isGround());
//        assertTrue("Formula is literal", fol.isLiteral());
    }

    @Test //(expected = ParseException.class)
    public void parseTooMuch() throws IOException {
        Formula fol = (FolFormula) PARSER_STOCKS.parseFormula(" Strong(acme) , B");
    }
    // parsing exceptions: unknown predicates or facts, symbols (!%& and -<. in formula)
    // ground formula
}
