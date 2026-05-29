package org.tweetyproject.arg.bipolar.gradual;

import java.util.List;

public class SumAggregationFunction extends AbstractAggregationFunction {
    @Override
    public Double eval(List<Double> attackers, List<Double> supporters) {
        Double result = 0d;
        for (Double x: supporters) {
            result += x;
        }
        for (Double x : attackers) {
            result -= x;
        }
        return result;
    }
}
