/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.lp.asp.syntax;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;


/**
 * This class models a rule for a disjunctive logic program.
 * A rule is a collection of literals and more sophisticated rule elements
 * like Aggregate or Arithmetic. It uses separate lists for the
 * head and the body. It also implements the Comparable interface to allow
 * the ordering in collections.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 *
 */
public class Rule 
	extends 	DLPElementAdapter 
	implements 	Comparable<Rule>, 
				DLPElement, 
				net.sf.tweety.commons.util.rules.Rule<DLPHead, DLPElement> {

	DLPHead head = new DLPHead();
	List<DLPElement>	body = new LinkedList<DLPElement>();
	
	/** Default-Ctor: Creates an empty rule without any head literals or body elements */
	public Rule() {}
	
	/** Copy-Ctor: Makes a deep copy of the given rule */
	public Rule(Rule other) {
		for(DLPLiteral headLits : other.head) {
			this.head.add((DLPLiteral)headLits.clone());
		}
		
		for(DLPElement bodyElement : other.body) {
			this.body.add((DLPElement)bodyElement.clone());
		}
	}
	
	/** 
	 * Ctor: Create a rule with the given head, cause there are no
	 * body elements the created rule is a fact.
	 * 
	 * @param head	The head of the rule as ELPHead
	 */
	public Rule(DLPHead head) {
		this.head = head;
	}
	
	/**
	 * Ctor: Create a rule with the given head, cause there are no
	 * body elements the created rule is a fact.
	 * 
	 * @param head	The head of the rule as ELPLiteral
	 */
	public Rule(DLPLiteral head) {
		this.head.add(head);
	}
	
	public Rule(DLPLiteral head, DLPElement body) {
		this.head.add(head);
		this.body.add(body);
	}
	
	public Rule(DLPLiteral head, List<DLPElement> litsBody) {
		this.head.add(head);
		this.body.addAll(litsBody);
	}
	
	public Rule(List<DLPLiteral> litsHead, List<DLPElement> litsBody) {
		this.head.addAll(litsHead);
		this.body.addAll(litsBody);
	}
	
	public Rule(String ruleexpr) {
		/*
		try {
			ELPParser ep = new ELPParser( new StringReader( ruleexpr ));
			Rule r = ep.rule();
			this.head = r.head;
			this.body = r.body;
		} catch (Exception e) {
			System.err.println("Rule: could not parse input!");
			System.err.println(e);
			System.err.println("Input: " + ruleexpr);
		}
		*/
	}
	
	@Override
	public SortedSet<DLPLiteral> getLiterals() {
		SortedSet<DLPLiteral> literals = new TreeSet<DLPLiteral>();
		literals.addAll(head);
		for(DLPElement pe : body) {
			literals.addAll(pe.getLiterals());
		}
		return literals;
	}
	
	@Override
	public	boolean		isFact() {
		return body.isEmpty() && head.size() >= 1;
	}
	
	@Override
	public	boolean		isConstraint() {
		return head.size() == 0;
	}
	
	/**
	 * Proofs if the given rule is safe for use in a solver.
	 * To get a felling when a rule is safe read the following text
	 * from the dlv documentation:
	 * 
	 * A variable X in an aggregate-free rule is safe if at least one of the following conditions is satisfied:
	 * X occurs in a positive standard predicate in the body of the rule;
	 * X occurs in a true negated standard predicate in the body of the rule;
	 * X occurs in the last argument of an arithmetic predicate A and all other arguments of A are safe. (*not supported yet)
	 * A rule is safe if all its variables are safe. However, cyclic dependencies are disallowed, e.g., :- #succ(X,Y), #succ(Y,X) is not safe.
	 * 
	 * @return true if the rule is safe considering the above conditions, false otherwise.
	 */
	public boolean isSafe() {
		Set<Term<?>> variables = new HashSet<Term<?>>();
		Set<DLPLiteral> allLit = getLiterals();
		
		// TODO: only depth of one... the entire asp-library has major desing issues... best solution: Redesign core interfaces
		// TOTALLY HACKED WILL NOT WORK FOR EVERYTHING:...
		for(DLPLiteral l : allLit) {
			if(!l.isGround()) {
				for(Term<?> t : l.getAtom().getTerms()) {
					if(t instanceof Variable) {
						variables.add((Variable)t);
					} else if(t instanceof DLPAtom) {
						if(!((DLPAtom)t).isGround()) {
							for(Term<?> t2 : ((DLPAtom)t).getTerms()) {
								if(t2 instanceof Variable) {
									variables.add((Variable)t2);
								} else if(t2 instanceof Constant) {
									Constant st = (Constant) t2;
									if(st.get().charAt(0) >= 65 && st.get().charAt(0) <= 90) {
										variables.add(st);
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(variables.size() == 0)
			return true;
		
		for(Term<?> x : variables) {
			boolean safe = false;
			for(DLPLiteral l : allLit) {
				if(	l instanceof DLPNeg || l instanceof DLPAtom ) {
					for(Term<?> t : l.getAtom().getTerms()) {
						if(t.equals(x)) {
							safe = true;
						}
					}
				}
			}
			
			if(!safe)
				return false;
		}
		
		return true;
	}
	
	@Override
	public String	toString() {
		String ret = "";
		if (head.size() > 0) {
			ret += head.toString();	
		}
		if (body.size() > 0) {
			ret += ":- " + body.get(0);
			for(int i=1; i<body.size(); ++i) {
				ret += ", " + body.get(i);
			}
		}
		ret += ".";
		
		return ret;
	}
	
	@Override 
	public boolean equals(Object other) {
		if(!(other instanceof Rule)) 	return false;
		Rule or = (Rule)other;
		
		boolean reval = this.head.equals(or.head) && this.body.equals(or.body);
		return reval;
	}
	
	@Override
	public int hashCode() {
		return head.hashCode() + body.hashCode();
	}

	@Override
	public int compareTo(Rule arg0) {
		int comp = 0;
		
		// facts first:
		if(getPremise().size() == 0 && arg0.getPremise().size() != 0) {
			return -1;
		} else if(getPremise().size() != 0 && arg0.getPremise().size() == 0) {
			return 1;
		}
		
		// then order alphabetically starting by the head.
		comp = getConclusion().toString().compareTo(arg0.getConclusion().toString());
		if(comp != 0)
			return comp;
		
		//TODO: This implementation is not compatible with equals, because
		//      the comparison depends on the order in which the elements
		//      have been added to the premise list. Ex.:
		//      compareTo(:- a,b., :- b,a.) = -1 
		// if the head is the same use the body.
		for(int i=0; i<body.size() && i<arg0.body.size(); ++i) {
			comp = body.get(i).toString().compareTo(arg0.body.get(i).toString());
			if(comp != 0)
				return comp;
		}
		
		return comp;
	}
	
	@Override
	public Rule clone() {
		return new Rule(this);
	}

	@Override
	public DLPSignature getSignature() {
		DLPSignature reval = new DLPSignature();
		reval.addSignature(head.getSignature());
		for(DLPElement bodyElement : body) {
			reval.addSignature(bodyElement.getSignature());
		}
		return reval;
	}

	@Override
	public Collection<DLPElement> getPremise() {
		return Collections.unmodifiableList(body);
	}

	@Override
	public DLPHead getConclusion() {
		return head;
	}

	@Override
	public Rule substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		Rule reval = new Rule();
		reval.head = head.substitute(v, t);
		for(DLPElement bodyElement : body) {
			reval.body.add(bodyElement.substitute(v,t));
		}
		return reval;
	}

	@Override
	public Set<DLPAtom> getAtoms() {
		Set<DLPAtom> reval = new HashSet<DLPAtom>();
		reval.addAll(head.getAtoms());
		for(DLPElement bodyElement : body) {
			reval.addAll(bodyElement.getAtoms());
		}
		return reval;
	}

	@Override
	public Set<DLPPredicate> getPredicates() {
		Set<DLPPredicate> reval = new HashSet<DLPPredicate>();
		reval.addAll(head.getPredicates());
		for(DLPElement bodyElement : body) {
			reval.addAll(bodyElement.getPredicates());
		}
		return reval;
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = new HashSet<Term<?>>();
		reval.addAll(head.getTerms());
		for(DLPElement bodyElement : body) {
			reval.addAll(bodyElement.getTerms());
		}
		return reval;
	}

	@Override
	public void setConclusion(DLPHead conclusion) {
		if(conclusion==null) {
			this.head.clear();
		} else {
			this.head = conclusion;
		}
	}
	
	public void setConclusion(DLPLiteral literal) {
		this.head.clear();
		this.head.add(literal);
	}

	@Override
	public void addPremise(DLPElement premise) {
		this.body.add(premise);
	}

	@Override
	public void addPremises(Collection<? extends DLPElement> premises) {
		this.body.addAll(premises);
	}
}
