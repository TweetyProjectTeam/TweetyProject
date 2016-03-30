package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.parser.LitParser;
import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Testing some example KBs with various queries.
 */
public final class TestQueries {

    private Answer query(String KB, String formula) throws IOException {
        // set up reasoner
        DelpParser parser = new DelpParser();
        DefeasibleLogicProgram delp = parser.parseBeliefBase(KB);
        DelpReasoner reasoner = new DelpReasoner(delp, new GeneralizedSpecificity());

        // set up query
        LitParser litParser = new LitParser();
        litParser.setSignature(parser.getSignature());
        return reasoner.query(litParser.parseFormula(formula));
    }

    private String getKB(String resourceName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(resourceName)));
        StringBuilder bufferKB = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            bufferKB.append(line);
        }
        return bufferKB.toString();
    }

    @Test
    public void birds() throws IOException {
        String KB = getKB("/birds.txt"); // load birds.txt

        Answer answer;
        // tina
        answer = query(KB, "Flies(tina)");
        assertTrue("Tina should fly", answer.getAnswerBoolean());
        answer = query(KB, "~Flies(tina)");
        assertFalse("Tina should fly", answer.getAnswerBoolean());
        // tweety
        answer = query(KB, "Flies(tweety)");
        assertFalse("Tweety does not fly", answer.getAnswerBoolean());
        answer = query(KB, "~Flies(tweety)");
        assertTrue("Tweety does not fly", answer.getAnswerBoolean());
    }

    @Test
    public void nixon() throws IOException {
        String KB = getKB("/nixon.txt"); // load nixon.txt

        Answer answer;
        // tina
        answer = query(KB, "~Pacifist(nixon)");
        // TODO: should be UNDECIDED!
        assertFalse("Nixon may or may not be a pacifist", answer.getAnswerBoolean());
//        "Pacifist(nixon)" = UNDECIDED
//        "Has_a_gun(nixon)" = UNDECIDED
    }

    @Test
    public void stocks() throws IOException {
        String KB = getKB("/stocks.txt"); // load birds.txt
        Answer ans = query(KB, "Buy_stock(acme)");
        assertTrue("Buying stock ACME should be supported", ans.getAnswerBoolean());
    }

    @Test
    public void counterarguments() throws IOException {
        String KB = getKB("/counterarg.txt");
//        "a" = UNDECIDED
//        "c" = UNDECIDED
    }
}