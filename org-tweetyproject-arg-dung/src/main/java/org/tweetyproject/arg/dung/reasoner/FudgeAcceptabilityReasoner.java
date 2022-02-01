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
import org.tweetyproject.logics.pl.sat.DimacsSatSolver;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
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

	private DimacsSatSolver satSolver;
	
	private Map<Argument,Proposition> in;
	private Map<Argument,Proposition> out;
	private Map<Argument,Proposition> undec;
	private Map<Argument,Proposition> in2;
	private Map<Argument,Proposition> out2;
	private Map<Argument,Proposition> undec2;

	private String cnf_baseFormulas; 
	private int num_cnf_baseFormulas;
	private String cnf_baseFormulas2;
	private int num_cnf_baseFormulas2;
	private String cnf_attackFormulas;
	private int num_cnf_attackFormulas;
	
	// the next index used for generating propositions
	private int index;
	// maps propositions to the number used by the SAT solver
	private Map<Proposition,Integer> prop_index;
	// maps numbers used by the SAT solver to propositions
	private Map<Integer,Proposition> prop_inverted_index;
	
	/**
	 * Creates a new FudgeAcceptabilityReasoner.
	 * @param satSolver some DimacsSatSolver.	 * 
	 */
	public FudgeAcceptabilityReasoner(DimacsSatSolver satSolver){
		this.satSolver = satSolver;
	}
	
	/**
	 * Creates a new proposition and indexes it in this objects
	 * indices
	 * @param prop_name the name of the proposition
	 * @return the proposition (side effect: proposition is added to this
	 *   object's indices)
	 */
	private Proposition createAndIndexProposition(String prop_name) {
		Proposition p = new Proposition(prop_name);
		this.prop_index.put(p, this.index);
		this.prop_inverted_index.put(this.index++, p);
		return p;
	}
	
	/**
	 * Initialises some formulas that are used for every SAT call
	 * @param af some AF
	 */
	private void initBaseFormulas(DungTheory af) {
		this.prop_index = new HashMap<>();
		this.prop_inverted_index = new HashMap<>();
		this.index = 1;
		this.in = new HashMap<Argument,Proposition>();
		this.out = new HashMap<Argument,Proposition>();
		this.undec = new HashMap<Argument,Proposition>();
		this.cnf_baseFormulas = "";
		this.num_cnf_baseFormulas = 0;
		this.in2 = new HashMap<Argument,Proposition>();
		this.out2 = new HashMap<Argument,Proposition>();
		this.undec2 = new HashMap<Argument,Proposition>();
		this.cnf_baseFormulas2 = "";
		this.num_cnf_baseFormulas2 = 0;
		for(Argument a: af){
			in.put(a, this.createAndIndexProposition("in_" + a.getName()));
			out.put(a, this.createAndIndexProposition("out_" + a.getName()));
			undec.put(a, this.createAndIndexProposition("undec_" + a.getName()));
			in2.put(a, this.createAndIndexProposition("in2_" + a.getName()));
			out2.put(a, this.createAndIndexProposition("out2_" + a.getName()));
			undec2.put(a, this.createAndIndexProposition("undec2_" + a.getName()));
			// for every argument only one of in/out/undec can be true
			this.cnf_baseFormulas += prop_index.get(in.get(a)) + " " +  prop_index.get(out.get(a)) + " " + prop_index.get(undec.get(a)) + " 0\n";
			this.num_cnf_baseFormulas++;
			this.cnf_baseFormulas += "-" + prop_index.get(in.get(a)) + " -" + prop_index.get(out.get(a)) + " 0\n";
			this.num_cnf_baseFormulas++;
			this.cnf_baseFormulas += "-" + prop_index.get(in.get(a)) + " -" + prop_index.get(undec.get(a)) + " 0\n";
			this.num_cnf_baseFormulas++;
			this.cnf_baseFormulas += "-" + prop_index.get(out.get(a)) + " -" + prop_index.get(undec.get(a)) + " 0\n";
			this.num_cnf_baseFormulas++;
			
			this.cnf_baseFormulas2 += prop_index.get(in2.get(a)) + " " +  prop_index.get(out2.get(a)) + " " + prop_index.get(undec2.get(a)) + " 0\n";
			this.num_cnf_baseFormulas2++;
			this.cnf_baseFormulas2 += "-" + prop_index.get(in2.get(a)) + " -" + prop_index.get(out2.get(a)) + " 0\n";
			this.num_cnf_baseFormulas2++;
			this.cnf_baseFormulas2 += "-" + prop_index.get(in2.get(a)) + " -" + prop_index.get(undec2.get(a)) + " 0\n";
			this.num_cnf_baseFormulas2++;
			this.cnf_baseFormulas2 += "-" + prop_index.get(out2.get(a)) + " -" + prop_index.get(undec2.get(a)) + " 0\n";
			this.num_cnf_baseFormulas2++;
		}	
		// an argument is in iff all attackers are out
		this.cnf_attackFormulas = "";	
		this.num_cnf_attackFormulas = 0;
		String cnf_oneAttack = "";
		for(Argument a: af){
			if(af.getAttackers(a).isEmpty()){
				this.cnf_baseFormulas += prop_index.get(in.get(a)) + " 0\n";
				this.num_cnf_baseFormulas++;
				this.cnf_baseFormulas2 += prop_index.get(in2.get(a)) + " 0\n";
				this.num_cnf_baseFormulas2++;
			}else{
				String cnf_attackersOr = "";
				String cnf_attackersNotOr = "";
				String cnf_attackersOr2 = "";
				String cnf_attackersNotOr2 = "";
				for(Argument b: af.getAttackers(a)){
					cnf_attackersOr += prop_index.get(in.get(b)) + " ";
					cnf_attackersNotOr += "-"+prop_index.get(out.get(b)) + " ";
					this.cnf_baseFormulas += "-" + prop_index.get(in.get(a)) + " " + prop_index.get(out.get(b)) + " 0\n";
					this.num_cnf_baseFormulas++;
					cnf_attackersOr2 += prop_index.get(in2.get(b)) + " ";
					cnf_attackersNotOr2 += "-"+prop_index.get(out2.get(b)) + " ";
					this.cnf_baseFormulas2 += "-" + prop_index.get(in2.get(a)) + " " + prop_index.get(out2.get(b)) + " 0\n";
					this.num_cnf_baseFormulas2++;
					Proposition attack = this.createAndIndexProposition("r" + b.getName() + "_" + a.getName());
					cnf_oneAttack += prop_index.get(attack) + " ";
					this.cnf_attackFormulas += "-" + prop_index.get(attack) + " " + prop_index.get(this.in.get(b)) + " 0\n";
					this.num_cnf_attackFormulas++;
					this.cnf_attackFormulas += "-" + prop_index.get(attack) + " " + prop_index.get(this.in2.get(a)) + " 0\n";
					this.num_cnf_attackFormulas++;
					this.cnf_attackFormulas += prop_index.get(attack) + " -" + prop_index.get(this.in2.get(a)) + " -" + prop_index.get(this.in.get(b)) + " 0\n";
					this.num_cnf_attackFormulas++;
				}
				this.cnf_baseFormulas += cnf_attackersOr + "-" + prop_index.get(out.get(a)) + " 0\n";
				this.num_cnf_baseFormulas++;
				this.cnf_baseFormulas += cnf_attackersNotOr + "" + prop_index.get(in.get(a)) + " 0\n";
				this.num_cnf_baseFormulas++;
				this.cnf_baseFormulas2 += cnf_attackersOr2 + "-" + prop_index.get(out2.get(a)) + " 0\n";
				this.num_cnf_baseFormulas2++;
				this.cnf_baseFormulas2 += cnf_attackersNotOr2 + "" + prop_index.get(in2.get(a)) + " 0\n";
				this.num_cnf_baseFormulas2++;
			}
		}
		this.cnf_attackFormulas += cnf_oneAttack + " 0\n";
		this.num_cnf_attackFormulas++;
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
		//beliefSet.addAll(this.baseFormulas);
		// enforce that all argument of t are in
		for(Argument a: t)
			beliefSet.add(in.get(a));
		//  s intersected with E is non-empty
		Disjunction d = new Disjunction();
		for(Argument a: s)
			d.add(this.in.get(a));
		beliefSet.add(d);
		PossibleWorld w = (PossibleWorld) this.satSolver.getWitness(beliefSet,this.prop_index,this.prop_inverted_index,this.cnf_baseFormulas,this.num_cnf_baseFormulas);
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
		//beliefSet.addAll(this.baseFormulas);
		// enforce that all argument of t are in
		for(Argument a: t)
			beliefSet.add(in.get(a));
		PossibleWorld w = (PossibleWorld) this.satSolver.getWitness(beliefSet,this.prop_index,this.prop_inverted_index,this.cnf_baseFormulas,this.num_cnf_baseFormulas);
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
		//beliefSet.addAll(this.baseFormulas);
		// set S''
		//beliefSet.addAll(this.baseFormulas2);
		//(1) S is a subset of S''		
		for(Argument a: s)
			beliefSet.add(this.in2.get(a));
		// (2)S' attacks S''
		//beliefSet.addAll(this.attackFormulas);
		// (3)S' is not a subset of t for all t in u
		for(Collection<Argument> t: u) {
			Disjunction d = new Disjunction();
			for(Argument a: af)
				if(!t.contains(a))
					d.add(in.get(a));
			beliefSet.add(d);
		}
		PossibleWorld w = (PossibleWorld) this.satSolver.getWitness(beliefSet,this.prop_index,this.prop_inverted_index, this.cnf_baseFormulas + this.cnf_baseFormulas2 + this.cnf_attackFormulas, this.num_cnf_baseFormulas+this.num_cnf_baseFormulas2+this.num_cnf_attackFormulas);
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
