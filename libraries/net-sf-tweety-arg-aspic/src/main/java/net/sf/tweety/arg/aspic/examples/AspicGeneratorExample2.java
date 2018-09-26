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
package net.sf.tweety.arg.aspic.examples;

import java.io.File;
import java.io.IOException;

import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.util.RandomAspicArgumentationTheoryGenerator;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.arg.dung.writer.ApxWriter;
import net.sf.tweety.arg.dung.writer.TgfWriter;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This code shows the use of the ASPIC theory generator. It generates some random ASPIC
 * theories, constructs the corresponding AF graphs, and writes them to a specific folder
 * 
 * @author Matthias Thimm
 */
public class AspicGeneratorExample2 {
	public static void main(String[] args) throws IOException {
		int numberAtoms = 20;
		int numberFormulas = 100;
		int maxLiteralsInPremises = 2;
		double percentageStrictRules = 0.2;
		int numberOfAFs = 500;
		String pathToExportFolder = "/Users/mthimm/Desktop/aspic";
		ApxWriter apx = new ApxWriter();
		TgfWriter tgf = new TgfWriter();
		
		for(int i = 0; i < numberOfAFs; i++) {
			AspicArgumentationTheory<PropositionalFormula> theory = RandomAspicArgumentationTheoryGenerator.next(numberAtoms, numberFormulas, maxLiteralsInPremises, percentageStrictRules);
			DungTheory aaf = theory.asDungTheory(true);
			System.out.println(aaf);
			apx.write(aaf, new File(pathToExportFolder + "/aspic_" + i + ".apx" ));
			tgf.write(aaf, new File(pathToExportFolder + "/aspic_" + i + ".tgf" ));			
		}		
	}
}
