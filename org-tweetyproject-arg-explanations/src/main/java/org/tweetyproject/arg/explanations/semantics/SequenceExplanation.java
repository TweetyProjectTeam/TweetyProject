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
import org.tweetyproject.serialisability.SerialisationSequence;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Models a sequence-based representation for explanations for the acceptance of some argument.
 * The explanation is read-only; modifying operations throw an {@link UnsupportedOperationException}.
 *
 * @author Lars Bengel
 */
public class SequenceExplanation extends Explanation implements List<Extension<DungTheory>> {

    /** the serialisation sequence that represents this explanation */
    private final SerialisationSequence<Extension<DungTheory>> sequence;

    /**
     * Instantiates an explanation for the given argument
     * @param argument some argument
     * @param sequence the explaining serialisation sequence
     */
    public SequenceExplanation(Argument argument, SerialisationSequence<Extension<DungTheory>> sequence) {
        super(argument);
        this.sequence = sequence;
    }

    /**
     * returns the sequence representation of the explanation
     * @return sequence representation of the explanation
     */
    public SerialisationSequence<Extension<DungTheory>> getSequence() {
        return this.sequence;
    }

    @Override
    public Collection<Argument> getSetExplanation() {
        return this.getArgumentsOfStatus(ArgumentStatus.IN);
    }

    @Override
    public int compareTo(Explanation o) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Extension<DungTheory> getArgumentsOfStatus(ArgumentStatus status) {
        if (status.equals(ArgumentStatus.IN)) {
            Extension<DungTheory> result = new Extension<>();
            for (Extension<DungTheory> set : this.sequence)
                result.addAll(set);
            return result;
        }
        throw new IllegalArgumentException("Arguments of status different from \"IN\" cannot be determined from an extension alone");
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
    public Iterator<Extension<DungTheory>> iterator() {
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
    public boolean add(Extension<DungTheory> arguments) {
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
    public boolean addAll(Collection<? extends Extension<DungTheory>> c) {
        return sequence.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Extension<DungTheory>> c) {
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
    public Extension<DungTheory> get(int index) {
        return sequence.get(index);
    }

    @Override
    public Extension<DungTheory> set(int index, Extension<DungTheory> element) {
        return sequence.set(index, element);
    }

    @Override
    public void add(int index, Extension<DungTheory> element) {
        sequence.add(index, element);
    }

    @Override
    public Extension<DungTheory> remove(int index) {
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
    public ListIterator<Extension<DungTheory>> listIterator() {
        return sequence.listIterator();
    }

    @Override
    public ListIterator<Extension<DungTheory>> listIterator(int index) {
        return sequence.listIterator(index);
    }

    @Override
    public List<Extension<DungTheory>> subList(int fromIndex, int toIndex) {
        return sequence.subList(fromIndex, toIndex);
    }
}
