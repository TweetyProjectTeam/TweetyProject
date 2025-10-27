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
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.Argument;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Models a sequence-based representation for explanations for the acceptance of some argument
 *
 *
 * @author Lars Bengel
 */
public class SequenceExplanation extends Explanation implements List<Collection<? extends Argument>> {

    /** the sequence of sets of arguments that represents this explanation */
    List<Collection<? extends Argument>> sequence;

    /**
     * Instantiates an explanation for the given argument
     * @param argument some argument
     * @param sequence the explaining sequence of sets of arguments
     */
    public SequenceExplanation(Argument argument, SerialisationSequence sequence) {
        super(argument);
        this.sequence = sequence;
    }

    /**
     * returns the sequence representation of the explanation
     * @return sequence representation of the explanation
     */
    public List<Collection<? extends Argument>> getSequence() {
        return new SerialisationSequence(sequence);
    }

    @Override
    public Collection<Argument> getSetExplanation() {
        return ((SerialisationSequence) getSequence()).getExtension();
    }

    @Override
    public int compareTo(Explanation o) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Extension<?> getArgumentsOfStatus(ArgumentStatus status) {
        return ((SerialisationSequence) sequence).getArgumentsOfStatus(status);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", getArgument(), sequence.toString());
    }

    @Override
    public int size() {
        return sequence.size();
    }

    @Override
    public boolean isEmpty() {
        return sequence.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return sequence.contains(o);
    }

    @Override
    public Iterator<Collection<? extends Argument>> iterator() {
        return sequence.iterator();
    }

    @Override
    public Object[] toArray() {
        return sequence.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return sequence.toArray(a);
    }

    @Override
    public boolean add(Collection<? extends Argument> arguments) {
        return sequence.add(arguments);
    }

    @Override
    public boolean remove(Object o) {
        return sequence.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return sequence.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Collection<? extends Argument>> c) {
        return sequence.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Collection<? extends Argument>> c) {
        return sequence.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return sequence.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return sequence.retainAll(c);
    }

    @Override
    public void clear() {
        sequence.clear();
    }

    @Override
    public Collection<? extends Argument> get(int index) {
        return sequence.get(index);
    }

    @Override
    public Collection<? extends Argument> set(int index, Collection<? extends Argument> element) {
        return sequence.set(index, element);
    }

    @Override
    public void add(int index, Collection<? extends Argument> element) {
        sequence.add(index, element);
    }

    @Override
    public Collection<? extends Argument> remove(int index) {
        return sequence.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return sequence.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return sequence.lastIndexOf(o);
    }

    @Override
    public ListIterator<Collection<? extends Argument>> listIterator() {
        return sequence.listIterator();
    }

    @Override
    public ListIterator<Collection<? extends Argument>> listIterator(int index) {
        return sequence.listIterator(index);
    }

    @Override
    public List<Collection<? extends Argument>> subList(int fromIndex, int toIndex) {
        return sequence.subList(fromIndex, toIndex);
    }
}
