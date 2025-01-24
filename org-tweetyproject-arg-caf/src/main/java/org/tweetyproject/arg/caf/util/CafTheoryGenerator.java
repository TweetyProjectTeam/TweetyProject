/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.caf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.util.CnfSampler;

/**
 * The {@code CafTheoryGenerator} class is responsible for generating 
 * {@link ConstrainedArgumentationFramework} (CAFs) based on a 
 * given {@link DungTheory}. The class can generate one or multiple CAFs with random
 *  constraints in CNF (Conjunctive Normal Form).
 * 
 * This generator can either use a custom {@link DungTheoryGenerator} 
 * or instantiate a default one. 
 * 
 * @author Sandra Hoffmann
 *
 */
public class CafTheoryGenerator {
	
	private DungTheoryGenerator generator;
	/** Random numbers generator. */
	private Random random = new Random();
	
    /**
     * Default constructor. Instantiates a default {@link DungTheoryGenerator} 
     * using the default {@link DungTheoryGenerationParameters}.
     */
	public CafTheoryGenerator() {
		DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
		this.generator = new DefaultDungTheoryGenerator(params);
	}
	
    /**
     * Constructor with a custom {@link DungTheoryGenerator}.
     * 
     * @param generator the custom {@code DungTheoryGenerator} to use for generating Dung theories.
     */
	public CafTheoryGenerator(DungTheoryGenerator generator) {
		this.generator = generator;
	}
	
	/**
	 * Checks whether there is a next element.
	 * @return whether there is a next element
	 */
	public boolean hasNext() {
		return true;
	};
	
    /**
     * Generates a new CAF using a randomly generated constraint.
     * 
     * @return the next CAF with a random constraint.
     */
	public ConstrainedArgumentationFramework next() {
		DungTheory newTheory = generator.next();
		PlFormula newBelief = generateConstraint(newTheory);
		return new ConstrainedArgumentationFramework(newTheory,newBelief);	
		
	};
	
    /**
     * Generates numCAFs CAFs, each with a different randomly generated constraint.
     * Each CAF is based on the same (randomly generated) DungTheory.
     * 
     * @param numCAFs the number of constraints and CAFs to generate.
     * @return a list of CAFs with random constraints.
     */
	public ArrayList<ConstrainedArgumentationFramework> next(int numCAFs) {
		DungTheory newTheory = generator.next();
		return next(newTheory, numCAFs);
	};
	
    /**
     * Generates numCAFs CAFs, each with a different randomly generated constraint.
     * Each CAF is based on the same given DungTheory.
     * 
     * @param theory the Dung theory for which the CAFs are generated.
     * @param numCAFs the number of constraints and CAFs to generate.
     * @return a list of CAFs with random constraints based on the given theory.
     */
	public ArrayList<ConstrainedArgumentationFramework> next(DungTheory theory, int numCAFs) {
		ArrayList<PlFormula> constraints = generateConstraints(theory, numCAFs);
		ArrayList<ConstrainedArgumentationFramework> cafs = new ArrayList<>();
		
		for(int i = 0; i < numCAFs; i++) {
			cafs.add(new ConstrainedArgumentationFramework(theory,constraints.get(i)));
		}
		return cafs;		
	};
	
	
	//generates a constraint in CNF for a given DungTheory
	private PlFormula generateConstraint(DungTheory theory) {
		return generateConstraints(theory, 1).get(0);
	}
	
	//generates a list of constraints in CNF for a given DungTheory
	private ArrayList<PlFormula> generateConstraints(DungTheory theory, int numConstraints) {
		ArrayList<PlFormula> constraints = new ArrayList<>();
		Collection<Proposition> literals = new ArrayList<>();
		Iterator<Argument> it = theory.iterator();
		while(it.hasNext()) {
			Argument arg = it.next();
			literals.add(new Proposition(arg.getName()));
		}	

		CnfSampler constraintGen = new CnfSampler(new PlSignature(literals), random.nextDouble());
		for (int i = 0; i < numConstraints; i++) {
			constraints.add(constraintGen.sampleFormula());
		}
		return constraints;
	}
	
	/**
	 * Set the seed for the generation. Every two
	 * runs of generations with the same seed
	 * are ensured to be identical.
	 * @param seed some seed.
	 */
	public void setSeed(long seed){
		this.random = new Random(seed);
	}

}
