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
 package org.tweetyproject.arg.delp;

import org.tweetyproject.arg.delp.parser.DelpParser;
import org.tweetyproject.arg.delp.semantics.DialecticalTree;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.tweetyproject.arg.delp.TestArguments.*;
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
        new DialecticalTree(ARG_TINA_NOT_FLIES).getDefeaters(null, null);
    }

    @Test
    public void defeatersWhenNull2() {
        Set<DialecticalTree> trees = new DialecticalTree(ARG_TINA_NOT_FLIES)
                .getDefeaters(new DefeasibleLogicProgram(), null);
        assertTrue("no defeaters for empty arguments", trees.isEmpty());
    }

    @Test
    public void properDefeater() {
        Set<DialecticalTree> trees = new DialecticalTree(ARG_TINA_NESTS)
                .getDefeaters(DELP_BIRDS, null);
        assertEquals("one defeater", 1, trees.size());
    }
}
