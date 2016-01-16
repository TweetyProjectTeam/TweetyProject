package net.sf.tweety.logics.rdl.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.ClassicalInference;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.rdl.DefaultTheory;
import net.sf.tweety.math.probability.Probability;

/**
 * Models a default rule in Reiter's default logic, see [R. Reiter. A logic for
 * default reasoning. Artificial Intelligence, 13:81–132, 1980].
 * 
 * @author Matthias Thimm, Nils Geilen
 *
 */
public class DefaultRule extends RelationalFormula {

	/** The prerequisite of the default rule */
	private FolFormula pre;
	/** The justifications of the default rule */
	private Collection<FolFormula> jus;
	/** The conclusion of the default rule */
	private FolFormula conc;

	public DefaultRule(FolFormula pre, Collection<FolFormula> jus, FolFormula conc) throws ParserException {
		super();
		if (pre == null)
			throw new ParserException("Prerequisite needed to form default rule.");
		if (conc == null)
			throw new ParserException("Conclusion needed to form default rule.");
		if (jus.isEmpty())
			throw new ParserException("Justification needed to form default rule.");
		this.pre = pre;
		this.jus = new LinkedList();
		this.jus.addAll(jus);
		this.conc = conc;
	}

	/*
	 * normal ^= a:c/c
	 */
	public boolean isNormal(DefaultTheory dt) {
		// TODO implement me
		/*
		 * for(FolFormula f: jus){ if(! ClassicalInference.equivalent(f, conc))
		 * return false; } return true;
		 */
		ClassicalInference inf = new ClassicalInference(dt.getFacts());
		for (FolFormula f : jus) {
			if (!inf.eq(f, conc))
				return false;
		}
		return true;
	}

	public FolFormula getPre() {
		return pre;
	}

	public Collection<FolFormula> getJus() {
		return jus;
	}

	public FolFormula getConc() {
		return conc;
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
		Set<Variable> vars = conc.getQuantifierVariables();
		vars.addAll(pre.getQuantifierVariables());
		for (FolFormula f : jus)
			vars.addAll(f.getQuantifierVariables());
		return vars;
	}

	@Override
	public Set<Variable> getUnboundVariables() {
		// TODO Auto-generated method stub
		Set unbound = conc.getUnboundVariables();
		unbound.addAll(pre.getUnboundVariables());
		for (FolFormula f : jus)
			unbound.addAll(f.getUnboundVariables());
		return unbound;
	}

	@Override
	public boolean containsQuantifier() {
		// TODO Auto-generated method stub
		boolean result = conc.containsQuantifier() || pre.containsQuantifier();
		for (FolFormula f : jus)
			result |= f.containsQuantifier();
		return result;
	}

	@Override
	public boolean isWellBound() {
		// TODO Auto-generated method stub
		if (!conc.isWellBound())
			return false;
		if (!pre.isWellBound())
			return false;
		for (FolFormula f : jus)
			if (!f.isWellBound())
				return false;
		return true;
	}

	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		// TODO Auto-generated method stub
		if (!conc.isWellBound(boundVariables))
			return false;
		if (!pre.isWellBound(boundVariables))
			return false;
		for (FolFormula f : jus)
			if (!f.isWellBound(boundVariables))
				return false;
		return true;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		if (!conc.isClosed())
			return false;
		if (!pre.isClosed())
			return false;
		for (FolFormula f : jus)
			if (!f.isClosed())
				return false;
		return true;
	}

	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		// TODO Auto-generated method stub
		if (!conc.isClosed(boundVariables))
			return false;
		if (!pre.isClosed(boundVariables))
			return false;
		for (FolFormula f : jus)
			if (!f.isClosed(boundVariables))
				return false;
		return true;
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
		Set<FOLAtom> atoms = conc.getAtoms();
		atoms.addAll(pre.getAtoms());
		for (FolFormula f : jus)
			atoms.addAll(f.getAtoms());
		return atoms;
	}

	@Override
	public Set<Functor> getFunctors() {
		// TODO Auto-generated method stub
		Set<Functor> funs = conc.getFunctors();
		funs.addAll(pre.getFunctors());
		for (FolFormula f : jus)
			funs.addAll(f.getFunctors());
		return funs;
	}

	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		List<FolFormula> fs = new ArrayList<>();
		for (FolFormula f : jus)
			fs.add(f.substitute(v, t));
		;
		return new DefaultRule((FolFormula)pre.substitute(v, t), fs, (FolFormula)conc.substitute(v, t));
	}

	@Override
	public Probability getUniformProbability() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RelationalFormula complement() {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("");
	}

	@Override
	public Disjunction combineWithOr(Disjunctable formula) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("");
	}

	@Override
	public Conjunction combineWithAnd(Conjuctable formula) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("");
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String result = pre + " :: ";
		Iterator i = jus.iterator();
		if (i.hasNext())
			result += i.next();
		while (i.hasNext())
			result += " ; " + i.next();
		return result + " / " + conc;
	}

	@Override
	public RelationalFormula clone() {
		// TODO Auto-generated method stub
		try {
			return new DefaultRule(pre, jus, conc);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(o instanceof DefaultRule){
			DefaultRule d = (DefaultRule)o;
			for(FolFormula f: this.jus){
				boolean b= false;
				for(FolFormula g: d.jus)
					b|=f.equals(g);
				if(!b)
					return false;
			}
			return this.pre.equals(d.pre) && this.conc.equals(d.conc);
		}
		return false;
	}

}
