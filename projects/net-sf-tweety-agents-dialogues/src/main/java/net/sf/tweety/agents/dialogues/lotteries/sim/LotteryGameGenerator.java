package net.sf.tweety.agents.dialogues.lotteries.sim;

import java.util.Random;

import net.sf.tweety.agents.dialogues.lotteries.LotteryAgent;
import net.sf.tweety.agents.dialogues.lotteries.LotteryGameSystem;
import net.sf.tweety.agents.sim.MultiAgentSystemGenerator;
import net.sf.tweety.agents.sim.SimulationParameters;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.divisions.Division;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;
import net.sf.tweety.arg.prob.lotteries.SubgraphProbabilityFunction;
import net.sf.tweety.arg.prob.lotteries.UtilityFunction;
import net.sf.tweety.math.probability.Probability;

/**
 * Generates lottery games.
 * @author Matthias Thimm
 */
public class LotteryGameGenerator implements MultiAgentSystemGenerator<LotteryAgent,LotteryGameSystem> {
	
	/** Key for the simulation parameter which refers to the universal theory generated. */
	public static final int PARAM_UNIVERSALTHEORY = 0;
	/** Key for the simulation parameter which refers to the argument of the dialogue. */
	public static final int PARAM_ARGUMENT = 1;
	/** Key for the simulation parameter which refers to the dummy agent's theory. */
	public static final int PARAM_DUMMY_THEORY = 2;
	/** Key for the simulation parameter which refers to the probability function for the actual lottery agent. */
	public static final int PARAM_LOT_PROB = 3;
	/** Key for the simulation parameter which refers to the utility function for the actual lottery agent. */
	public static final int PARAM_LOT_UTIL = 4;
	/** Key for the simulation parameter which refers to the semantics used. */
	public static final int PARAM_SEM = 5;
	
	/** for generating Dung theories. */
	private DungTheoryGenerator gen;
	
	/** The semantics used. */
	private int semantics;
	
	/** Random numbers generator. */
	private Random random = new Random();
	
	/**
	 * Creates a new game generator.
	 * @param gen for generating Dung theories.
	 * @param semantics the semantics used.
	 */
	public LotteryGameGenerator(DungTheoryGenerator gen, int semantics){
		this.gen = gen;
		this.semantics = semantics;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.MultiAgentSystemGenerator#generate(net.sf.tweety.agents.sim.SimulationParameters)
	 */
	@Override
	public LotteryGameSystem generate(SimulationParameters params) {
		Argument arg = new Argument("A");
		DungTheory theory = this.gen.generate(arg);		
		params.put(LotteryGameGenerator.PARAM_UNIVERSALTHEORY, theory);
		params.put(LotteryGameGenerator.PARAM_ARGUMENT, arg);		
		params.put(LotteryGameGenerator.PARAM_SEM, this.semantics);
		// random utility function		
		UtilityFunction util = new UtilityFunction();
		for(Division d: Division.getStandardDivisions(theory))
			util.put(d, random.nextDouble());
		params.put(LotteryGameGenerator.PARAM_LOT_UTIL, util);
		// random probability function
		SubgraphProbabilityFunction prob = new SubgraphProbabilityFunction(theory);
		for(DungTheory t: prob.keySet())
			prob.put(t, new Probability(random.nextDouble()));
		prob.normalize();		
		params.put(LotteryGameGenerator.PARAM_LOT_PROB, prob);
		// sample some random theory
		params.put(LotteryGameGenerator.PARAM_DUMMY_THEORY, prob.sample(this.random));		
		return new LotteryGameSystem(theory);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sim.MultiAgentSystemGenerator#setSeed(long)
	 */
	public void setSeed(long seed){
		this.random = new Random(seed);
	}
}
