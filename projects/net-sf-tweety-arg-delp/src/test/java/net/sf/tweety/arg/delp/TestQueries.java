package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Testing some example KBs with various queries.
 *
 * @author Linda.Briesemeister
 */
public final class TestQueries {

    private final static Logger LOGGER = Logger.getLogger(TestQueries.class.getName());

    private DelpAnswer query(String KB, String formula) throws IOException {
        DelpParser parser = new DelpParser();

        // set up reasoner
        DefeasibleLogicProgram delp = parser.parseBeliefBase(KB);
        DelpReasoner reasoner = new DelpReasoner(delp, new GeneralizedSpecificity());

        // perform query
        DelpAnswer answer = (DelpAnswer) reasoner.query(parser.parseFormula(formula));
        LOGGER.info("DeLP answer to query '"+formula+"' = "+answer);
        return answer;
    }

    @Test
    public void birds() throws IOException {
        String KB = Utilities.getKB("/birds.txt");

        DelpAnswer answer;
        // tina
        answer = query(KB, "Flies(tina)");
        assertEquals("Tina should fly", DelpAnswer.Type.YES, answer.getType());
        answer = query(KB, "~Flies(tina)");
        assertEquals("Tina should fly", DelpAnswer.Type.NO, answer.getType());
        // tweety
        answer = query(KB, "Flies(tweety)");
        assertEquals("Tweety does not fly", DelpAnswer.Type.NO, answer.getType());
        answer = query(KB, "~Flies(tweety)");
        assertEquals("Tweety does not fly", DelpAnswer.Type.YES, answer.getType());
    }

    @Test
    public void nixon() throws IOException {
        String KB = Utilities.getKB("/nixon.txt"); // load nixon.txt

        DelpAnswer answer;
        answer = query(KB, "~Pacifist(nixon)"); // TODO: should be UNDECIDED!
        answer = query(KB, "Pacifist(nixon)"); // TODO: should be UNDECIDED!
        answer = query(KB, "Has_a_gun(nixon)"); // UNDECIDED
    }

    @Test
    public void stocks() throws IOException {
        String KB = Utilities.getKB("/stocks.txt");
        DelpAnswer ans = query(KB, "Buy_stock(acme)");
        assertEquals("Buying stock ACME should be supported",  DelpAnswer.Type.YES, ans.getType());
    }

    @Test
    public void counterarguments() throws IOException {
        String KB = Utilities.getKB("/counterarg.txt");
        DelpAnswer answer;
        answer = query(KB, "a");
//        "a" = UNDECIDED
        answer = query(KB, "c");
//        "c" = UNDECIDED
    }

    @Test
    public void hobbes() throws IOException {
        String KB = Utilities.getKB("/hobbes.txt");
        DelpAnswer answer;
        answer = query(KB, "~dangerous(hobbes)");
        // "UNDECIDED"?
    }

    @Test
    public void dtree() throws IOException {
        String KB = Utilities.getKB("/dtree.txt");
        DelpAnswer answer;
        answer = query(KB, "a"); // TODO: UNDECIDED
        answer = query(KB, "~b"); // YES
        assertEquals(DelpAnswer.Type.YES, answer.getType());
        answer = query(KB, "b"); // NO
        assertEquals(DelpAnswer.Type.NO, answer.getType());
    }
}