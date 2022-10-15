package org.tweetyproject.arg.bipolar.analysis;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.bipolar.inducers.ApproxPEAFInducer;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.Set;

/**
 * This class implements approximate probabilistic justification of a set of queries using Monte Carlo Sampling of
 * induced EAFs from a PEAF.
 * </br>
 * </br>See
 * </br>
 * </br> Li, Hengfei. Probabilistic argumentation. 2015. PhD Thesis. Aberdeen University.
 *
 * @author Taha Dogan Gunes
 */
public class ApproxAnalysis extends AbstractAnalysis implements ProbabilisticJustificationAnalysis {

    /**
     * The error level defines how much the computed result is tolerated for deviation.
     * <p>
     * Example: If errorLevel is 0.1, then the result will be in the range [x - 0.1, x + 0.1].
     */
    private final double errorLevel;

    /**
     * Creates an ApproxAnalysis object
     *
     * @param peafTheory        The PEAFTheory object
     * @param extensionReasoner An extension reasoner object
     * @param errorLevel        the error level in double
     */
    public ApproxAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner, double errorLevel) {
        super(peafTheory, extensionReasoner, AnalysisType.APPROX);
        this.errorLevel = errorLevel;
    }

    /**
     * Computes approximately what is probabilistic justification of the given set of arguments in the PEAF given error
     * level
     * @param <AnalysisResult>
     *
     * @param args the set of arguments necessary for the query
     * @return the result of the analysis
     */
    @Override
    public  AnalysisResult query(Set<BArgument> args) {
        final double[] M = {0.0};
        final double[] N = {0.0};
        final double[] metric = {0.0};
        final double[] p_i = {0.0};
        final long[] i = {0};
        final double[] total = {0.0};

        do {
            ApproxPEAFInducer approxPEAFInducer = new ApproxPEAFInducer(this.peafTheory);
            approxPEAFInducer.induce(iEAF -> {
                double contribution = 0;

                contribution = computeContributionOfAniEAF(args, iEAF);

                total[0] += contribution;
                M[0] = M[0] + contribution;
                N[0] = N[0] + 1.0;
                i[0] += 1;
                p_i[0] = (M[0] + 2) / (N[0] + 4);
                metric[0] = ((4.0 * p_i[0] * (1.0 - p_i[0])) / Math.pow(errorLevel, 2)) - 4.0;
            });
        } while (N[0] <= metric[0]);


        return this.createResult(M[0] / N[0], i[0], total[0]);
    }
}
