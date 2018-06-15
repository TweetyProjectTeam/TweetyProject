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
package siwo.simulator;

import siwo.syntax.*;

import net.sf.tweety.logicprogramming.asplibrary.solver.*;
import net.sf.tweety.logicprogramming.asplibrary.syntax.*;
import net.sf.tweety.logicprogramming.asplibrary.util.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * reference implementation using disjunctive logical programs
 * with weak constraints for world state transition computation
 * 
 * @author Thomas Vengels
 *
 */
public class DLVSimulator extends AbstractSimulator {

	DLVComplex dlvx = null;
	
	/**
	 * default constructor, a dlv complex instance is
	 * required for transition computation
	 * 
	 * @param dlvx instance to a dlv complex wrapper
	 */
	public DLVSimulator(DLVComplex dlvx) {
		this.dlvx = dlvx;
	}
	
	@Override
	public WorldState performStep(WorldState current_state) {

		// create state transition program
		Program p = this.getProgram(current_state);
		
		// call solver
		List<AnswerSet> asl = dlvx.computeModels(p, 1);
		
		// create a new state
		WorldState w2 = new WorldState();
		
		// adapt answer set as new world state
		// first answer set is adapted as a new world state
		if (asl.size() > 0) {
			AnswerSet as = asl.get(0);
			
			// we do not want auxilliary attributes inside an
			// answer set
			System.out.println("answerset, literals: " +as.size()+", conflicts "+as.weight);
			
			// create a new world state
			w2.F = as;
			w2.A.addAll( current_state.A );
			w2.C.addAll( current_state.C );
			w2.I.addAll( current_state.I );
			w2.Rpre.addAll( current_state.Rpre );
			
			return w2;
			
		} else {
			// big problem here..
			System.out.println("failed calculating world transition");
		}
		
		return null;
	}
	
	
	/**
	 * this method transforms a given simworld into
	 * an extended logic program
	 * 
	 * @param sw world state to transform
	 * @return corresponding elp to sw
	 */
	public Program getProgram(WorldState sw) {
		// return value
		Program p = new Program();
		Rule r = new Rule();
		
		// number of possible future world states
		// that should be evaluated
		int max_level=0;
		
		// state transition from current simworld state
		// to a possible future state.
		//
		// base algorithm:
		// * transform all rules from current_state
		// * run an asp solver (for example dlv-complex)
		// * parse unique answer set, and adapt is as a new world state
		
		// transformation.
		// first, collect all fluent symbols
		Map<String,Integer> fluentArity = new HashMap<String,Integer>();
		Set<String> fluents = fluentArity.keySet();
		for (ActionRule ar : sw.A)
			ar.getFluents(fluentArity);
		for (EffectRule er : sw.E)
			er.getFluents(fluentArity);
		
		// catch action symbols
		Map<String,Integer> actions = new HashMap<String,Integer>();
		for (ActionRule ar : sw.A) {
			actions.put( ar.name.getSymbol(), ar.name.getArity());
		}
		
		// transform facts from initial world description
		addComment(p,"initial facts");
		for (String pred : sw.F.literals.keySet()) {
			for (Literal l : sw.F.literals.get(pred)) {
				r = new Rule();				
				r.addHead( fluentLiteral(l,fluents));
				p.add(r);
			}
		}
		
		// transformation of initial rules
		addComment(p,"initial rules");
		for (Rule q : sw.Rpre) {
			r = new Rule();
			
			// transform head
			r.addHead( fluentLiteral(q.getHead().get(0),fluents) );
			
			// transform body literals
			for (Literal l : q.getBody()) {
				r.addBody( fluentLiteral(l,fluents));
			}
			p.add(r);
		}
		
				
		// add intentions (actions that agents signaled to perform)
		addComment(p," agents intentions");
		for (Atom a : sw.I) {
			r = new Rule();
			r.addHead( a.rewrite("intends_"));
			p.add(r);
		}
		
		// transform all action rules
		// rule index counter
		int i = 1;
		for (ActionRule ar : sw.A) {
			p.add( this.transformActionRule(max_level, ar, fluents, i) );
			++i;
		}
				
		// transform all constraint rules
		i = 1;
		for (ConstraintRule cr : sw.C) {
			p.add( this.transformConstraintRule(max_level, cr, fluents, i) );
			++i;
		}
		
		// transform all effect rules
		i = 1;
		addComment(p," effects");
		for (EffectRule er : sw.E) {
			p.add( this.transformEffect(er, fluents, i, actions));
			++i;
		}
		
		// propagate fluents as facts that are not removed,
		// i.e. fluents that stay by inertia
		addComment(p," fluents");
		for (Entry<String,Integer> f : fluentArity.entrySet()) {
			
			Literal fluent = rewrite(f.getKey(),f.getValue());
			Literal fluPrev = rewrite("prev_"+f.getKey(),f.getValue());
			Literal fluDel = rewrite("del_"+f.getKey(),f.getValue());
			
			// fluents staying through inertia
			r = new Rule();
			r.addHead( fluent );
			r.addBody( fluPrev );
			r.addBody( new Not( fluDel ));
			p.add(r);						
		}
		
		// propagate fluents for alternate world state
		addComment(p," alternate world fluents");
		for (Entry<String,Integer> f : fluentArity.entrySet()) {
			
			Literal fluent = rewrite("maybe_"+f.getKey(),f.getValue());
			Literal fluPrev = rewrite("prev_"+f.getKey(),f.getValue());
			Literal fluDel = rewrite("del_"+f.getKey(),f.getValue());
			Literal fluNot = rewrite("not_"+f.getKey(),f.getValue());
			
			// all fluents from new state are part of the
			// possible new state, too
			r = new Rule();
			r.addHead( fluent );
			r.addBody( fluPrev );
			r.addBody( new Not( fluDel ));
			r.addBody( new Not( fluNot ));
			p.add(r);			
		}
		
		return p;
	}

