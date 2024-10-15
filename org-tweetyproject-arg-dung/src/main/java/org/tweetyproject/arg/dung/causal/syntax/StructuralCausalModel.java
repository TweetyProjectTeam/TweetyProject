/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.causal.syntax;

import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.logics.pl.syntax.*;

import java.util.*;

/**
 * This class describes a structural causal model in the sense of Pearl.
 *
 * @see "Judea Pearl, 'Causality: models, reasoning and inference', 2000"
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class StructuralCausalModel implements BeliefBase, Collection<PlFormula> {
    private Collection<Proposition> backgroundAtoms;
    private Map<Proposition, PlFormula> explainableAtoms;

    /**
     * Initializes an empty causal model
     */
    public StructuralCausalModel() {
        backgroundAtoms = new HashSet<>();
        explainableAtoms = new HashMap<>();
    }

    /**
     * Initializes a causal model based on the given structural equations.
     * Every structural equation must be a logical equivalence of the form: {@code e <=> f(...)}
     * where 'e' is a literal and f(...) is some logical formula representing the 'cause' of 'e'.
     * The equations must be non-cyclic.
     *
     * @param equations the set of structural equations
     * @throws CyclicDependencyException iff the equations are cyclic
     */
    public StructuralCausalModel(Collection<PlFormula> equations) throws CyclicDependencyException {
        this();
        Map<PlFormula, PlFormula> formulas = new HashMap<>();
        Collection<Proposition> explainable = new HashSet<>();
        Collection<Proposition> atoms = new HashSet<>();
        for (PlFormula formula : equations) {
            if (formula instanceof Equivalence) {
                Equivalence equation = (Equivalence) formula;
                if (!equation.getFormulas().getFirst().isLiteral()) {
                    throw new IllegalArgumentException("Left side of Structural Equation must be literal!");
                }
                explainable.addAll(equation.getFormulas().getFirst().getAtoms());
                atoms.addAll(equation.getAtoms());
                formulas.put(equation.getFormulas().getFirst(), equation.getFormulas().getSecond());
            } else {
                throw new IllegalArgumentException("Structural Equations must be Equivalences");
            }
        }
        atoms.removeAll(explainable);
        this.addBackgroundAtoms(atoms);
        while (!formulas.isEmpty()) {
            boolean changed = false;
            for (PlFormula lit : formulas.keySet()) {
                try {
                    changed |= this.addExplainableAtom(lit, formulas.get(lit));
                    formulas.remove(lit);
                } catch (CyclicDependencyException ignored) {
                }
            }
            if (!changed) throw new CyclicDependencyException("The given set contains formulas with cyclic dependencies.");
        }
    }

    /**
     * Returns the set of background atoms of the causal model
     * @return the set of background atoms
     */
    public Collection<Proposition> getBackgroundAtoms() {
        return new HashSet<>(backgroundAtoms);
    }

    /**
     * Returns the set of explainable atoms of the causal model
     * @return the set of background atoms
     */
    public Collection<Proposition> getExplainableAtoms() {
        return new HashSet<>(explainableAtoms.keySet());
    }

    /**
     * Returns the set of all atoms of the causal model
     * @return the set of all atoms
     */
    public Collection<Proposition> getAtoms() {
        Collection<Proposition> result = new HashSet<>(getBackgroundAtoms());
        result.addAll(getExplainableAtoms());
        return result;
    }

    /**
     * Returns the structural equations of the causal model
     * @return the structural equations
     */
    public Collection<PlFormula> getStructuralEquations() {
        Collection<PlFormula> result = new HashSet<>();
        for (Proposition atom : explainableAtoms.keySet()) {
            result.add(new Equivalence(atom, explainableAtoms.get(atom)));
        }
        return result;
    }

    /**
     * Get the cause of the given atom
     * @param atom some atom of the causal model
     * @return the cause of the atom
     */
    public PlFormula getCause(Proposition atom) {
        return explainableAtoms.get(atom);
    }

    /**
     * Adds a background atom to the model
     * @param atom some new background atom
     * @return true iff added successfully
     */
    public boolean addBackgroundAtom(Proposition atom) {
        return backgroundAtoms.add(atom);
    }

    /**
     * Add new background atoms to the model
     * @param atoms a set of new background atoms
     * @return true iff added successfully
     */
    public boolean addBackgroundAtoms(Collection<Proposition> atoms) {
        return backgroundAtoms.addAll(atoms);
    }

    /**
     * Adds a new explainable atom together with its cause to the causal model
     * @param atom  some new explainable atom
     * @param cause the cause of the new atom
     * @return true iff added successfully
     * @throws CyclicDependencyException iff the new equation introduces a cyclic dependency
     */
    public boolean addExplainableAtom(PlFormula atom, PlFormula cause) throws CyclicDependencyException {
        if (!atom.isLiteral() || atom instanceof Negation) throw new IllegalArgumentException("Atom must be a proposition");
        Proposition prop = (Proposition) atom;
        if (explainableAtoms.containsKey(prop)) throw new IllegalArgumentException("Atom already exists in the model");
        if (cause.getAtoms().contains(prop)) throw new CyclicDependencyException("Causal relation cannot be cyclic. 'cause' must not contain variable.");

        if (!getAtoms().containsAll(cause.getAtoms())) throw new CyclicDependencyException("Cause contains atoms that are not yet part of the causal model.");

        PlFormula ret = explainableAtoms.put(prop, cause);
        return !cause.equals(ret);
    }

    /**
     * Constructs the twin model for this causal model, i.e., it creates for each structural equation a copy where all
     * explainable atoms X are replaced by a twin version X*
     *
     * @return the twin model
     */
    public StructuralCausalModel getTwinModel() {
        Collection<PlFormula> structuralEquations = this.getStructuralEquations();

        StructuralCausalModel model = null;
        // copy the model
        try {
            model = new StructuralCausalModel(structuralEquations);
        } catch (CyclicDependencyException ignored) {
        }
        assert model != null;

        // create twin copy for each structural equation
        Map<PlFormula, PlFormula> twinEquations = new HashMap<>();
        for (PlFormula equation : structuralEquations) {
            PlFormula atom = ((Equivalence) equation).getFormulas().getFirst();
            PlFormula cause = ((Equivalence) equation).getFormulas().getSecond();
            if (atom instanceof Proposition) {
                Proposition twinAtom = new Proposition(((Proposition) atom).getName() + "*");
                PlFormula twinCause = cause.clone();
                for (Proposition prop : cause.getAtoms()) {
                    if (model.getBackgroundAtoms().contains(prop)) continue;
                    twinCause = twinCause.replace(prop, new Proposition(((Proposition) atom).getName() + "*"), cause.numberOfOccurrences(prop));
                }
                twinEquations.put(twinAtom, twinCause);
            } else {
                throw new UnsupportedOperationException("Unsupported Structural Equation Type");
            }
        }

        // add twin equations
        while (!twinEquations.isEmpty()) {
            for (PlFormula lit : twinEquations.keySet()) {
                try {
                    this.addExplainableAtom(lit, twinEquations.get(lit));
                    twinEquations.remove(lit);
                } catch (CyclicDependencyException ignored) {
                }
            }
        }

        return model;
    }

    /**
     * Performs an intervention on the explainable atom 'v' by setting it to a given truth value.
     * Removes the original cause of the atom and thus permanently changes the causal model
     * @param v some explainable atom
     * @param x Truth value of the intervention
     */
    public StructuralCausalModel intervene(Proposition v, boolean x) {
        if(!this.explainableAtoms.containsKey(v)){
            throw new IllegalArgumentException("The specified variable has to be an explainable atom of the causal model");
        }

        StructuralCausalModel newModel = this.clone();
        if (x) {
            newModel.explainableAtoms.put(v, new Tautology());
        } else {
            newModel.explainableAtoms.put(v, new Contradiction());
        }
        return newModel;
    }

    @Override
    public StructuralCausalModel clone() {
        try {
            return new StructuralCausalModel(this.getStructuralEquations());
        } catch (CyclicDependencyException ignored) {
            // will never happen
        }
        return null;
    }

    @Override
    public Signature getMinimalSignature() {
        PlSignature sig = new PlSignature(getBackgroundAtoms());
        sig.addAll(getExplainableAtoms());
        return sig;

    }

    @Override
    public int size() {
        return getExplainableAtoms().size();
    }

    @Override
    public boolean isEmpty() {
        return getExplainableAtoms().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Proposition) {
            return getBackgroundAtoms().contains(o) || getExplainableAtoms().contains(o);
        } else if (o instanceof PlFormula) {
            return explainableAtoms.containsValue(o) || getStructuralEquations().contains(o);
        }
        return false;
    }

    @Override
    public Iterator<PlFormula> iterator() {
        return getStructuralEquations().iterator();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean add(PlFormula plFormula) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean result = true;
        for (Object o : c) {
            result &= contains(o);
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends PlFormula> c) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    public static class CyclicDependencyException extends Throwable {
        public CyclicDependencyException(String string) {
        }
    }
}
