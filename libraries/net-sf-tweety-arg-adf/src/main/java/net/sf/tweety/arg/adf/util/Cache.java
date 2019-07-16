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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<T, R> implements Function<T, R> {

	private Map<T, R> cache;

	private Function<T, R> function;

	/**
	 * Creates an empty cache
	 * 
	 * @param function some function
	 */
	public Cache(Function<T, R> function) {
		this.cache = new HashMap<T, R>();
		this.function = function;
	}

	@Override
	public R apply(T input) {
		R result = cache.computeIfAbsent(input, function);
		return result;
	}

	public R put(T input, R output) {
		return cache.put(input, output);
	}

	public R remove(T input) {
		return cache.remove(input);
	}

	/**
	 * Returns the size of this cache
	 * @return the size of this cache
	 * @see java.util.Map#size()
	 */
	public int size() {
		return cache.size();
	}

	/**
	 * Sets the function which is used for future calls, but does not recompute
	 * already cached elements.
	 * 
	 * @param function some function
	 */
	public void setFunction(Function<T, R> function) {
		this.function = function;
	}

}
