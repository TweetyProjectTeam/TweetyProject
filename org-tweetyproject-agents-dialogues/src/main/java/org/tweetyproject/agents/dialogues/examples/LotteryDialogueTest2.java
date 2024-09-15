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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.tweetyproject.agents.dialogues.lotteries.AbstractLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.LotteryGameSystem;
import org.tweetyproject.agents.dialogues.lotteries.ProbabilisticLotteryAgent;
import org.tweetyproject.agents.dialogues.lotteries.sim.DirectGameProtocol;
import org.tweetyproject.agents.dialogues.lotteries.sim.DirectGameProtocolGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.DummyAgentGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.LotteryGameGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.ProbabilisticLotteryAgentGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.RandomLotteryAgentGenerator;
import org.tweetyproject.agents.dialogues.lotteries.sim.UtilityBasedAgentGenerator;
import org.tweetyproject.agents.sim.AgentGenerator;
import org.tweetyproject.agents.sim.GameSimulator;
import org.tweetyproject.agents.sim.MultiAgentSystemGenerator;
import org.tweetyproject.agents.sim.ProtocolGenerator;
import org.tweetyproject.agents.sim.SimulationResult;
import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.FileDungTheoryGenerator;
import org.tweetyproject.commons.ParserException;

/**
 * Main class for empirical evaluation in [Hunter, Thimm. 2015, to appear]. Shows
 * how a simulation of a multi-agent system can be set up. It defines a dialogue
 * game between different agents, in particular one based on an action selection
 * strategy using lotteries.
 * 
 * @author Matthias Thimm
 */
public class LotteryDialogueTest2 {

	/** The argumentation semantics used */
	public static Semantics semantics = Semantics.GROUNDED_SEMANTICS;
	/** Timeout */
	public static int timeout = 60 * 60 * 72 * 3; // timeout of 72*3 hours
	/** Number of repetitions per file */
	public static int rep = 1;

	/**
	 * Main method for evaluation.
	 * 
	 * @param args additional arguments
	 * @throws ParserException       if parsing failed
	 * @throws FileNotFoundException if a file could not be found
	 * @throws IOException           if some general IO issue occurred
	 */
	public static void main(String[] args) throws ParserException, FileNotFoundException, IOException {
		String pathToApxGraphs = args[0];
		// String pathToApxGraphs = "/Users/mthimm/Desktop/tmp";

		// Agent generators
		List<AgentGenerator<AbstractLotteryAgent, LotteryGameSystem>> ag_gens = new ArrayList<AgentGenerator<AbstractLotteryAgent, LotteryGameSystem>>();
		ag_gens.add(new UtilityBasedAgentGenerator("UtilBased"));
		ag_gens.add(new RandomLotteryAgentGenerator("Random"));
		ag_gens.add(new ProbabilisticLotteryAgentGenerator("LotteryNaive"));
		ag_gens.add(new ProbabilisticLotteryAgentGenerator("LotterySimple", ProbabilisticLotteryAgent.UPDATE_SIMPLE));
		ag_gens.add(new ProbabilisticLotteryAgentGenerator("LotterySticky01", ProbabilisticLotteryAgent.UPDATE_STICKY,
				0.1));
		ag_gens.add(new ProbabilisticLotteryAgentGenerator("LotterySticky03", ProbabilisticLotteryAgent.UPDATE_STICKY,
				0.3));
		ag_gens.add(new ProbabilisticLotteryAgentGenerator("LotterySticky05", ProbabilisticLotteryAgent.UPDATE_STICKY,
				0.5));
		ag_gens.add(new ProbabilisticLotteryAgentGenerator("LotterySticky07", ProbabilisticLotteryAgent.UPDATE_STICKY,
				0.7));
		ag_gens.add(new ProbabilisticLotteryAgentGenerator("LotterySticky09", ProbabilisticLotteryAgent.UPDATE_STICKY,
				0.9));
		ag_gens.add(new ProbabilisticLotteryAgentGenerator("LotteryRough", ProbabilisticLotteryAgent.UPDATE_ROUGH));

		// for every different move selection strategy
		for (AgentGenerator<AbstractLotteryAgent, LotteryGameSystem> ag_gen : ag_gens) {
			// AAF generator
			File[] apxFiles = new File(pathToApxGraphs).listFiles(new ApxFilenameFilter());
			int numRuns = apxFiles.length * rep;
			DungTheoryGenerator aaf_gen = new FileDungTheoryGenerator(apxFiles, new ApxParser(), true);

			// MAS generator
			MultiAgentSystemGenerator<AbstractLotteryAgent, LotteryGameSystem> masGenerator = new LotteryGameGenerator(
					aaf_gen, semantics, false);
			List<AgentGenerator<AbstractLotteryAgent, LotteryGameSystem>> agentGenerators = new ArrayList<AgentGenerator<AbstractLotteryAgent, LotteryGameSystem>>();

			agentGenerators.add(ag_gen);
			agentGenerators.add(new DummyAgentGenerator("Audience"));

			ProtocolGenerator<DirectGameProtocol, AbstractLotteryAgent, LotteryGameSystem> protGenerator = new DirectGameProtocolGenerator();
			final GameSimulator<DirectGameProtocol, AbstractLotteryAgent, LotteryGameSystem> sim = new GameSimulator<DirectGameProtocol, AbstractLotteryAgent, LotteryGameSystem>(
					masGenerator, protGenerator, agentGenerators);
			// Run iterated simulations and show aggregated results (with timeout)
			Callable<String> callee = new Callable<String>() {
				@Override
				public String call() throws Exception {
					SimulationResult<DirectGameProtocol, AbstractLotteryAgent, LotteryGameSystem> result = sim
							.run(numRuns);
					System.out.println(result.csvDisplay());
					return null;
				}
			};
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<String> future = executor.submit(callee);
			try {
				future.get(timeout, TimeUnit.SECONDS);
			} catch (Exception e) {
				System.out.println("Aborted...");
				e.printStackTrace();
			}
			executor.shutdownNow();
		}

	}

    /** Default Constructor */
    public LotteryDialogueTest2(){}
}
