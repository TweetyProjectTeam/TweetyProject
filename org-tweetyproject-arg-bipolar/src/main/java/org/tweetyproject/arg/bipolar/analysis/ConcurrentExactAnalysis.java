package org.tweetyproject.arg.bipolar.analysis;


import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.bipolar.inducers.LiExactPEAFInducer;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class implements exact probabilistic justification of a set of queries by generating all possible
 * induces EAFs from a PEAF in parallel.
 *
 * @author Taha Dogan Gunes
 */
public class ConcurrentExactAnalysis extends AbstractAnalysis implements ProbabilisticJustificationAnalysis {
    /**
     * The fixed thread pool to run the contributions in parallel
     */
    private final ExecutorService executorService;

    /**
     * Constructs ConcurrentExactAnalysis with noThreads equal to availableProcessors - 1
     *
     * @param peafTheory        the PEAFTheory to be analyzed
     * @param extensionReasoner the extension reasoner
     */
    public ConcurrentExactAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner) {
        this(peafTheory, extensionReasoner, Runtime.getRuntime().availableProcessors() - 1);
    }

    /**
     * Constructs ConcurrentExactAnalysis with given noThreads
     *
     * @param peafTheory        the PEAFTheory to be analyzed
     * @param extensionReasoner the extension reasoner
     * @param noThreads         the number of threads
     */
    public ConcurrentExactAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner, int noThreads) {
        super(peafTheory, extensionReasoner, AnalysisType.CONCURRENT_EXACT);
        this.executorService = Executors.newFixedThreadPool(noThreads);
    }

    public Double total = new Double(0.0);
    public Double p = new Double(0.0);
    /**
     * Computes exactly what is probabilistic justification of the given set of arguments in the PEAF.
     * <p>
     * Warning: It is intractable when the number of arguments in PEAF is above 15.
     *
     * @param args the set of arguments necessary for the query
     * @return the result of the analysis
     * @see ConcurrentApproxAnalysis for larger PEAFs
     */
    @Override
    public AnalysisResult query(Set<BArgument> args) {
        LiExactPEAFInducer exactPEAFInducer = new LiExactPEAFInducer(this.peafTheory);
        this.total = 0.0;
        this.p = 0.0;
        AtomicLong i = new AtomicLong(0);
        
        

        exactPEAFInducer.induce(iEAF ->
                executorService.submit((Callable<Void>) () -> {
                    double contribution = computeContributionOfAniEAF(args, iEAF);
                    this.total += contribution;
                    this.p += contribution * iEAF.getInducePro();
                    i.incrementAndGet();
                    return null;
                }));

        try {
            executorService.shutdown();
        } finally {
            try {
                //noinspection ResultOfMethodCallIgnored
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return this.createResult(p.doubleValue(), i.get(), total.doubleValue());
    }

}
