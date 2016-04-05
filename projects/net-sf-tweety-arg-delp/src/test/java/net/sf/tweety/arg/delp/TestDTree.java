package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.semantics.DialecticalTree;
import net.sf.tweety.arg.delp.syntax.DelpArgument;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import static net.sf.tweety.arg.delp.TestArguments.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing dialectical trees.
 *
 * @author Linda.Briesemeister
 */
public final class TestDTree {

    private static DefeasibleLogicProgram DELP_BIRDS;

    @BeforeClass
    public static void init() throws IOException {
        DELP_BIRDS = new DelpParser().parseBeliefBase(Utilities.getKB("/birds.txt")).ground();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullArg() {
        new DialecticalTree(null);
    }

    @Test
    public void creation() {
        DialecticalTree tree;

        tree = new DialecticalTree(ARG_TINA_NOT_FLIES);
        assertEquals("trees newly initialized are always undefeated",
                DialecticalTree.Mark.UNDEFEATED, tree.getMarking());

        tree = new DialecticalTree(ARG_TINA_FLIES1);
        assertEquals("trees newly initialized are always undefeated",
                DialecticalTree.Mark.UNDEFEATED, tree.getMarking());

        tree = new DialecticalTree(ARG_TINA_FLIES2);
        assertEquals("trees newly initialized are always undefeated",
                DialecticalTree.Mark.UNDEFEATED, tree.getMarking());

        tree = new DialecticalTree(ARG_TINA_NESTS);
        assertEquals("trees newly initialized are always undefeated",
                DialecticalTree.Mark.UNDEFEATED, tree.getMarking());
    }

    @Test(expected = IllegalArgumentException.class)
    public void defeatersWhenNull1() {
        new DialecticalTree(ARG_TINA_NOT_FLIES).getDefeaters(null, null, null);
    }

    @Test
    public void defeatersWhenNull2() {
        Set<DialecticalTree> trees = new DialecticalTree(ARG_TINA_NOT_FLIES)
                .getDefeaters(null, new DefeasibleLogicProgram(), null);
        assertTrue("no defeaters for empty arguments", trees.isEmpty());
    }

    @Test
    public void defeatersWhenNull3() {
        Set<DialecticalTree> trees = new DialecticalTree(ARG_TINA_NESTS)
                .getDefeaters(null, DELP_BIRDS, null);
        assertTrue("no defeaters for empty arguments", trees.isEmpty());
    }

    @Test
    public void properDefeater() {
        Set<DialecticalTree> trees = new DialecticalTree(ARG_TINA_NESTS)
                .getDefeaters(Collections.singleton(ARG_TINA_NOT_FLIES), DELP_BIRDS, null);
        assertEquals("one defeater", 1, trees.size());
    }
}
