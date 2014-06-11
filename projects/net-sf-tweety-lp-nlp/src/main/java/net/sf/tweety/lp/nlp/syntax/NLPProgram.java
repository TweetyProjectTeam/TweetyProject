package net.sf.tweety.lp.nlp.syntax;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.commons.util.rules.RuleSet;
import net.sf.tweety.logics.commons.syntax.interfaces.LogicProgram;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * A nested logic program
 * @author Tim Janus
 */
public class NLPProgram 
	extends RuleSet<NLPRule> 
	implements LogicProgram<FolFormula, FolFormula, NLPRule> {
	
	/** kill warning */
	private static final long serialVersionUID = 1050122194243070233L;

	/**
	 * @return	A set of facts as FOL formulas
	 */
	public Set<FolFormula> getFacts() {
		Set<FolFormula> reval = new HashSet<FolFormula>();
		for(NLPRule rule : this) {
			if(rule.isFact()) {
				reval.add(rule.getConclusion());
			}
		}
		return reval;
	}
	
	@Override
	public void addFact(FolFormula fact) {
		this.add(new NLPRule(fact));
	}
	
	
	@Override
	public FolSignature getSignature() {
		FolSignature reval = new FolSignature();
		for(NLPRule rule : this) {
			reval.addSignature(rule.getSignature());
		}
		return reval;
	}

	@Override
	public NLPProgram substitute(Term<?> t, Term<?> v) {
		NLPProgram reval = new NLPProgram();
		for(NLPRule rule : this) {
			reval.add(rule.substitute(t, v));
		}
		return reval;
	}
	
	@Override
	public NLPProgram substitute(
			Map<? extends Term<?>, ? extends Term<?>> map)
			throws IllegalArgumentException {
		NLPProgram reval = this;
		for(Term<?> t : map.keySet()) {
			reval = reval.substitute(t, map.get(t));
		}
		return reval;
	}

	@Override
	public NLPProgram exchange(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		NLPProgram reval = new NLPProgram();
		for(NLPRule rule : this) {
			reval.add((NLPRule)rule.exchange(t, v));
		}
		return reval;
	}
}
