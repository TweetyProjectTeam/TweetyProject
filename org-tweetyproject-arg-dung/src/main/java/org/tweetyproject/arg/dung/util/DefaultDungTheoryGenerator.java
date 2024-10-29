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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Implements a customizable Dung theory generator.
 * @author Matthias Thimm
 */
public class DefaultDungTheoryGenerator implements DungTheoryGenerator {

	/** The parameters for generation. */
	private DungTheoryGenerationParameters params;
	
	/** Random numbers generator. */
	private Random random = new Random();
	
	/**
	 * Creates a new generator with the given parameters.
	 * @param params some generation parameters.
	 */
	public DefaultDungTheoryGenerator(DungTheoryGenerationParameters params){
		this.params = params;
	}
	
	/**
	 * Creates a new generator with the given parameters.
	 * @param numberOfArguments the number of arguments.
	 * @param attackProbability the probability that there is an attack between any two arguments
	 */
	public DefaultDungTheoryGenerator(int numberOfArguments, double attackProbability){
		DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
		params.numberOfArguments = numberOfArguments;
		params.attackProbability = attackProbability;
		params.avoidSelfAttacks = false;
		this.params = params;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.util.DungTheoryGenerator#next()
	 */
	@Override
	public DungTheory next() {
		if(this.params.enforceTreeShape)
			return this.generateTreeShape(new Argument("a"));
		DungTheory theory = new DungTheory();
		for(int i = 0; i < this.params.numberOfArguments; i++)
			theory.add(new Argument("a" + i));		
		for(Argument a: theory)
			for(Argument b: theory){
				if(a == b && this.params.avoidSelfAttacks)
					continue;
				if(this.random.nextDouble() <= this.params.attackProbability)
					theory.add(new Attack(a,b));
			}
		return theory;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.util.DungTheoryGenerator#next(org.tweetyproject.arg.dung.syntax.Argument)
	 */
	@Override
	public DungTheory next(Argument arg){
		DungTheory theory = new DungTheory();
		if(this.params.enforceTreeShape){
			boolean inExtension = false;
			do{
				theory = this.generateTreeShape(arg);
				inExtension = new SimpleGroundedReasoner().query(theory,arg);
			}while(!inExtension);
			return theory;
		}		
		theory.add(arg);
		for(int i = 1; i < this.params.numberOfArguments; i++)
			theory.add(new Argument("a" + i));
		for(Argument a: theory)
			for(Argument b: theory){
				if(a == b && this.params.avoidSelfAttacks)
					continue;
				if(this.random.nextDouble() <= this.params.attackProbability){
					Attack att = new Attack(a,b);
					theory.add(att);
					//Check whether this makes the argument out
					if(!new SimpleGroundedReasoner().query(theory,arg))
						theory.remove(att);
				}
			}
		return theory;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.argumentation.util.DungTheoryGenerator#setSeed(double)
	 */
	public void setSeed(long seed){
		this.random = new Random(seed);
	}
	
	/**
	 * Generates a Dung theory with a tree shape where the given argument
	 * is the root.
	 * @param arg some argument.
	 * @return a Dung theory.
	 */
	private DungTheory generateTreeShape(Argument arg){
		DungTheory theory = new DungTheory();
		theory.add(arg);
		int numOfArguments = 1;
		Queue<Argument> q = new LinkedList<Argument>();
		q.add(arg);
		while(numOfArguments < this.params.numberOfArguments){
			Argument a = new Argument("a" + numOfArguments++);
			theory.add(new Attack(a, (Argument)theory.toArray()[this.random.nextInt(numOfArguments-1)]));
			theory.add(a);
		}
		return theory;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "Def"+this.params.toString();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.util.DungTheoryGenerator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return true;
	}

}
