package org.tweetyproject.arg.dung.reasoner;

/** This enum lists different types of aggregation types for aggregating a support vector of Order-based extensions.
 * see "On Supported Inference and Extension Selection in Abstract Argumentation Frameworks (S. Konieczny et al., 2015): Definition 4
 */
public enum AggregationFunction {
    /** SUM */
    SUM ("returns 1D vector with SUM of all elements of the support vector"),
    /**MAX*/
    MAX("returns 1D vector with the MAXIMAL element of the support vector"),
    /**MIN*/
    MIN("returns 1D vector with the MINIMAL element of the support vector"),
    /**LEXIMAX*/
    LEXIMAX("returns vector with the elements of the support vector re-arranged in DECREASING order"),
    /**LEXIMIN*/
    LEXIMIN("returns vector with the elements of the support vector re-arranged in INCREASING order");

    AggregationFunction(String description){
        this.description = description;
    }

    private final String description;

    /**
     * Returns the description of the Aggregation Function.
     * @return the description of the Aggregation Function.
     */
    public String description(){
        return this.description;
    }
}
