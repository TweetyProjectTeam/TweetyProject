package org.tweetyproject.arg.bipolar.gradual;

import java.util.List;

public class ProductAggregationFunction extends AbstractAggregationFunction {
    @Override
    public Double eval(List<Double> attackers, List<Double> supporters) {
        double result = 1d;
        for (Double a : attackers) {
            result *= 1 - a;
        }
        double tmp = 1d;
        for (Double s : supporters) {
            tmp *= 1 - s;
        }
        return result - tmp;
    }
}
