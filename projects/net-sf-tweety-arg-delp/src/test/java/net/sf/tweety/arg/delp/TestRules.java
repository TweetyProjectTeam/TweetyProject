package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.syntax.DelpFact;
import net.sf.tweety.arg.delp.syntax.StrictRule;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import org.junit.Test;

import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for rules: facts, strict and defeasible rules.
 *
 * @author Linda.Briesemeister
 */
public class TestRules {

    /**
     * Compare DeLP literals as they arise in rules, i.e.,
     *   ~B < A          (negation is always smaller)
     *   A < B           (atoms = predicates are sorted by predicate name first... )
     *   A(Y) < A(X,Z)   (... then arity, ...)
     *   A(X,Z) < A(Y,Z) (... then names of arguments)
     */
    private Comparable<FolFormula> compareLiterals = new Comparable<FolFormula>() {
        @Override
        public int compareTo(FolFormula o) {
            // TODO: implement!
            return 0;
        }
    };

    @Test
    public void factInstantiation() {
        DelpFact fact;
        String factAsString;

        factAsString = "Scared(tina).";
        fact = new DelpFact(new FOLAtom(new Predicate("Scared", 1), Stream
                        .of(new Constant("tina"))
                        .collect(Collectors.toList())));
        assertEquals("Fact representation matches", factAsString, fact.toString());

        factAsString = "!Scared(tweety).";
        fact = new DelpFact(new Negation(new FOLAtom(new Predicate("Scared", 1), Stream
                .of(new Constant("tweety"))
                .collect(Collectors.toList()))));
        assertEquals("Fact representation matches", factAsString, fact.toString());

        // TODO: fix cloning of DeLP facts!
        //assertEquals("Cloning works", fact.toString(), fact.clone().toString());
    }

    @Test
    public void strictRules() {
        StrictRule strict;
        String ruleAsString;
        SortedSet<FolFormula> premiseSorted;

        ruleAsString = "!Has_a_gun(X) <- Pacifist(X).";
        strict = new StrictRule(
                new Negation(new FOLAtom(new Predicate("Has_a_gun", 1), Stream
                        .of(new Variable("X"))
                        .collect(Collectors.toList()))),
                Stream
                        .of(new FOLAtom(new Predicate("Pacifist", 1), Stream
                                .of(new Variable("X"))
                                .collect(Collectors.toList())))
                        .collect(Collectors.toSet()));
        assertTrue("Strict rule head matches", ruleAsString.startsWith(strict.getConclusion().toString()));
        // TODO: compare string representation of sorted premises
//        premiseSorted = strict.getPremise()
//                .stream()
//                .sorted()
//                .collect(Collectors.toSet());

        ruleAsString = "Has_a_gun(X) <- Lives_in_chicago(Y),Republican(X).";
        strict = new StrictRule(
                new FOLAtom(new Predicate("Has_a_gun", 1), Stream
                        .of(new Variable("X"))
                        .collect(Collectors.toList())),
                Stream
                        .of(
                                new FOLAtom(new Predicate("Lives_in_chicago", 1), Stream
                                        .of(new Variable("Y"))
                                        .collect(Collectors.toList())),
                                new FOLAtom(new Predicate("Republican", 1), Stream
                                        .of(new Variable("X"))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toSet()));
        assertTrue("Strict rule head matches", ruleAsString.startsWith(strict.getConclusion().toString()));
        // TODO: compare string representation of sorted premises
    }

//    @Test
//    public void defeasibleRules() {
//        DefeasibleRule rule;
//        String ruleAsString;
//
//        ruleAsString = "!Has_a_gun(X) -< Lives_in_chicago(X),Pacifist(X).";
//        rule = new DefeasibleRule(
//                new Negation(new FOLAtom(new Predicate("Has_a_gun", 1), Stream
//                        .of(new Variable("X"))
//                        .collect(Collectors.toList()))),
//                Stream
//                        .of(
//                                new FOLAtom(new Predicate("Lives_in_chicago", 1), Stream
//                                        .of(new Variable("X"))
//                                        .collect(Collectors.toList())),
//                                new FOLAtom(new Predicate("Pacifist", 1), Stream
//                                        .of(new Variable("X"))
//                                        .collect(Collectors.toList())))
//                        .collect(Collectors.toSet()));
//        assertEquals("Strict rule represenation matches", ruleAsString, rule.toString());
//    "!Nests_in_trees(X) -< !Flies(X)."
//    }
}
