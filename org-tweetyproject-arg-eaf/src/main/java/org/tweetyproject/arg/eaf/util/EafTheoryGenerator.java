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
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.ml.syntax.Necessity;
import org.tweetyproject.logics.ml.syntax.Possibility;



/**
 * The EafTheoryGenerator class is responsible for generating EpistemicArgumentationFramworks(EAFs) based on a 
 * given DungTheory. The class can generate one or multiple EAFs with random epistemic constraints in DNF format.
 *  
 * 
 * @author Sandra Hoffmann
 *
 */
public class EafTheoryGenerator {
	private DungTheoryGenerator generator;
	/** Random numbers generator. */
	private Random random = new Random();
	
    /**
     * Default constructor. Instantiates a default DungTheoryGenerator
     */
	public EafTheoryGenerator() {
		DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
		this.generator = new DefaultDungTheoryGenerator(params);
	}
	
    /**
     * Constructor with a custom DungTheoryGenerator
     * 
     * @param generator the custom DungTheoryGenerator to use for generating Dung theories.
     */
	public EafTheoryGenerator(DungTheoryGenerator generator) {
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
     * Generates a new EAF using a randomly generated constraint.
     * 
     * @return the next EAF with a random constraint.
     */
	public EpistemicArgumentationFramework next() {
		DungTheory newTheory = generator.next();
		FolFormula newBelief = generateConstraint(newTheory);
		return new EpistemicArgumentationFramework(newTheory,newBelief);	
		
	};	
	
    /**
     * Generates numEAFs EAFs, each with a different randomly generated constraint.
     * Each EAF is based on the same (randomly generated) DungTheory.
     * 
     * @param numEAFs the number of constraints and EAFs to generate.
     * @return a list of EAFs with random constraints.
     */
	public ArrayList<EpistemicArgumentationFramework> next(int numEAFs) {
		DungTheory newTheory = generator.next();
		return next(newTheory, numEAFs);
	};
	
    /**
     * Generates numEAFs EAFs, each with a different randomly generated constraint.
     * Each EAF is based on the same given DungTheory.
     * 
     * @param theory the Dung theory for which the EAFs are generated.
     * @param numEAFs the number of constraints and EAFs to generate.
     * @return a list of EAFs with random constraints based on the given theory.
     */
	public ArrayList<EpistemicArgumentationFramework> next(DungTheory theory, int numEAFs) {
		ArrayList<FolFormula> constraints = generateConstraints(theory, numEAFs);
		ArrayList<EpistemicArgumentationFramework> eafs = new ArrayList<>();
		
		for(int i = 0; i < numEAFs; i++) {
			eafs.add(new EpistemicArgumentationFramework(theory,constraints.get(i)));
		}
		return eafs;		
	};
	

	
	//generates an epistemic constraint in CNF for a given DungTheory
	private FolFormula generateConstraint(DungTheory theory) {
		return generateConstraints(theory, 1).get(0);
	}
	
	//generates a list of epistemic constraints in CNF for a given DungTheory
	private ArrayList<FolFormula> generateConstraints(DungTheory theory, int numConstraints) {
		ArrayList<FolFormula> constraints = new ArrayList<>();
		ArrayList<Predicate> literals = new ArrayList<>();
		Iterator<Argument> it = theory.iterator();
		while(it.hasNext()) {
			Argument arg = it.next();
			literals.add(new Predicate(arg.getName()));
		}
		
		for (int i = 0; i < numConstraints; i++) {
            Set<RelationalFormula> disjunction = new HashSet<>();
            // Randomly choose 1 - (args/2) disjuncts
            int numDisjuncts = 1 + random.nextInt(theory.getNumberOfNodes()/2);

            for (int j = 0; j < numDisjuncts; j++) {
                Set<RelationalFormula> conjunction = new HashSet<>();
                // Randomly choose 1-2 conjuncts
                int numConjuncts = 1 + random.nextInt(2);

                for (int k = 0; k < numConjuncts; k++) {
                    Predicate literal = literals.get(random.nextInt(literals.size()));
                    boolean isNegated = random.nextBoolean();
                    boolean isNecessity = random.nextBoolean();
                    
                    if (isNecessity) {
                        if (isNegated) {
                        	conjunction.add(new Necessity(new Negation(new FolAtom(literal))));
                        } else {
                            conjunction.add(new Necessity(new FolAtom(literal)));
                        }
                    } else {
                        if (isNegated) {
                        	conjunction.add(new Possibility(new Negation(new FolAtom(literal))));
                        } else {
                            conjunction.add(new Possibility(new FolAtom(literal)));
                        }
                    }
                }
                disjunction.add(new Conjunction(conjunction));
            }
            constraints.add(new Disjunction(disjunction));
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
