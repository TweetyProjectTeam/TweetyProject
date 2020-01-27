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

package net.sf.tweety.arg.bipolar.syntax;

import net.sf.tweety.arg.dung.ldo.syntax.LdoFormula;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Signature;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class models a set of arguments used by bipolar abstract argumentation theory.
 * @author Lars Bengel
 *
 */
public class ArgumentSet implements BipolarEntity, Collection<BArgument>, Comparable<ArgumentSet> {
    private Set<BArgument> arguments;

    public ArgumentSet() {
        this(new HashSet<>());
    }

    public ArgumentSet(Extension ext) {
        this();
        for (Argument argument: ext)
            this.add((new BArgument(argument)));
    }

    public ArgumentSet(BArgument argument) {
        this(new HashSet<>());
        this.add(argument);
    }

    public ArgumentSet(Collection<? extends BArgument> arguments) {
        this.arguments = new HashSet<>(arguments);
    }

    public String toString() {
        String s = "{";
        boolean first = true;
        Iterator var3 = this.iterator();

        while(var3.hasNext()) {
            BArgument a = (BArgument)var3.next();
            if (first) {
                s = s + a;
                first = false;
            } else {
                s = s + "," + a;
            }
        }

        return s + "}";
    }

    public int hashCode() {
        int prime = 1;
        int result = 1;
        result = 31 * result + (this.arguments == null ? 0 : this.arguments.hashCode());
        return result;
    }

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

    public boolean add(BArgument arg0) {
        return this.arguments.add(arg0);
    }

    public boolean addAll(Collection<? extends BArgument> arg0) {
        return this.arguments.addAll(arg0);
    }

    public void clear() {
        this.arguments.clear();
    }

    public boolean contains(Object arg0) {
        return this.arguments.contains(arg0);
    }

    public boolean containsAll(Collection<?> arg0) {
        return this.arguments.containsAll(arg0);
    }

    public boolean isEmpty() {
        return this.arguments.isEmpty();
    }

    public Iterator<BArgument> iterator() {
        return this.arguments.iterator();
    }

    public boolean remove(Object arg0) {
        return this.arguments.remove(arg0);
    }

    public boolean removeAll(Collection<?> arg0) {
        return this.arguments.removeAll(arg0);
    }

    public boolean retainAll(Collection<?> arg0) {
        return this.arguments.retainAll(arg0);
    }

    public int size() {
        return this.arguments.size();
    }

    public Object[] toArray() {
        return this.arguments.toArray();
    }

    public <T> T[] toArray(T[] arg0) {
        return this.arguments.toArray(arg0);
    }

    public int compareTo(ArgumentSet arg0) {
        if (this.hashCode() < arg0.hashCode()) {
            return -1;
        } else {
            return this.hashCode() > arg0.hashCode() ? 1 : 0;
        }
    }

    @Override
    public LdoFormula getLdoFormula() {
        return null;
    }

    @Override
    public Signature getSignature() {
        return null;
    }
}
