package org.tweetyproject.arg.bipolar.gradual;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.math.func.SimpleFunction;

public abstract class AbstractInfluenceFunction implements SimpleFunction<Pair<Double,Double>,Double> {
    @Override
    public Double eval(Pair<Double, Double> x) {
        return eval(x.getFirst(), x.getSecond());
    }

    public abstract Double eval(Double base, Double update);
}
