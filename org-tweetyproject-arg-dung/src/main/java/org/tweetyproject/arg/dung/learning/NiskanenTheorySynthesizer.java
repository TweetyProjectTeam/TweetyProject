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

package org.tweetyproject.arg.dung.learning;

import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.MaxSatSolver;
import org.tweetyproject.logics.pl.sat.OpenWboSolver;
import org.tweetyproject.logics.pl.syntax.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Implementation of the MaxSAT algorithm from:
 * Niskanen, Andreas, Johannes Wallner, and Matti Järvisalo. "Synthesizing argumentation frameworks from examples." Journal of Artificial Intelligence Research 66 (2019)
 *
 * @author Lars Bengel
 */
public class NiskanenTheorySynthesizer {

    private MaxSatSolver maxSatSolver;
    private Collection<Argument> arguments;
    private PlParser parser;
    private Method getClause;

    /**
     *
     * @param args a set of arguments
     * @param semantics a semantics
     * @param solverLocation path to the open wbo solver binary
     * @throws NoSuchMethodException if the given semantics is not implemented
     */
    public NiskanenTheorySynthesizer(Collection<Argument> args, Semantics semantics, String solverLocation) throws NoSuchMethodException {
        this.arguments = args;
        this.parser = new PlParser();
        this.maxSatSolver = new OpenWboSolver(solverLocation);

        switch (semantics) {
            case ST:
                this.getClause = NiskanenTheorySynthesizer.class.getDeclaredMethod("getStableClause", DungTheory.class, Extension.class);
            case CF:
                this.getClause = NiskanenTheorySynthesizer.class.getDeclaredMethod("getConflictFreeClause", DungTheory.class, Extension.class);
            case CO:
                this.getClause = NiskanenTheorySynthesizer.class.getDeclaredMethod("getCompleteClause", DungTheory.class, Extension.class);
            case ADM:
                this.getClause = NiskanenTheorySynthesizer.class.getDeclaredMethod("getAdmissibleClause", DungTheory.class, Extension.class);
            default:
                throw new IllegalArgumentException("semantics not implemented.");
        }

    }

    /**
     * learns an argumentation framework fulfilling as many positive examples as possible while trying to not fulfill any negative examples
     * @param positiveExamples a map of positive examples and their weights
     * @param negativeExamples a map of negative examples and their weights
     * @return a argumentation framework inferred from the given examples
     * @throws IOException if an error occurs
     * @throws InvocationTargetException if an error occurs
     * @throws IllegalAccessException if an error occurs
     */
    public DungTheory learnExamples(Map<Extension<DungTheory>, Integer> positiveExamples, Map<Extension<DungTheory>, Integer> negativeExamples) throws IOException, InvocationTargetException, IllegalAccessException {
        DungTheory theory = new DungTheory();
        theory.addAll(this.arguments);
        PlBeliefSet hardClauses = new PlBeliefSet();
        Map<PlFormula, Integer> softClauses = new HashMap<>();

        // create hard and soft clauses for each positive example
        int nr = 0;
        for (Extension<DungTheory> ext: positiveExamples.keySet()) {
            PlFormula ext_prop = new Proposition("E_" + nr);
            // create hard clause
            Object[] parameters = new Object[2];
            parameters[0] = theory;
            parameters[1] = ext;
            PlFormula ext_formula = new Equivalence(ext_prop, (PlFormula) this.getClause.invoke(this, parameters));

            //create soft clause
            softClauses.put(ext_prop, positiveExamples.get(ext));
            nr++;
            hardClauses.add(ext_formula);
        }

        // create hard and soft clauses for each negative example
        for (Extension<DungTheory> ext: negativeExamples.keySet()) {
            PlFormula ext_prop = new Proposition("E_" + nr);
            // create hard clause
            Object[] parameters = new Object[2];
            parameters[0] = theory;
            parameters[1] = ext;
            PlFormula ext_formula = new Equivalence(ext_prop, (PlFormula) this.getClause.invoke(this, parameters));

            //create soft clause
            softClauses.put(new Negation(ext_prop), negativeExamples.get(ext));
            nr++;
            hardClauses.add(ext_formula);
        }


        // compute result with open wbo solver
        Interpretation<PlBeliefSet, PlFormula> result = this.maxSatSolver.getWitness(hardClauses, softClauses);
        if (result == null) {
            return null;
        }

        // parse result into argumentation framework
        for (Argument a: this.arguments) {
            for (Argument b: this.arguments) {
                Proposition prop = new Proposition("r_" + a.getName() + "_" + b.getName());
                if (result.satisfies(prop)) {
                    theory.addAttack(a, b);
                }
            }

        }
        return theory;
    }

