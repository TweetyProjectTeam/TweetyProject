/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * Implements the Fudge approach to determine the set of acceptable arguments of an AF
 * wrt. preferred semantics and skeptical reasoning, cf. [Thimm, Cerutti, Vallati;
 * 2021, in preparation].
 * 
 * @author Matthias Thimm
 *
 */
public class FudgeAcceptabilityReasoner extends AbstractAcceptabilityReasoner {

	private SatSolver satSolver;
	
	private Map<Argument,Proposition> in;
	private Map<Argument,Proposition> out;
	private Map<Argument,Proposition> undec;
	private Map<Argument,Proposition> in2;
	private Map<Argument,Proposition> out2;
	private Map<Argument,Proposition> undec2;
	private PlBeliefSet baseFormulas;
	private PlBeliefSet baseFormulas2;
	private PlBeliefSet attackFormulas;
	
	/**
	 * Creates a new FudgeAcceptabilityReasoner.
	 * @param satSolver some SatSolver.	 * 
	 */
	public FudgeAcceptabilityReasoner(SatSolver satSolver){
		this.satSolver = satSolver;
	}
	
	/**
	 * Initialises some formulas that are used for every SAT call
	 * @param af some AF
	 */
	private void initBaseFormulas(DungTheory af) {
		this.in = new HashMap<Argument,Proposition>();
		this.out = new HashMap<Argument,Proposition>();
		this.undec = new HashMap<Argument,Proposition>();
		this.baseFormulas = new PlBeliefSet();
		this.in2 = new HashMap<Argument,Proposition>();
		this.out2 = new HashMap<Argument,Proposition>();
		this.undec2 = new HashMap<Argument,Proposition>();
		this.baseFormulas2 = new PlBeliefSet();
		for(Argument a: af){
			in.put(a, new Proposition("in_" + a.getName()));
			out.put(a, new Proposition("out_" + a.getName()));
			undec.put(a, new Proposition("undec_" + a.getName()));
			in2.put(a, new Proposition("in2_" + a.getName()));
			out2.put(a, new Proposition("out2_" + a.getName()));
			undec2.put(a, new Proposition("undec2_" + a.getName()));
			// for every argument only one of in/out/undec can be true
			this.baseFormulas.add(in.get(a).combineWithOr(out.get(a)).combineWithOr(undec.get(a)));
			this.baseFormulas.add((PlFormula)in.get(a).complement().combineWithOr(out.get(a).complement()));
			this.baseFormulas.add((PlFormula)in.get(a).complement().combineWithOr(undec.get(a).complement()));
			this.baseFormulas.add((PlFormula)out.get(a).complement().combineWithOr(undec.get(a).complement()));
			this.baseFormulas2.add(in2.get(a).combineWithOr(out2.get(a)).combineWithOr(undec2.get(a)));
			this.baseFormulas2.add((PlFormula)in2.get(a).complement().combineWithOr(out2.get(a).complement()));
			this.baseFormulas2.add((PlFormula)in2.get(a).complement().combineWithOr(undec2.get(a).complement()));
			this.baseFormulas2.add((PlFormula)out2.get(a).complement().combineWithOr(undec2.get(a).complement()));
		}	
		// an argument is in iff all attackers are out
		this.attackFormulas = new PlBeliefSet();
		Disjunction oneAttack = new Disjunction();
		this.attackFormulas.add(oneAttack);
		for(Argument a: af){
			if(af.getAttackers(a).isEmpty()){
				this.baseFormulas.add(((PlFormula)in.get(a)));
				this.baseFormulas2.add(((PlFormula)in2.get(a)));
			}else{
				Collection<PlFormula> attackersOr = new HashSet<PlFormula>();//new Contradiction();
				Collection<PlFormula> attackersNotOr = new HashSet<PlFormula>();//new Contradiction();
				Collection<PlFormula> attackersOr2 = new HashSet<PlFormula>();//new Contradiction();
				Collection<PlFormula> attackersNotOr2 = new HashSet<PlFormula>();//new Contradiction();
				for(Argument b: af.getAttackers(a)){
					attackersOr.add(in.get(b));
					attackersNotOr.add((PlFormula)out.get(b).complement());					
					this.baseFormulas.add(((PlFormula)in.get(a).complement()).combineWithOr((PlFormula)out.get(b)));
					attackersOr2.add(in2.get(b));
					attackersNotOr2.add((PlFormula)out2.get(b).complement());					
					this.baseFormulas2.add(((PlFormula)in2.get(a).complement()).combineWithOr((PlFormula)out2.get(b)));
					Proposition attack = new Proposition("r" + b.getName() + "_" + a.getName());
					oneAttack.add(attack);
					this.attackFormulas.add(new Negation(attack).combineWithOr(this.in.get(b)));
					this.attackFormulas.add(new Negation(attack).combineWithOr(this.in2.get(a)));
					this.attackFormulas.add(attack.combineWithOr(new Negation(this.in2.get(a))).combineWithOr(new Negation(this.in.get(b))));
				}
				this.baseFormulas.add(new Disjunction(attackersOr).combineWithOr((PlFormula)out.get(a).complement()));
				this.baseFormulas.add(new Disjunction(attackersNotOr).combineWithOr((PlFormula)in.get(a)));	
				this.baseFormulas2.add(new Disjunction(attackersOr2).combineWithOr((PlFormula)out2.get(a).complement()));
				this.baseFormulas2.add(new Disjunction(attackersNotOr2).combineWithOr((PlFormula)in2.get(a)));
			}
		}
	}
	
