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
package org.tweetyproject.arg.bipolar.inducers;

import org.tweetyproject.arg.bipolar.syntax.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * ApproxPEAFInducer induces a set of random EAFs from a PEAF
 *
 * @author Taha Dogan Gunes
 */
public class ApproxPEAFInducer extends AbstractPEAFInducer {
    /**
     * Internal intermediate data structure to expand an EAF in the inducer
     *
     * @see ExactPEAFInducer.EAF_F
     */
    class EAF_F {
        /**
         * Arguments of the EAF
         */
        Set<BArgument> eArguments;
        /**
         * Supports of the EAF
         */
        Set<Support> eSupports;
        /**
         * The next arguments to add to the EAF
         */
        Set<BArgument> newEArguments;

        /**
         * @param eArguments
         * @param eSupports
         * @param newEArguments
         */
        public EAF_F(Set<BArgument> eArguments,
                     Set<Support> eSupports,
                     Set<BArgument> newEArguments) {
            this.eArguments = eArguments;
            this.eSupports = eSupports;
            this.newEArguments = newEArguments;
        }

        /**
         * For compatibility this re-implementation maps to InducibleEAF
         *
         * @return an InducibleEAF
         * @see InducibleEAF
         */
        public InducibleEAF convertToInducible() {
            List<Support> supportList = new ArrayList<Support>();
            for (Support eSupport : eSupports) {
                supportList.add((Support) eSupport);
            }
            HashSet<BArgument> args = new HashSet<>();
            HashSet<List<Support>> lists = new HashSet<List<Support>>();
            args.addAll(eArguments);
            lists.add(supportList);

            InducibleEAF inducibleEAF = new InducibleEAF(args,
                    new HashSet<>(supportList),
                    new HashSet<>(),
                    new HashSet<>(),
                    0, 0);

            inducibleEAF.addAttackLinks();

            return inducibleEAF;
        }

        /**
         * Makes a direct copy of this EAF_F
         *
         * @return a copied EAF_F
         */
        public EAF_F copy() {
            return new EAF_F(this.eArguments, this.eSupports, this.newEArguments);
        }
    }

    /**
     * The default constructor for the ApproxPEAFInducer
     *
     * @param peafTheory a theory
     */
    public ApproxPEAFInducer(PEAFTheory peafTheory) {
        super(peafTheory);
    }

    /**
     * Inducer induces inducibleEAFs and gives to a consumer function
     *
     * @param consumer the function that consumes InducibleEAFs
     */
    @Override
    public void induce(Consumer<InducibleEAF> consumer) {
        Stack<EAF_F> stack = new Stack<>();

        // eta is added, Algorithm 8 Line 2 EAF_F <- {eta}, {}, {}
        stack.push(new EAF_F(new HashSet<>(), new HashSet<Support>(new ArrayList<Support>(Arrays.asList(peafTheory.getSupports().iterator().next()))), new HashSet<BArgument>(new ArrayList<BArgument>(Arrays.asList(peafTheory.getArguments().get(0))))));

        // Turn recursive random induce to sequential
        while (!stack.isEmpty()) {
            EAF_F eaf = stack.pop();

            // compute expanding supports (ES)
            // if ES.isEmpty
            //     return eaf;
            Set<Support> expandingSupports = new HashSet<Support>();
            for (BArgument newArgument : eaf.newEArguments) {
                // These new arguments have these supports
//                System.out.println(newEArgument.getSupports());
                expandingSupports.addAll(this.peafTheory.getSupports(newArgument));
            }
            // these are cleared such that new ones can be added
            //       System.out.println("Add new arguments: " + eaf.newEArguments);
            eaf.eArguments.addAll(eaf.newEArguments);
            eaf.newEArguments.clear();

            if (expandingSupports.isEmpty()) {
                consumer.accept(eaf.convertToInducible());
                return;
            }

            // Line 6-7
            EAF_F eaf_c = eaf.copy();

            // Line 8-14
            for (Support eSupport : expandingSupports) {
                double r = ThreadLocalRandom.current().nextDouble();
                if (r <= ((SetSupport) eSupport).getConditionalProbability()) {
                    eaf_c.eSupports.add(eSupport);

                    // This is to eliminate visiting same nodes again
                    for (BArgument to : eSupport.getSupported()) {
                        if (!eaf_c.eArguments.contains(to)) {
                            eaf_c.newEArguments.add(to);
                        }
                    }
                }
            }

            // Line 15
            // This is omitted since attacks are not supported.

            stack.push(eaf_c);
        }
    }
}
