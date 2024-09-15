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
package org.tweetyproject.logics.mln.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.*;
import org.tweetyproject.logics.mln.analysis.*;
import org.tweetyproject.logics.mln.reasoner.SimpleMlnReasoner;
import org.tweetyproject.logics.mln.syntax.MarkovLogicNetwork;
import org.tweetyproject.logics.mln.syntax.MlnFormula;
import org.tweetyproject.math.func.MaxAggregator;
import org.tweetyproject.math.norm.AggregatingNorm;
import org.tweetyproject.math.probability.Probability;

/**
 * Provides examples of constructing Markov Logic Networks (MLNs) programmatically
 * and demonstrates how to use coherence measures.
 * <p>
 * This class includes various examples, such as the "Nixon" scenarios, to show
 * how to define MLNs and apply coherence measures using the TweetyProject libraries.
 * </p>
 *
 * @author Matthias Thimm
 */
public class MlnExample2 {

    /**
     * Default constructor for the {@code MlnExample2} class.
     * <p>
     * Initializes an instance of the MLN example class to demonstrate
     * the construction and analysis of Markov Logic Networks.
     * </p>
     */
    public MlnExample2() {
        // No specific initialization required.
    }

    /**
     * Constructs the Nixon1 MLN example, which represents a scenario with Quakers,
     * Republicans, and Pacifists with certain probabilities.
     *
     * @return a Pair containing the Markov Logic Network and its FolSignature
     * @throws ParserException if there is an error during parsing
     * @throws IOException if there is an I/O error
     */
    public static Pair<MarkovLogicNetwork, FolSignature> Nixon1() throws ParserException, IOException {
        Predicate quaker = new Predicate("quaker", 1);
        Predicate republican = new Predicate("republican", 1);
        Predicate pacifist = new Predicate("pacifist", 1);

        FolSignature sig = new FolSignature();
        sig.add(quaker);
        sig.add(republican);
        sig.add(pacifist);

        sig.add(new Constant("d1"));

        FolParser parser = new FolParser();
        parser.setSignature(sig);

        MarkovLogicNetwork mln = new MarkovLogicNetwork();

        mln.add(new MlnFormula((FolFormula) parser.parseFormula("!quaker(X) || pacifist(X)"), new Probability(0.95)));
        mln.add(new MlnFormula((FolFormula) parser.parseFormula("!republican(X) || !pacifist(X)"), new Probability(0.95)));

        return new Pair<>(mln, sig);
    }

    /**
     * Constructs the Nixon2 MLN example, which represents Nixon being both a Quaker,
     * Republican, and President.
     *
     * @return a Pair containing the Markov Logic Network and its FolSignature
     * @throws ParserException if there is an error during parsing
     * @throws IOException if there is an I/O error
     */
    public static Pair<MarkovLogicNetwork, FolSignature> Nixon2() throws ParserException, IOException {
        Predicate quaker = new Predicate("quaker", 1);
        Predicate republican = new Predicate("republican", 1);
        Predicate president = new Predicate("president", 1);

        FolSignature sig = new FolSignature();
        sig.add(quaker);
        sig.add(republican);
        sig.add(president);

        sig.add(new Constant("nixon"));

        FolParser parser = new FolParser();
        parser.setSignature(sig);

        MarkovLogicNetwork mln = new MarkovLogicNetwork();

        // Nixon is a Quaker, Republican, and President (strict formula)
        mln.add(new MlnFormula((FolFormula) parser.parseFormula("quaker(nixon) && republican(nixon) && president(nixon)"))); // p = 1

        return new Pair<>(mln, sig);
    }

    /**
     * Constructs the Nixon3 MLN example, which represents Reagan as an actor and
     * president, with certain probabilities on roles.
     *
     * @return a Pair containing the Markov Logic Network and its FolSignature
     * @throws ParserException if there is an error during parsing
     * @throws IOException if there is an I/O error
     */
    public static Pair<MarkovLogicNetwork, FolSignature> Nixon3() throws ParserException, IOException {
        Predicate actor = new Predicate("actor", 1);
        Predicate president = new Predicate("president", 1);

        FolSignature sig = new FolSignature();
        sig.add(actor);
        sig.add(president);

        sig.add(new Constant("reagan"));

        FolParser parser = new FolParser();
        parser.setSignature(sig);

        MarkovLogicNetwork mln = new MarkovLogicNetwork();

        mln.add(new MlnFormula((FolFormula) parser.parseFormula("!president(X) || actor(X)"), new Probability(0.9)));
        mln.add(new MlnFormula((FolFormula) parser.parseFormula("president(reagan) && actor(reagan)"))); // p = 1

        return new Pair<>(mln, sig);
    }

    /**
     * Main method to demonstrate the usage of coherence measures on different Nixon MLN examples.
     * <p>
     * This method computes and prints the coherence value of several MLN examples using a MaxAggregator
     * based coherence measure. It also demonstrates the merging of MLNs and computing the coherence of
     * the merged network.
     * </p>
     *
     * @param args command-line arguments (not used in this example)
     * @throws ParserException if there is an error during parsing
     * @throws IOException if there is an I/O error
     */
    public static void main(String[] args) throws ParserException, IOException {
        AggregatingCoherenceMeasure measure = new AggregatingCoherenceMeasure(new AggregatingNorm(new MaxAggregator()), new MaxAggregator());

        // Nixon example 1: Quakers, Republicans, Pacifists
        Pair<MarkovLogicNetwork, FolSignature> ex1 = Nixon1();
        // Nixon example 2: Nixon as Quaker, Republican, President
        Pair<MarkovLogicNetwork, FolSignature> ex2 = Nixon2();
        // Nixon example 3: Reagan as actor and president
        Pair<MarkovLogicNetwork, FolSignature> ex3 = Nixon3();

        // Running coherence measures on individual examples
        MarkovLogicNetwork mln1 = ex1.getFirst();
        FolSignature sig1 = ex1.getSecond();
        MarkovLogicNetwork mln2 = ex2.getFirst();
        FolSignature sig2 = ex2.getSecond();
        MarkovLogicNetwork mln3 = ex3.getFirst();
        FolSignature sig3 = ex3.getSecond();

        SimpleMlnReasoner reasoner = new SimpleMlnReasoner();
        reasoner.setTempDirectory("/Users/mthimm/Desktop/tmp");

        // Output coherence values for each individual example
        System.out.println("#1: Measure " + measure.toString() + ", coherence value " + measure.coherence(mln1, reasoner, sig1));
        System.out.println("#2: Measure " + measure.toString() + ", coherence value " + measure.coherence(mln2, reasoner, sig2));
        System.out.println("#3: Measure " + measure.toString() + ", coherence value " + measure.coherence(mln3, reasoner, sig3));
        System.out.println();

        // Merging examples 1 and 2 and running coherence measure on the merged network
        MarkovLogicNetwork mergedMln = new MarkovLogicNetwork();
        mergedMln.addAll(mln1);
        mergedMln.addAll(mln2);

        FolSignature mergedSig = new FolSignature();
        mergedSig.addSignature(sig1);
        mergedSig.addSignature(sig2);

        System.out.println("Merged: Measure " + measure.toString() + ", coherence value " + measure.coherence(mergedMln, reasoner, mergedSig));
    }
}
