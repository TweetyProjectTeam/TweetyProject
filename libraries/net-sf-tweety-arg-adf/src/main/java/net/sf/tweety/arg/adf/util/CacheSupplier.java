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
package net.sf.tweety.arg.adf.util;

import java.util.function.Supplier;

/**
 * Delegates the first get() call to the given supplier and stores its result in
 * cache. Consecutive calls return the cached result, therefore computation is
 * only done once. Does not maintain a reference to the delegate once its result
 * is computed.
 * 
 * @author Mathias Hofer
 *
 */
public class CacheSupplier<T> implements Supplier<T> {

	private T cache;

	private Supplier<T> delegate;

	public CacheSupplier(Supplier<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T get() {
		if (cache == null) {
			cache = delegate.get();
			delegate = null;
		}
		return cache;
	}

}
