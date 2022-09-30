package org.tweetyproject.arg.peaf.inducers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.tweetyproject.arg.peaf.syntax.*;

import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * ExactPEAFInducer generates all possible EAFs that can be generated from a PEAF.
 * Computationally, this implementation is not great since the all variations of EAFs increase by the number of
 * arguments and links exponentially. It is good to use for small PEAFs.
 * <p>
 * FIXME: In some instances, probabilities are found to be more than 1.
 *
 * @author Taha Dogan Gunes
 */
public class ExactPEAFInducer extends AbstractPEAFInducer {

    /**
     * Used internally for debugging the inducer
     * <p>
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

        stack.push(new EAF_F(Sets.newHashSet(), Sets.newHashSet(peafTheory.getSupports().get(0)), Sets.newHashSet(peafTheory.getEta()), 1.0));

        while (!stack.isEmpty()) {
            EAF_F eaf = stack.pop();

            double po = 1.0;

            // line 3, page 80
            // FIXME: This query can be improved by reducing this set at each iteration
            // FIXME: This can be done by storing NAS inside iEAF object

            Set<PSupport> supportsLeft = Sets.newHashSet(peafTheory.getSupports());
            supportsLeft.removeAll(eaf.eSupports);
            Set<EArgument> args = Sets.newHashSet();
            args.addAll(eaf.eArguments);
            args.addAll(eaf.newEArguments);

            for (PSupport support : supportsLeft) {
                if (support.getName().equals("0") && eaf.eArguments.size() == 1) {
                    continue;
                }

                EArgument notIn = null;

                for (EArgument from : support.getFroms()) {
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
            Set<ESupport> expandingSupports = Sets.newHashSet();
            debugPrint(" New arguments: " + eaf.newEArguments);

            for (EArgument newEArgument : eaf.newEArguments) {
                expandingSupports.addAll(newEArgument.getSupports());
            }

            eaf.eArguments.addAll(eaf.newEArguments);
            eaf.newEArguments.clear();

            consumer.accept(eaf.convertToInducible());

            debugPrint(eaf.convertToInducible());

            for (Set<ESupport> eSupports : Sets.powerSet(expandingSupports)) {

                EAF_F eaf_c = eaf.copy();
                double xpi = npi;
                for (ESupport eSupport : eSupports) {
                    eaf_c.eSupports.add(eSupport);

                    // This is to eliminate visiting same nodes again
                    for (EArgument to : eSupport.getTos()) {
                        if (!eaf_c.eArguments.contains(to)) {
                            eaf_c.newEArguments.add(to);
                        }
                    }

                    xpi *= ((PSupport) eSupport).getConditionalProbability();
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
        Set<EArgument> eArguments;
        /**
         * Supports of the EAF
         */
        Set<ESupport> eSupports;
        /**
         * The next arguments to add to the EAF
         */
        Set<EArgument> newEArguments;
        /**
         * Reference to EAFs that originate this EAF
         */
        List<EAF_F> createdFrom = Lists.newArrayList();
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
        public EAF_F(Set<EArgument> eArguments,
                     Set<ESupport> eSupports,
                     Set<EArgument> newEArguments, double pi) {
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
            List<PSupport> supportList = Lists.newArrayList();
            for (ESupport eSupport : eSupports) {
                supportList.add((PSupport) eSupport);
            }


            InducibleEAF inducibleEAF = new InducibleEAF(Sets.newHashSet(eArguments),
                    Sets.newHashSet(supportList),
                    Sets.newHashSet(),
                    Sets.newHashSet(),
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
            EAF_F i = new EAF_F(Sets.newHashSet(this.eArguments), Sets.newHashSet(this.eSupports), Sets.newHashSet(this.newEArguments), this.pi);
            i.createdFrom.add(this);
            return i;
        }
    }

}
