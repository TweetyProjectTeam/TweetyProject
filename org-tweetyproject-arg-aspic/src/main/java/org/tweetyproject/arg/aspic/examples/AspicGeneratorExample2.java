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

import java.io.File;
import java.io.IOException;

import org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory;
import org.tweetyproject.arg.aspic.util.RandomAspicArgumentationTheoryGenerator;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.ApxWriter;
import org.tweetyproject.arg.dung.writer.TgfWriter;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This code shows the use of the ASPIC theory generator. It generates some random ASPIC
 * theories, constructs the corresponding AF graphs, and writes them to a specific folder.
 * 
 * @author Matthias Thimm
 */
public class AspicGeneratorExample2 {
	public static void main(String[] args) throws IOException {
		int numberAtoms = 18;
		int numberFormulas = 80;
		int maxLiteralsInPremises = 2;
		double percentageStrictRules = 1;
		int numberOfAFs = 100;
		String pathToExportFolder = "/Users/mthimm/Desktop/aspic";
		ApxWriter apx = new ApxWriter();
		TgfWriter tgf = new TgfWriter();
		
		RandomAspicArgumentationTheoryGenerator gen = new RandomAspicArgumentationTheoryGenerator(numberAtoms, numberFormulas, maxLiteralsInPremises, percentageStrictRules);
		
		for(int i = 0; i < numberOfAFs; i++) {
			AspicArgumentationTheory<PlFormula> theory = gen.next();
			DungTheory aaf = theory.asDungTheory(true);
			System.out.println(aaf);
			apx.write(aaf, new File(pathToExportFolder + "/aspic_" + numberAtoms + "_" + numberFormulas + "_" + maxLiteralsInPremises + "_" + percentageStrictRules + "__" + i + ".apx" ));
			tgf.write(aaf, new File(pathToExportFolder + "/aspic_" + numberAtoms + "_" + numberFormulas + "_" + maxLiteralsInPremises + "_" + percentageStrictRules + "__" + i + ".tgf" ));			
		}		
	}
}
