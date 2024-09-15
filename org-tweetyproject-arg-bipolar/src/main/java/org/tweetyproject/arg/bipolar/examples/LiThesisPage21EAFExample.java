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

import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.bipolar.syntax.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * LiThesisPage21EAFExample class
 */
public class LiThesisPage21EAFExample {

    /**
     * Example
     * @param args the args
     */
    public static void main(String[] args) {
        EAFTheory eafTheory = new EAFTheory();
        for(int i = 0; i< 9; i++) {
        	eafTheory.addArgument(i);
        }

        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(1)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(3)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(0)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(4)))));
        HashSet<BArgument> arg3And4 = new HashSet<BArgument>();
        arg3And4.add(eafTheory.getArguments().get(3));
        arg3And4.add(eafTheory.getArguments().get(4));
        eafTheory.addSupport(arg3And4, new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(7)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(1)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(5)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(5)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(8)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))));
        eafTheory.addSupport(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(8)))));

        eafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(2)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(5)))));
        eafTheory.addAttack(new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(6)))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(eafTheory.getArguments().get(3)))));


        DungTheory dungTheory = eafTheory.convertToDAFNaively();

        System.out.println("EAF Theory Pretty Print:");
        eafTheory.prettyPrint();

        System.out.println("DAF Theory Pretty Print:");
        System.out.println(dungTheory.prettyPrint());

        System.out.println("SimplePreferredReasoner:");
        SimplePreferredReasoner reasoner1 = new SimplePreferredReasoner();
        for (Extension model : reasoner1.getModels(dungTheory)) {
            System.out.println(model);
        }
    }
}
