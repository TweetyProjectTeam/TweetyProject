package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Vector;

import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;




/**
 * Reasoner for selecting the "best" Extensions of a set of Extensions.
 * Based on "Rank Extension Ranking Semantics" (K. Skiba, 2023)
 * 
 * @author Benjamin Birner
 *
 */
public class RankBasedPairwiseExtensionReasoner {

	private AggregationFunction aggregationFunction;
	
	private ExtensionRankingSemantics semantics;
	
	
	
	/**
     * Create reasoner with specific aggregation function and Extension Ranking Semantics.
     *
     * @param agg type of aggregation to use.
     * @param sem type of Extension Ranking Semantics.
     */
	public RankBasedPairwiseExtensionReasoner(AggregationFunction agg, ExtensionRankingSemantics sem) {
		aggregationFunction = agg;
		semantics = sem;
	}
	
	
	/**
	 * selects the best Extensions of a set of Extensions concerning a given Aggregation, Extension Ranking Semantics and Strength Vector
	 * by pairwise comparison. 
	 * 
	 * @param extensions a set of Extensions
	 * @param dung an Argumentation Framework
	 * @param function a function to calculate the Strength Vector
	 * @return a set of Extensions which are considered as the best ones
	 */
	public  LinkedList<Extension<DungTheory>> getModels(Collection<Extension<DungTheory>> extensions, DungTheory dung, StrengthVectorFunction function) throws Exception{
		
		int argmax =  - extensions.size();
		LinkedList<Extension<DungTheory>> bestExt = new LinkedList<Extension<DungTheory>>();
		LinkedList<Extension<DungTheory>> greaterEqual = new LinkedList<Extension<DungTheory>>();
		LinkedList<Extension<DungTheory>> lessEqual = new LinkedList<Extension<DungTheory>>();
		for(Extension<DungTheory> ext1 : extensions) {
			boolean isSem1 = isSpecifiedSemantics(ext1, dung);
			for(Extension<DungTheory> ext2 : extensions) {
				if(!ext1.equals(ext2)) {
					boolean isSem2 = isSpecifiedSemantics(ext2, dung);
					if(isSem1 && !isSem2) {
						lessEqual.add(ext2);
					}else {
						if(!isSem1 && isSem2) {
							greaterEqual.add(ext2);
						}else {
							Vector<Double> aVec1 = aggregate(function.getStrengthVector(ext1, extensions, dung));
							Vector<Double> aVec2 = aggregate(function.getStrengthVector(ext2, extensions, dung));
							//the better vector is the one with lower value
							Integer comp = compare(aVec1, aVec2);
							if(comp < 0) {
								lessEqual.add(ext2);
							}else {
								if(comp > 0) {
									greaterEqual.add(ext2);
								}else {
									lessEqual.add(ext2);
									greaterEqual.add(ext2);
								}
							}
						}
					}
				}
			}
			if( argmax < (lessEqual.size() - greaterEqual.size())) {
				argmax = (lessEqual.size() - greaterEqual.size());
				bestExt.clear();
				bestExt.add(ext1);
			}else {
				if( argmax == (lessEqual.size() - greaterEqual.size())) {
					bestExt.add(ext1);
				}
			}
			greaterEqual.clear();
			lessEqual.clear();
		}
		return bestExt;
	}
	
	
	/**
	 * checks if the Extension <code>ext<code> is a <code>semantics<code> semantics.
	 * 
	 * @param ext the Extension to check
	 * @param dung a Argumentation Framework
	 * @return true if <code>ext<code> is a <code>semantics<code> semantics.
	 * @throws Exception
	 */
	private boolean isSpecifiedSemantics(Extension<DungTheory> ext, DungTheory dung) throws Exception {
		
		switch (Objects.requireNonNull(semantics)) {

        	case R_CF -> {	
        		return dung.isConflictFree(ext);
        	}
        	case R_AD -> {
        		return dung.isAdmissable(ext);
        	}
        	case R_CO -> {
        		return dung.isComplete(ext);
        	}
        	case R_SST -> {                                                       
        		return dung.isStable(ext);
        	}
        	case R_PR -> {
        		SimplePreferredReasoner spr = new SimplePreferredReasoner();
        		Collection<Extension<DungTheory>> exts = spr.getModels(dung);
        		return exts.contains(ext);
        	}
        	case R_GR -> {
        		SimpleGroundedReasoner sgr = new SimpleGroundedReasoner();
        		Collection<Extension<DungTheory>> exts = sgr.getModels(dung);
        		return exts.contains(ext);
        	}
		}
		throw new Exception("Unsupported semantics");
	}
	
	
	
	
	