    /**
     * create the formula encoding the conflict-free restriction on the extension
     * @param theory a dung theory
     * @param ext an extension
     * @return a formula encoding the conflict-freeness of the extension
     * @throws IOException if an input/output operation fails
     */
    private PlFormula getConflictFreeClause(DungTheory theory, Extension<DungTheory> ext) throws IOException {
        Collection<PlFormula> formulas = new HashSet<>();
        for (Argument a: ext) {
            for (Argument b: ext) {
                formulas.add(this.parser.parseFormula("!r_" + a.getName() + "_" + b.getName()));
            }
        }
        return new Conjunction(formulas);
    }

    /**
     * encode admissibility of the extension into a formula
     * @param theory a dung theory
     * @param ext an extension
     * @return formula representing the admissibility of ext
     * @throws IOException if an input/output operation fails
     */
    private PlFormula getAdmissibleClause(DungTheory theory, Extension<DungTheory> ext) throws IOException {
        Collection<PlFormula> formulas = new HashSet<>();
        formulas.add(this.getConflictFreeClause(theory, ext));
        for (Argument a: ext) {
            for (Argument b: theory) {
                if (!ext.contains(b)) {
                    Collection<PlFormula> subFormulas = new HashSet<>();
                    for (Argument c: ext) {
                        subFormulas.add(this.parser.parseFormula("r_" + c.getName() + "_" + b.getName()));
                    }
                    PlFormula subFormula = new Implication(this.parser.parseFormula("r_" + b.getName() + "_" + a.getName()), new Disjunction(subFormulas));
                    formulas.add(subFormula);
                }
            }
        }
        return new Conjunction(formulas);
    }

    /**
     * encode stability of the extension into a formula
     * @param theory a dung theory
     * @param ext an extension
     * @return formula representing the stability of ext
     * @throws IOException if an input/output operation fails
     */
    @SuppressWarnings("unused")
    private PlFormula getStableClause(DungTheory theory, Extension<DungTheory> ext) throws IOException {
        Collection<PlFormula> formulas = new HashSet<>();
        formulas.add(this.getConflictFreeClause(theory, ext));
        for (Argument a: theory) {
            if (!ext.contains(a)) {
                Collection<PlFormula> subFormulas = new HashSet<>();
                for (Argument b : ext) {
                    subFormulas.add(this.parser.parseFormula("r_" + b.getName() + "_" + a.getName()));
                }

                formulas.add(new Disjunction(subFormulas));
            }
        }
        return new Conjunction(formulas);
    }

    /**
     * encode completeness of the extension into a formula
     * @param theory a dung theory
     * @param ext an extension
     * @return formula representing the completeness of ext
     * @throws IOException if an input/output operation fails
     */
    @SuppressWarnings("unused")
    private PlFormula getCompleteClause(DungTheory theory, Extension<DungTheory> ext) throws IOException {
        Collection<PlFormula> formulas = new HashSet<>();
        formulas.add(this.getAdmissibleClause(theory, ext));
        for (Argument a: theory) {
            if (!ext.contains(a)) {
                Collection<PlFormula> subFormulas = new HashSet<>();
                for (Argument b : theory) {
                    Collection<PlFormula> subSubFormulas = new HashSet<>();
                    subSubFormulas.add(this.parser.parseFormula("r_" + b.getName() + "_" + a.getName()));
                    for (Argument c: ext) {
                        subSubFormulas.add(this.parser.parseFormula("!r_" + c.getName() + "_" + b.getName()));
                    }
                    subFormulas.add(new Conjunction(subSubFormulas));
                }
                formulas.add(new Disjunction(subFormulas));
            }
        }
        return new Conjunction(formulas);
    }
}
