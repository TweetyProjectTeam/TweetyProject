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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.ProtocolTerminatedException;
import net.sf.tweety.agents.dialogues.oppmodels.ArguingAgent;
import net.sf.tweety.agents.dialogues.oppmodels.BeliefState;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameProtocol;
import net.sf.tweety.agents.dialogues.oppmodels.GroundedGameSystem;
import net.sf.tweety.agents.dialogues.oppmodels.T3BeliefState;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameProtocolGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameT1AgentGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.GroundedGameT3AgentGenerator;
import net.sf.tweety.agents.dialogues.oppmodels.sim.T1Configuration;
import net.sf.tweety.agents.dialogues.oppmodels.sim.T3Configuration;
import net.sf.tweety.agents.sim.MultiAgentSystemGenerator;
import net.sf.tweety.agents.sim.ProtocolGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;
import net.sf.tweety.arg.dung.util.DefaultDungTheoryGenerator;
import net.sf.tweety.arg.dung.util.DungTheoryGenerationParameters;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;
import net.sf.tweety.commons.TweetyConfiguration;
import net.sf.tweety.commons.TweetyLogging;

public class GroundedTest2 {

	public static int timeout = 60*10; // 10 minutes
	public static int numberOfRunsEach = 5000;
	
	public static void main(String[] args) throws ProtocolTerminatedException{
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.ERROR;
		TweetyLogging.initLogging();
		
		GroundedTest.attackProbability = 0.4;
		GroundedTest.frameworkSize = 10;	
		GroundedTest.enforceTreeShape = false;
		
		DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
		params.attackProbability = GroundedTest.attackProbability;
		params.numberOfArguments = GroundedTest.frameworkSize;	
		params.enforceTreeShape = GroundedTest.enforceTreeShape;	
		DungTheoryGenerator gen = new DefaultDungTheoryGenerator(params);
		//gen.setSeed(GroundedTest.RANDOM_SEED1);
		// PRO agent knows 50% of all arguments, CONTRA agent knows 90% of all arguments			
		final MultiAgentSystemGenerator<ArguingAgent,GroundedGameSystem> masGenerator = new GroundedGameGenerator(gen, 0.5, 0.9);
		//masGenerator.setSeed(GroundedTest.RANDOM_SEED2);
				
		// The PRO agent has a T1 belief state without opponent model
		T1Configuration configPro = new T1Configuration();
		configPro.maxRecursionDepth = 0;
		configPro.probRecursionDecay = 0;
		configPro.oppModelCorrect = true;
		// The CONTRA agent
		T3Configuration configCon = new T3Configuration();
		configCon.maxRecursionDepth = 2;
		configCon.probRecursionDecay = 0.6;
		configCon.maxRecursionWidth = 3;
		configCon.percentageVirtualArguments = 0.3;
		configCon.percentageVirtualAttacks = 0.8;
		
		final GroundedGameT1AgentGenerator proGenerator = new GroundedGameT1AgentGenerator(GroundedGameSystem.AgentFaction.PRO,configPro);
		final GroundedGameT3AgentGenerator conGenerator = new GroundedGameT3AgentGenerator(GroundedGameSystem.AgentFaction.CONTRA,configCon);
		//proGenerator.setSeed(GroundedTest.RANDOM_SEED3);
		//conGenerator.setSeed(GroundedTest.RANDOM_SEED4);			
		
		final ProtocolGenerator<GroundedGameProtocol,ArguingAgent,GroundedGameSystem> protGenerator = new GroundedGameProtocolGenerator();

		//0 holds score for pro in pro vs. T3
		//1 holds score for T3 in pro vs. T3
		//2 holds score for pro in pro vs. T2
		//3 holds score for T2 in pro vs. T2
		//4 holds score for pro in pro vs. T1
		//5 holds score for T1 in pro vs. T1
		final int[] results = {0,0,0,0,0,0};
		
		for(int i = 0; i < GroundedTest2.numberOfRunsEach; i++){
			final int k = i;
			Callable<String> callee = new Callable<String>(){
		    	@Override
		    	public String call() throws Exception {
		    		SimulationParameters sParams = new SimulationParameters();
					GroundedGameSystem mas = masGenerator.generate(sParams);
					// create agents for T3 test
					ArguingAgent proAgent = proGenerator.generate(mas, sParams);
					ArguingAgent conAgentT3 = conGenerator.generate(mas, sParams);
					//make backups
					ArguingAgent proBackup = new ArguingAgent(proAgent.getFaction(), (BeliefState) proAgent.getBeliefState().clone());
					ArguingAgent conBackup = new ArguingAgent(conAgentT3.getFaction(), (BeliefState) conAgentT3.getBeliefState().clone());
					mas.add(proAgent);
					mas.add(conAgentT3);								
					GroundedGameProtocol prot = protGenerator.generate(mas,sParams);
					mas.execute(prot);	
					if(prot.hasWinner()){
						Agent winner = prot.getWinner();
						if(winner == proAgent)
							results[0]++;
						else results[1]++;					
					}
					mas.remove(proAgent);
					mas.remove(conAgentT3);
					// create agents for T2 test
					mas.getEnvironment().reset();
					proAgent = new ArguingAgent(proBackup.getFaction(), (BeliefState) proBackup.getBeliefState().clone());
					ArguingAgent conAgentT2 = new ArguingAgent(conBackup.getFaction(), ((T3BeliefState) conBackup.getBeliefState()).projectToT2BeliefState() );
					mas.add(proAgent);
					mas.add(conAgentT2);								
					prot = protGenerator.generate(mas,sParams);
					mas.execute(prot);	
					if(prot.hasWinner()){
						Agent winner = prot.getWinner();
						if(winner == proAgent)
							results[2]++;
						else results[3]++;					
					}
					mas.remove(proAgent);
					mas.remove(conAgentT2);
					// create agents for T1 test
					mas.getEnvironment().reset();
					proAgent = new ArguingAgent(proBackup.getFaction(), (BeliefState) proBackup.getBeliefState().clone());
					ArguingAgent conAgentT1 = new ArguingAgent(conBackup.getFaction(), ((T3BeliefState) conBackup.getBeliefState()).projectToT2BeliefState().sampleT1BeliefState() );
					mas.add(proAgent);
					mas.add(conAgentT1);								
					prot = protGenerator.generate(mas,sParams);
					mas.execute(prot);	
					if(prot.hasWinner()){
						Agent winner = prot.getWinner();
						if(winner == proAgent)
							results[4]++;
						else results[5]++;					
					}			
					System.out.println(k + ":\t" + results[0] + "\t" + results[1] + "\t" + results[2] + "\t" + results[3] + "\t" + results[4] + "\t" + results[5] );
					return "";
				}
			};			
			ExecutorService executor = Executors.newSingleThreadExecutor();
        	Future<String> future = executor.submit(callee);
        	try {
            	future.get(GroundedTest2.timeout, TimeUnit.SECONDS);	            
        	} catch (Exception e) {
        		System.out.println("Aborted...");
        	}
        	executor.shutdownNow();
		}				
			
	}
}
