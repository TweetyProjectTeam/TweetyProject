package net.sf.tweety.agents.dialogues.test;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sf.tweety.agents.ProtocolTerminatedException;
import net.sf.tweety.agents.dialogues.oppmodels.ArguingAgent;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameProtocol;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameSystem;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameProtocolGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameT1AgentGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameT2AgentGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameT3AgentGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.T1Configuration;
import net.sf.tweety.agents.dialogues.oppmodels.sim.T2Configuration;
import net.sf.tweety.agents.dialogues.oppmodels.sim.T3Configuration;
import net.sf.tweety.agents.sim.AgentGenerator;
import net.sf.tweety.agents.sim.GameSimulator;
import net.sf.tweety.agents.sim.MultiAgentSystemGenerator;
import net.sf.tweety.agents.sim.ProtocolGenerator;
import net.sf.tweety.agents.sim.SimulationResult;
import net.sf.tweety.arg.dung.util.DefaultDungTheoryGenerator;
import net.sf.tweety.arg.dung.util.DungTheoryGenerationParameters;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;
import net.sf.tweety.commons.TweetyConfiguration;
import net.sf.tweety.commons.TweetyLogging;

public class GroundedTest {
	
	//Global parameters for simulation
	public static int frameworkSize;
	public static double attackProbability;
	public static boolean enforceTreeShape;
	public static int timeout = 60*60*6; // timeout of six hours
	
	public static int numberOfRunsEach = 100;
	
	//to ensure comparability
	public static long RANDOM_SEED1 = 43589744589l;
	public static long RANDOM_SEED2 = 96498321389l;
	public static long RANDOM_SEED3 = 647597568l;
	public static long RANDOM_SEED4 = 213596455579l;
		
