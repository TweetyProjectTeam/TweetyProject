package net.sf.tweety.arg.adf.sat;

import java.util.Collection;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

//TODO contract - only clauses - no special formulae (i.e. constants)
public interface SatSolverState extends AutoCloseable {
	
	public default boolean satisfiable() {
		return witness() != null;
	}
	
	public Interpretation<PlBeliefSet, PlFormula> witness();
	
	public boolean add(Disjunction clause);
	
	public boolean add(Collection<Disjunction> clauses);

	public boolean remove(Disjunction clause);
	
}
