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

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.ArgumentUtils;
import org.tweetyproject.arg.dung.reasoner.ExtensionRankingReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.ExtensionRankingSemantics;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.commons.util.SetTools;


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
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
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

        DungTheory figure3= new DungTheory();
        figure3.add(a);
        figure3.add(b);
        figure3.add(c);
        Argument d = new Argument("d");
        figure3.add(d);
        figure3.addAttack(a,b);
        figure3.addAttack(b,c);
        figure3.addAttack(c,d);
        figure3.addAttack(d,c);

        final DungTheory theory = figure3;

        //see Base Function results for all theory subsets/extensions
        printBFResults(theory);

        //view ranking for reasoner of specific semantic
        ExtensionRankingReasoner cf_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_CF);
        printModels(cf_reasoner,theory);
        printModels(cf_reasoner,theory,true);


        ExtensionRankingReasoner ad_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_AD);
        printModels(ad_reasoner,theory);
        printModels(ad_reasoner,theory, true);

        ExtensionRankingReasoner co_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_CO);
        printModels(co_reasoner,theory);
        printModels(co_reasoner,theory,true);

        ExtensionRankingReasoner gr_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_GR);
        printModels(gr_reasoner,theory);
        printModels(gr_reasoner,theory, true);

        ExtensionRankingReasoner pr_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_PR);
        printModels(pr_reasoner,theory);
        printModels(pr_reasoner,theory,true);

        ExtensionRankingReasoner sst_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_SST);
        printModels(sst_reasoner,theory);
        printModels(sst_reasoner,theory,true);

        ExtensionRankingReasoner co_pr_reasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_CO_PR);
        printModels(co_pr_reasoner,theory);
        printModels(co_pr_reasoner,theory, true);

    }

    /**
     * Print table of rankings of specified reasoner
     * @param rankingReasoner a ranking reasoner for specific semantic
     * @param theory a dung theory
     * @param cardinalityMode if true, extensions are ranked by cardinality of BF results rather than subset relations
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public static void printModels(ExtensionRankingReasoner rankingReasoner, DungTheory theory, boolean cardinalityMode) throws InvocationTargetException, IllegalAccessException {
        System.out.println();
        List<List<Extension<DungTheory>>> result = rankingReasoner.getModels(theory, cardinalityMode);
        System.out.println("Cardinality Mode: " + cardinalityMode);
        System.out.println(rankingReasoner.getSemantics().toString() +" ranks:");
        int size = result.size()-1;
        for (List<Extension<DungTheory>> level: result) {
            System.out.println(size-- + ": " + level);
        }
        System.out.println();
    }

    /**
     * Print table of rankings of specified reasoner. Default behaviour (Cardinality Mode: OFF)
     * @param rankingReasoner a ranking reasoner for specific semantic
     * @param theory a dung theory
     * @throws InvocationTargetException should never happen
     * @throws IllegalAccessException should never happen
     */
    public static void printModels(ExtensionRankingReasoner rankingReasoner, DungTheory theory) throws InvocationTargetException, IllegalAccessException{
        printModels(rankingReasoner, theory, false);
    }

    /**
     * Print all extensions of theory with their respective Base Function results.
     * @param theory a dung theory
     * @throws NoSuchMethodException should never happen
     */
    public static void printBFResults(DungTheory theory) throws NoSuchMethodException {
        ExtensionRankingReasoner rankingReasoner = new ExtensionRankingReasoner(ExtensionRankingSemantics.R_CF);
        System.out.println();
        System.out.println("Base Function results:");
        Set<Set<Argument>> subsets = new SetTools<Argument>().subsets(theory);
        List<Extension<DungTheory>> extensions = new LinkedList<>();
        for(Set<Argument> set : subsets){
            Extension<DungTheory> ext = new Extension<>(set);
            extensions.add(ext);
        }

        for(Extension<DungTheory> extension : extensions){
            System.out.print(extension + ": ");
            System.out.print("CF = " + rankingReasoner.getConflicts(extension, theory));
            System.out.print(" | UD = " + rankingReasoner.getUndefended(extension, theory));
            System.out.print(" | DN = " + rankingReasoner.getDefendedNotIn(extension, theory));
            System.out.println(" | UA = " + rankingReasoner.getUnattacked(extension, theory));
        }
        System.out.println();
    }

}
