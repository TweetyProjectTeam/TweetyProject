package net.sf.tweety.arg.dung.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import net.sf.tweety.arg.dung.*;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

/**
 * Implements a customizable Dung theory generator.
 * @author Matthias Thimm
 */
public class DefaultDungTheoryGenerator implements DungTheoryGenerator {

	/** The parameters for generation. */
	private DungTheoryGenerationParameters params;
	
	/** Random numbers generator. */
	private Random random = new Random();
	
	/**
	 * Creates a new generator with the given parameters.
	 * @param params some generation parameters.
	 */
	public DefaultDungTheoryGenerator(DungTheoryGenerationParameters params){
		this.params = params;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#generate()
	 */
	@Override
	public DungTheory generate() {
		if(this.params.enforceTreeShape)
			return this.generateTreeShape(new Argument("A"));
		DungTheory theory = new DungTheory();
		for(int i = 0; i < this.params.numberOfArguments; i++)
			theory.add(new Argument("A" + i));		
		for(Argument a: theory)
			for(Argument b: theory){
				if(a == b && this.params.avoidSelfAttacks)
					continue;
				if(this.random.nextDouble() <= this.params.attackProbability)
					theory.add(new Attack(a,b));
			}
		return theory;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#generate(net.sf.tweety.argumentation.dung.syntax.Argument)
	 */
	@Override
	public DungTheory generate(Argument arg){
		DungTheory theory = new DungTheory();
		if(this.params.enforceTreeShape){
			boolean inExtension = false;
			do{
				theory = this.generateTreeShape(arg);
				inExtension = new GroundReasoner(theory).getExtensions().iterator().next().contains(arg);
			}while(!inExtension);
			return theory;
		}		
		theory.add(arg);
		for(int i = 1; i < this.params.numberOfArguments; i++)
			theory.add(new Argument("A" + i));
		for(Argument a: theory)
			for(Argument b: theory){
				if(a == b && this.params.avoidSelfAttacks)
					continue;
				if(this.random.nextDouble() <= this.params.attackProbability){
					Attack att = new Attack(a,b);
					theory.add(att);
					//Check whether this makes the argument out
					if(!new GroundReasoner(theory).getExtensions().iterator().next().contains(arg))
						theory.remove(att);
				}
			}
		return theory;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.util.DungTheoryGenerator#setSeed(double)
	 */
	public void setSeed(long seed){
		this.random = new Random(seed);
	}
	
	/**
	 * Generates a Dung theory with a tree shape where the given argument
	 * is the root.
	 * @param arg some argument.
	 * @return a Dung theory.
	 */
	private DungTheory generateTreeShape(Argument arg){
		DungTheory theory = new DungTheory();
		theory.add(arg);
		int numOfArguments = 1;
		Queue<Argument> q = new LinkedList<Argument>();
		q.add(arg);
		while(numOfArguments <= this.params.numberOfArguments){
			Argument a = new Argument("A" + numOfArguments++);
			theory.add(new Attack(a, (Argument)theory.toArray()[this.random.nextInt(numOfArguments-1)]));
			theory.add(a);
		}
		return theory;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "Def"+this.params.toString();
	}

}
