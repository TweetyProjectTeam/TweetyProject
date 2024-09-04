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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.syntax;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.Visitor;
import org.tweetyproject.graphs.Node;

/**
 * An immutable representation of an ADF argument
 * <p>
 * This argument can also be used as an {@link AcceptanceCondition}, since it is
 * its atomic building block.
 *
 * @author Mathias Hofer
 *
 */
public final class Argument implements AcceptanceCondition, Node {

    private final String name;
    /**
     * Argument
     */
    public Argument() {
        this(null);
    }

    /**
     * @param name the name of the argument
     */
    public Argument(String name) {
        this.name = name;
    }

    @Override
    public Stream<Argument> arguments() {
        return Stream.of(this);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition#contains(org.tweetyproject.arg.adf.syntax.Argument)
     */
    @Override
    public boolean contains(Argument arg) {
        return arg == this;
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition#children()
     */
    @Override
    public Set<AcceptanceCondition> getChildren() {
        return Collections.emptySet();
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition#accept(org.tweetyproject.arg.adf.syntax.acc.Visitor, java.lang.Object)
     */
    @Override
    public <U, D> U accept(Visitor<U, D> visitor, D topDownData) {
        return visitor.visit(this, topDownData);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
        if(!o.getClass().equals(this.getClass())) return false;
        if(!((Argument)o).getName().equals(getName())) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

}
