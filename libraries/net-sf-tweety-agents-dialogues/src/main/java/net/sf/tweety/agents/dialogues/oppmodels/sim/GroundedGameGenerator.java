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
package net.sf.tweety.agents.dialogues.oppmodels.sim;

import java.util.Random;

import net.sf.tweety.agents.dialogues.oppmodels.ArguingAgent;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameSystem;
import net.sf.tweety.agents.sim.MultiAgentSystemGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates grounded games.
 * @author Matthias Thimm
 */
public class GroundedGameGenerator implements MultiAgentSystemGenerator<ArguingAgent,GroundedGameSystem> {

	/** Logger */
	static private Logger log = LoggerFactory.getLogger(GroundedGameGenerator.class);
	
	/** Key for the simulation parameter which refers to the universal theory generated. */
	public static final int PARAM_UNIVERSALTHEORY = 0;
	/** Key for the simulation parameter which refers to the argument of the dialogue. */
	public static final int PARAM_ARGUMENT = 1;

	/** The percentage of the arguments known to the PRO agent. */
	private double viewPercentagePro;
	/** The percentage of the arguments known to the CON agent. */
	private double viewPercentageCon;
	
	/** for generating Dung theories. */
	private DungTheoryGenerator gen;
	
	/** Random numbers generator. */
	private Random random = new Random();
	
	/**
	 * Creates a new game generator.
	 * @param gen for generating Dung theories.
	 * @param viewPercentagePro the percentage of the arguments known to the PRO agent
	 * @param viewPercentageCon the percentage of the arguments known to the CON agent
	 */
	public GroundedGameGenerator(DungTheoryGenerator gen, double viewPercentagePro, double viewPercentageCon){
		this.gen = gen;
		this.viewPercentagePro = viewPercentagePro;
		this.viewPercentageCon = viewPercentageCon;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.MultiAgentSystemGenerator#generate(net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	public GroundedGameSystem generate(SimulationParameters params) {
		log.info("Starting to generate a grounded game system");
		Argument arg = new Argument("A");
		DungTheory theory = this.gen.next(arg);		
		log.trace("Generated Dung theory with " + this.gen + ":\n" +
				"=========\n" + theory.toString() + "\n=========");		
		log.trace("Central argument of dialog is: " + arg);		
		params.put(GroundedGameGenerator.PARAM_UNIVERSALTHEORY, theory);
		params.put(GroundedGameGenerator.PARAM_ARGUMENT, arg);
		// generate each agent view
		Extension proView = new Extension();
		Extension conView = new Extension();		
		//both views must contain the argument of the discussion
		proView.add(arg);
		conView.add(arg);
		for(Argument a: theory){
			if(this.random.nextDouble()<= this.viewPercentagePro)
				proView.add(a);
			if(this.random.nextDouble()<= this.viewPercentageCon)
				conView.add(a);
		}
		params.put(GroundedGameSystem.AgentFaction.PRO, proView);
		params.put(GroundedGameSystem.AgentFaction.CONTRA, conView);
		log.trace("Arguments in the view of agent PRO are:\n" +
				"=========\n" + proView + "\n=========");
		log.trace("Arguments in the view of agent CON are:\n" +
				"=========\n" + conView + "\n=========");
		log.info("Ending to generate a grounded game system");
		return new GroundedGameSystem(theory);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.MultiAgentSystemGenerator#setSeed(long)
	 */
	public void setSeed(long seed){
		this.random = new Random(seed);
	}
}
