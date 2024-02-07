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
import java.util.function.Consumer;

/**
 * ExactPEAFInducer generates all possible EAFs that can be generated from a PEAF.
 * Computationally, this implementation is not great since the all variations of EAFs increase by the number of
 * arguments and links exponentially. It is good to use for small PEAFs.
 * FIXME: In some instances, probabilities are found to be more than 1.
 *
 * @author Taha Dogan Gunes
 */
public class ExactPEAFInducer extends AbstractPEAFInducer {

    /**
     * Used internally for debugging the inducer
     * TODO: Could be removed and migrated to a proper logging module.
     *
     * @param message a string
     */
    private void debugPrint(Object message) {
        boolean INTERNAL_DEBUG_MESSAGES = false;
        if (INTERNAL_DEBUG_MESSAGES) {
            System.out.println(message);
        }
    }

    /**
     * Inducer induces inducibleEAFs and gives to a consumer function
     *
     * @param consumer the function that consumes InducibleEAFs
     */
    @Override
    public void induce(Consumer<InducibleEAF> consumer) {
        Stack<EAF_F> stack = new Stack<>();

        stack.push(new EAF_F(new HashSet<>(), new HashSet<Support>(peafTheory.getSupports()), new HashSet<>(new ArrayList<>(Arrays.asList(peafTheory.getEta()))), 1.0));

        while (!stack.isEmpty()) {
            EAF_F eaf = stack.pop();

            double po = 1.0;

            // line 3, page 80
            // FIXME: This query can be improved by reducing this set at each iteration
            // FIXME: This can be done by storing NAS inside iEAF object

            Set<Support> supportsLeft = new HashSet<Support>(peafTheory.getSupports());
            supportsLeft.removeAll(eaf.eSupports);
            Set<BArgument> args = new HashSet<BArgument>();
            args.addAll(eaf.eArguments);
            args.addAll(eaf.newEArguments);

            for (Support support : supportsLeft) {
                if ( eaf.eArguments.size() == 1) {
                    continue;
                }

                BArgument notIn = null;

                for (BArgument from : support.getSupporter()) {
                    // R_S - R_S^F
                    if (!args.contains(from)) {
                        notIn = from;
                        break;
                    }
                }

                if (notIn == null) {
                    po *= (1.0 - support.getConditionalProbability());
                }

                debugPrint(support);
                debugPrint("Not in: " + notIn);
                debugPrint("EAF Args: " + eaf.eArguments);
                debugPrint("po is: " + po);
            }

            debugPrint("po: " + po);
            double npi = eaf.pi;

            debugPrint("eaf pi (before): " + eaf.pi);
            eaf.pi = eaf.pi * po;

            debugPrint("eaf pi (after): " + eaf.pi);
            Set<Support> expandingSupports = new HashSet<Support>();
            debugPrint(" New arguments: " + eaf.newEArguments);

            for (BArgument newEArgument : eaf.newEArguments) {
                expandingSupports.addAll(this.peafTheory.getSupports(newEArgument));
            }

            eaf.eArguments.addAll(eaf.newEArguments);
            eaf.newEArguments.clear();

            consumer.accept(eaf.convertToInducible());

            debugPrint(eaf.convertToInducible());

            for (Set<Support> eSupports : this.peafTheory.powerSet(expandingSupports)) {

                EAF_F eaf_c = eaf.copy();
                double xpi = npi;
                for (Support eSupport : eSupports) {
                    eaf_c.eSupports.add(eSupport);

                    // This is to eliminate visiting same nodes again
                    for (BArgument to : eSupport.getSupported()) {
                        if (!eaf_c.eArguments.contains(to)) {
                            eaf_c.newEArguments.add(to);
                        }
                    }

                    xpi *= ((Support) eSupport).getConditionalProbability();
                }

                if (!eSupports.isEmpty()) {
                    eaf_c.pi = xpi;
                    debugPrint(eSupports);
                    stack.push(eaf_c);

                }
            }

        }
    }

    /**
     * The default constructor for the ExactPEAFInducer
     *
     * @param peafTheory
     */
    public ExactPEAFInducer(PEAFTheory peafTheory) {
        super(peafTheory);
    }

    /**
     * Simpler re-implementation of InducibleEAF (It is a temporary data structure that helps to expand an induced
     * EAF.
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
         * Reference to EAFs that originate this EAF
         */
        List<EAF_F> createdFrom = new ArrayList<EAF_F>();
        /**
         * The result value of the EAF (notation from the thesis)
         */
        double pi;

        /**
         * The default constructor for the EAF_F
         *
         * @param eArguments    the set of arguments
         * @param eSupports     the set of supports
         * @param newEArguments the new arguments after this EAF
         * @param pi            the result value
         */
        public EAF_F(Set<BArgument> eArguments,
                     Set<Support> eSupports,
                     Set<BArgument> newEArguments, double pi) {
            this.eArguments = eArguments;
            this.eSupports = eSupports;
            this.newEArguments = newEArguments;
            this.pi = pi;
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


            InducibleEAF inducibleEAF = new InducibleEAF(new HashSet<>(eArguments),
                    new HashSet<>(supportList),
                    new HashSet<>(),
                    new HashSet<>(),
                    0, Math.log(this.pi));


            inducibleEAF.addAttackLinks();

            return inducibleEAF;
        }

        /**
         * Makes a direct copy of this EAF_F
         *
         * @return a copied EAF_F
         */
        public EAF_F copy() {
            EAF_F i = new EAF_F(new HashSet<>(this.eArguments), new HashSet<>(this.eSupports), new HashSet<>(this.newEArguments), this.pi);
            i.createdFrom.add(this);
            return i;
        }
    }

}
