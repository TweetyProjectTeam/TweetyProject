/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability.semantics;

import org.tweetyproject.arg.dung.semantics.AbstractArgumentationInterpretation;
import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Representation of a serialisation sequence,
 * i.e., a sequence of initial sets of the respective reducts.
 *
 * @see "Lydia Blümel, Matthias Thimm. 'A Ranking Semantics for Abstract Argumentation based on Serialisability.' Computational Models of Argument: Proceedings of COMMA 2022 353 (2022): 104."
 *
 * @author Lars Bengel
 * @author Julian Sander
 */
public class SerialisationSequence extends AbstractArgumentationInterpretation<DungTheory> implements List<Collection<? extends Argument>>, Comparable<SerialisationSequence> {

    /** internal storage of the serialisation sequence itself */
    private List<Collection<? extends Argument>> sequence;
    /** the corresponding extension of the sequence */
    private Collection<Argument> arguments;

    /**
     * Initializes an empty serialisation sequence
     */
    public SerialisationSequence() {
        sequence = new LinkedList<>();
        arguments = new HashSet<>();
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.argumentation.dung.semantics.AbstractArgumentationInterpretation#getArgumentsOfStatus(org.tweetyproject.argumentation.dung.semantics.ArgumentStatus)
     */
    @Override
    public Extension<DungTheory> getArgumentsOfStatus(ArgumentStatus status) {
        if(status.equals(ArgumentStatus.IN)) return new Extension<>(this.arguments);
        throw new IllegalArgumentException("Arguments of status different from \"IN\" cannot be determined from an extension alone");
    }

    /**
     * Initializes a serialisation sequence with {@code arguments} as the first element
     * @param arguments an initial set
     */
    public SerialisationSequence(Collection<? extends Argument> arguments) {
        this();
        this.add(arguments);
    }

    /**
     * Initializes a serialisation sequence starting with the given List of initial sets
     * @param sequence a sequence of initial sets
     */
    public SerialisationSequence(List<? extends Collection<? extends Argument>> sequence) {
        this();
        this.addAll(sequence);
    }

    /**
     * Computes the corresponding extension as the union of all the sets in the sequence
     * @return An extension containing all arguments of this sequence
     */
    public Extension<DungTheory> getExtension() {
        return getArgumentsOfStatus(ArgumentStatus.IN);
    }

    @Override
    public int size() {
        return arguments.size();
    }

    @Override
    public boolean isEmpty() {
        return arguments.isEmpty();
    }

    /**
     * Checks whether the given object is in the sequence. Can be either an {@link Extension<>} or an {@link Argument}.
     * @param o element whose presence in this list is to be tested
     * @return true, if the element is present in the sequence
     */
    @Override
    public boolean contains(Object o) {
        if (o instanceof Argument) {
            return arguments.contains(o);
        } else if (o instanceof Extension<?>) {
            return sequence.contains(o);
        } else {
            throw new IllegalArgumentException("Illegal type.");
        }
    }

    @Override
    public Iterator<Collection<? extends Argument>> iterator() {
        return sequence.listIterator();
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
        if (!this.arguments.addAll(arguments)) return false;
        this.sequence.add(arguments);
        return true;
    }

    /**
     * Removed the given initial set from the sequence.
     * If given an argument removes it from the initial set it occurs in.
     * @param o element to be removed from this list, if present
     * @return true, if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        if (o instanceof Argument) {
            this.arguments.remove(o);
            for (Collection<? extends Argument> ext: sequence) {
                ext.remove(o);
            }
        } else if (o instanceof Collection<?>) {
            this.sequence.remove(o);
            this.arguments.removeAll((Collection<Argument>) o);
        } else {
            throw new IllegalArgumentException("Wrong type.");
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.sequence.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Collection<? extends Argument>> c) {
        for (Collection<? extends Argument> ext: c) {
            this.add(ext);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Collection<? extends Argument>> c) {
        this.sequence.addAll(index, c);
        for (Collection<? extends Argument> ext: c) {
            this.arguments.addAll(ext);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = true;
        for (Object ext: c) {
            result &= this.remove(ext);
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public void clear() {
        this.sequence = new LinkedList<>();
        this.arguments = new HashSet<>();
    }

    @Override
    public Collection<? extends Argument> get(int index) {
        return this.sequence.get(index);
    }

    @Override
    public Collection<? extends Argument> set(int index, Collection<? extends Argument> element) {
        this.arguments.removeAll(this.get(index));
        this.arguments.addAll(element);
        return this.sequence.set(index, element);
    }

    @Override
    public void add(int index, Collection<? extends Argument> element) {
        this.sequence.add(index,element);
        this.arguments.addAll(element);
    }

    @Override
    public Collection<? extends Argument> remove(int index) {
        Collection<? extends Argument> element = this.get(index);
        this.remove(element);
        return element;
    }

    @Override
    public int indexOf(Object o) {
        return this.sequence.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.sequence.lastIndexOf(o);
    }

    @Override
    public ListIterator<Collection<? extends Argument>> listIterator() {
        return this.sequence.listIterator();
    }

    @Override
    public ListIterator<Collection<? extends Argument>> listIterator(int index) {
        return this.sequence.listIterator(index);
    }

    @Override
    public List<Collection<? extends Argument>> subList(int fromIndex, int toIndex) {
        return this.sequence.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SerialisationSequence other = (SerialisationSequence) obj;
        if (sequence == null) {
            return other.sequence == null;
        } else return sequence.equals(other.sequence);
    }

    @Override
    public int compareTo(SerialisationSequence o) {
        return Integer.compare(this.hashCode(), o.hashCode());
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result
                + ((sequence == null) ? 0 : sequence.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("(");
        for (Collection<? extends Argument> ext: this.sequence) {
            s.append(ext.toString());
            if (this.sequence.indexOf(ext) != this.sequence.size()-1) s.append(",");
        }
        s.append(")");
        return s.toString();
    }
}
