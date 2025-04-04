package org.tweetyproject.arg.explanations.semantics;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.Iterator;

public class SetExplanation extends Explanation {

    protected Collection<Argument> arguments;

    public SetExplanation(Collection<Argument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public int compareTo(Explanation o) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int size() {
        return this.arguments.size();
    }

    @Override
    public boolean isEmpty() {
        return this.arguments.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.arguments.contains(o);
    }

    @Override
    public Iterator<Argument> iterator() {
        return this.arguments.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.arguments.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.arguments.toArray(a);
    }

    @Override
    public boolean add(Argument argument) {
        return this.arguments.add(argument);
    }

    @Override
    public boolean remove(Object o) {
        return this.arguments.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.arguments.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Argument> c) {
        return this.arguments.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.arguments.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.arguments.retainAll(c);
    }

    @Override
    public void clear() {
        this.arguments.clear();
    }

    @Override
    public Extension<?> getArgumentsOfStatus(ArgumentStatus status) {
        return switch (status) {
            case IN -> new Extension<>(arguments);
            default -> new Extension<>();
        };
    }

    @Override
    public String toString() {
        return new Extension<DungTheory>(arguments).toString();
    }
}
