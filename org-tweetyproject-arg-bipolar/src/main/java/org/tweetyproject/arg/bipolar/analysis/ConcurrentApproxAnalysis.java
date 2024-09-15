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
package org.tweetyproject.arg.bipolar.analysis;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.bipolar.inducers.ApproxPEAFInducer;
import org.tweetyproject.arg.bipolar.syntax.BArgument;
import org.tweetyproject.arg.bipolar.syntax.PEAFTheory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class implements approximate probabilistic justification of a set of queries using Monte Carlo Sampling of
 * induced EAFs from a PEAF. The computation is done in batches, the main thread checks the condition of when to stop
 * after each batch.
 *
 * @author Taha Dogan Gunes
 */
public class ConcurrentApproxAnalysis extends AbstractAnalysis implements ProbabilisticJustificationAnalysis {
    /**
     * The error level defines how much the computed result is tolerated for deviation.
     * <p>
     * Example: If errorLevel is 0.1, then the result will be in the range [x - 0.1, x + 0.1].
     */
    private final double errorLevel;
    /**
     * The fixed thread pool to run the contributions in parallel
     */
    private final ExecutorService executorService;
    /**
     * The number of jobs to be completed for checking when to stop.
     * This is to reduce the effect of one thread stalling all the batch
     * (increasing too much would create unnecessary iterations)
     */
    private final int batchSize;

    /**
     * Constructs ConcurrentApproxAnalysis with noThreads equal to availableProcessors - 1
     *
     * @param peafTheory        the PEAFTheory to be analyzed
     * @param extensionReasoner the extension reasoner
     * @param errorLevel        the error level in double
     */
    public ConcurrentApproxAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner, double errorLevel) {
        this(peafTheory, extensionReasoner, errorLevel, Runtime.getRuntime().availableProcessors() - 1);
    }

    /**
     * Constructs ConcurrentApproxAnalysis with batchSize equal to noThreads*2
     *
     * @param peafTheory        the PEAFTheory to be analyzed
     * @param extensionReasoner the extension reasoner
     * @param errorLevel        the error level in double
     * @param noThreads         the number of threads
     */
    public ConcurrentApproxAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner, double errorLevel, int noThreads) {
        this(peafTheory, extensionReasoner, errorLevel, noThreads, noThreads * 2);
    }

    /**
     * The default constructor for ConcurrentApproxAnalysis
     *
     * @param peafTheory        the PEAFTheory to be analyzed
     * @param extensionReasoner the extension reasoner
     * @param errorLevel        the error level in double
     * @param noThreads         the number of threads
     * @param batchSize         the number jobs to be completed for checking when to stop
     */
    public ConcurrentApproxAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner, double errorLevel, int noThreads, int batchSize) {
        super(peafTheory, extensionReasoner, AnalysisType.CONCURRENT_APPROX);
        this.errorLevel = errorLevel;
        this.executorService = Executors.newFixedThreadPool(noThreads);
        this.batchSize = batchSize;
    }
    /** Total */
    public double total = 0.0;
    /**
     * Computes approximately what is probabilistic justification of the given set of arguments in the PEAF given error
     * level concurrently.
     *
     * @param args the set of arguments necessary for the query
     * @return an AnalysisResult object
     */
    @Override
    public AnalysisResult query(Set<BArgument> args) {

        // tests for cyclic
        new ApproxPEAFInducer(peafTheory);

        final double[] M = {0.0};
        final double[] N = {0.0};
        final double[] metric = {0.0};
        final double[] p_i = {0.0};
        final long[] i = {0};
        this.total = 0.0;
        AtomicInteger poolAvailability = new AtomicInteger(this.batchSize);
        List<Future<Double>> futures = new ArrayList<Future<Double>>();


        do {
            // Submit a group of threads
            if (poolAvailability.get() > 0) {
                Future<Double> future = executorService.submit(() -> {
                    poolAvailability.decrementAndGet();
                    double[] contribution = {0.0};
                    ApproxPEAFInducer approxPEAFInducer = new ApproxPEAFInducer(peafTheory);
                    approxPEAFInducer.induce(iEAF -> {
                        contribution[0] = computeContributionOfAniEAF(args, iEAF);

                        this.total = contribution[0];
                    });
                    return contribution[0];
                });
                futures.add(future);
            }

            // Make sure all the batch is completed.
            ListIterator<Future<Double>> iter = futures.listIterator();
            while (iter.hasNext()) {
                Future<Double> future = iter.next();

                double contribution = 0;
                try {
                    // future.get() stalls the main thread
                    contribution = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                M[0] = M[0] + contribution;
                N[0] = N[0] + 1.0;
                i[0] += 1;
                p_i[0] = (M[0] + 2) / (N[0] + 4);
                metric[0] = ((4.0 * p_i[0] * (1.0 - p_i[0])) / Math.pow(errorLevel, 2)) - 4.0;

                poolAvailability.incrementAndGet();
                iter.remove();
            }

        } while (N[0] <= metric[0]);


        // The condition is now satisfied, we don't need to run all the tasks now.
        executorService.shutdownNow();

        return this.createResult(M[0] / N[0], i[0], total);
    }
}
