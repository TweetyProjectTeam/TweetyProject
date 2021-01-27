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
package org.tweetyproject.agents.dialogues.lotteries.sim;

import java.util.Random;

import org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.LotteryGameSystem;
import org.tweetyproject.agents.sim.MultiAgentSystemGenerator;
import org.tweetyproject.agents.sim.SimulationParameters;
import org.tweetyproject.arg.dung.divisions.Division;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.prob.lotteries.SubgraphProbabilityFunction;
import org.tweetyproject.arg.prob.lotteries.UtilityFunction;
import org.tweetyproject.math.probability.Probability;

/**
 * Generates lottery games.
 * @author Matthias Thimm
 */
public class LotteryGameGenerator implements MultiAgentSystemGenerator<AbstractLotteryAgent,LotteryGameSystem> {
	
	/** Key for the simulation parameter which refers to the universal theory generated. */
	public static final int PARAM_UNIVERSALTHEORY = 0;
	/** Key for the simulation parameter which refers to the argument of the dialogue. */
	public static final int PARAM_ARGUMENT = 1;
	/** Key for the simulation parameter which refers to the dummy agent's theory. */
	public static final int PARAM_DUMMY_THEORY = 2;
	/** Key for the simulation parameter which refers to the probability function for the actual lottery agent. */
	public static final int PARAM_LOT_PROB = 3;
	/** Key for the simulation parameter which refers to the utility function for the actual lottery agent. */
	public static final int PARAM_LOT_UTIL = 4;
	/** Key for the simulation parameter which refers to the semantics used. */
	public static final int PARAM_SEM = 5;
	
	/** for generating Dung theories. */
	private DungTheoryGenerator gen;
	
	/** The semantics used. */
	private Semantics semantics;
	
	/** Random numbers generator. */
	private Random random = new Random();
	
	/** whether the theories generated should ensure 
	 * 	one specific argument to be skeptically inferred. */
	private boolean ensureArg;
	
	/**
	 * Creates a new game generator.
	 * @param gen for generating Dung theories.
	 * @param semantics the semantics used.
	 */
	public LotteryGameGenerator(DungTheoryGenerator gen, Semantics semantics){
		this(gen,semantics,true);
	}
	
	/**
	 * Creates a new game generator.
	 * @param gen for generating Dung theories.
	 * @param semantics the semantics used.
	 * @param ensureArg whether the theories generated should ensure 
	 * 	one specific argument to be skeptically inferred. 
	 */
	public LotteryGameGenerator(DungTheoryGenerator gen, Semantics semantics, boolean ensureArg){
		this.gen = gen;
		this.semantics = semantics;
		this.ensureArg = ensureArg;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.MultiAgentSystemGenerator#generate(org.tweetyproject.agents.sim.SimulationParameters)
	 */
	@Override
	public LotteryGameSystem generate(SimulationParameters params) {
		DungTheory theory;
		if(this.ensureArg){
			Argument arg = new Argument("A");
			theory = this.gen.next(arg);				
			params.put(LotteryGameGenerator.PARAM_ARGUMENT, arg);		
		
		}else{
			theory = this.gen.next();
		}
		params.put(LotteryGameGenerator.PARAM_SEM, this.semantics);
		params.put(LotteryGameGenerator.PARAM_UNIVERSALTHEORY, theory);
		// random utility function		
		UtilityFunction util = new UtilityFunction();
		for(Division d: Division.getStandardDivisions(theory))
			util.put(d, random.nextDouble());
		params.put(LotteryGameGenerator.PARAM_LOT_UTIL, util);
		// random probability function
		SubgraphProbabilityFunction prob = new SubgraphProbabilityFunction(theory);
		for(DungTheory t: prob.keySet())
			prob.put(t, new Probability(random.nextDouble()));
		prob.normalize();		
		params.put(LotteryGameGenerator.PARAM_LOT_PROB, prob);
		// sample some random theory
		params.put(LotteryGameGenerator.PARAM_DUMMY_THEORY, prob.sample(this.random));		
		return new LotteryGameSystem(theory);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.agents.sim.MultiAgentSystemGenerator#setSeed(long)
	 */
	public void setSeed(long seed){
		this.random = new Random(seed);
	}
}
