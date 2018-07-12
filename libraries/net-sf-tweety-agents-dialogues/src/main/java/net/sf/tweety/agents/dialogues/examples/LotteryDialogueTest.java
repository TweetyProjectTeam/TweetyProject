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
package net.sf.tweety.agents.dialogues.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sf.tweety.agents.ProtocolTerminatedException;
import net.sf.tweety.agents.dialogues.lotteries.AbstractLotteryAgent;
import net.sf.tweety.agents.dialogues.lotteries.LotteryGameSystem;
import net.sf.tweety.agents.dialogues.lotteries.sim.UtilityBasedAgentGenerator;
import net.sf.tweety.agents.dialogues.lotteries.sim.DirectGameProtocol;
import net.sf.tweety.agents.dialogues.lotteries.sim.DirectGameProtocolGenerator;
import net.sf.tweety.agents.dialogues.lotteries.sim.DummyAgentGenerator;
import net.sf.tweety.agents.dialogues.lotteries.sim.ProbabilisticLotteryAgentGenerator;
import net.sf.tweety.agents.dialogues.lotteries.sim.LotteryGameGenerator;
import net.sf.tweety.agents.sim.AgentGenerator;
import net.sf.tweety.agents.sim.GameSimulator;
import net.sf.tweety.agents.sim.MultiAgentSystemGenerator;
import net.sf.tweety.agents.sim.ProtocolGenerator;
import net.sf.tweety.agents.sim.SimulationResult;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.util.DefaultDungTheoryGenerator;
import net.sf.tweety.arg.dung.util.DungTheoryGenerationParameters;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;

public class LotteryDialogueTest {
	
	//Global parameters for simulation
	public static int frameworkSize;
	public static double attackProbability;
	public static boolean enforceTreeShape;
	public static int timeout = 60*60*72; // timeout of 72 hours
	
	public static int numberOfRunsEach = 100;
	
	public static Semantics semantics = Semantics.GROUNDED_SEMANTICS;
	
	//to ensure comparability
	public static long RANDOM_SEED1 = 435844589l;
	public static long RANDOM_SEED2 = 96421389l;
	public static long RANDOM_SEED3 = 6477568l;
	public static long RANDOM_SEED4 = 2136455579l;
	
	public static void runSimulation(boolean baseline) throws ProtocolTerminatedException{
		// We generate Dung theories with the given number of arguments and attack probability.
		// In every theory, the argument under consideration is guaranteed to
		// be in the grounded extension (so under perfect information, the PRO
		// agent should always win)
		DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
		params.attackProbability = LotteryDialogueTest.attackProbability;
		params.numberOfArguments = LotteryDialogueTest.frameworkSize;	
		params.enforceTreeShape = LotteryDialogueTest.enforceTreeShape;	
		DungTheoryGenerator gen = new DefaultDungTheoryGenerator(params);
		gen.setSeed(LotteryDialogueTest.RANDOM_SEED1);
		
		// MAS generator
		MultiAgentSystemGenerator<AbstractLotteryAgent,LotteryGameSystem> masGenerator = new LotteryGameGenerator(gen,semantics);
		masGenerator.setSeed(GroundedTest.RANDOM_SEED2);
		List<AgentGenerator<AbstractLotteryAgent,LotteryGameSystem>> agentGenerators = new ArrayList<AgentGenerator<AbstractLotteryAgent,LotteryGameSystem>>();
			
		if(baseline)
			agentGenerators.add(new UtilityBasedAgentGenerator("BASE"));
		else
			agentGenerators.add(new ProbabilisticLotteryAgentGenerator("PRO"));
		agentGenerators.add(new DummyAgentGenerator("AUDIENCE"));
		agentGenerators.get(0).setSeed(GroundedTest.RANDOM_SEED3);
		agentGenerators.get(1).setSeed(GroundedTest.RANDOM_SEED4);			
			
		ProtocolGenerator<DirectGameProtocol,AbstractLotteryAgent,LotteryGameSystem> protGenerator = new DirectGameProtocolGenerator();
		final GameSimulator<DirectGameProtocol,AbstractLotteryAgent,LotteryGameSystem> sim = new GameSimulator<DirectGameProtocol,AbstractLotteryAgent,LotteryGameSystem>(masGenerator,protGenerator,agentGenerators);
		// Run iterated simulations and show aggregated results (with timeout)
		Callable<String> callee = new Callable<String>(){
		    @Override
		    public String call() throws Exception {
		    	SimulationResult<DirectGameProtocol,AbstractLotteryAgent,LotteryGameSystem> result = sim.run(LotteryDialogueTest.numberOfRunsEach);
				System.out.println(result.csvDisplay());
		        return null;
		    }
		};			
		ExecutorService executor = Executors.newSingleThreadExecutor();
	    Future<String> future = executor.submit(callee);
	    try {
	    	future.get(LotteryDialogueTest.timeout, TimeUnit.SECONDS);	            
	    } catch (Exception e) {
	        System.out.println("Aborted...");
	        e.printStackTrace();
	    }
	    executor.shutdownNow();
	}
	
	public static void main(String[] args) throws ProtocolTerminatedException{
		LotteryDialogueTest.attackProbability = 0.3;
		LotteryDialogueTest.frameworkSize = 10;		
		LotteryDialogueTest.enforceTreeShape = true;
		//baseline
		//LotteryDialogueTest.runSimulation(true);
		//actual agent
		LotteryDialogueTest.runSimulation(false);
	}
}
