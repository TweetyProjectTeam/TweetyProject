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

public class CheckIfSelfSupportingExample {

    public static void main(String[] args) {
        EAFTheory eafTheory = new EAFTheory();
        for (int i = 0; i<4; i++) {
        	eafTheory.addArgument(i);
        }

        // eta = 0
        // a = 1
        // b = 2
        // c = 3

        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(1))));
        eafTheory.addSupport(new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(0))), new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(2))));
        eafTheory.addAttack(new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(2))), new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(1))));
        eafTheory.addSupport(new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(1))), new HashSet<BArgument>(Arrays.asList(eafTheory.getArguments().get(3))));


        List<BArgument> eArguments = eafTheory.getArguments();

        Set<BArgument> selfSupported = new HashSet<>();
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

    /** Default Constructor */
    public CheckIfSelfSupportingExample(){}
}
