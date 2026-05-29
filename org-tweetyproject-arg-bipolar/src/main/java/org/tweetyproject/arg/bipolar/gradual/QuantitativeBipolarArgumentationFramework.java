package org.tweetyproject.arg.bipolar.gradual;

import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.graphs.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuantitativeBipolarArgumentationFramework extends BipolarArgumentationFramework {
    private Map<Argument,Number> strengths;

    public QuantitativeBipolarArgumentationFramework() {
        super();
        this.strengths = new StrengthAssignment();
    }

    public QuantitativeBipolarArgumentationFramework(BipolarArgumentationFramework graph) {
        super(graph);
        this.strengths = new StrengthAssignment();
        for (Argument arg : this) {
            strengths.put(arg, 1d);
        }
    }

    public Number getStrength(Argument argument) {
        return strengths.get(argument);
    }

    public List<Number> getStrength(List<Argument> arguments) {
        List<Number> result = new ArrayList<>();
        for (Argument argument : arguments) {
            result.add(getStrength(argument));
        }
        return result;
    }

    public Number setStrength(Argument argument, Number value) {
        return this.strengths.put(argument, value);
    }
}
