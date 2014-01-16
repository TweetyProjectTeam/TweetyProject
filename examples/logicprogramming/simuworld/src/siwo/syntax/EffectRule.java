package siwo.syntax;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.logicprogramming.asplibrary.syntax.*;

/**
 * this class models an effect rule. effect rules are applied
 * automatically when updating the world state, compared to
 * action rules which must be triggered by agents.
 * 
 * @author Thomas Venges
 *
 */
public class EffectRule {
	public EffectRule() {
		this.C = new LinkedList<Literal>();
		this.A = new LinkedList<Atom>();
		this.D = new LinkedList<Atom>();
		this.B = new LinkedList<Atom>();
	}
	
	public List<Literal> C;
	public List<Atom> A;
	public List<Atom> D;
	public List<Atom> B;	
		
	public Set<Term> getAllTerms() {
		Set<Term> ret = new LinkedHashSet<Term>(); 
		Set<String> already_known = new HashSet<String>();
		for (Literal l : this.C) {
			Atom a = l.getAtom();
			for (Term t : a.getTerms())
				if (already_known.add(t.toString()))
					ret.add(t);
		}
		return ret;
	}

	public void getFluents(Map<String, Integer> fluentArity) {
		for (Literal l : this.A) {
			Atom a = l.getAtom();
			fluentArity.put(a.getSymbol(), a.getArity());
		}
		for (Literal l : this.D) {
			Atom a = l.getAtom();
			fluentArity.put(a.getSymbol(), a.getArity());
		}
	}
}
