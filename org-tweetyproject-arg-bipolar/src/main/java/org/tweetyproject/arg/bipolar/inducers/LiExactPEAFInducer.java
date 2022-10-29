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


import org.tweetyproject.arg.bipolar.syntax.ArgumentSet;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.BipolarEntity;
import org.tweetyproject.arg.bipolar.syntax.InducibleEAF;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;
import org.tweetyproject.arg.bipolar.syntax.Support;

import java.util.*;
import java.util.function.Consumer;

/**
 * This is a direct re-implementation of the Hengfei Li's ruby code in Java.
 * </br>
 * </br>See
 * </br>
 * </br> Li, Hengfei. Probabilistic argumentation. 2015. PhD Thesis. Aberdeen University.
 * <p>
 * The comments also have the ruby code equivalent.
 * LiExactPEAFInducer generates all possible EAFs that can be generated from a PEAF.
 * <p>
 * FIXME: This is to be used in @see Runner of the module. It is a great reference implementation.
 *
 * @author Taha Dogan Gunes
 */
@Deprecated
public class LiExactPEAFInducer extends AbstractPEAFInducer {

    /**
     * The default constructor for the inducer
     *
     * @param peafTheory a PEAFObject reference
     */
    @Deprecated
    public LiExactPEAFInducer(PEAFTheory peafTheory) {
        super(peafTheory);
    }

    /**
     * Inducer induces inducibleEAFs and gives to a consumer function
     *
     * @param consumer the function that consumes InducibleEAFs
     */
    @Deprecated
    public void induce(Consumer<InducibleEAF> consumer) {
        InducibleEAF f = new InducibleEAF(new HashSet(),
                new HashSet(),
                new HashSet(),
                new HashSet(), Math.log(1.0), Math.log(1.0));

        // Store inducible that need to expand
        List<InducibleEAF> expansion = new ArrayList<>();
        expansion.add(f);
        int z = 0;

        while (!expansion.isEmpty()) {

            InducibleEAF toExpand = expansion.remove(0);
            // Before accepting explore all the attacks and add these links (traverse all the tree)
            toExpand.addAttackLinks();
            System.out.println(toExpand.arguments.toString());
            consumer.accept(toExpand);


            Map<Set<BArgument>, Map<String, Object>> expandingArgs = expand(toExpand);

            if (expandingArgs.isEmpty()) {
                continue;
            }


            int noCombinations = 1 << expandingArgs.size();

            for (int i = 1; i < noCombinations; i++) {
                List<BArgument> newArgs = new ArrayList<>();
                List<Support> supports = new ArrayList<>();

                double pInside = toExpand.getpInside();
                int n = i;

                for (Map.Entry<Set<BArgument>, Map<String, Object>> entry : expandingArgs.entrySet()) {
                	Set<BArgument> argIds = entry.getKey();
                    Map<String, Object> map = entry.getValue();

                    if ((n & 1) == 1) {
                        newArgs.addAll(argIds);
                        supports.addAll((Collection<? extends Support>) map.get("supports"));
                        pInside += Math.log(1.0 - Math.exp((double) map.get("pro")));
                    }
                    n = n >> 1;
                }
                List<BipolarEntity> args = new ArrayList<>();
                args.addAll(toExpand.getArguments());
                args.addAll(newArgs);
                supports.addAll(toExpand.getSupports());

                double pOutside = Math.log(1.0);

                for (Support sr : this.peafTheory.getSupports()) {
                    if (supports.contains(sr)) {
                        continue;
                    }

                    BArgument notIn = null;

                    for (BArgument fa : sr.getSupporter()) {
                        if (!args.contains(fa)) {
                            notIn = fa;
                            break;
                        }
                    }

                    if (notIn == null) {
                        pOutside += Math.log(1.0 - sr.getConditionalProbability());
//                        System.out.println(Math.exp(pOutside));
                    }
                }

                double induceP = pInside + pOutside;
                InducibleEAF indu = new InducibleEAF(new HashSet(args), new HashSet(supports), new HashSet(), new HashSet(newArgs), pInside, induceP);

                expansion.add(indu);
                z++;
            }

        }

    }

    /**
     * Internal function to expand the InducibleEAF
     *
     * @param indu an InducibleEAF object
     * @return a map of all possible ways that the InducibleEAF can be expanded
     */
    private Map<Set<BArgument>, Map<String, Object>> expand(InducibleEAF indu) {
        Map<Set<BArgument>, Map<String, Object>> expandable = new HashMap<>();

        for (Support sr : this.peafTheory.getSupports()) {

            // next if indu.supports.include?(sr.id)
            if (indu.getSupports().contains(sr)) {
                continue;
            }

            boolean foundNotIn = false;
            boolean foundNewSup = false;


            //  sr.from.each { |fa|
            //        found_not_in = true if !indu.arguments.include?(fa.id)
            //        break if found_not_in
            //        found_new_sup = true if indu.new_args.include?(fa.id)
            //  }
            for (BArgument fa : sr.getSupporter()) {
                if (!indu.getArguments().contains(fa)) {
                    foundNotIn = true;
                    break;
                }
                if (indu.getNewArguments().contains(fa)) {
                    foundNewSup = true;
                }
            }

            //if !found_not_in and (found_new_sup or indu.new_args.empty?)
            //        if expandable.has_key?(sr.to.id)
            //          expandable[sr.to.id][:supports].push(sr.id)
            //          expandable[sr.to.id][:pro] *= (1 - sr.cp)
            //        else
            //          expandable[sr.to.id] = {:supports => [sr.id], :pro => (1 - sr.cp)}
            //        end
            //      end
            if (!foundNotIn && (foundNewSup || indu.getNewArguments().isEmpty())) {
                if (expandable.containsKey(sr.getSupported())) {
                    Map<String, Object> map = expandable.get(sr.getSupported());

                    List<Support> supports = (List<Support>) map.get("supports");
                    supports.add(sr);

                    double probability = (double) map.get("pro");
                    map.replace("pro", probability + Math.log(1.0 - sr.getConditionalProbability()));
                } else {
                    Map<String, Object> map = new HashMap<>();
                    List<Support> supports = new ArrayList<>();
                    supports.add(sr);
                    map.put("supports", supports);
                    map.put("pro", Math.log(1.0 - sr.getConditionalProbability()));
                    expandable.put(  ((ArgumentSet) sr.getSupported()).getArguments(), map);
                }

            }

        }

        return expandable;
    }
}
