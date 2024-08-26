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

import java.util.Iterator;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * This class models an argument used by bipolar abstract argumentation theories.
 * @author Lars Bengel
 *
 */
public class BArgument extends org.tweetyproject.arg.dung.syntax.Argument implements BipolarEntity {

    /**
     * Constructs a new bipolar argument with the specified name.
     *
     * @param name the name of the argument
     */
    public BArgument(String name) {
        super(name);
    }

    /**
     * Constructs a new bipolar argument from an existing argument.
     *
     * @param arg the argument to be converted
     */
    public BArgument(Argument arg) {
        super(arg.getName());
    }

    /**
     * Checks if this argument is equal to the specified object.
     * <p>
     * This method checks for equality based on the argument's name.
     * </p>
     *
     * @param arg0 the object to be compared
     * @return {@code true} if this argument is equal to the specified object;
     *         {@code false} otherwise
     */
    @Override
    public boolean contains(Object arg0) {
        return this.equals(arg0);
    }

    /**
     * Returns an iterator over the arguments in this set.
     * <p>
     * This method is not implemented for the current class and returns {@code null}.
     * In a complete implementation, it would provide an iterator over a collection
     * of arguments.
     * </p>
     *
     * @return an iterator over the arguments
     */
    @Override
    public Iterator<BArgument> iterator() {
        return null;
    }

    /**
     * Checks if this argument is supported by another argument in the given theory.
     * <p>
     * This method determines if there exists a support relation in the given
     * {@link EAFTheory} where this argument is supported by the specified argument.
     * </p>
     *
     * @param theory the theory in which to check for support
     * @param x the argument that might support this argument
     * @return {@code true} if this argument is supported by the specified argument in the theory;
     *         {@code false} otherwise
     */
    public boolean isSupportedBy(EAFTheory theory, BipolarEntity x) {
        for (Support support : theory.supports) {
            BipolarEntity froms = support.getSupporter();
            BipolarEntity tos = support.getSupported();
            if (tos.contains(this) && froms.contains(x)) {
                return true;
            }
        }

        return false;
    }
}