	/**
	 * Determines some admissible set E with t subset of E and s intersected with E is non-empty (or returns null if no such extension exists).
	 * @param af some Dung theory
	 * @param t a set of arguments (those already shown to be skeptically preferred accepted)
	 * @param s a set of arguments (candidates for being skeptically preferred accepted)
	 * @return a set of arguments or null
	 */
	private Collection<Argument> admExt(DungTheory af, Collection<Argument> t, Collection<Argument> s){
		PlBeliefSet beliefSet = new PlBeliefSet();
		beliefSet.addAll(this.baseFormulas);
		// enforce that all argument of t are in
		for(Argument a: t)
			beliefSet.add(in.get(a));
		//  s intersected with E is non-empty
		Disjunction d = new Disjunction();
		for(Argument a: s)
			d.add(this.in.get(a));
		beliefSet.add(d);
		PossibleWorld w = (PossibleWorld) this.satSolver.getWitness(beliefSet);
		if(w == null)
			return null;
		Collection<Argument> args = new HashSet<Argument>();
		for(Proposition p: w)
			if(p.getName().startsWith("in_"))
				args.add(new Argument(p.getName().substring(3)));
		return args;
	}
	
	/**
	 * Returns an admissible set S with S subset of t or null if no such set exists.
	 * @param af some Dung theory
	 * @param t a set of arguments
	 * @return a set of arguments or null
	 */
	private Collection<Argument> admExt(DungTheory af, Collection<Argument> t){
		PlBeliefSet beliefSet = new PlBeliefSet();
		beliefSet.addAll(this.baseFormulas);
		// enforce that all argument of t are in
		for(Argument a: t)
			beliefSet.add(in.get(a));
		PossibleWorld w = (PossibleWorld) this.satSolver.getWitness(beliefSet);
		if(w == null)
			return null;
		Collection<Argument> args = new HashSet<Argument>();
		for(Proposition p: w)
			if(p.getName().startsWith("in_"))
				args.add(new Argument(p.getName().substring(3)));
		return args;		
	}
	
