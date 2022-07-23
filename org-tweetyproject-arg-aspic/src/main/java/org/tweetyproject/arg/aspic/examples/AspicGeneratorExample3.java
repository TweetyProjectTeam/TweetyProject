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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.aspic.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory;
import org.tweetyproject.arg.aspic.util.RandomAspicArgumentationTheoryGenerator;
import org.tweetyproject.arg.aspic.writer.AspicWriter;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * This code shows the use of the ASPIC theory generator. It generates some random ASPIC
 * theories with some parameter combinations and writes them to disk.
 * 
 * @author Matthias Thimm
 */
public class AspicGeneratorExample3 {
	/**
	 * 
	 * @param args args
	 * @throws IOException exception
	 */
	public static void main(String[] args) throws IOException {
		int numberOfAFs = 100;
		int[] l_numberAtoms = {5,10,15};//{10,15,20,25,30};
		int[] l_numberFormulas = {10,20,30};//{100,200,300,400};
		int[] l_maxLiteralsInPremises = {2,3};//4
		double[] l_percentageStrictRules = {0.2,0.4,0.6};
		
		String pathToFolder = "/Users/mthimm/Desktop/random_small_aspic_instances";
		
		AspicWriter<PlFormula> writer = new AspicWriter<PlFormula>();
		
		Random rand = new Random();
		for(int numberAtoms: l_numberAtoms)
			for(int numberFormulas: l_numberFormulas)
				for(int maxLiteralsInPremises: l_maxLiteralsInPremises)
					for(double percentageStrictRules: l_percentageStrictRules) {
						RandomAspicArgumentationTheoryGenerator gen = new RandomAspicArgumentationTheoryGenerator(numberAtoms, numberFormulas, maxLiteralsInPremises, percentageStrictRules);						
						for(int i = 0; i < numberOfAFs; i++) {
							System.out.println(pathToFolder + "/rand_" + numberAtoms + "_" + numberFormulas + "_" + maxLiteralsInPremises + "_" + percentageStrictRules + "__" + i + ".aspic");
							AspicArgumentationTheory<PlFormula> theory = gen.next();
							writer.write(theory, new File(pathToFolder + "/rand_" + numberAtoms + "_" + numberFormulas + "_" + maxLiteralsInPremises + "_" + percentageStrictRules + "__" + i + ".aspic" ));							
							//write example query							
							BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(pathToFolder + "/rand_" + numberAtoms + "_" + numberFormulas + "_" + maxLiteralsInPremises + "_" + percentageStrictRules + "__" + i + ".query" )));
			
							List<Proposition> sig = new ArrayList<>(((PlSignature) theory.getMinimalSignature()).toCollection());							
						    writer1.write(sig.get(rand.nextInt(sig.size())).toString());
						    writer1.close();
						}		
					}
	}
}
