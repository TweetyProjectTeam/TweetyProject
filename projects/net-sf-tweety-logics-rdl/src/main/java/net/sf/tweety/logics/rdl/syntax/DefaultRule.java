package net.sf.tweety.logics.rdl.syntax;

import java.util.Collection;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.math.probability.Probability;

/**
 * Models a default rule in Reiter's default logic, see [R. Reiter. A logic for default reasoning. Artificial Intelligence, 13:81–132, 1980].
 * 
 * @author Matthias Thimm
 *
 */
public class DefaultRule extends RelationalFormula{

	/** The prerequisite of the default rule */
	private FolFormula pre;
	/** The justifications of the default rule */
	private Collection<FolFormula> jus;
	/** The conclusion of the default rule */
	private FolFormula conc;
	
	public boolean isNormal(){
		//TODO implement me
		return false;
	}
	
	@Override
	public Set<? extends Predicate> getPredicates() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isLiteral() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Set<Variable> getQuantifierVariables() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<Variable> getUnboundVariables() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean containsQuantifier() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isWellBound() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Set<Term<?>> getTerms() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<FOLAtom> getAtoms() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<Functor> getFunctors() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Probability getUniformProbability() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RelationalFormula complement() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Disjunction combineWithOr(Disjunctable formula) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Conjunction combineWithAnd(Conjuctable formula) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RelationalFormula clone() {
		// TODO Auto-generated method stub
		return null;
	}	
}
