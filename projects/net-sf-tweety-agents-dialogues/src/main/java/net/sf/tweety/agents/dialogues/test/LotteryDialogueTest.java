package net.sf.tweety.agents.dialogues.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sf.tweety.agents.ProtocolTerminatedException;
import net.sf.tweety.agents.dialogues.lotteries.LotteryAgent;
import net.sf.tweety.agents.dialogues.lotteries.LotteryGameSystem;
import net.sf.tweety.agents.dialogues.lotteries.sim.BaselineAgentGenerator;
import net.sf.tweety.agents.dialogues.lotteries.sim.DirectGameProtocol;
import net.sf.tweety.agents.dialogues.lotteries.sim.DirectGameProtocolGenerator;
import net.sf.tweety.agents.dialogues.lotteries.sim.DummyAgentGenerator;
import net.sf.tweety.agents.dialogues.lotteries.sim.LotteryAgentGenerator;
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
	public static int timeout = 60*60*6; // timeout of six hours
	
	public static int numberOfRunsEach = 50;
	
	public static int semantics = Semantics.GROUNDED_SEMANTICS;
	
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
		MultiAgentSystemGenerator<LotteryAgent,LotteryGameSystem> masGenerator = new LotteryGameGenerator(gen,semantics);
		masGenerator.setSeed(GroundedTest.RANDOM_SEED2);
		List<AgentGenerator<LotteryAgent,LotteryGameSystem>> agentGenerators = new ArrayList<AgentGenerator<LotteryAgent,LotteryGameSystem>>();
			
		if(baseline)
			agentGenerators.add(new BaselineAgentGenerator());
		else
			agentGenerators.add(new LotteryAgentGenerator());
		agentGenerators.add(new DummyAgentGenerator());
		agentGenerators.get(0).setSeed(GroundedTest.RANDOM_SEED3);
		agentGenerators.get(1).setSeed(GroundedTest.RANDOM_SEED4);			
			
		ProtocolGenerator<DirectGameProtocol,LotteryAgent,LotteryGameSystem> protGenerator = new DirectGameProtocolGenerator();
		final GameSimulator<DirectGameProtocol,LotteryAgent,LotteryGameSystem> sim = new GameSimulator<DirectGameProtocol,LotteryAgent,LotteryGameSystem>(masGenerator,protGenerator,agentGenerators);
		// Run iterated simulations and show aggregated results (with timeout)
		Callable<String> callee = new Callable<String>(){
		    @Override
		    public String call() throws Exception {
		    	SimulationResult<DirectGameProtocol,LotteryAgent,LotteryGameSystem> result = sim.run(LotteryDialogueTest.numberOfRunsEach);
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
		LotteryDialogueTest.frameworkSize = 5;		
		LotteryDialogueTest.enforceTreeShape = true;
		//baseline
		LotteryDialogueTest.runSimulation(true);
		//actual agent
		LotteryDialogueTest.runSimulation(false);
	}
}
