package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.syntax.DefeasibleRule;
import net.sf.tweety.arg.delp.syntax.DelpFact;
import net.sf.tweety.arg.delp.syntax.StrictRule;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.Negation;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for rules: facts, strict and defeasible rules.
 *
 * @author Linda.Briesemeister
 */
public final class TestRules {

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
        String premiseSorted;

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
        // compare string representation of SINGLE premise with rule string:
        assertTrue("Strict rule premise matches", ruleAsString.endsWith(
                strict.getPremise()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","))+"."));

        ruleAsString = "Has_a_gun(X) <- Lives_in_chicago(Y),Republican(X).";
        strict = new StrictRule(
                new FOLAtom(new Predicate("Has_a_gun", 1), Stream
                        .of(new Variable("X"))
                        .collect(Collectors.toList())),
                Stream
                        .of(
                                new FOLAtom(new Predicate("Republican", 1), Stream
                                        .of(new Variable("X"))
                                        .collect(Collectors.toList())),
                                new FOLAtom(new Predicate("Lives_in_chicago", 1), Stream
                                        .of(new Variable("Y"))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toSet()));
        assertTrue("Strict rule head matches", ruleAsString.startsWith(strict.getConclusion().toString()));
        // compare string representation of sorted premises with rule string:
        premiseSorted = strict.getPremise()
                .stream()
                .sorted(Utilities.compareLiterals)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        assertTrue("Strict rule premise matches", ruleAsString.endsWith(premiseSorted+"."));
    }

    @Test
    public void defeasibleRules() {
        DefeasibleRule rule;
        String ruleAsString;
        String premiseSorted;

        ruleAsString = "!Has_a_gun(X) -< Lives_in_chicago(X),Pacifist(X).";
        rule = new DefeasibleRule(
                new Negation(new FOLAtom(new Predicate("Has_a_gun", 1), Stream
                        .of(new Variable("X"))
                        .collect(Collectors.toList()))),
                Stream
                        .of(
                                new FOLAtom(new Predicate("Lives_in_chicago", 1), Stream
                                        .of(new Variable("X"))
                                        .collect(Collectors.toList())),
                                new FOLAtom(new Predicate("Pacifist", 1), Stream
                                        .of(new Variable("X"))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toSet()));
        assertTrue("Defeasible rule head matches", ruleAsString.startsWith(rule.getConclusion().toString()));
        // compare string representation of sorted premises with rule string:
        premiseSorted = rule.getPremise()
                .stream()
                .sorted(Utilities.compareLiterals)
                .map(Object::toString)
                .collect(Collectors.joining(","));
        assertTrue("Defeasible rule premise matches", ruleAsString.endsWith(premiseSorted+"."));

        ruleAsString = "!Nests_in_trees(X) -< !Flies(X).";
        rule = new DefeasibleRule(
                new Negation(new FOLAtom(new Predicate("Nests_in_trees", 1), Stream
                        .of(new Variable("X"))
                        .collect(Collectors.toList()))),
                Stream
                        .of(new Negation(
                                new FOLAtom(new Predicate("Flies", 1), Stream
                                        .of(new Variable("X"))
                                        .collect(Collectors.toList()))))
                        .collect(Collectors.toSet()));
        assertTrue("Defeasible rule head matches", ruleAsString.startsWith(rule.getConclusion().toString()));
        // compare string representation of SINGLE premise with rule string:
        assertTrue("Defeasible rule premise matches", ruleAsString.endsWith(
                rule.getPremise()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","))+"."));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void strictNotClonable() {
        StrictRule strict = new StrictRule(
                new Negation(new FOLAtom(new Predicate("Has_a_gun", 1), Stream
                        .of(new Variable("X"))
                        .collect(Collectors.toList()))),
                Stream
                        .of(new FOLAtom(new Predicate("Pacifist", 1), Stream
                                .of(new Variable("X"))
                                .collect(Collectors.toList())))
                        .collect(Collectors.toSet()));
        strict.clone();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void defeasibleNotClonable() {
        DefeasibleRule rule = new DefeasibleRule(
                new Negation(new FOLAtom(new Predicate("Nests_in_trees", 1), Stream
                        .of(new Variable("X"))
                        .collect(Collectors.toList()))),
                Stream
                        .of(new Negation(
                                new FOLAtom(new Predicate("Flies", 1), Stream
                                        .of(new Variable("X"))
                                        .collect(Collectors.toList()))))
                        .collect(Collectors.toSet()));
        rule.clone();
    }
}
