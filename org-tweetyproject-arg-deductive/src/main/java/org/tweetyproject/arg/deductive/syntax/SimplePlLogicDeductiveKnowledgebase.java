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
 *  Copyright 2017 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.deductive.syntax;

import java.util.Collection;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.commons.util.rules.Derivation;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 *
 * According to
 * http://www0.cs.ucl.ac.uk/staff/a.hunter/papers/ac13t.pdf
 * a simple logic knowledge base (propositional version only in this implementation) is
 * a set of literals---in this implementation rules with empty body---and a set of
 * simple rules, @see SimplePlRule
 * @author Federico Cerutti (federico.cerutti@acm.org)
 *
 *
 */
public class SimplePlLogicDeductiveKnowledgebase extends BeliefSet<SimplePlRule,PlSignature> {

/**
 * Represents a deductive knowledge base for propositional logic arguments.
 * This class extends a knowledge base and allows for the generation of arguments
 * and their interactions (attacks) following the structure described in the
 * paper "A Comparison of Formal Argumentation Systems" by Hunter and Caminada (2013).
 */
    /**
     * Default constructor for creating an empty deductive knowledge base.
     */
    public SimplePlLogicDeductiveKnowledgebase() {
        super();
    }

    /**
     * Constructor for creating a deductive knowledge base with an initial collection of rules.
     *
     * @param _kb The initial collection of propositional logic rules.
     */
    public SimplePlLogicDeductiveKnowledgebase(Collection<SimplePlRule> _kb) {
        super(_kb);
    }

    /**
     * Retrieves the minimal signature of the knowledge base.
     * This signature is based on the propositional logic rules contained within the knowledge base.
     *
     * @return The minimal propositional logic signature of the knowledge base.
     */
    public Signature getMinimalSignature() {
        PlSignature signature = new PlSignature();
        for (SimplePlRule f : this) {
            signature.addSignature(f.getSignature());
        }
        return signature;
    }

    /**
     * Builds the argumentation framework (AF) by generating arguments and attacks between them
     * based on the knowledge base. The arguments and attacks are formed according to the structure
     * described in the paper "A Comparison of Formal Argumentation Systems" by Hunter and Caminada (2013).
     *
     * This method creates simple undercut and rebuttal attacks between the generated arguments.
     *
     * @return The DungTheory representing the argumentation framework with arguments and attacks.
     */
    public DungTheory getAF() {
        DungTheory af = new DungTheory();

        // Generate arguments from the derivations in the knowledge base
        for (Derivation<SimplePlRule> derivation : Derivation.allDerivations(this)) {
            if (derivation.size() != 1)
                af.add(new SimplePlLogicArgument(derivation));
        }

        // Generate attacks between the arguments
        for (Argument arga : af.getNodes()) {
            for (Argument argb : af.getNodes()) {
                SimplePlLogicArgument larga = (SimplePlLogicArgument) arga;
                SimplePlLogicArgument largb = (SimplePlLogicArgument) argb;

                Sat4jSolver solver = new Sat4jSolver();
                solver.getWitness((PlFormula) (larga.getClaim()).combineWithAnd(largb.getClaim()));

                // Check for rebuttal attack
                if (!solver.isConsistent((PlFormula) (larga.getClaim()).combineWithAnd(largb.getClaim()))) {
                    af.add(new Attack(arga, argb));
                }

                // Check for undercut attack
                for (SimplePlRule r : largb.getSupport()) {
                    for (PlFormula p : r.getPremise()) {
                        if (!solver.isConsistent((PlFormula) (larga.getClaim()).combineWithAnd(p))) {
                            af.add(new Attack(arga, argb));
                        }
                    }
                }
            }
        }

        return af;
    }

    /**
     * Instantiates a new propositional logic signature for the knowledge base.
     * This method is called when initializing the signature of the knowledge base.
     *
     * @return A new instance of a propositional logic signature.
     */
    @Override
    protected PlSignature instantiateSignature() {
        return new PlSignature();
    }
}



