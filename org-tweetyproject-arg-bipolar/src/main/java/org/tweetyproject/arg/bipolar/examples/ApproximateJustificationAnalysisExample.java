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

import org.tweetyproject.arg.bipolar.analysis.AnalysisResult;
import org.tweetyproject.arg.bipolar.analysis.ApproxAnalysis;
import org.tweetyproject.arg.bipolar.analysis.ExactAnalysis;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Demonstrates the use of exact and approximate justification analysis on a PEAFTheory.
 * <p>
 * This example sets up a PEAFTheory with a number of arguments, defines supports and attacks among them,
 * and then performs exact and approximate analyses to justify a specific argument.
 * </p>
 */
public class ApproximateJustificationAnalysisExample {

    /**
     * Example
     * @param args the args
     */
    public static void main(String[] args) {
        int numOfArgs = 7;  // Number of arguments to add to the PEAFTheory
        PEAFTheory peafTheory = new PEAFTheory();

        // Add arguments to the theory
        for (int i = 0; i < numOfArgs; i++) {
            peafTheory.addArgument(i);
        }

        // Define supports among arguments with associated probabilities
        peafTheory.addSupport(
            new HashSet<>(),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(0))),
            1.0
        );
        peafTheory.addSupport(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(0))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(2))),
            0.6
        );
        peafTheory.addSupport(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(0))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(1))),
            0.7
        );
        peafTheory.addSupport(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(0))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(3))),
            0.9
        );
        peafTheory.addSupport(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(0))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(4))),
            0.3
        );
        peafTheory.addSupport(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(3))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(5))),
            0.5
        );

        // Define supports involving multiple arguments
        Set<BArgument> argSet = new HashSet<>();
        argSet.add(peafTheory.getArguments().get(3));
        argSet.add(peafTheory.getArguments().get(4));
        peafTheory.addSupport(
            argSet,
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(6))),
            0.9
        );

        // Define attack relationships among arguments
        peafTheory.addAttack(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(5))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(2)))
        );
        peafTheory.addAttack(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(5))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(1)))
        );
        peafTheory.addAttack(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(1))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(5)))
        );
        peafTheory.addAttack(
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(1))),
            new HashSet<>(Arrays.asList(peafTheory.getArguments().get(6)))
        );

        // Define the query for analysis
        Set<BArgument> query = new HashSet<>();
        query.add(peafTheory.getArguments().get(0));

        // Perform exact analysis
        ExactAnalysis exactAnalysis = new ExactAnalysis(peafTheory, new SimplePreferredReasoner());
        AnalysisResult exactResult = exactAnalysis.query(query);
        exactResult.print();

        // Perform approximate analysis
        ApproxAnalysis approxAnalysis = new ApproxAnalysis(peafTheory, new SimplePreferredReasoner(), 0.1);
        AnalysisResult approxResult = approxAnalysis.query(query);
        approxResult.print();
    }

    /** Default Constructor */
    public ApproximateJustificationAnalysisExample() {}
}

