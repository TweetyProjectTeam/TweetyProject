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
package org.tweetyproject.web.services;

import java.util.concurrent.Callable;


/**
 * A generic Callable implementation that throws an UnsupportedOperationException in the call method.
 *
 * The Callee class is a simple implementation of the java.util.concurrent.Callable interface
 * that provides a placeholder for the call method. When an instance of this class is used in a
 * Callable context, it will throw an UnsupportedOperationException for the unimplemented call method.
 * @see Callable
 * @see UnsupportedOperationException
 */

public class Callee implements Callable {

    @Override
    public Object call() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'call'");
    }


}
