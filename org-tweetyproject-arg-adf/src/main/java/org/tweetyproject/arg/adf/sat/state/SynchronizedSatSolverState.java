package org.tweetyproject.arg.adf.sat.state;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * A synchronized wrapper of {@link SatSolverState}. May be too restrictive, since it does not know anything about implementation details.
 * 
 * @author Mathias
 *
 */
public final class SynchronizedSatSolverState implements SatSolverState {

	private final SatSolverState delegate;
	
	/**
	 * 
	 * @param delegate delegate
	 */
	public SynchronizedSatSolverState(SatSolverState delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}
	
	@Override
	public synchronized boolean satisfiable() {
		return delegate.satisfiable();
	}

	@Override
	public synchronized Set<Literal> witness() {
		return delegate.witness();
	}

	@Override
	public synchronized Set<Literal> witness(Collection<? extends Literal> filter) {
		return delegate.witness(filter);
	}

	@Override
	public synchronized void assume(Literal literal) {
		delegate.assume(literal);
	}

	@Override
	public synchronized boolean add(Clause clause) {
		return delegate.add(clause);
	}

	@Override
	public synchronized void close() {
		delegate.close();
	}

}
