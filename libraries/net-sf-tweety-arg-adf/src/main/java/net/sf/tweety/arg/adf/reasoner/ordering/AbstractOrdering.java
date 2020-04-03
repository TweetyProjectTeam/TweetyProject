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
package net.sf.tweety.arg.adf.reasoner.ordering;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * 
 * @author Mathias Hofer
 *
 * @param <T> the type for which we establish an ordering
 */
public abstract class AbstractOrdering<T> implements Ordering<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.ordering.Ordering#filter(java.util.
	 * function.Predicate)
	 */
	@Override
	public Ordering<T> filter(Predicate<T> filter) {
		return new FilteredOrdering<T>(filter, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.ordering.Ordering#limit(int)
	 */
	@Override
	public Ordering<T> limit(int limit) {
		return new LimitedOrdering<T>(limit, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.ordering.Ordering#order(net.sf.tweety.arg.
	 * adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public List<T> order(AbstractDialecticalFramework adf) {
		return stream(adf)
				.sorted((o1, o2) -> compare(o1, o2, adf))
				.collect(Collectors.toList());
	}

	protected abstract Stream<T> stream(AbstractDialecticalFramework adf);

	protected abstract int compare(T o1, T o2, AbstractDialecticalFramework adf);

	private static final class LimitedOrdering<T> extends AbstractOrdering<T> {

		private final int limit;

		private final AbstractOrdering<T> delegate;

		/**
		 * @param limit
		 * @param delegate
		 */
		public LimitedOrdering(int limit, AbstractOrdering<T> delegate) {
			this.limit = limit;
			this.delegate = delegate;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.reasoner.ordering.AbstractOrdering#stream(net.
		 * sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
		 */
		@Override
		protected Stream<T> stream(AbstractDialecticalFramework adf) {
			return delegate.stream(adf);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.reasoner.ordering.AbstractOrdering#compare(java
		 * .lang.Object, java.lang.Object,
		 * net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
		 */
		@Override
		protected int compare(T o1, T o2, AbstractDialecticalFramework adf) {
			return delegate.compare(o1, o2, adf);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.reasoner.ordering.AbstractOrdering#order(net.sf
		 * .tweety.arg.adf.syntax.AbstractDialecticalFramework)
		 */
		@Override
		public List<T> order(AbstractDialecticalFramework adf) {
			return delegate.stream(adf)
					.sorted((o1, o2) -> compare(o1, o2, adf))
					.limit(limit)
					.collect(Collectors.toList());
		}
	}

	private static final class FilteredOrdering<T> extends AbstractOrdering<T> {

		private final Predicate<T> filter;

		private final AbstractOrdering<T> delegate;

		/**
		 * @param filter
		 * @param delegate
		 */
		public FilteredOrdering(Predicate<T> filter, AbstractOrdering<T> delegate) {
			this.filter = filter;
			this.delegate = delegate;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.reasoner.ordering.AbstractOrdering#compare(java
		 * .lang.Object, java.lang.Object,
		 * net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
		 */
		@Override
		protected int compare(T o1, T o2, AbstractDialecticalFramework adf) {
			return delegate.compare(o1, o2, adf);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.sf.tweety.arg.adf.reasoner.ordering.AbstractOrdering#stream(net.
		 * sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
		 */
		@Override
		protected Stream<T> stream(AbstractDialecticalFramework adf) {
			return delegate.stream(adf)
					.filter(filter);
		}

	}
}
