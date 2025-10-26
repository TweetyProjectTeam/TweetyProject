package org.tweetyproject.arg.deductive.util;

import org.tweetyproject.arg.deductive.syntax.DeductiveKnowledgeBase;
import org.tweetyproject.commons.BeliefSetIterator;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.*;

/**
 * Generates random Deductive Knowledge Bases.
 * Will only generate binary formulas.
 *
 * @author Lars Bengel
 */
public class RandomDeductiveKnowledgeBaseGenerator implements BeliefSetIterator<PlFormula, DeductiveKnowledgeBase> {

    private static final Random random = new Random();
    private final int numberOfAtoms;
    private final int numberOfFacts;
    private final int numberOfFormulas;
    private final String[] argumentNames = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};

    /**
     * Creates a random Deductive Knowledge Base Generator
     * @param numberOfAtoms the number of atoms
     * @param numberOfFacts the number of facts to generate
     * @param numberOfFormulas the number of binary formulas to generate
     */
    public RandomDeductiveKnowledgeBaseGenerator(int numberOfAtoms, int numberOfFacts, int numberOfFormulas) {
        this.numberOfAtoms = numberOfAtoms;
        this.numberOfFacts = numberOfFacts;
        this.numberOfFormulas = numberOfFormulas;
    }


    /** Description missing */
    @Override
    public boolean hasNext() {
        return true;
    }
    /** Description missing */
    @Override
    public DeductiveKnowledgeBase next() {
        DeductiveKnowledgeBase kb = new DeductiveKnowledgeBase();
        List<Proposition> idToAtom = new ArrayList<>();
        Map<Proposition, Integer> atomToId = new HashMap<>();
        for (int i = 0; i < this.numberOfAtoms; i++) {
            Proposition p = new Proposition(argumentNames[i]);
            idToAtom.add(p);
            atomToId.put(p, i);
        }

        // add random facts
        int numAdded = 0;
        while (true) {
            if (numAdded == this.numberOfFacts) break;

            Proposition p = randomChoice(idToAtom);
            PlFormula formula = random.nextBoolean() ? p : new Negation(p);
            if (!kb.contains(formula)) {
                kb.add(formula);
                numAdded++;
            }
        }

        // add random formulas
        numAdded = 0;
        while (true) {
            if (numAdded == this.numberOfFormulas) break;
            Proposition p1 = randomChoice(idToAtom);
            Proposition p2;
            do {
                p2 = randomChoice(idToAtom);
            } while (p1.equals(p2));

            PlFormula f1 = random.nextBoolean() ? p1 : new Negation(p1);
            PlFormula f2 = random.nextBoolean() ? p2 : new Negation(p2);

            int old_size = kb.size();
            switch (random.nextInt(8)) {
                case 0,1 -> kb.add(new Conjunction(f1, f2));
                case 2,3 -> kb.add(new Disjunction(f1, f2));
                case 4,5,6 -> kb.add(new Implication(f1, f2));
                case 7 -> kb.add(new Equivalence(f1, f2));
            }
            if (old_size != kb.size()) numAdded++;
        }

        return kb;
    }

    /**
     * Description missing
     * @param <C> Description missing
     * @param c Description missing
     * @return Description missing
     * @throws IndexOutOfBoundsException Description missing
     */
    private static <C> C randomChoice(Collection<C> c) throws IndexOutOfBoundsException {
        try {
            int num = random.nextInt(c.size());
            for(C e: c) if (--num < 0) return e;
        } catch (IllegalArgumentException e) {
            throw new IndexOutOfBoundsException(String.format("Collection of size %s not permitted", c.size()));
        }
        throw new RuntimeException("Should not happen");
    }
}
