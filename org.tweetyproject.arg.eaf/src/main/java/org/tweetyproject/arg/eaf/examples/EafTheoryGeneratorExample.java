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
package org.tweetyproject.arg.eaf.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.KwtDungTheoryGenerator;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;
import org.tweetyproject.arg.eaf.util.EafTheoryGenerator;
import org.tweetyproject.arg.eaf.writer.EafApxWriter;

/**
 * 
 */
public class EafTheoryGeneratorExample {

	/** Default Constructor */
	public EafTheoryGeneratorExample(){

	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		EafApxWriter writer = new EafApxWriter();
		String path = System.getProperty("user.home")
				+ File.separator + "Documents"
				+ File.separator + "TweetyProject"
				+ File.separator + "EafTheoryGeneratorExample";
		createDir(path);
		
		
		//EAFs generated with DefaultDungTheoryGenerator and random constraint
		EafTheoryGenerator EAFgen = new EafTheoryGenerator();
		
		String pathSubFolder = path + File.separator + "default";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "eaf_" + i + ".apx");
			writer.write(EAFgen.next(), f);
		}
		
		//EAFs generated with the same underlying DungTheory using the KWTDungTheoryGenerator
		
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
		
		
		EAFgen = new EafTheoryGenerator(new KwtDungTheoryGenerator(num_arguments,
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
				p_other_attacks));
		
		pathSubFolder = path + File.separator + "kwt";
		createDir(pathSubFolder);
		ArrayList<EpistemicArgumentationFramework> eafs = EAFgen.next(20);
		
		int index = 0;
		for(EpistemicArgumentationFramework eaf:eafs) {
			File f = new File(pathSubFolder + File.separator +  "eaf_" + index + ".apx");
			writer.write(eaf, f);
			index++;
		}

		
		//EAFs generated with the same underlying DungTheory by passing a DungTheory to next
		DungTheory theory = new DungTheory();
		
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
		theory.add(f);
		theory.addAttack(a,b);
		theory.addAttack(a,c);
		theory.addAttack(b,d);
		theory.addAttack(c,e);
		theory.addAttack(e,f);
		theory.addAttack(f,e);
		
		pathSubFolder = path + File.separator + "givenAF";
		createDir(pathSubFolder);
		eafs = EAFgen.next(theory, 20);
		
		index = 0;
		for(EpistemicArgumentationFramework eaf:eafs) {
			File f2 = new File(pathSubFolder + File.separator +  "eaf_" + index + ".apx");
			writer.write(eaf, f2);
			index++;
		}		
	}
	
	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}
	

}


