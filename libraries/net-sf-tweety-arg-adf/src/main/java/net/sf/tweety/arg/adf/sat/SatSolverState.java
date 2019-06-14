package net.sf.tweety.arg.adf.sat;

import java.util.Collection;

import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;

//TODO contract
public interface SatSolverState extends AutoCloseable {

	// TODO abstract away, since it is only needed for native solvers
	public long getHandle();
	
	public boolean isTrue(Proposition p);

	public boolean add(Disjunction clause);
	
	public boolean add(Collection<Disjunction> clauses);

	public boolean remove(Disjunction clause);
	
	public void setSatCalled();

}
