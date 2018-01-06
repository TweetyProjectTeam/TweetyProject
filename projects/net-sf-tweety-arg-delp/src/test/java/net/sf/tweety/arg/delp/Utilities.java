/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
 package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.syntax.DelpRule;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;

/**
 * Utility functions for test classes to access KBs etc.
 *
 * @author Linda.Briesemeister
 */
class Utilities {

    /**
     * Get knowledge base as a String from the named test resource.
     * @param resourceName name of the text file with the KB
     * @return knowledge base as a String
     * @throws IOException if text file cannot be read
     */
    static String getKB(String resourceName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Utilities.class.getResourceAsStream(resourceName)));
        StringBuilder bufferKB = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            bufferKB.append(line+"\n");
        }
        return bufferKB.toString();
    }

    /**
     * Compare DeLP literals as they arise in rules, i.e.,
     *   ~B < A          (negation is always smaller)
     *   A < B           (atoms = predicates are sorted by predicate name first... )
     *   A(Y) < A(X,Z)   (... then arity, ...)
     *   A(Z,X) < A(Z,Y) (... then names of arguments)
     */
    static Comparator<FolFormula> compareLiterals = (Comparator<FolFormula>) (fol1, fol2) -> {
        if (fol1 instanceof Negation && !(fol2 instanceof Negation)) return -1;
        if (fol2 instanceof Negation && !(fol1 instanceof Negation)) return 1;
        FOLAtom atom1, atom2;
        atom1 = (FOLAtom) fol1.getAtoms().iterator().next();
        atom2 = (FOLAtom) fol2.getAtoms().iterator().next();
        int result = atom1.getPredicate().getName().compareTo(atom2.getPredicate().getName());
        if (result != 0) return result; // predicate names differ
        // predicate names are equal: look at arity
        result = Integer.compare(atom1.getPredicate().getArity(),atom2.getPredicate().getArity());
        if (result != 0) return result; // arity differs
        // arity is the same: look at arguments
        assert atom1.isComplete();
        assert atom2.isComplete();
        assert atom1.getArguments().size() == atom2.getArguments().size();
        for (int i=0; i < atom1.getArguments().size(); i++) {
            result = atom1.getArguments().get(i).get().toString().compareTo(
                    atom2.getArguments().get(i).get().toString());
            if (result != 0) return result; //argument name differs
        }
        return 0;
    };

    /**
     * Compare DeLP rules:
     *   1) compare heads (conclusion) as literals
     *   2) if the same, then
     *     a) DelpFact < StrictRule < DefeasibleRule
     *     b) within same subclass, compare set of literals...
     */
    static Comparator<DelpRule> compareRules = (Comparator<DelpRule>) (rule1, rule2) -> {
        // TODO: implement!
        return 0;
    };

}
