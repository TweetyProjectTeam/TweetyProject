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

import org.tweetyproject.arg.bipolar.io.EdgeListWriter;
import org.tweetyproject.arg.bipolar.syntax.*;

/**
 * Provides an example of constructing an Argumentation Framework (EAFTheory) and saving its representation to a file.
 * <p>
 * This example demonstrates the creation of an argumentation framework with eight arguments, setting up various support
 * and attack relationships between these arguments, and finally writing the resulting framework to a file.
 */
public class EAFExample {

    /**
     * The entry point of the example program. Constructs an {@link EAFTheory} instance, adds arguments, support, and attack
     * relationships, and writes the framework to a file.
     * <p>
     * The following steps are performed in the main method:
     * <ol>
     *  <li>Create an instance of {@link EAFTheory}.</li>
     *  <li>Add eight arguments to the framework.</li>
     *  <li>Define support relationships between various sets of arguments.</li>
     *  <li>Define attack relationships between specific arguments.</li>
     *  <li>Print the string representation of the framework to the console.</li>
     *  <li>Write the framework to a file named "eaf.txt" using {@link EdgeListWriter}.</li>
     * </ol>
     *
     * @param _args command-line arguments (not used in this example)
     */
    public static void main(String[] _args) {
        int numOfArgs = 8;

        EAFTheory eafTheory = new EAFTheory();

        for (int i = 0; i < numOfArgs; i++) {
            eafTheory.addArgument(i);
        }

        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(1)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(3)))));
        HashSet<BArgument> args1 = new HashSet<BArgument>();
        args1.add(eafTheory.getArguments().get(1));
        args1.add(eafTheory.getArguments().get(3));
        eafTheory.addSupport(args1, new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(3)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(5)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(7)))));
        HashSet<BArgument> args2 = new HashSet<BArgument>();
        args2.add(eafTheory.getArguments().get(5));
        args2.add(eafTheory.getArguments().get(7));
        eafTheory.addSupport(args2, new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(4)))));

        eafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(5)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(4)))));
        eafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))));

        System.out.println(eafTheory.toString());

        EdgeListWriter.write("eaf.txt", eafTheory);
    }

    /**
     * Default constructor for the {@code EAFExample} class.
     * Initializes an instance of this class. This constructor does not perform any specific initialization.
     */
    public EAFExample() {}
}