	/**
	 * Returns an admissible set S' s.t. there is an admissible set S'' with<br/>
	 * (1) S is a subset of S'',<br/>
	 * (2)S' attacks S'', and<br/>
	 * (3)S' is not a subset of t for all t in u.<br/>
	 * If there is no such S' then null is returned.
	 * @param af some Dung Theory
	 * @param s some set of arguments
	 * @param u a set of sets of arguments
	 * @return a set of arguments or null
	 */
	private Collection<Argument> admExtAtt(DungTheory af, Collection<Argument> s, Collection<Collection<Argument>> u){
		PlBeliefSet beliefSet = new PlBeliefSet();
		// set S'
		beliefSet.addAll(this.baseFormulas);
		// set S''
		beliefSet.addAll(this.baseFormulas2);
		//(1) S is a subset of S''		
		for(Argument a: s)
			beliefSet.add(this.in2.get(a));
		// (2)S' attacks S''
		beliefSet.addAll(this.attackFormulas);
		// (3)S' is not a subset of t for all t in u
		for(Collection<Argument> t: u) {
			Disjunction d = new Disjunction();
			for(Argument a: af)
				if(!t.contains(a))
				d.add(in.get(a));
			beliefSet.add(d);
		}
		PossibleWorld w = (PossibleWorld) this.satSolver.getWitness(beliefSet);
		if(w == null)
			return null;
		Collection<Argument> args = new HashSet<Argument>();
		for(Proposition p: w)
			if(p.getName().startsWith("in_"))
				args.add(new Argument(p.getName().substring(3)));
		return args;
	}
	
	@Override
	public Collection<Argument> getAcceptableArguments(DungTheory aaf) {
		this.initBaseFormulas(aaf);
//		\State $C$ $:=$ $\arguments$
		Collection<Argument> c = new HashSet<Argument>(aaf);
//		\State $T$ $:=$ $\emptyset$ 
		Collection<Argument> t = new HashSet<Argument>();
//		\While{\textsc{True}}
		while(true) {
//		    \If{$C$ $=$ $\emptyset$}
//   		    \State\Return $T$
//    		\EndIf
			if(c.isEmpty())
				return t;
//    		\State $E$ $:=$ $\textsf{AdmExt}(\AF, T, C)$
			Collection<Argument> e = admExt(aaf,t,c);
//    		\If{$E$ $=$ \textsc{False}}
//    		\State\Return $T$
//    		\EndIf
			if(e == null)
				return t;
//    		\State $C$ $:=$ $C \setminus E^+$	
			c.removeAll(aaf.getAttacked(e));
//    		\For{$a\in E\cap C$}
			for(Argument a: e) {
				if(!c.contains(a))
					continue;			
//    		    \State $S$ $:=$ $\emptyset$
				Collection<Collection<Argument>> s = new HashSet<Collection<Argument>>();
//    		    \While{\textsc{True}}
				while(true) {
//    		        \State $E'$ $:=$ $\textsf{AdmExtAtt}(\AF,T\cup\{a\},S)$
					Collection<Argument> tp = new HashSet<Argument>(t);
					tp.add(a);
					Collection<Argument> ep = admExtAtt(aaf,tp,s);
//    		        \If{$E'$ $=$ \textsc{False}}
					if(ep == null) {
//    		         	\State $T$ $:=$ $T\cup \{a\}$
						t.add(a);
//    		         	\State $C$ $:=$ $C\setminus \{a\}$
						c.remove(a);
//    		         	\State \textbf{break}
						break;
//        		    \EndIf
					}
//        		    \State $C$ $:=$ $C \setminus E'^+$
					c.removeAll(aaf.getAttacked(ep));
//        		    \State $E''$ $:=$ $\textsf{AdmExt}(\AF,E'\cup T \cup\{a\})$
					Collection<Argument> eppp = new HashSet<Argument>(ep);
					eppp.addAll(t);
					eppp.add(a);
					Collection<Argument> epp = admExt(aaf,eppp);
//        		    \If{$E''$ $=$ \textsc{False}}
					if(epp == null) {
//        		        \State $C$ $:=$ $C \setminus \{a\}$
						c.remove(a);
//        		        \State \textbf{break}
						break;
//        		    \EndIf
					}
//        		    \State $C$ $:=$ $C \setminus E''^+$
					c.removeAll(aaf.getAttacked(epp));
//        		    \State $S$ $:=$ $S$ $\cup$ $\{E''\}$
					s.add(epp);
//    		    \EndWhile
				}
//    		\EndFor
			}
//		\EndWhile
		}
	}

	@Override
	public boolean isInstalled() {
		return this.satSolver.isInstalled();
	}
}
