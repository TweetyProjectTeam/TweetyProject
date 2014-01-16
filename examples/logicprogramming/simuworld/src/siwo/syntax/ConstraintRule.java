package siwo.syntax;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logicprogramming.asplibrary.syntax.*;

/**
 * this class models a simuworld constraint rule. constraints
 * are used to reject actions leading to unwant world states.
 * 
 * @author Thomas Vengels
 *
 */
public class ConstraintRule {
	public ConstraintRule(List<Atom> constraints) {
		this.C = (constraints == null)?new LinkedList<Atom>() : constraints;
	}
	
	public List<Atom> C;

	
	public Set<Term> getAllTerms() {
		Set<Term> ret = new LinkedHashSet<Term>(); 
		Set<String> already_known = new HashSet<String>();
		for (Atom a : this.C) {
			for (Term t : a.getTerms())
				if (already_known.add(t.toString()))
					ret.add(t);
		}
		return ret;
	}
}
