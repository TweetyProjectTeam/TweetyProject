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

import org.tweetyproject.arg.bipolar.syntax.EAFTheory;
import org.tweetyproject.arg.bipolar.syntax.BArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class demonstrates the use of the {@link EAFTheory} class to check for self-supporting sets of arguments.
 * 
 * In this example, an extended argumentation framework (EAF) is created with a set of arguments and supports/attacks between them.
 * The goal is to check whether certain sets of arguments are self-supporting using the {@code checkIsSelfSupporting} method
 * of the {@link EAFTheory} class.
 * 
 * @author Your Name
 */
public class CheckIfSelfSupportingExample {

    /**
     * The entry point of the example program that sets up an {@link EAFTheory} instance,
     * adds arguments, defines supports and attacks, and checks whether various sets of arguments
     * are self-supporting.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        EAFTheory eafTheory = new EAFTheory();
        for (int i = 0; i < 4; i++) {
            eafTheory.addArgument(i);
        }

        // Arguments:
        // eta = 0, a = 1, b = 2, c = 3

        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(1))));
        eafTheory.addSupport(new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(0))), new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(2))));
        eafTheory.addAttack(new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(2))), new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(1))));
        eafTheory.addSupport(new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(1))), new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(3))));

        List<BArgument> eArguments = eafTheory.getArguments();
        Set<BArgument> selfSupported = new HashSet<>();

        // Various checks for self-supporting sets of arguments
        selfSupported.add(eArguments.get(0));
        System.out.println("{eta} is " + eafTheory.checkIsSelfSupporting(selfSupported) + ", but must be true");
        selfSupported.clear();

        selfSupported.add(eArguments.get(2));
        System.out.println("{b} is " + eafTheory.checkIsSelfSupporting(selfSupported) + ", but must be false");
        selfSupported.clear();

        selfSupported.add(eArguments.get(1));
        selfSupported.add(eArguments.get(2));
        System.out.println("{a, b} is " + eafTheory.checkIsSelfSupporting(selfSupported) + ", but must be false");
        selfSupported.clear();

        selfSupported.add(eArguments.get(0));
        selfSupported.add(eArguments.get(1));
        selfSupported.add(eArguments.get(3));
        System.out.println("{eta, a, c} is " + eafTheory.checkIsSelfSupporting(selfSupported) + ", but must be true");
        selfSupported.clear();

        selfSupported.add(eArguments.get(0));
        selfSupported.add(eArguments.get(2));
        System.out.println("{eta, b} is " + eafTheory.checkIsSelfSupporting(selfSupported) + ", but must be true");

        selfSupported.add(eArguments.get(0));
        selfSupported.add(eArguments.get(3));
        System.out.println("{eta, c} is " + eafTheory.checkIsSelfSupporting(selfSupported) + ", but must be false");
    }

    /**
     * Default constructor for the {@code CheckIfSelfSupportingExample} class.
     * Initializes an instance of this class, though it currently has no specific initialization logic.
     */
    public CheckIfSelfSupportingExample() {}
}

