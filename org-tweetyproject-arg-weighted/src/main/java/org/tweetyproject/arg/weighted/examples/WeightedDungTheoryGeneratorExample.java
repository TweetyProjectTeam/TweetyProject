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

import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.dung.writer.ApxWriter;
import org.tweetyproject.arg.weighted.util.WeightedSemiringDungTheoryGenerator;
import org.tweetyproject.arg.weighted.writer.WeightedApxWriter;
import org.tweetyproject.math.algebra.BooleanSemiring;
import org.tweetyproject.math.algebra.FuzzySemiring;
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
		
		//weighted Semiring
		WeightedSemiringDungTheoryGenerator genWeighted = new WeightedSemiringDungTheoryGenerator(params, new WeightedSemiring());
		String pathSubFolder = path + File.separator + "Weighted";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "weighted" + i + ".dl");
			writer.write(genWeighted.next(), f); 
		}
		
		
		//fuzzy Semiring
		WeightedSemiringDungTheoryGenerator genFuzzy = new WeightedSemiringDungTheoryGenerator(params, new FuzzySemiring());
		pathSubFolder = path + File.separator + "Fuzzy";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "fuzzy" + i + ".dl");
			writer.write(genFuzzy.next(), f); 
		}
		
		
		//boolean Semiring (Dung Style Framework)
		WeightedSemiringDungTheoryGenerator genBoolean = new WeightedSemiringDungTheoryGenerator(params, new BooleanSemiring());
		pathSubFolder = path + File.separator + "Boolean";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "boolean" + i + ".dl");
			writer.write(genBoolean.next(), f); 
		}

	}

	
	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}

}
