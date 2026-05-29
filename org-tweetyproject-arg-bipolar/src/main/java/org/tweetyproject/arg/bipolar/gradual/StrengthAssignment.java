package org.tweetyproject.arg.bipolar.gradual;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.BeliefBase;
import org.tweetyproject.commons.Signature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StrengthAssignment extends HashMap<Argument,Number> implements BeliefBase {

    @Override
    public Signature getMinimalSignature() {
        DungSignature sig = new DungSignature();
        for(Argument a: this.keySet())
            sig.add(a);
        return sig;
    }

    public List<Number> get(List<Argument> keys) {
        List<Number> result = new ArrayList<>();
        for (Argument key : keys) {
            result.add(this.get(key));
        }
        return result;
    }
}
