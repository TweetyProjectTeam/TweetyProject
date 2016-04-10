package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import org.junit.BeforeClass;
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

    private final static DelpParser PARSER_BIRDS = new DelpParser();
    private static DelpReasoner REASONER_BIRDS;
    private final static DelpParser PARSER_NIXON = new DelpParser();
    private static DelpReasoner REASONER_NIXON;
    private final static DelpParser PARSER_STOCKS = new DelpParser();
    private static DelpReasoner REASONER_STOCKS;
    private final static DelpParser PARSER_COUNTER = new DelpParser();
    private static DelpReasoner REASONER_COUNTER;
    private final static DelpParser PARSER_HOBBES = new DelpParser();
    private static DelpReasoner REASONER_HOBBES;
    private final static DelpParser PARSER_DTREE = new DelpParser();
    private static DelpReasoner REASONER_DTREE;

    @BeforeClass
    public static void initParsers() {
        DefeasibleLogicProgram delp;
        try {
            delp = PARSER_BIRDS.parseBeliefBase(Utilities.getKB("/birds.txt"));
            REASONER_BIRDS = new DelpReasoner(delp, new GeneralizedSpecificity());
            delp = PARSER_NIXON.parseBeliefBase(Utilities.getKB("/nixon.txt"));
            REASONER_NIXON = new DelpReasoner(delp, new GeneralizedSpecificity());
            delp = PARSER_STOCKS.parseBeliefBase(Utilities.getKB("/stocks.txt"));
            REASONER_STOCKS = new DelpReasoner(delp, new GeneralizedSpecificity());
            delp = PARSER_COUNTER.parseBeliefBase(Utilities.getKB("/counterarg.txt"));
            REASONER_COUNTER = new DelpReasoner(delp, new GeneralizedSpecificity());
            delp = PARSER_HOBBES.parseBeliefBase(Utilities.getKB("/hobbes.txt"));
            REASONER_HOBBES = new DelpReasoner(delp, new GeneralizedSpecificity());
            delp = PARSER_DTREE.parseBeliefBase(Utilities.getKB("/dtree.txt"));
            REASONER_DTREE = new DelpReasoner(delp, new GeneralizedSpecificity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DelpAnswer query(DelpReasoner reasoner, DelpParser parser, String formula) throws IOException {
        // perform query
        DelpAnswer answer = (DelpAnswer) reasoner.query(parser.parseFormula(formula));
        LOGGER.info("DeLP answer to query '"+formula+"' = "+answer);
        return answer;
    }

    @Test
    public void birds() throws IOException {
        DelpAnswer answer;
        // tina
        answer = query(REASONER_BIRDS, PARSER_BIRDS, "Flies(tina)");
        assertEquals("Tina should fly", DelpAnswer.Type.YES, answer.getType());
        answer = query(REASONER_BIRDS, PARSER_BIRDS, "~Flies(tina)");
        assertEquals("Tina should fly", DelpAnswer.Type.NO, answer.getType());
        // tweety
        answer = query(REASONER_BIRDS, PARSER_BIRDS, "Flies(tweety)");
        assertEquals("Tweety does not fly", DelpAnswer.Type.NO, answer.getType());
        answer = query(REASONER_BIRDS, PARSER_BIRDS, "~Flies(tweety)");
        assertEquals("Tweety does not fly", DelpAnswer.Type.YES, answer.getType());
    }

    @Test
    public void nixon() throws IOException {
        DelpAnswer answer;
        answer = query(REASONER_NIXON, PARSER_NIXON, "~pacifist(nixon)"); // UNDECIDED
        assertEquals(DelpAnswer.Type.UNDECIDED, answer.getType());
        answer = query(REASONER_NIXON, PARSER_NIXON, "pacifist(nixon)"); // UNDECIDED
        assertEquals(DelpAnswer.Type.UNDECIDED, answer.getType());
        answer = query(REASONER_NIXON, PARSER_NIXON, "has_a_gun(nixon)"); // YES
        assertEquals(DelpAnswer.Type.YES, answer.getType());
    }

    @Test
    public void stocks() throws IOException {
        DelpAnswer ans = query(REASONER_STOCKS, PARSER_STOCKS, "Buy_stock(acme)");
        assertEquals("Buying stock ACME should be supported",  DelpAnswer.Type.YES, ans.getType());
    }

    @Test
    public void counterarguments() throws IOException {
        DelpAnswer answer;
        answer = query(REASONER_COUNTER, PARSER_COUNTER, "a");
        assertEquals(DelpAnswer.Type.UNDECIDED, answer.getType());
        answer = query(REASONER_COUNTER, PARSER_COUNTER, "c");
        assertEquals(DelpAnswer.Type.UNDECIDED, answer.getType());
    }

    @Test
    public void hobbes() throws IOException {
        DelpAnswer answer;
        answer = query(REASONER_HOBBES, PARSER_HOBBES, "~dangerous(hobbes)");
        assertEquals(DelpAnswer.Type.UNDECIDED, answer.getType());
    }

    @Test
    public void dtree() throws IOException {
        DelpAnswer answer;
        answer = query(REASONER_DTREE, PARSER_DTREE, "a"); // UNDECIDED
        assertEquals(DelpAnswer.Type.UNDECIDED, answer.getType());
        answer = query(REASONER_DTREE, PARSER_DTREE, "~b"); // YES
        assertEquals(DelpAnswer.Type.YES, answer.getType());
        answer = query(REASONER_DTREE, PARSER_DTREE, "b"); // NO
        assertEquals(DelpAnswer.Type.NO, answer.getType());
    }
}