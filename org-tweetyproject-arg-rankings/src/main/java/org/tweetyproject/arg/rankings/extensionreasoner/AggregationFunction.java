package org.tweetyproject.arg.rankings.extensionreasoner;

/**
 * This enum lists different types of aggregation types for aggregating a support vector of Order-based extensions.
 * see "On Supported Inference and Extension Selection in Abstract Argumentation Frameworks (S. Konieczny et al., 2015): Definition 4
 * @author Daniel Letkemann
 */
public enum AggregationFunction {
    /**
     * SUM
     */
    SUM("returns 1D vector with SUM of all elements of of an aggregated vector"),
    /**
     * MAX
     */
    MAX("returns 1D vector with the MAXIMAL element of of an aggregated vector"),
    /**
     * MIN
     */
    MIN("returns 1D vector with the MINIMAL element of of an aggregated vector"),
    /**
     * LEXIMAX
     */
    LEXIMAX("returns vector with the elements of of an aggregated vector re-arranged in DECREASING order"),
    /**
     * LEXIMIN
     */
    LEXIMIN("returns vector with the elements of of an aggregated vector re-arranged in INCREASING order"),
    /**
     * AVG
     */
    AVG("returns 1D vector with the average of an aggregated vector");

    AggregationFunction(String description) {
        this.description = description;
    }

    private final String description;

    /**
     * Returns the description of the Aggregation Function.
     *
     * @return the description of the Aggregation Function.
     */
    public String description() {
        return this.description;
    }
}