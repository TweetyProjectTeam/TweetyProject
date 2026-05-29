package org.tweetyproject.arg.bipolar.gradual;

public class LinearInfluenceFunction extends AbstractInfluenceFunction {
    private final Double k;

    public LinearInfluenceFunction(Double k) {
        this.k = k;
    }

    @Override
    public Double eval(Double base, Double update) {
        return base + ((base/k) * Math.min(0, update)) + (((1-base)/k) * Math.max(0, update));
    }
}
