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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.SetTools;

/**
 * Creates abstract argumentation graphs by first generating the corresponding set
 * of preferred extensions and then construct the graph by using the construction method
 * from [Dunne, Dvorak, Linsbichler, Woltran: Characteristics of multiple viewpoints in abstract argumentation.
 *  Artificial Intelligence 228: 153-178, 2015].
 *  
 * The implementation is randomized: sets of extensions are randomly generated and tested whether they are
 * realisable wrt. preferred semantics. Only when one realizable set is found, the graph is constructed. This
 * means the generation process is not guaranteed to terminate. This may happen when the required number of 
 * extensions is too large for the given number of credulously accepted arguments.
 */
public class PrSigBasedDungTheoryGenerator implements DungTheoryGenerator {

	/**
	 * The number of arguments that are credulously accepted wrt. preferred semantics
	 *  in the generated frameworks
	 */
	private int num_credulously_accepted_arguments;
	
	/**
	 * The number of arguments that are skeptically accepted wrt. preferred semantics
	 *  in the generated frameworks
	 */
	private int num_skeptically_accepted_arguments;
	
	/**
	 * The maximum number of (preferred) extensions  in the generated frameworks
	 */
	private int max_num_extensions;
	
	/**
	 * the probability of each edge to be re-wired after construction (distorts the result a bit)
	 */
	private double distortion_factor;
	
	/**
	 * For randomness.
	 */
	private Random rand;
	
	/**
	 * Creates a new theory generator.
	 * @param max_num_extensions the maximum number of (preferred) extensions  in the generated frameworks
	 * @param num_credulously_accepted_arguments The number of arguments that are credulously accepted wrt. preferred semantics
	 *  in the generated frameworks
	 * @param num_skeptically_accepted_arguments The number of arguments that are skeptically accepted wrt. preferred semantics
	 *  in the generated frameworks
	 *  @param distortion_factor the probability of each edge to be re-wired after construction (distorts the result a bit)
	 */
	public PrSigBasedDungTheoryGenerator(int max_num_extensions, int num_credulously_accepted_arguments, int num_skeptically_accepted_arguments, double distortion_factor) {
		if(num_credulously_accepted_arguments < num_skeptically_accepted_arguments)
			throw new IllegalArgumentException("The number of skeptically accepted arguments cannot be larger than the number of credulously accepted arguments.");
		this.max_num_extensions = max_num_extensions;
		this.num_credulously_accepted_arguments = num_credulously_accepted_arguments;
		this.num_skeptically_accepted_arguments = num_skeptically_accepted_arguments;
		this.distortion_factor =  distortion_factor;
		this.rand = new Random();
	}
	
	/**
	 * Creates a new theory generator (without distortion).
	 * @param num_extensions the number of (preferred) extensions  in the generated frameworks
	 * @param num_credulously_accepted_arguments The number of arguments that are credulously accepted wrt. preferred semantics
	 *  in the generated frameworks
	 * @param num_skeptically_accepted_arguments The number of arguments that are skeptically accepted wrt. preferred semantics
	 *  in the generated frameworks
	 */
	public PrSigBasedDungTheoryGenerator(int num_extensions, int num_credulously_accepted_arguments, int num_skeptically_accepted_arguments) {
		this(num_extensions,num_credulously_accepted_arguments,num_skeptically_accepted_arguments,0);
	}
	
	@Override
	public boolean hasNext() {		
		return true;
	}

	/**
	 * Returns a set of preferred extensions (this method is not guaranteed to terminate, depending on the parameters).
	 * @return a set of preferred extensions.
	 */
	private Collection<Extension<DungTheory>>  generateExtensionSet(){		
		Collection<Extension<DungTheory>>  exts;
		do {
			exts = new HashSet<>();
			// create a non-empty and pref-closed set of extensions (this is realisable for preferred semantics)
			List<Argument> all_args = new ArrayList<>();
			for(int i = 0; i < this.num_credulously_accepted_arguments; i++)
				all_args.add(new Argument("a" + i));
			for(int i = 0; i < this.max_num_extensions; i++) {
				Extension<DungTheory> ext = new Extension<>();
				// add all skeptically accepted arguments (the first num_skeptically_accepted_arguments)
				for(int j=0; j< this.num_skeptically_accepted_arguments; j++)
					ext.add(all_args.get(j));
				// randomly add further arguments
				for(int j=this.num_skeptically_accepted_arguments; j < all_args.size(); j++) {
					if(this.rand.nextBoolean())
						ext.add(all_args.get(j));
				}
				exts.add(ext);
			}			
		}while(!Realizability.isPrefClosed(exts));
		return exts;
	}
	
