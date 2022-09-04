package org.tweetyproject.arg.bipolar.syntax;

public class WeightedSetSupport extends SetSupport{
	public WeightedSetSupport(ArgumentSet supporter, ArgumentSet supported, double cp) {
		super(supporter, supported);
		this.cp = cp;
	}

	public double cp;

}