	/**
	 * this method transforms an action rule into the
	 * corresponding extended logical program
	 * 
	 * @param fluents	set of volatile atom functors
	 * @param ruleIndex unique rule number
	 * @return asp representation of given action rule
	 */
	protected Program transformActionRule(int max_level, ActionRule ar, Set<String> fluents, int ruleIndex ) {
		Program p = new Program();
		
		addComment(p,ar.name.getSymbol() + " action");
		
		// every action rule with trigger ar(T) and condition C(V), where
		// T and V are terms, is rewritten as:
		//
		// action_ruleindex_ar(T + V) :- intends_ar(T), C(V)		
		
		Rule r = null;
		Term ag = ar.name.getTerm(ar.name.getArity()-1);
		LinkedHashSet<Term> lhs = ar.getAllTerms();
		Atom arName = Atom.instantiate("apply_"+ruleIndex+"_"+ar.name.getSymbol(), lhs);
		Atom arFire = Atom.instantiate("take_"+ruleIndex+"_"+ar.name.getSymbol(), lhs);
		Literal arDoNotFire = new Neg(arFire);
		Atom arFail = Atom.instantiate("fail_"+ruleIndex+"_"+ar.name.getSymbol(), lhs);
		
		// action rule is applicable if an agents intends it, and C is fulfilled		
		r = new Rule();
		r.addHead( arName );
		r.addBody( ar.name.rewrite("intends_" ));
		for (Literal l : ar.C) {
			r.addBody( fluentLiteral(l, fluents));
		}		
		r.addBody( new Not( new Atom("blocked",ag)));
		p.add(r);
		
		// arbitrarily take or leave an applicable action
		r = new Rule();
		r.addHead(arFire);
		r.addHead(arDoNotFire);
		r.addBody(arName);
		p.add(r);
			
		// add rules for fact derivation / suppression
		// if we take a rule, any assumption holds
		for (Atom a : ar.A) {
			r = new Rule();
			r.addHead( a );
			r.addBody( arFire );
			p.add(r);
		}
		// if we take a rule, don't hold on any disposable
		for (Atom a : ar.D) {
			r = new Rule();
			r.addHead( a.rewrite("del_"));
			r.addBody( arFire );
			p.add(r);
		}
		
		// todo: add missed oportunities
		// these are applicable action dropped
		// that will not cause any conflict with the
		// initially world state.
		for (Atom a : ar.A) {
			r = new Rule();
			r.addHead( a.rewrite("maybe_"));
			r.addBody( arDoNotFire );
			p.add(r);
		}
		for (Atom a : ar.D) {
			r = new Rule();
			r.addHead( a.rewrite("not_"));
			r.addBody( arDoNotFire );
			p.add(r);
		}
		
		// verify failure of leaved rule
		for (Atom a : ar.A) {
			r = new Rule();
			r.addHead( arFail );
			r.addBody( arDoNotFire );
			r.addBody( a.rewrite("conflict_"));
			p.add(r);
		}
		
		// deny solution if a dropped rule will not cause a conflict
		WeakConstraint wc = new WeakConstraint();
		wc.addBody( arDoNotFire );
		wc.addBody( new Not( arFail ));
		p.add(wc);
		
		return p;
	}

