package org.tweetyproject.arg.explanations.semantics;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DialectialSequenceExplanation extends Explanation {
    private Argument argument;
    private List<Collection<Argument>> supporters;
    private List<Collection<Argument>> defeated;

    public DialectialSequenceExplanation(Argument argument) {
        this.argument = argument;
        this.supporters = new ArrayList<>();
        this.defeated = new ArrayList<>();
    }

    public boolean add(Collection<Argument> s, Collection<Argument> t) {
        boolean result = false;
        result |= supporters.add(s);
        result |= defeated.add(t);
        return result;
    }

    public Argument getArgument() {
        return argument;
    }

    public List<Collection<Argument>> getDefeated() {
        return defeated;
    }

    public List<Collection<Argument>> getSupporters() {
        return supporters;
    }

    @Override
    public int compareTo(Explanation o) {
        return 0;
    }

    @Override
    public Extension<?> getArgumentsOfStatus(ArgumentStatus status) {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < supporters.size(); i++) {
            if (i!=0) s.append(", ");
            s.append(String.format("[%s, %s]", supporters.get(i), defeated.get(i)));
        }
        return s.toString();
    }
}