	/**
     * Returns the aggregated version of a vector based on the selected aggregation function of reasoner.
     * For SUM/MIN/MAX/AVG returns a 1D vector.
     * For LEXIMIN/LEXIMAX dimensions are equal to input vector.
     * For max/min of an empty vector, returns a 1D vector with 0/+inf respectively.
     *
     * @param vector a vector of Doubles
     * @return vector aggregated by function
	 * @throws Exception 
     */
    private Vector<Double> aggregate(Vector<Double> vector) throws Exception {
        //switch case for all aggregation types
        Vector<Double> returnVec = new Vector<>();
        switch (Objects.requireNonNull(aggregationFunction)) {

            case AVG -> {
                double sum = 0d;
                for (double x : vector) {
                    sum += x;
                }
                returnVec.add(sum / vector.size());
            }

            case SUM -> {
                double sum = 0;
                for (double x : vector) {
                    sum += x;
                }
                returnVec.add(sum);
            }
            case MAX -> {
                double max = 0;
                for (double x : vector) {
                    if (x > max) {
                        max = x;
                    }
                }
                returnVec.add(max);
            }
            case MIN -> {
                double min = Double.POSITIVE_INFINITY;
                for (double x : vector) {
                    if (x < min) {
                        min = x;
                    }
                }
                returnVec.add(min);
            }
            case LEXIMAX -> {
                returnVec = vector;
                returnVec.sort(Collections.reverseOrder());
                return returnVec;
            }
            case LEXIMIN -> {
                returnVec = vector;
                Collections.sort(returnVec);
                return returnVec;
            }
        }
        return returnVec;
    }
    
    
    
    /**
     * For SUM/MAX/MIN/AVG, compares two vectors using the value of their single element.
     * Otherwise, for LEXIMIN/LEXIMAX compares two vectors lexicographically.
     *
     * @param v1 first vector to compare
     * @param v2 second vector to compare
     * @return the value 0 if v1 == v2; a value less then 0 if v1 < v2 and a value greater than 0 if v1 > v2
     */
    public Integer compare(Vector<Double> v1, Vector<Double> v2) throws Exception {
        int returnInt;
        switch (aggregationFunction) {

            case SUM, MAX, MIN, AVG -> {
                returnInt = Double.compare(v1.get(0), v2.get(0));
                return returnInt;
            }
            case LEXIMAX, LEXIMIN -> {
                Double[] arr1 = new Double[v1.size()];
                v1.toArray(arr1);
                Double[] arr2 = new Double[v2.size()];
                v2.toArray(arr2);
                returnInt = Arrays.compare(arr1, arr2);
                return returnInt;
            }
        }
        throw new Exception("Unsupported aggregation function");


    }
	
	
	
	
	/**
     * Gets the current aggregation function of this reasoner instance.
     *
     * @return aggregation function enum
     */
    public AggregationFunction getAggregationFunction() {
        return aggregationFunction;
    }

    /**
     * Sets the aggregation function of this reasoner instance.
     *
     * @param aggregationFunction aggregation function enum
     */
    public void setAggregationFunction(AggregationFunction aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
    }

	
    /**
     * Gets the current Extension Ranking Semantics of this reasoner instance.
     *
     * @return semantics  enum
     */
    public ExtensionRankingSemantics getExtensionRankingSemantics() {
        return semantics;
    }

    /**
     * Sets the Extension Ranking Semantics of this reasoner instance.
     *
     * @param sem enum
     */
    public void setExtensionRankingSemantics(ExtensionRankingSemantics sem) {
        semantics = sem;
    }
	
	
}
