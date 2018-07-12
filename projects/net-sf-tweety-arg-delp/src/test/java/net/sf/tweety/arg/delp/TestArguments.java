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
import net.sf.tweety.arg.delp.syntax.DefeasibleRule;
import net.sf.tweety.arg.delp.syntax.DelpArgument;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Testing argument syntax.
 *
 * @author Linda.Briesemeister
 */
public final class TestArguments {

    private static DefeasibleLogicProgram DELP_BIRDS;

    // some terms and formulae:
    private final static List<Term<?>> TINA = Stream
            .of(new Constant("tina"))
            .collect(Collectors.toList());
    private final static FolFormula FOL_FLIES_TINA = new FOLAtom(new Predicate("Flies",1), TINA);
    private final static FolFormula FOL_NOT_FLIES_TINA = new Negation(FOL_FLIES_TINA);

    // some interesting arguments as strings and data structures:
    final static String STR_TINA_NOT_FLIES = "<{!Flies(tina) -< Chicken(tina).},!Flies(tina)>";
    final static DelpArgument ARG_TINA_NOT_FLIES = new DelpArgument(Stream
            .of(new DefeasibleRule(FOL_NOT_FLIES_TINA, Stream
                    .of(new FOLAtom(new Predicate("Chicken",1), TINA))
                    .collect(Collectors.toSet())))
            .collect(Collectors.toSet()),
            FOL_NOT_FLIES_TINA);
    final static String STR_TINA_FLIES1 = "<{Flies(tina) -< Bird(tina).},Flies(tina)>";
    final static DelpArgument ARG_TINA_FLIES1 = new DelpArgument(Stream
            .of(new DefeasibleRule(FOL_FLIES_TINA, Stream
                    .of(new FOLAtom(new Predicate("Bird",1), TINA))
                    .collect(Collectors.toSet())))
            .collect(Collectors.toSet()),
            FOL_FLIES_TINA);
    final static String STR_TINA_FLIES2 = "<{Flies(tina) -< Chicken(tina),Scared(tina).},Flies(tina)>";
    final static DelpArgument ARG_TINA_FLIES2 = new DelpArgument(Stream
            .of(new DefeasibleRule(FOL_FLIES_TINA, Stream
                    .of(
                            new FOLAtom(new Predicate("Chicken",1), TINA),
                            new FOLAtom(new Predicate("Scared",1), TINA))
                    .collect(Collectors.toSet())))
            .collect(Collectors.toSet()),
            FOL_FLIES_TINA);
    final static String STR_TINA_NESTS = "<{Flies(tina) -< Bird(tina).,Nests_in_trees(tina) -< Flies(tina).},Nests_in_trees(tina)>";
    final static DelpArgument ARG_TINA_NESTS = new DelpArgument(Stream
            .of(
                    new DefeasibleRule(new FOLAtom(new Predicate("Nests_in_trees",1), TINA), Stream
                            .of(FOL_FLIES_TINA)
                            .collect(Collectors.toSet())),
                    new DefeasibleRule(FOL_FLIES_TINA, Stream
                        .of(new FOLAtom(new Predicate("Bird",1), TINA))
                        .collect(Collectors.toSet())))
            .collect(Collectors.toSet()),
            new FOLAtom(new Predicate("Nests_in_trees",1), TINA));

    @BeforeClass
    public static void init() throws IOException {
        DELP_BIRDS = new DelpParser().parseBeliefBase(Utilities.getKB("/birds.txt")).ground();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullConclusion1() {
        new DelpArgument(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void createWithNullConclusion2() {
        new DelpArgument(null,null);
    }

    @Test
    public void argRepresentation() {
        // compare all arguments that have only one supporting rule with only one literal in body
        assertEquals("Tina does not fly", STR_TINA_NOT_FLIES, ARG_TINA_NOT_FLIES.toString());
        assertEquals("Tina flies 1", STR_TINA_FLIES1, ARG_TINA_FLIES1.toString());

        // TODO: compare 2 representations with sorting rules and literals in body...
        //assertEquals("Tina nests", STR_TINA_NESTS, ARG_TINA_NESTS.toString());
    }

    // TODO: test subarguments...
    // TODO: test attack opportunities...
    public void countering() {
        System.out.println("Attack opps Flies(tina): "+ARG_TINA_NOT_FLIES.getAttackOpportunities(DELP_BIRDS));
        System.out.println("Attack opps ~Flies(tina): "+ARG_TINA_FLIES1.getAttackOpportunities(DELP_BIRDS));
        System.out.println("Attack opps Nests_in_trees(tina): "+ARG_TINA_NESTS.getAttackOpportunities(DELP_BIRDS));
    }
}
