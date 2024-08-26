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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.bipolar.syntax;

import org.tweetyproject.arg.dung.ldo.syntax.LdoFormula;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Signature;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class models a set of arguments used in the context of bipolar abstract argumentation theory.
 * <p>
 * It provides functionalities for managing a collection of {@link BArgument} objects, including adding, removing,
 * and comparing sets of arguments. The class implements the {@link BipolarEntity} interface,
 * the {@link Collection} interface for managing arguments, and the {@link Comparable} interface for comparing argument sets.
 * </p>
 *
 * @see BArgument
 * @see BipolarEntity
 * @see Collection
 * @see Comparable
 *
 * @param <BArgument> the type of argument used in the argument set
 *
 * @author Lars Bengel
 */
public class ArgumentSet implements BipolarEntity, Collection<BArgument>, Comparable<ArgumentSet> {
    private Set<BArgument> arguments;

    /**
     * Constructs an empty {@code ArgumentSet}.
     */
    public ArgumentSet() {
        this(new HashSet<>());
    }

    /**
     * Constructs an {@code ArgumentSet} and initializes it with the arguments from a given {@code Extension<DungTheory>}.
     *
     * @param ext the extension containing arguments to initialize the set
     */
    public ArgumentSet(Extension<DungTheory> ext) {
        this();
        for (Argument argument: ext) {
            this.add(new BArgument(argument));
        }
    }

    /**
     * Constructs an {@code ArgumentSet} with a single argument.
     *
     * @param argument the argument to add to the set
     */
    public ArgumentSet(BArgument argument) {
        this(new HashSet<>());
        this.add(argument);
    }

    /**
     * Constructs an {@code ArgumentSet} initialized with a collection of arguments.
     *
     * @param arguments the collection of arguments to initialize the set
     */
    public ArgumentSet(Collection<? extends BArgument> arguments) {
        this.arguments = new HashSet<>(arguments);
    }

    /**
     * Returns a string representation of the {@code ArgumentSet}.
     * The arguments are enclosed in curly braces and separated by commas.
     *
     * @return a string representation of the argument set
     */
    @Override
    public String toString() {
        String s = "{";
        boolean first = true;
        Iterator<BArgument> var3 = this.iterator();

        while(var3.hasNext()) {
            BArgument a = var3.next();
            if (first) {
                s = s + a;
                first = false;
            } else {
                s = s + "," + a;
            }
        }

        return s + "}";
    }

    /**
     * Returns the hash code value for this argument set.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return 31 * 1 + (this.arguments == null ? 0 : this.arguments.hashCode());
    }

    /**
     * Compares this {@code ArgumentSet} to another object for equality.
     *
     * @param obj the object to compare with
     * @return {@code true} if the argument sets are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            ArgumentSet other = (ArgumentSet)obj;
            if (this.arguments == null) {
                if (other.arguments != null) {
                    return false;
                }
            } else if (!this.arguments.equals(other.arguments)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Returns the set of arguments contained in this {@code ArgumentSet}.
     *
     * @return the set of arguments
     */
    public Set<BArgument> getArguments() {
        return this.arguments;
    }

    /**
     * Adds an argument to this set.
     *
     * @param arg0 the argument to be added
     * @return {@code true} if the set did not already contain the argument
     */
    @Override
    public boolean add(BArgument arg0) {
        return this.arguments.add(arg0);
    }

    /**
     * Adds a collection of arguments to this set.
     *
     * @param arg0 the collection of arguments to be added
     * @return {@code true} if the set changed as a result of the call
     */
    @Override
    public boolean addAll(Collection<? extends BArgument> arg0) {
        return this.arguments.addAll(arg0);
    }

    /**
     * Clears the set, removing all arguments.
     */
    @Override
    public void clear() {
        this.arguments.clear();
    }

    /**
     * Returns {@code true} if this set contains the specified argument.
     *
     * @param arg0 the argument to check for containment
     * @return {@code true} if this set contains the specified argument
     */
    @Override
    public boolean contains(Object arg0) {
        return this.arguments.contains(arg0);
    }

    /**
     * Returns {@code true} if this set contains all of the elements in the specified collection.
     *
     * @param arg0 the collection to check for containment
     * @return {@code true} if this set contains all of the elements in the specified collection
     */
    @Override
    public boolean containsAll(Collection<?> arg0) {
        return this.arguments.containsAll(arg0);
    }

    /**
     * Returns {@code true} if this set contains no arguments.
     *
     * @return {@code true} if this set contains no arguments
     */
    @Override
    public boolean isEmpty() {
        return this.arguments.isEmpty();
    }

    /**
     * Returns an iterator over the arguments in this set.
     *
     * @return an iterator over the arguments in this set
     */
    @Override
    public Iterator<BArgument> iterator() {
        return this.arguments.iterator();
    }

    /**
     * Removes the specified argument from this set if it is present.
     *
     * @param arg0 the argument to be removed
     * @return {@code true} if the set contained the specified argument
     */
    @Override
    public boolean remove(Object arg0) {
        return this.arguments.remove(arg0);
    }

    /**
     * Removes from this set all of its elements that are contained in the specified collection.
     *
     * @param arg0 the collection of elements to be removed from this set
     * @return {@code true} if the set changed as a result of the call
     */
    @Override
    public boolean removeAll(Collection<?> arg0) {
        return this.arguments.removeAll(arg0);
    }

    /**
     * Retains only the elements in this set that are contained in the specified collection.
     *
     * @param arg0 the collection containing elements to be retained in this set
     * @return {@code true} if the set changed as a result of the call
     */
    @Override
    public boolean retainAll(Collection<?> arg0) {
        return this.arguments.retainAll(arg0);
    }

    /**
     * Returns the number of arguments in this set.
     *
     * @return the number of arguments in this set
     */
    @Override
    public int size() {
        return this.arguments.size();
    }

    /**
     * Returns an array containing all the arguments in this set.
     *
     * @return an array containing all the arguments in this set
     */
    @Override
    public Object[] toArray() {
        return this.arguments.toArray();
    }

    /**
     * Returns an array containing all the arguments in this set; the runtime type of the returned array is that of the specified array.
     *
     * @param <T>   the component type of the array to contain the arguments
     * @param arg0  the array into which the arguments of this set are to be stored
     * @return an array containing the arguments in this set
     */
    @Override
    public <T> T[] toArray(T[] arg0) {
        return this.arguments.toArray(arg0);
    }

    /**
     * Compares this {@code ArgumentSet} with another for order. The comparison is based on the hash codes of the sets.
     *
     * @param arg0 the other {@code ArgumentSet} to be compared
     * @return a negative integer, zero, or a positive integer as this set is less than, equal to, or greater than the specified set
     */
    @Override
    public int compareTo(ArgumentSet arg0) {
        if (this.hashCode() < arg0.hashCode()) {
            return -1;
        } else {
            return this.hashCode() > arg0.hashCode() ? 1 : 0;
        }
    }


    /**
     * Retrieves the LdoFormula associated with this argument set.
     *
     * @return {@code null} as this implementation does not currently support LdoFormula
     */
    @Override
    public LdoFormula getLdoFormula() {
        return null;
    }

    /**
     * Retrieves the signature of this argument set.
     *
     * @return {@code null} as this implementation does not currently support signatures
     */
    @Override
    public Signature getSignature() {
        return null;
    }
}
