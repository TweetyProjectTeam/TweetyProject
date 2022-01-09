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


import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.tweetyproject.agents.ProtocolTerminatedException;
import org.tweetyproject.agents.dialogues.oppmodels.ArguingAgent;
import org.tweetyproject.agents.dialogues.oppmodels.GroundedGameProtocol;
import org.tweetyproject.agents.dialogues.oppmodels.GroundedGameSystem;
import org.tweetyproject.agents.dialogues.oppmodels.sim.GroundedGameGenerator;
import org.tweetyproject.agents.dialogues.oppmodels.sim.GroundedGameProtocolGenerator;
import org.tweetyproject.agents.dialogues.oppmodels.sim.GroundedGameT1AgentGenerator;
import org.tweetyproject.agents.dialogues.oppmodels.sim.T1Configuration;
import org.tweetyproject.agents.sim.AgentGenerator;
import org.tweetyproject.agents.sim.GameSimulator;
import org.tweetyproject.agents.sim.MultiAgentSystemGenerator;
import org.tweetyproject.agents.sim.ProtocolGenerator;
import org.tweetyproject.agents.sim.SimulationResult;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;

/**
 * 
 * Shows how a simulation of a multi-agent system can be set up. It defines a
 * dialogue game between different agents with varying complexity of their
 * opponent models.
 * 
 * @author Matthias Thimm
 * 
 */
public class GroundedTest {

	// Global parameters for simulation
	public static int frameworkSize;
	public static double attackProbability;
	public static boolean enforceTreeShape;
	public static int timeout = 60 * 60 * 6; // timeout of six hours

	public static int numberOfRunsEach = 100;

	// to ensure comparability
	public static long RANDOM_SEED1 = 43589744589l;
	public static long RANDOM_SEED2 = 96498321389l;
	public static long RANDOM_SEED3 = 647597568l;
	public static long RANDOM_SEED4 = 213596455579l;

	/**
	 * This method shows that with increasing complexity of the T1-belief state of
	 * the CONTRA agent (and constant model of the PRO agent), the average utility
	 * of the CONTRA agent increases. NOTE: the simulation might take a while.
	 * 
	 * @throws ProtocolTerminatedException if the protocol already terminated
	 */
	@Test
	public void runSimulationT1() throws ProtocolTerminatedException {
		GroundedTest.attackProbability = 0.3;
		GroundedTest.frameworkSize = 10;

		GroundedTest.enforceTreeShape = true;

		// We run different simulations with increasing recursion depth
		// of the CON agent's belief state
		for (int i = 1; i < 4; i++) {
			// We generate Dung theories with the given number of arguments and attack
			// probability.
			// In every theory, the argument under consideration is guaranteed to
			// be in the grounded extension (so under perfect information, the PRO
			// agent should always win)
			DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
			params.attackProbability = GroundedTest.attackProbability;
			params.numberOfArguments = GroundedTest.frameworkSize;
			params.enforceTreeShape = GroundedTest.enforceTreeShape;
			DungTheoryGenerator gen = new DefaultDungTheoryGenerator(params);
			gen.setSeed(GroundedTest.RANDOM_SEED1);
			// PRO agent knows 50% of all arguments, CONTRA agent knows 90% of all arguments
			MultiAgentSystemGenerator<ArguingAgent, GroundedGameSystem> masGenerator = new GroundedGameGenerator(gen,
					0.5, 0.9);
			masGenerator.setSeed(GroundedTest.RANDOM_SEED2);
			List<AgentGenerator<ArguingAgent, GroundedGameSystem>> agentGenerators = new ArrayList<AgentGenerator<ArguingAgent, GroundedGameSystem>>();

			// The PRO agent has a T1 belief state without opponent model
			T1Configuration configPro = new T1Configuration();
			configPro.maxRecursionDepth = 0;
			configPro.probRecursionDecay = 0;
			configPro.oppModelCorrect = true;
			// The CONTRA agent has a T1 belief state of depth i,
			// every sub-model correctly and completely models the PRO agent
			T1Configuration configCon = new T1Configuration();
			configCon.maxRecursionDepth = i;
			configCon.probRecursionDecay = 0;
			configCon.oppModelCorrect = true;

			agentGenerators.add(new GroundedGameT1AgentGenerator(GroundedGameSystem.AgentFaction.PRO, configPro));
			agentGenerators.add(new GroundedGameT1AgentGenerator(GroundedGameSystem.AgentFaction.CONTRA, configCon));
			agentGenerators.get(0).setSeed(GroundedTest.RANDOM_SEED3);
			agentGenerators.get(1).setSeed(GroundedTest.RANDOM_SEED4);

			ProtocolGenerator<GroundedGameProtocol, ArguingAgent, GroundedGameSystem> protGenerator = new GroundedGameProtocolGenerator();
			final GameSimulator<GroundedGameProtocol, ArguingAgent, GroundedGameSystem> sim = new GameSimulator<GroundedGameProtocol, ArguingAgent, GroundedGameSystem>(
					masGenerator, protGenerator, agentGenerators);
			final int j = i;
			// Run iterated simulations and show aggregated results (with timeout)
			Callable<String> callee = new Callable<String>() {
				@Override
				public String call() throws Exception {
					SimulationResult<GroundedGameProtocol, ArguingAgent, GroundedGameSystem> result = sim
							.run(GroundedTest.numberOfRunsEach);
					System.out.print("T1;T1;" + GroundedTest.frameworkSize + ";" + GroundedTest.attackProbability + ";"
							+ (GroundedTest.enforceTreeShape ? ("tree") : ("no-tree")));
					System.out.print(";T1-" + j + ";");
					System.out.println(result.csvDisplay());
					assertTrue(result.csvDisplay().startsWith("PRO 58; CONTRA 42"));
					return null;
				}
			};
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<String> future = executor.submit(callee);
			try {
				future.get(GroundedTest.timeout, TimeUnit.SECONDS);
			} catch (Exception e) {
				System.out.println("Aborted...");
			}
			executor.shutdownNow();
		}
	}



}
