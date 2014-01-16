package net.sf.tweety.logics.fol.syntax;

import java.util.*;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.FunctionalTerm;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Variable;

/**
 * This abstract class captures the common functionalities of both
 * formulas and terms.
 * @author Matthias Thimm
 */
public abstract class LogicStructure {
	/**
	 * Returns all constants that appear in this structure.
	 * @return all constants that appear in this structure.
	 */
	public abstract Set<Constant> getConstants();
	
	/**
	 * Returns all functors that appear in this structure.
	 * @return all functors that appear in this structure.
	 */
	public abstract Set<Functor> getFunctors();
	
	/**
	 * Returns all variables that appear in this structure.
	 * @return all variables that appear in this structure.
	 */
	public abstract Set<Variable> getVariables();
	
	/**
	 * Returns all functional terms that appear in this structure.
	 * @return all functional terms that appear in this structure.
	 */
	public abstract Set<FunctionalTerm> getFunctionalTerms();
	
	/**
	 * Checks whether this structure contains any functional terms.
	 * @return "true" if this structure contains a functional term.
	 */
	public boolean containsFunctionalTerms(){
		return !this.getFunctionalTerms().isEmpty();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.FolFormula#toString()
	 */
	@Override
	public abstract String toString();
	
}
