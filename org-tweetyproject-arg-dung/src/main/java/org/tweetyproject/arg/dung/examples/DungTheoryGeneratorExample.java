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
package org.tweetyproject.arg.dung.examples;

import java.io.File;
import java.io.IOException;

import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.DungTheoryGenerationParameters;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.IsoSafeEnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.dung.util.PodlaszewskiCaminadaDungTheoryGenerator;
import org.tweetyproject.arg.dung.writer.ApxWriter;

/**
 * Example code for generating dung theories and exporting them to apx format.
 * 
 * @author Anna Gessler
 */
public class DungTheoryGeneratorExample {
	/**
	 * 
	 * @param args string
	 * @throws IOException Exception
	 */
	public static void main(String[] args) throws IOException{
		ApxWriter writer = new ApxWriter();
		String path = System.getProperty("user.home") 
				+ File.separator + "Documents" 
				+ File.separator + "TweetyProject"
				+ File.separator + "DungTheoryGeneratorExample";
		createDir(path);
		
		EnumeratingDungTheoryGenerator gen1 = new EnumeratingDungTheoryGenerator();
		String pathSubFolder = path + File.separator + "Enumerating";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "enumerating" + i + ".apx");
			writer.write(gen1.next(), f); 
		}
		
		DungTheoryGenerationParameters params = new DungTheoryGenerationParameters();
		DungTheoryGenerator gen2 = new DefaultDungTheoryGenerator(params);
		pathSubFolder = path + File.separator + "Default";
		createDir(pathSubFolder);
		for (int i = 0; i < 10; i++) {
			File f = new File(pathSubFolder + File.separator +  "default" + i + ".apx");
			writer.write(gen2.next(), f); 
		}
		
		DungTheoryGenerator gen3 = new IsoSafeEnumeratingDungTheoryGenerator();
		pathSubFolder = path + File.separator + "IsoSafeEnumerating";
		createDir(pathSubFolder);
		for (int i = 0; i < 10; i++) {
			File f = new File(pathSubFolder + File.separator +  "isoSafeEnumerating" + i + ".apx");
			writer.write(gen3.next(), f); 
		}
		
		DungTheoryGenerator gen4 = new PodlaszewskiCaminadaDungTheoryGenerator(2);
		pathSubFolder = path + File.separator + "Podlaszewski";
		createDir(pathSubFolder);
		for (int i = 0; i < 10; i++) {
			File f = new File(pathSubFolder + File.separator +  "podlaszewski" + i + ".apx");
			writer.write(gen4.next(), f); 
		}

	}
	
	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}
}
