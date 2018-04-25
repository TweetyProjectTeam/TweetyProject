/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons;

import java.util.Iterator;

/**
 * Classes implementing this interface are able to enumerate
 * belief sets.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 * @param <T> The type of belief sets
 */
public interface BeliefSetIterator<S extends Formula,T extends BeliefSet<S>> extends Iterator<T>{

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext();

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next();
}
