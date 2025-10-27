/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.explanations.semantics;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.Iterator;

/**
 * Models a set-based representation for explanations for the acceptance of some argument
 *
 * @author Lars Bengel
 */
public class SetExplanation extends Explanation implements Collection<Argument> {

    /** the set of arguments of this explanation */
    protected Collection<Argument> arguments;

    /**
     * Instantiates a new explanation for the given argument
     * @param argument some argument
     * @param arguments the explaining set of arguments
     */
    public SetExplanation(Argument argument, Collection<Argument> arguments) {
        super(argument);
        this.arguments = arguments;
    }

    @Override
    public Collection<Argument> getSetExplanation() {
        return getArgumentsOfStatus(ArgumentStatus.IN);
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
