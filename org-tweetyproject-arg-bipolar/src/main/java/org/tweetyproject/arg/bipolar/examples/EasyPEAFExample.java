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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.tweetyproject.arg.bipolar.inducers.LiExactPEAFInducer;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;
/**
 * Provides an example of constructing a Probabilistic Argumentation Framework (PEAFTheory), adding support relationships,
 * and inducing extensions using a specific inducer.
 * <p>
 * This example demonstrates the creation of a PEAFTheory instance with two arguments, setting up support relationships
 * with specified probabilities, and then inducing extensions of the framework using the {@link LiExactPEAFInducer}.
 */
public class EasyPEAFExample {

    /**
     * The entry point of the example program. Constructs a {@link PEAFTheory} instance, adds arguments, defines support
     * relationships with associated probabilities, prints the framework, and induces extensions using the
     * {@link LiExactPEAFInducer}.
     * <p>
     * The following steps are performed in the main method:
     * <ol>
     *  <li>Create an instance of {@link PEAFTheory}.</li>
     *  <li>Add two arguments to the framework.</li>
     *  <li>Define support relationships between the arguments with specified probabilities.</li>
     *  <li>Print the framework in a human-readable format using {@link PEAFTheory#prettyPrint()}.</li>
     *  <li>Induce extensions of the framework using {@link LiExactPEAFInducer} and print each induced extension.</li>
     * </ol>
     *
     * @param args command-line arguments (not used in this example)
     */
    public static void main(String[] args) {
        PEAFTheory peafTheory = new PEAFTheory();
        peafTheory.addArgument(0);
        peafTheory.addArgument(1);


        peafTheory.addSupport(new HashSet<BArgument>(), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), 1.0);
        peafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(1)))), 0.3);


        peafTheory.prettyPrint();
        LiExactPEAFInducer inducer = new LiExactPEAFInducer(peafTheory);

        inducer.induce(ind -> {
            System.out.println(ind);


        });
    }

    /** Default Constructor */
    public EasyPEAFExample(){}
}
