/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.agents.dialogues.oppmodels.sim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.agents.dialogues.oppmodels.BeliefState;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameSystem;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameUtilityFunction;
import net.sf.tweety.agents.dialogues.oppmodels.RecognitionFunction;
import net.sf.tweety.agents.dialogues.oppmodels.T3BeliefState;
import net.sf.tweety.agents.sim.SimulationParameters;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.probability.ProbabilityFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates agents of type T3.
 * @author Matthias Thimm
 */
public class GroundedGameT3AgentGenerator extends GroundedGameAgentGenerator {

	/** Logger */
	static private Logger log = LoggerFactory.getLogger(GroundedGameT3AgentGenerator.class);
	
	/** The configuration for generating agents. */
	private T3Configuration config;
	
	/**
	 * Creates a new generator for agents of type T1.
	 * @param faction the faction of the agents to be generated.
	 * @param config configuration for creating belief states.
	 */
	public GroundedGameT3AgentGenerator(GroundedGameSystem.AgentFaction faction, T3Configuration config) {
		super(faction);
		this.config = config;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.sim.GroundedGameAgentGenerator#generateBeliefState(net.sf.tweety.agents.argumentation.oppmodels.GroundedGameSystem, net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	protected BeliefState generateBeliefState(GroundedGameSystem mas, SimulationParameters params) {
		T3BeliefState state = this.generateBeliefState(mas,params,this.config.maxRecursionDepth,(Extension)params.get(this.getFaction()),this.getFaction());
		log.info("Generated a T3-belief state for " + this.getFaction() + " agent: ");
		log.info("=========\n" + state.display() + "\n=========");
		return state;
	}

	/**
	 * Generates the (sub-)belief state of a T3-belief state.
	 * @param mas the multi-agent system under consideration.
	 * @param params parameters for the simulation.
	 * @param depth the maximal depth of the recursive model.
	 * @param arguments the arguments that are currently in the view
	 * @param faction the faction of the model to be generated.
	 * @return a T3-belief state
	 */
	private T3BeliefState generateBeliefState(GroundedGameSystem mas, SimulationParameters params, int depth, Extension arguments, GroundedGameSystem.AgentFaction faction) {
		ProbabilityFunction<T3BeliefState> prob = new ProbabilityFunction<T3BeliefState>();
		Map<T3BeliefState,Double> mass = new HashMap<T3BeliefState,Double>();
		double totalMass = 0;
		// select some arguments and make them virtual
		Set<Argument> virtualArguments = new HashSet<Argument>();
		Set<Attack> virtualAttacks = new HashSet<Attack>();
		RecognitionFunction rec = new RecognitionFunction();
		int k = 0;
		// virtualize arguments
		// For simplicity we only consider the case that no more
		// than one virtual argument is mapped to a real one.
		Extension arguments2 = new Extension();
		for(Argument arg: arguments){
			// do not virtualize the argument of the dialog
			if(params.get(GroundedGameGenerator.PARAM_ARGUMENT).equals(arg))
				continue;			
			if(this.getRandom().nextDouble() <= this.config.percentageVirtualArguments){
				Argument vArg = new Argument(faction + "_" + k++);				
				Set<Argument> vArgs = new HashSet<Argument>();
				vArgs.add(vArg);						
				rec.put(arg, vArgs);
				virtualArguments.add(vArg);
			}else arguments2.add(arg);
		}
		arguments = arguments2;
		// virtualize attacks
		for(Argument vArg: virtualArguments){
			for(Argument a: ((DungTheory)params.get(GroundedGameGenerator.PARAM_UNIVERSALTHEORY)).getAttacked(rec.getPreimage(vArg))){
				if(arguments.contains(a))
					if(this.getRandom().nextDouble() <= this.config.percentageVirtualAttacks)
						virtualAttacks.add(new Attack(vArg,a));
				if(rec.get(a) != null){
					for(Argument vArg2: rec.get(a))
						if(this.getRandom().nextDouble() <= this.config.percentageVirtualAttacks)
							virtualAttacks.add(new Attack(vArg,vArg2));
				}
			}
		}
		for(Argument vArg: virtualArguments){
			for(Argument a: ((DungTheory)params.get(GroundedGameGenerator.PARAM_UNIVERSALTHEORY)).getAttackers(rec.getPreimage(vArg))){
				if(arguments.contains(a))
					if(this.getRandom().nextDouble() <= this.config.percentageVirtualAttacks)
						virtualAttacks.add(new Attack(a,vArg));
				if(rec.get(a) != null){
					for(Argument vArg2: rec.get(a))
						if(this.getRandom().nextDouble() <= this.config.percentageVirtualAttacks)
							virtualAttacks.add(new Attack(vArg2,vArg));
				}
			}
		}
		//if the maximal recursion depth is reached
		//leave the probability function "empty" 
		if(depth >= 0)
			for(int i = 0; i < this.config.maxRecursionWidth; i++){			
				Extension subView = new Extension();				
				for(Argument a: arguments)
					if(this.getRandom().nextDouble() >= this.config.probRecursionDecay)				
						subView.add(a);				
				//if the subview is empty, do not consider it further
				//(this corresponds to the end of the recursion)
				if(subView.isEmpty())
					continue;
				// everything is uniformly distributed
				T3BeliefState state = this.generateBeliefState(mas, params, depth-1, subView, faction.getComplement());
				if(mass.keySet().contains(state))
					mass.put(state, mass.get(state)+1);
				else mass.put(state, 1d);
				totalMass +=1;
			}
		// normalize probability function
		if(!mass.isEmpty())
			for(T3BeliefState bs: mass.keySet())
				prob.put(bs, new Probability(mass.get(bs)/totalMass));
		return new T3BeliefState(
				arguments,
				new GroundedGameUtilityFunction(
						(DungTheory)params.get(GroundedGameGenerator.PARAM_UNIVERSALTHEORY),
						(Argument)params.get(GroundedGameGenerator.PARAM_ARGUMENT),
						faction),
				virtualArguments,
				virtualAttacks,
				rec,
				prob);	
	}
}
