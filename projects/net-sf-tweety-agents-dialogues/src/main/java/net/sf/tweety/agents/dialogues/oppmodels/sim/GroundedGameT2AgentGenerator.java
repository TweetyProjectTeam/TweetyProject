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
package net.sf.tweety.agents.dialogues.oppmodels.sim;

import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.agents.dialogues.oppmodels.BeliefState;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameSystem;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameUtilityFunction;
import net.sf.tweety.agents.dialogues.oppmodels.T2BeliefState;
import net.sf.tweety.agents.sim.SimulationParameters;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.probability.ProbabilityFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates agents of type T2.
 * @author Matthias Thimm
 */
public class GroundedGameT2AgentGenerator extends GroundedGameAgentGenerator {

	/** Logger */
	static private Logger log = LoggerFactory.getLogger(GroundedGameT2AgentGenerator.class);
	
	/** The configuration for generating agents. */
	private T2Configuration config;
	
	/**
	 * Creates a new generator for agents of type T1.
	 * @param faction the faction of the agents to be generated.
	 * @param config configuration for creating belief states.
	 */
	public GroundedGameT2AgentGenerator(GroundedGameSystem.AgentFaction faction, T2Configuration config) {
		super(faction);
		this.config = config;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.argumentation.oppmodels.sim.GroundedGameAgentGenerator#generateBeliefState(net.sf.tweety.agents.argumentation.oppmodels.GroundedGameSystem, net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	protected BeliefState generateBeliefState(GroundedGameSystem mas, SimulationParameters params) {
		T2BeliefState state = this.generateBeliefState(mas,params,this.config.maxRecursionDepth,(Extension)params.get(this.getFaction()),this.getFaction());
		log.info("Generated a T2-belief state for " + this.getFaction() + " agent: ");
		log.info("=========\n" + state.display() + "\n=========");
		return state;
	}

	/**
	 * Generates the (sub-)belief state of a T2-belief state.
	 * @param mas the multi-agent system under consideration.
	 * @param params parameters for the simulation.
	 * @param depth the maximal depth of the recursive model.
	 * @param arguments the arguments that are currently in the view
	 * @param faction the faction of the model to be generated.
	 * @return a T2-belief state
	 */
	private T2BeliefState generateBeliefState(GroundedGameSystem mas, SimulationParameters params, int depth, Extension arguments, GroundedGameSystem.AgentFaction faction) {
		ProbabilityFunction<T2BeliefState> prob = new ProbabilityFunction<T2BeliefState>();
		Map<T2BeliefState,Double> mass = new HashMap<T2BeliefState,Double>();
		double totalMass = 0;
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
				T2BeliefState state = this.generateBeliefState(mas, params, depth-1, subView, faction.getComplement());
				if(mass.keySet().contains(state))
					mass.put(state, mass.get(state)+1);
				else mass.put(state, 1d);
				totalMass +=1;
			}
		// normalize probability function
		if(!mass.isEmpty())
			for(T2BeliefState bs: mass.keySet())
				prob.put(bs, new Probability(mass.get(bs)/totalMass));	
		return new T2BeliefState(
				arguments,
				new GroundedGameUtilityFunction(
						(DungTheory)params.get(GroundedGameGenerator.PARAM_UNIVERSALTHEORY),
						(Argument)params.get(GroundedGameGenerator.PARAM_ARGUMENT),
						faction),
				prob);	
	}
}