	/**
	 * This method shows that with increasing complexity of the T1-belief state of
	 * the CONTRA agent (and constant model of the PRO agent), the average utility of
	 * the CONTRA agent increases. NOTE: the simulation might take a while.
	 * @throws ProtocolTerminatedException
	 */
	public static void runSimulationT1() throws ProtocolTerminatedException{
		// We run different simulations with increasing recursion depth
		// of the CON agent's belief state
		for(int i = 1; i < 4; i++){
			// We generate Dung theories with the given number of arguments and attack probability.
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
			MultiAgentSystemGenerator<ArguingAgent,GroundedGameSystem> masGenerator = new GroundedGameGenerator(gen, 0.5, 0.9);
			masGenerator.setSeed(GroundedTest.RANDOM_SEED2);
			List<AgentGenerator<ArguingAgent,GroundedGameSystem>> agentGenerators = new ArrayList<AgentGenerator<ArguingAgent,GroundedGameSystem>>();
			
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
			
			agentGenerators.add(new GroundedGameT1AgentGenerator(GroundedGameSystem.AgentFaction.PRO,configPro));
			agentGenerators.add(new GroundedGameT1AgentGenerator(GroundedGameSystem.AgentFaction.CONTRA,configCon));
			agentGenerators.get(0).setSeed(GroundedTest.RANDOM_SEED3);
			agentGenerators.get(1).setSeed(GroundedTest.RANDOM_SEED4);			
			
			ProtocolGenerator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> protGenerator = new GroundedGameProtocolGenerator();
			final GameSimulator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> sim = new GameSimulator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem>(masGenerator,protGenerator,agentGenerators);
			final int j = i;
			// Run iterated simulations and show aggregated results (with timeout)
			Callable<String> callee = new Callable<String>(){
			    @Override
			    public String call() throws Exception {
			    	SimulationResult<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> result = sim.run(GroundedTest.numberOfRunsEach);
					System.out.print("T1;T1;" + GroundedTest.frameworkSize + ";" + GroundedTest.attackProbability + ";" + (GroundedTest.enforceTreeShape?("tree"):("no-tree")));
					System.out.print(";T1-" + j+ ";");					
					System.out.println(result.csvDisplay());
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
	
	/**
	 * Same as runSimulationT1()
	 * @throws ProtocolTerminatedException
	 */
	public static void runSimulationT2() throws ProtocolTerminatedException{
		// We run different simulations with increasing complexity
		// of the CON agent's belief state
		int depth[] = {2,3,4};				//depth complexity
		double decay[] = {0.4,0.3,0.2};		//decay complexity
		int width[] = {4,3,2};				//width complexity
		int idx[] = new int[3];
		idx[0] = 0;
		idx[1] = 0;
		idx[2] = 0;
		for(int i = 0; i < 27; i++){
			DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
			params.attackProbability = GroundedTest.attackProbability;
			params.numberOfArguments = GroundedTest.frameworkSize;			
			params.enforceTreeShape = GroundedTest.enforceTreeShape;	
			DungTheoryGenerator gen = new DefaultDungTheoryGenerator(params);
			gen.setSeed(GroundedTest.RANDOM_SEED1);
			// PRO agent knows 50% of all arguments, CONTRA agent knows 90% of all arguments
			MultiAgentSystemGenerator<ArguingAgent,GroundedGameSystem> masGenerator = new GroundedGameGenerator(gen, 0.5, 0.9);
			masGenerator.setSeed(GroundedTest.RANDOM_SEED2);
			List<AgentGenerator<ArguingAgent,GroundedGameSystem>> agentGenerators = new ArrayList<AgentGenerator<ArguingAgent,GroundedGameSystem>>();
		
			// The PRO agent has a T1 belief state without opponent model
			T1Configuration configPro = new T1Configuration();
			configPro.maxRecursionDepth = 0;
			configPro.probRecursionDecay = 0;
			configPro.oppModelCorrect = true;
			// The CONTRA agent has a T2 belief state of complexity i
			T2Configuration configCon = new T2Configuration();
			configCon.maxRecursionDepth = depth[idx[0]];
			configCon.probRecursionDecay = decay[idx[1]];
			configCon.maxRecursionWidth = width[idx[2]];
		
			agentGenerators.add(new GroundedGameT1AgentGenerator(GroundedGameSystem.AgentFaction.PRO,configPro));
			agentGenerators.add(new GroundedGameT2AgentGenerator(GroundedGameSystem.AgentFaction.CONTRA,configCon));
			agentGenerators.get(0).setSeed(GroundedTest.RANDOM_SEED3);
			agentGenerators.get(1).setSeed(GroundedTest.RANDOM_SEED4);
			
			ProtocolGenerator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> protGenerator = new GroundedGameProtocolGenerator();
			final GameSimulator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> sim = new GameSimulator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem>(masGenerator,protGenerator,agentGenerators);
			final int d1 = depth[idx[0]];
			final double d2 = decay[idx[1]];
			final int d3 =  width[idx[2]];
			// Run iterated simulations and show aggregated results (with timeout)
			Callable<String> callee = new Callable<String>(){
			    @Override
			    public String call() throws Exception {
			    	SimulationResult<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> result = sim.run(GroundedTest.numberOfRunsEach);		
			    	System.out.print("T1;T2;" + GroundedTest.frameworkSize + ";" + GroundedTest.attackProbability + ";" + (GroundedTest.enforceTreeShape?("tree"):("no-tree")));
					System.out.print(";T2-(" + d1 + "," + d2 + "," + d3 + ");");					
					System.out.println(result.csvDisplay());
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
	        idx[2]++;
	        if(idx[2]>2){
	        	idx[2] = 0;
	        	idx[1]++;
	        	if(idx[1]>2){
	        		idx[1] = 0;
	        		idx[0]++;
	        		if(idx[0]>2)
	        			idx[0] = 0;
	        	}
	        }
		}
	}
	
	/**
	 * Same as runSimulationT1()
	 * @throws ProtocolTerminatedException
	 */
	public static void runSimulationT3() throws ProtocolTerminatedException{
		// We run different simulations with increasing complexity
		// of the CON agent's belief state
		int depth[] = {2,3,4};				//depth complexity
		double decay[] = {0.4,0.3,0.2};		//decay complexity
		int width[] = {4,3,2};				//width complexity
		double virtArg[] = {0.3,0.2,0.1};	//virtual arguments
		double virtAtt[] = {0.7,0.8,0.9};	//virtual attacks
		int idx[] = new int[5];
		idx[0] = 0;
		idx[1] = 0;
		idx[2] = 0;
		idx[3] = 0;
		idx[4] = 0;
		
		for(int i = 0; i < 243; i++){
			DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
			params.attackProbability = GroundedTest.attackProbability;
			params.numberOfArguments = GroundedTest.frameworkSize;	
			params.enforceTreeShape = GroundedTest.enforceTreeShape;
			DungTheoryGenerator gen = new DefaultDungTheoryGenerator(params);
			gen.setSeed(GroundedTest.RANDOM_SEED1);
			// PRO agent knows 50% of all arguments, CONTRA agent knows 90% of all arguments
			MultiAgentSystemGenerator<ArguingAgent,GroundedGameSystem> masGenerator = new GroundedGameGenerator(gen, 0.5, 0.9);
			masGenerator.setSeed(GroundedTest.RANDOM_SEED2);
			List<AgentGenerator<ArguingAgent,GroundedGameSystem>> agentGenerators = new ArrayList<AgentGenerator<ArguingAgent,GroundedGameSystem>>();
		
			// The PRO agent has a T1 belief state without opponent model
			T1Configuration configPro = new T1Configuration();
			configPro.maxRecursionDepth = 0;
			configPro.probRecursionDecay = 0;
			configPro.oppModelCorrect = true;
			// The CONTRA agent has a T3 belief state
			T3Configuration configCon = new T3Configuration();
			configCon.maxRecursionDepth = depth[idx[0]];
			configCon.probRecursionDecay = decay[idx[1]];
			configCon.maxRecursionWidth = width[idx[2]];
			configCon.percentageVirtualArguments = virtArg[idx[3]];
			configCon.percentageVirtualAttacks = virtAtt[idx[4]];
				
			agentGenerators.add(new GroundedGameT1AgentGenerator(GroundedGameSystem.AgentFaction.PRO,configPro));
			agentGenerators.add(new GroundedGameT3AgentGenerator(GroundedGameSystem.AgentFaction.CONTRA,configCon));
			agentGenerators.get(0).setSeed(GroundedTest.RANDOM_SEED3);
			agentGenerators.get(1).setSeed(GroundedTest.RANDOM_SEED4);
		
			ProtocolGenerator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> protGenerator = new GroundedGameProtocolGenerator();
			final GameSimulator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> sim = new GameSimulator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem>(masGenerator,protGenerator,agentGenerators);
			final int d1 = depth[idx[0]];
			final double d2 = decay[idx[1]];
			final int d3 =  width[idx[2]];
			final double d4 = virtArg[idx[3]];
			final double d5 = virtAtt[idx[4]];
			// Run iterated simulations and show aggregated results (with timeout)
			Callable<String> callee = new Callable<String>(){
			    @Override
			    public String call() throws Exception {
			    	SimulationResult<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> result = sim.run(GroundedTest.numberOfRunsEach);
			    	System.out.print("T1;T3;" + GroundedTest.frameworkSize + ";" + GroundedTest.attackProbability + ";" + (GroundedTest.enforceTreeShape?("tree"):("no-tree")));
					System.out.print(";T3-(" + d1 + "," + d2 + "," + d3 + "," + d4 + "," + d5 + ");");					
					System.out.println(result.csvDisplay());					
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
	        idx[4]++;
	        if(idx[4]>2){
	        	idx[4] = 0;
	        	idx[3]++;
	        	if(idx[3]>2){
	        		idx[3] = 0;
	        		idx[2]++;
	        		if(idx[2]>2){
	        			idx[2] = 0;
	        			idx[1]++;
	        			if(idx[1]>2){
	        				idx[1] = 0;
	        				idx[0]++;
	        				if(idx[0]>2)
	        					idx[0] = 0;
	        			}
	        		}
	        	}
	        }
		}
	}
	
	public static void main(String[] args) throws ProtocolTerminatedException{
		// set logging level to "TRACE" to get detailed descriptions
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.ERROR;
		TweetyLogging.initLogging();
		GroundedTest.attackProbability = 0.3;
		GroundedTest.frameworkSize = 10;
		
		GroundedTest.enforceTreeShape = true;
		GroundedTest.runSimulationT1();
		//GroundedTest.enforceTreeShape = false;
		//GroundedTest.runSimulationT1();
				
		
		//GroundedTest.runSimulationT2();
		//GroundedTest.runSimulationT3();
		//GroundedTest.enforceTreeShape = false;
		//GroundedTest.runSimulationT1();
		//GroundedTest.runSimulationT2();
		//GroundedTest.runSimulationT3();
	}
}
