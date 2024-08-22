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
package org.tweetyproject.agents.dialogues.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.tweetyproject.agents.ProtocolTerminatedException;
import org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.LotteryGameSystem;
import org.tweetyproject.agents.dialogues.lotteries.sim.UtilityBasedAgentGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.DirectGameProtocol;
import org.tweetyproject.agents.dialogues.lotteries.sim.DirectGameProtocolGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.DummyAgentGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.ProbabilisticLotteryAgentGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.LotteryGameGenerator;
import org.tweetyproject.agents.sim.AgentGenerator;
import org.tweetyproject.agents.sim.GameSimulator;
import org.tweetyproject.agents.sim.MultiAgentSystemGenerator;
import org.tweetyproject.agents.sim.ProtocolGenerator;
import org.tweetyproject.agents.sim.SimulationResult;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;

/**
 *
 * Shows how a simulation of a multi-agent system can be set up. It defines a
 * dialogue game between different agents, in particular one based on an action
 * selection strategy using lotteries.
 *
 * @author Matthias Thimm
 *
 */
public class LotteryDialogueTest {

	// Global parameters for simulation

	/**
	 * The size of the argumentation framework being used in the simulation.
	 * Represents the number of arguments in the framework.
	 */
	public static int frameworkSize;

	/**
	 * The probability that an attack exists between any two arguments in the
	 * argumentation framework.
	 * The value should be between 0.0 and 1.0, where 0.0 means no attacks, and 1.0
	 * means all possible attacks are present.
	 */
	public static double attackProbability;

	/**
	 * A flag indicating whether the framework should enforce a tree shape
	 * structure.
	 * If true, the argumentation framework will be constructed as a tree (no
	 * cycles).
	 */
	public static boolean enforceTreeShape;

	/**
	 * The timeout duration for each simulation run in seconds.
	 * Default is set to 72 hours (259200 seconds).
	 */
	public static int timeout = 60 * 60 * 72; // 72 hours

	/**
	 * The number of simulation runs to be executed for each experiment.
	 * Default is set to 100 runs.
	 */
	public static int numberOfRunsEach = 100;

	/**
	 * The semantics to be used for evaluating the argumentation framework in the
	 * simulation.
	 * Default is set to GROUNDED_SEMANTICS.
	 */
	public static Semantics semantics = Semantics.GROUNDED_SEMANTICS;

	// Seeds for ensuring comparability between runs

	/**
	 * A fixed random seed to ensure comparability of simulation results.
	 */
	public static long RANDOM_SEED1 = 435844589l;

	/**
	 * A fixed random seed to ensure comparability of simulation results.
	 */
	public static long RANDOM_SEED2 = 96421389l;

	/**
	 * A fixed random seed to ensure comparability of simulation results.
	 */
	public static long RANDOM_SEED3 = 6477568l;

	/**
	 * A fixed random seed to ensure comparability of simulation results.
	 */
	public static long RANDOM_SEED4 = 2136455579l;

	/**
	 * Runs a simulation of the multi-agent system where agents engage in a dialogue
	 * game based on a Dung theory. The simulation is repeated for a number of runs
	 * and the results are aggregated. The simulation runs with a set timeout, and
	 * if it exceeds the timeout, it is aborted.
	 *
	 * @param baseline a flag to indicate whether the baseline agent
	 *                 (UtilityBasedAgent)
	 *                 or the probabilistic lottery agent
	 *                 (ProbabilisticLotteryAgent)
	 *                 should be used in the simulation.
	 *
	 * @throws ProtocolTerminatedException if the protocol ends prematurely during
	 *                                     the simulation.
	 */
	public static void runSimulation(boolean baseline) throws ProtocolTerminatedException {
		// We generate Dung theories with the given number of arguments and attack
		// probability.
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
		MultiAgentSystemGenerator<AbstractLotteryAgent, LotteryGameSystem> masGenerator = new LotteryGameGenerator(gen,
				semantics);
		masGenerator.setSeed(GroundedTest.RANDOM_SEED2);
		List<AgentGenerator<AbstractLotteryAgent, LotteryGameSystem>> agentGenerators = new ArrayList<AgentGenerator<AbstractLotteryAgent, LotteryGameSystem>>();

		if (baseline)
			agentGenerators.add(new UtilityBasedAgentGenerator("BASE"));
		else
			agentGenerators.add(new ProbabilisticLotteryAgentGenerator("PRO"));
		agentGenerators.add(new DummyAgentGenerator("AUDIENCE"));
		agentGenerators.get(0).setSeed(GroundedTest.RANDOM_SEED3);
		agentGenerators.get(1).setSeed(GroundedTest.RANDOM_SEED4);

		ProtocolGenerator<DirectGameProtocol, AbstractLotteryAgent, LotteryGameSystem> protGenerator = new DirectGameProtocolGenerator();
		final GameSimulator<DirectGameProtocol, AbstractLotteryAgent, LotteryGameSystem> sim = new GameSimulator<DirectGameProtocol, AbstractLotteryAgent, LotteryGameSystem>(
				masGenerator, protGenerator, agentGenerators);
		// Run iterated simulations and show aggregated results (with timeout)
		Callable<String> callee = new Callable<String>() {
			@Override
			public String call() throws Exception {
				SimulationResult<DirectGameProtocol, AbstractLotteryAgent, LotteryGameSystem> result = sim
						.run(LotteryDialogueTest.numberOfRunsEach);
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

	/**
	 * LotteryDialogue test main method
	 *
	 * @param args the args
	 * @throws ProtocolTerminatedException
	 */
	public static void main(String[] args) throws ProtocolTerminatedException {
		LotteryDialogueTest.attackProbability = 0.3;
		LotteryDialogueTest.frameworkSize = 10;
		LotteryDialogueTest.enforceTreeShape = true;
		// baseline
		// LotteryDialogueTest.runSimulation(true);
		// actual agent
		LotteryDialogueTest.runSimulation(false);
	}

	/** Default Constructor */
	public LotteryDialogueTest() {
	}
}
