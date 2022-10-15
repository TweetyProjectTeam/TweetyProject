package org.tweetyproject.arg.bipolar.analysis;


import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.bipolar.inducers.LiExactPEAFInducer;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.Set;

/**
 * This class implements exact probabilistic justification of a set of queries by generating all possible
 * induces EAfs from a PEAF.
 * </br>
 * </br>See
 * </br>
 * </br> Li, Hengfei. Probabilistic argumentation. 2015. PhD Thesis. Aberdeen University.
 *
 * @author Taha Dogan Gunes
 */
public class ExactAnalysis extends AbstractAnalysis implements ProbabilisticJustificationAnalysis {

    /**
     * Creates an ExactAnalysis object
     *
     * @param peafTheory        The PEAFTheory object
     * @param extensionReasoner An extension reasoner object
     */
    public ExactAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner) {
        super(peafTheory, extensionReasoner, AnalysisType.EXACT);
    }

    /**
     * Computes exactly what is probabilistic justification of the given set of arguments in the PEAF.
     * <p>
     * Warning: It is intractable when the number of arguments in PEAF is above 12.
     *
     * @param args the set of arguments necessary for the query
     * @return the result of the analysis
     * @see ApproxAnalysis for larger PEAFs
     */
    @Override
    public AnalysisResult query(Set<BArgument> args) {

        LiExactPEAFInducer exactPEAFInducer = new LiExactPEAFInducer(this.peafTheory);

        final double[] p = {0.0};
        final double[] total = {0.0};
        final long[] i = {0};


        exactPEAFInducer.induce(iEAF -> {
            double contribution = 0;

            contribution = computeContributionOfAniEAF(args, iEAF);

//            if (iEAF.getInducePro() > 0.0 && contribution > 0.0) {
//                System.out.println(iEAF + " c:" +contribution);
//            }


            p[0] += contribution * iEAF.getInducePro();
            total[0] += iEAF.getInducePro();
            i[0] += 1;

        });
//        System.out.println("Total is: "  + total[0]);

        return this.createResult(p[0], i[0], total[0]);
    }
}