	/**
	 * Generate the defense set for each argument, see the notion of CNF-defense formula, definition 7 of
	 * [Dunne, Dvorak, Linsbichler, Woltran: Characteristics of multiple viewpoints in abstract argumentation.
	 * Artificial Intelligence 228: 153-178, 2015]
	 * @param exts a set of extensions
	 * @param a an argument 
	 * @return the defense set of a wrt. exts
	 */
	private Set<Set<Argument>> generateDefenseSets(Collection<Extension<DungTheory>> exts, Argument a){
		Set<Set<Argument>> exts_with_a = new HashSet<>();
		for(Extension<DungTheory> ext: exts)
			if(ext.contains(a))
				exts_with_a.add(new HashSet<>(ext));
		for(Set<Argument> ext: exts_with_a)
			ext.remove(a);
		SetTools<Argument> setTools = new SetTools<Argument>();
		return setTools.permutations(exts_with_a);
	}
	
	@Override
	public DungTheory next() {
		Collection<Extension<DungTheory>>  exts =  this.generateExtensionSet();		
		Collection<Argument> args = Realizability.args(exts);		
		Collection<Set<Argument>> pairs = Realizability.pairs(exts);
		Map<Argument,Map<Set<Argument>,Argument>> defenseSets = new HashMap<>();
		int idx = 0;
		DungTheory af = new DungTheory();
		// arguments
		af.addAll(args);
		for(Argument a: args) {
			Set<Set<Argument>> s = this.generateDefenseSets(exts, a);			
			Map<Set<Argument>,Argument> map = new HashMap<>();
			for(Set<Argument> supp: s) {
				Argument suppArg = new Argument("alpha"+idx++);
				map.put(supp, suppArg);
				af.add(suppArg);
			}				
			defenseSets.put(a, map);
		}		
		// attacks
		for(Argument a: args) {
			// every attack between two original arguments that are not a pair
			for(Argument b: args) {				
				Set<Argument> s = new HashSet<>();
				s.add(a);
				s.add(b);
				if(!pairs.contains(s)) {
					af.add(new Attack(a,b));
				}
			}
			// auxiliary attacks
			for(Set<Argument> gamma: defenseSets.get(a).keySet()) {
				for(Argument b: gamma) {
					af.add(new Attack(b,defenseSets.get(a).get(gamma)));
					af.add(new Attack(defenseSets.get(a).get(gamma),defenseSets.get(a).get(gamma)));
					af.add(new Attack(defenseSets.get(a).get(gamma),a));
				}
			}
		}		
		// now add distortion
		Collection<Attack> attacks = af.getAttacks();
		if(this.distortion_factor>0) {
			SetTools<Argument> setTools = new SetTools<>();
			for(Attack att: attacks) {
				if(this.rand.nextDouble() < this.distortion_factor) {
					af.remove(att);
					// flip either attacker or attackee
					Attack new_att;
					if(rand.nextBoolean()) {
						new_att = new Attack(setTools.randomElement(af),att.getAttacked());
					}else {
						new_att = new Attack(att.getAttacker(),setTools.randomElement(af));
					}
					af.add(new_att);				
				}
			}
		}
		return af;
	}

	@Override
	public DungTheory next(Argument arg) {
		// trivial solution to support this operation: just add the argument to the graph		
		DungTheory af = this.next();
		af.add(arg);
		return af;
	}

	@Override
	public void setSeed(long seed) {
		this.rand.setSeed(seed);		
	}

}
