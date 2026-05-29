package org.tweetyproject.arg.bipolar.gradual;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.math.func.SimpleFunction;

import java.util.List;

public abstract class AbstractAggregationFunction implements SimpleFunction<Pair<List<Double>,List<Double>>,Double> {

    @Override
    public Double eval(Pair<List<Double>, List<Double>> values) {
        return eval(values.getFirst(), values.getSecond());
    }

    public abstract Double eval(List<Double> attackers, List<Double> supporters);
}