	/**
	 * this method transforms a constraint rule into
	 * an extended logical program.
	 * 
	 * @param cr constraint rule to transform
	 * @param fluents fluents within simulation world
	 * @param ruleIndex unique rule number
	 * @return
	 */
	protected Program transformConstraintRule(int max_level, ConstraintRule cr, Set<String> fluents, int ruleIndex) {
		Program p = new Program();
		addComment(p," constraint "+ruleIndex);
		for (int i = 0; i <= max_level; i++) {
			Program p2 = transformConstraintRule2(i+"_",cr,fluents,ruleIndex);
			p.add(p2);
		}
		return p;
	}
	
	/**
	 * helper function for constraint transformation.
	 * 
	 * @param level intended symbol level
	 * @param cr constraint rule
	 * @param fluents set of fluent symbols
	 * @param ruleIndex unique rule index
	 * @return
	 */
	protected Program transformConstraintRule2(String level, ConstraintRule cr, Set<String> fluents, int ruleIndex) {
		Program p = new Program();
		Rule r = null;
		
		// part 1: take the constraint as a hard constraint
		r = new Rule();
		r.addBody( cr.C );
		p.add(r);
					
		// part 2: add additional tests for dropped actions
		// get ground atom of constraint
		Set<Term> terms = cr.getAllTerms();
		Atom cName = Atom.instantiate("constraint_"+ruleIndex, terms);
		List<Atom> conflicts = new LinkedList<Atom>();
		
		r = new Rule();
		r.addHead(cName);
		for (Atom a : cr.C) {
			// if a is a fluent, it will cause conflicts.
			if (fluents.contains(a.getSymbol())) {
				// remember a
				conflicts.add(a);
				// rewrite it as maybe_a
				r.addBody( a.rewrite("maybe_"));
			} else {
				// a is background knowledge
				r.addBody(a);			
			}
		}
		p.add(r);
		
		// if constraint fires, we have to
		// indicate a conflict
		for (Atom a : conflicts) {
			r = new Rule();
			r.addHead( a.rewrite("conflict_"));
			r.addBody( cName );
			p.add(r);
		}
		
		return p;
	}
	
	
	/**
	 * helper method to transform an effect. unlike actions, effects
	 * cannot be blocked which results in a leaner transformation.
	 * 
	 * @param er effect rule 
	 * @param fluents set of fluent identifiers
	 * @param index unique number of effect rule
	 * @param act indices of actions (used for blocking)
	 * @return
	 */
	protected Program transformEffect(EffectRule er, Set<String> fluents, int index, Map<String,Integer> act) {
		Program p = new Program();
		
		addComment(p," effect "+index);
		
		Rule r = new Rule();
		
		// collect variables
		Set<Term> st = er.getAllTerms();
		Atom erApply = Atom.instantiate("effect_"+index, st);
		
		// rewrite condition
		r = new Rule();
		r.addHead(erApply);
		for (Literal l : er.C)
			r.addBody( fluentLiteral(l,fluents));
		p.add(r);
		
		// transform all A effects
		for (Literal l: er.A) {
			r = new Rule();
			r.addHead( l );
			r.addBody(erApply);
			p.add(r);
		}
		// transform all D effects
		for (Literal l: er.D) {
			r = new Rule();
			r.addHead( l.getAtom().rewrite("del_") );
			r.addBody(erApply);
			p.add(r);
		}
		// transform all B effects
		for (Literal l : er.B) {
			String aName = l.getAtom().getSymbol();
			Integer aIndex = act.get(aName);
			int aI = aIndex;
			if (aI == l.getAtom().getArity()) {
				Literal intends = l.getAtom().rewrite("intends_");
				Term agTerm = l.getAtom().getTerm(aIndex-1);
				r = new Rule();
				r.addHead( new Atom("blocked",agTerm));
				r.addBody( intends );
				r.addBody( erApply );
				p.add(r);
			}
		}
		
		return p;
	}
	
	
	protected Literal fluentLiteral(Literal l, Set<String> fluents) {
		Atom atm = l.getAtom();
		if (fluents.contains(atm.getSymbol())) {
			if (l.isDefaultNegated())
				return new Not( atm.rewrite("prev_"));
			else
				return atm.rewrite("prev_");
		} else {
			return l;
		}
	}
	
	
	
	protected Literal rewrite(String functor, int arity) {
		Term t[] = new Term[arity];
		for (int i = 0; i < arity; i++)
			t[i] = new StdTerm("A"+i);
		return new Atom(functor,t);
	}
	
	protected void addComment(Program p, String text) {

		p.add( new Comment(
			"\r\n\r\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
			"% " + text
		));
	}
	
}
