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

package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.reasoner.ExtensionRankingReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.ExtensionRankingSemantics;
import org.tweetyproject.arg.dung.syntax.*;


import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Example on how to use the OrderingSemanticsReasoner.
 *
 * @author Lars Bengel
 */
public class ExtensionRankingExample {
	/**
	 * Shows extension ranks of a theory for the respective semantic
	 * @param args arguments
	 * @throws NoSuchMethodException Exception
	 * @throws IllegalAccessException Exception
	 * @throws InvocationTargetException Exception
	 */
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

//        Extension debug = new Extension();
//        debug.add(new Argument("A1")); debug.add(new Argument("A3")); debug.add(new Argument("A4")); debug.add(new Argument("A5")); debug.add(new Argument("A6")); debug.add(new Argument("A7"));

        DungTheory figure0 = new DungTheory();
        Argument a = new Argument("1");
        Argument b = new Argument("2");
        Argument c = new Argument("3");
        figure0.add(a);
        figure0.add(b);
        figure0.add(c);
        figure0.addAttack(a, b);
        figure0.addAttack(b, c);
    //theory based on Fig.1 from "Ranking Extensions in Abstract Argumentation" - Skiba et al., 2021
        DungTheory figure1 = new DungTheory();
        Argument a1 = new Argument("1");
        Argument a2 = new Argument("2");
        Argument a3 = new Argument("3");
        Argument a4 = new Argument("4");
        Argument a5 = new Argument("5");
        Argument a6 = new Argument("6");
        Argument a7 = new Argument("7");
        figure1.add(a1);
        figure1.add(a2);
        figure1.add(a3);
        figure1.add(a4);
        figure1.add(a5);
        figure1.add(a6);
        figure1.add(a7);
        figure1.addAttack(a1,a2);
        figure1.addAttack(a2,a3);
        figure1.addAttack(a3,a4);
        figure1.addAttack(a3,a5);
        figure1.addAttack(a3,a6);
        figure1.addAttack(a4,a3);
        figure1.addAttack(a4,a6);
        figure1.addAttack(a5,a5);
        figure1.addAttack(a6,a7);
        figure1.addAttack(a7,a6);
    //theory based on Fig.2 from "Ranking Extensions in Abstract Argumentation" - Skiba et al., 2021
        DungTheory figure2 = new DungTheory();
        figure2.add(a1);
        figure2.add(a2);
        figure2.add(a3);
        figure2.add(a4);
        figure2.add(a5);
        figure2.add(a6);
        figure2.addAttack(a1,a2);
        figure2.addAttack(a2,a3);
        figure2.addAttack(a3,a4);
        figure2.addAttack(a4,a5);
        figure2.addAttack(a5,a6);

        DungTheory theory = figure1;



        ExtensionRankingReasoner cf_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_CF);
        System.out.println();
        List<List<Extension<DungTheory>>> cf_result = cf_reasoner.getModels(theory);
        System.out.println("r-cf ranks:");
        for (List<Extension<DungTheory>> level: cf_result) {
            System.out.println(level);
        }
        Extension<DungTheory> ext1 = new Extension<DungTheory>();




        System.out.println();
        ExtensionRankingReasoner ad_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_AD);
        System.out.println();
        List<List<Extension<DungTheory>>> ad_result = ad_reasoner.getModels(theory);
        System.out.println("r-ad ranks:");
        for (List<Extension<DungTheory>> level: ad_result) {
            System.out.println(level);
        }
        System.out.println();


        ExtensionRankingReasoner co_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_CO);
        List<List<Extension<DungTheory>>> co_result = co_reasoner.getModels(theory);
        System.out.println("r-co ranks:");
        for (List<Extension<DungTheory>> level: co_result) {
            System.out.println(level);
        }
        System.out.println();

        ExtensionRankingReasoner gr_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_GR);
        List<List<Extension<DungTheory>>> gr_result = gr_reasoner.getModels(theory);
        System.out.println("r-gr ranks:");
        for (List<Extension<DungTheory>> level: gr_result) {
            System.out.println(level);
        }
        System.out.println();

        ExtensionRankingReasoner pr_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_PR);
        List<List<Extension<DungTheory>>> pr_result = pr_reasoner.getModels(theory);
        System.out.println("r-pr ranks:");
        for (List<Extension<DungTheory>> level: pr_result) {
            System.out.println(level);
        }
        System.out.println();

        ExtensionRankingReasoner sst_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_SST);
        List<List<Extension<DungTheory>>> sst_result = sst_reasoner.getModels(theory);
        System.out.println("r-sst ranks:");
        for (List<Extension<DungTheory>> level: sst_result) {
            System.out.println(level);
        }

                System.out.println();


//        System.out.println();
//        ExtensionRankingReasoner BF_debug = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_SST);
//        System.out.println();
//        List<List<Extension>> BF_result = BF_debug.getModels(theory);
//        System.out.println("sst ranks basefuncs:");
//        List<List<Extension>> temp = new ArrayList<>();
//        for (List<Extension> level: BF_result) {
//            System.out.println();
//            for (Extension arguments : level) {
//                System.out.print(arguments + "|  ");
//                System.out.print("CF: " + BF_debug.getConflicts(arguments, theory));
//                System.out.print("| UD: " + BF_debug.getUndefended(arguments, theory));
//                System.out.print("| DN: " + BF_debug.getDefendedNotIn(arguments, theory));
//                System.out.println("| UA: " + BF_debug.getUnattacked(arguments, theory));
//
//            }
//
//        }

    }

}
