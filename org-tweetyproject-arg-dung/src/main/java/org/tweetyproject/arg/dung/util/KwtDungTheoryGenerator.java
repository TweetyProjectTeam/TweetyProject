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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Creates AAFs with a complex structure for tasks
 * related to skeptical reasoning wrt. preferred semantics,
 * see [Kuhlmann, Wujek, Thimm, 2022]
 * 
 * @author Matthias Thimm
 */
public class KwtDungTheoryGenerator implements DungTheoryGenerator {
	private Random rand;
	private int num_arguments;
	private int num_skept_arguments;
	private int size_ideal_extension;
	private int num_cred_arguments;
	private int num_pref_exts;
	
	private double p_ideal_attacked;
	private double p_ideal_attack_back;
	private double p_other_skept_args_attacked;
	private double p_other_skept_args_attack_back;
	private double p_cred_args_attacked;
	private double p_cred_args_attack_back;
	private double p_other_attacks;
	
	public KwtDungTheoryGenerator(
			int num_arguments,
			int num_skept_arguments,
			int size_ideal_extension,
			int num_cred_arguments,
			int num_pref_exts,
			double p_ideal_attacked,
			double p_ideal_attack_back,
			double p_other_skept_args_attacked,
			double p_other_skept_args_attack_back,
			double p_cred_args_attacked,
			double p_cred_args_attack_back,
			double p_other_attacks) {
		this.num_arguments = num_arguments;
		this.num_skept_arguments = num_skept_arguments;
		this.size_ideal_extension = size_ideal_extension;
		this.num_cred_arguments = num_cred_arguments;
		this.num_pref_exts = num_pref_exts;
		this.p_ideal_attacked = p_ideal_attacked;
		this.p_ideal_attack_back = p_ideal_attack_back;
		this.p_other_skept_args_attacked = p_other_skept_args_attacked;
		this.p_other_skept_args_attack_back = p_other_skept_args_attack_back;
		this.p_cred_args_attacked = p_cred_args_attacked;
		this.p_cred_args_attack_back = p_cred_args_attack_back;
		this.p_other_attacks = p_other_attacks;
	}
	
	
	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public DungTheory next() {
		DungTheory af = new DungTheory();
		
		List<List<Argument>> pref_exts = new ArrayList<List<Argument>>();
		List<Argument> ideal_ext = new ArrayList<Argument>();
		List<Argument> other_skept_args = new ArrayList<Argument>();
		List<Argument> unaccepted_arguments = new ArrayList<Argument>();
		for(int i = 0; i < num_pref_exts; i++)
			pref_exts.add(new ArrayList<Argument>());
		
		// skeptically accepted arguments
		for(int i = 0; i < num_skept_arguments; i++) {
			Argument a = new Argument("a"+i);
			af.add(a);
			if(i < size_ideal_extension)
				ideal_ext.add(a);
			else 
				other_skept_args.add(a);
			for(int j = 0; j < num_pref_exts; j++)
				pref_exts.get(j).add(a);
		}
		
		// credulously accepted arguments
		for(int i = num_skept_arguments; i < num_skept_arguments + num_cred_arguments;i++) {
			Argument a = new Argument("a"+i);
			af.add(a);
			// choose uniformly at random a number n with 0 < n < num_pref_exts-1 of
			// extensions where the argument will be added
			int num = rand.nextInt(num_pref_exts-2)+1;
			Collections.shuffle(pref_exts, rand);
			for(int j = 0 ; j < num; j++)
				pref_exts.get(j).add(a);			
		}		
		
		// remaining arguments
		for(int i = num_skept_arguments + num_cred_arguments; i < num_arguments;i++) {
			Argument a = new Argument("a"+i);
			af.add(a);
			unaccepted_arguments.add(a);
		}
		
		// all arguments in the ideal extension should be attacked
		// by some unaccepted argument (in order to have an empty
		// grounded extension) and defended by the ideal extension
		for(Argument c: ideal_ext){
			for(Argument a: unaccepted_arguments)
				if(rand.nextDouble() < p_ideal_attacked) {
					af.add(new Attack(a,c));
					for(Argument b: ideal_ext)
						if(rand.nextDouble() < p_ideal_attack_back)
							af.add(new Attack(b,a));
				}
		}
		
		// all remaining skept. accepted arguments must be attacked
		// by some unaccepted argument (in order to have an empty 
		// grounded extension and defended by each pref. extension
		for(Argument c: other_skept_args) {
			for(Argument a: unaccepted_arguments)
				if(rand.nextDouble() < p_other_skept_args_attacked) {
					af.add(new Attack(a,c));
					for(List<Argument> ext: pref_exts) {						
						for(Argument b: ext)
							if(!ideal_ext.contains(b) && !other_skept_args.contains(b))
								if(rand.nextDouble() < p_other_skept_args_attack_back)
									af.add(new Attack(b,a));
					}
				}			
		}
		
		// for every pref. extension and every not skeptically accepted
		// argument in there, it should be attacked from outside and defended
		for(List<Argument> pref_ext: pref_exts) {
			for(Argument a: pref_ext)
				if(!ideal_ext.contains(a) && !other_skept_args.contains(a)) {
					for(Argument b: af)
						if(!ideal_ext.contains(b) && !other_skept_args.contains(b) && !pref_ext.contains(b)) {
							if(rand.nextDouble() < p_cred_args_attacked)
								af.add(new Attack(b,a));
								for(Argument c: pref_ext) {
									if(!ideal_ext.contains(c) && !other_skept_args.contains(c)) {
										if(rand.nextDouble() < p_cred_args_attack_back)
											af.add(new Attack(c,b));
									}
								}
						}
				}
		}
		
		// add some other attacks between unaccepted arguments
		for(Argument a: unaccepted_arguments)
			for(Argument b: unaccepted_arguments)
				if(rand.nextDouble() < p_other_attacks)
					af.add(new Attack(a,b));
		
		// add something to increase the likelihood of having 
		// no stable extensions
		Argument c = new Argument("c");
		af.add(c);
		af.add(new Attack(c,c));
		for(Argument a: unaccepted_arguments)
			if(rand.nextBoolean())
				if(rand.nextBoolean())
					af.add(new Attack(a,c));
				else af.add(new Attack(c,a));
		
		return af;
	}

	@Override
	public DungTheory next(Argument arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSeed(long seed) {
		this.rand.setSeed(seed);		
	}
}
