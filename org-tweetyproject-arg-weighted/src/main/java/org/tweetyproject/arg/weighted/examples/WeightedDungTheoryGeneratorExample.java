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
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.weighted.examples;

import java.io.File;
import java.io.IOException;

import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;
import org.tweetyproject.arg.dung.util.KwtDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.PodlaszewskiCaminadaDungTheoryGenerator;
import org.tweetyproject.arg.weighted.util.WeightedSemiringDungTheoryGenerator;
import org.tweetyproject.arg.weighted.writer.WeightedApxWriter;
import org.tweetyproject.math.algebra.BooleanSemiring;
import org.tweetyproject.math.algebra.FuzzySemiring;
import org.tweetyproject.math.algebra.NonNumericSemiring;
import org.tweetyproject.math.algebra.NonNumericSemiring.SemiringElement;
import org.tweetyproject.math.algebra.ProbabilisticSemiring;
import org.tweetyproject.math.algebra.WeightedSemiring;

/**
 * @author Sandra Hoffmann
 *
 */
public class WeightedDungTheoryGeneratorExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		WeightedApxWriter writer = new WeightedApxWriter();
		String path = System.getProperty("user.home") 
				+ File.separator + "Documents" 
				+ File.separator + "TweetyProject"
				+ File.separator + "WeightedDungTheoryGeneratorExample";
		createDir(path);
		
		DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
		
		//weighted Semiring with a max weight of 20
		WeightedSemiringDungTheoryGenerator<Double> genWeighted = new WeightedSemiringDungTheoryGenerator<>(new DefaultDungTheoryGenerator(params), new WeightedSemiring(20f));
		String pathSubFolder = path + File.separator + "Weighted";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "weighted" + i + ".dl");
			//write the weighted AF to file. The weights are rounded to the nearest integer value.
			writer.write(genWeighted.next(), f, 0); 
		}
		
		
		//fuzzy Semiring and KwtDungTheoryGenerator
		int num_arguments = 150;
		int num_skept_arguments = 50;
		int size_ideal_extension = 20;
		int num_cred_arguments = 10;
		int num_pref_exts = 100;
		double p_ideal_attacked = 0.3;
		double p_ideal_attack_back = 0.2;
		double p_other_skept_args_attacked = 0.3;
		double p_other_skept_args_attack_back = 0.2;
		double p_cred_args_attacked = 0.3;
		double p_cred_args_attack_back = 0.2;
		double p_other_attacks = 0.2;
		
		WeightedSemiringDungTheoryGenerator<Double> genFuzzy = new WeightedSemiringDungTheoryGenerator<>(new KwtDungTheoryGenerator(num_arguments,
				num_skept_arguments,
				size_ideal_extension,
				num_cred_arguments,
				num_pref_exts,
				p_ideal_attacked,
				p_ideal_attack_back,
				p_other_skept_args_attacked,
				p_other_skept_args_attack_back,
				p_cred_args_attacked,
				p_cred_args_attack_back,
				p_other_attacks), new FuzzySemiring());
		pathSubFolder = path + File.separator + "Fuzzy";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "fuzzy" + i + ".dl");
			//write the weighted AF to file. The weights are rounded to 2 decimal places.
			writer.write(genFuzzy.next(), f, 2); 
		}
		
		//probabilistic Semiring and PodlaszewskiCaminadaDungTheoryGenerator
		WeightedSemiringDungTheoryGenerator<Double> genProbabilistic = new WeightedSemiringDungTheoryGenerator<>(new PodlaszewskiCaminadaDungTheoryGenerator(3), new ProbabilisticSemiring());
		pathSubFolder = path + File.separator + "Probabilistic";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "probabilistic" + i + ".dl");
			//write the weighted AF to file. The weights are rounded to 2 decimal places.
			writer.write(genProbabilistic.next(), f, 2); 
		}
		
		
		//boolean Semiring (Dung Style Framework) where the Boolean values are converted to doubles (false = 0.0, true = 1.0)
		WeightedSemiringDungTheoryGenerator<Boolean> genBoolean = new WeightedSemiringDungTheoryGenerator<>(new DefaultDungTheoryGenerator(params), new BooleanSemiring());
		pathSubFolder = path + File.separator + "Boolean";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "boolean" + i + ".dl");
			//write the weighted AF to file. The weights are converted to numerical values.
			writer.write(genBoolean.next(), f, true); 
		}
		
		
		//NonNumeric Semiring with Elements Good, Fair, Bad where the weight values are converted to doubles (bad = 0.0, fair = 1.0, good = 2.0)
		WeightedSemiringDungTheoryGenerator<SemiringElement> genNonNumeric = new WeightedSemiringDungTheoryGenerator<>(new DefaultDungTheoryGenerator(params), new NonNumericSemiring());
		pathSubFolder = path + File.separator + "NonNumeric";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "nonNumeric" + i + ".dl");
			//write the weighted AF to file.
			writer.write(genNonNumeric.next(), f); 
		}

	}

	
	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}

}
