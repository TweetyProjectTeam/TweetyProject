package org.tweetyproject.arg.bipolar.analysis;

/**
 * Represents the result of an analysis, including performance metrics and analysis type.
 * <p>
 * This class encapsulates details about the analysis result such as the result value, number of iterations,
 * analysis type, and total probability. It provides methods to retrieve these values and to print the results
 * for debugging purposes.
 * </p>
 *
 * @author Taha Dogan Gunes
 */
public class AnalysisResult {

    /** The result value of the analysis. */
    private final double result;

    /** The number of iterations performed during the analysis. */
    private final long noIterations;

    /** The type of analysis conducted. */
    private final AnalysisType type;

    /** The total probability calculated during the analysis. */
    private final double totalProbability;

    /**
     * Constructs an {@code AnalysisResult} with the specified parameters.
     *
     * @param result the result of the analysis
     * @param noIterations the number of iterations performed
     * @param type the type of analysis conducted
     * @param totalProbability the total probability calculated
     */
    public AnalysisResult(double result, long noIterations, AnalysisType type, double totalProbability) {
        this.result = result;
        this.noIterations = noIterations;
        this.type = type;
        this.totalProbability = totalProbability;
    }

    /**
     * Returns the result of the analysis.
     *
     * @return the result value
     */
    public double getResult() {
        return result;
    }

    /**
     * Returns the number of iterations performed during the analysis.
     *
     * @return the number of iterations
     */
    public long getNoIterations() {
        return noIterations;
    }

    /**
     * Returns the type of analysis conducted.
     *
     * @return the type of analysis
     */
    public AnalysisType getType() {
        return type;
    }

    /**
     * Returns the total probability calculated during the analysis.
     *
     * @return the total probability
     */
    public double getTotalProbability() {
        return totalProbability;
    }

    /**
     * Prints the details of the analysis result to the standard output.
     * <p>
     * The output includes the type of analysis, result value, number of iterations, and total probability.
     * </p>
     */
    public void print() {
        System.out.println("Type: " + this.type +
                           ", Result: " + this.getResult() +
                           ", Iterations: " + this.getNoIterations() +
                           ", Total Probability: " + this.getTotalProbability());
    }
}
