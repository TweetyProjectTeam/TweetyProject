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
package org.tweetyproject.arg.caf.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.caf.util.CafTheoryGenerator;
import org.tweetyproject.arg.caf.writer.CafApxWriter;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.KwtDungTheoryGenerator;


/**
 *  CAFTheoryGeneratorExample class
 * @author Sandra Hoffmann
 *
 */
public class CAFTheoryGeneratorExample  {

	/** Default Constructor */
	public CAFTheoryGeneratorExample(){

	}
	

	/**
	 * Example main method
	 * @param args the args
	 */
	public static void main(String[] args) throws IOException {
		
		CafApxWriter writer = new CafApxWriter();
		String path = System.getProperty("user.home")
				+ File.separator + "Documents"
				+ File.separator + "TweetyProject"
				+ File.separator + "CafTheoryGeneratorExample";
		createDir(path);
		
		
		//CAFs generated with DefaultDungTheoryGenerator and random constraint
		CafTheoryGenerator CAFgen = new CafTheoryGenerator();
		
		String pathSubFolder = path + File.separator + "default";
		createDir(pathSubFolder);
		for (int i = 0; i < 20; i++) {
			File f = new File(pathSubFolder + File.separator +  "caf_" + i + ".apx");
			writer.write(CAFgen.next(), f);
		}
		
		//CAFs generated with the same underlying DungTheory using the KWTDungTheoryGenerator
		
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
		
		
		CAFgen = new CafTheoryGenerator(new KwtDungTheoryGenerator(num_arguments,
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
		ArrayList<ConstrainedArgumentationFramework> cafs = CAFgen.next(20);
		
		int index = 0;
		for(ConstrainedArgumentationFramework caf:cafs) {
			File f = new File(pathSubFolder + File.separator +  "caf_" + index + ".apx");
			writer.write(caf, f);
			index++;
		}

		
		//CAFs generated with the same underlying DungTheory by passing a DungTheory to next
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
		cafs = CAFgen.next(theory, 20);
		
		index = 0;
		for(ConstrainedArgumentationFramework caf:cafs) {
			File f2 = new File(pathSubFolder + File.separator +  "caf_" + index + ".apx");
			writer.write(caf, f2);
			index++;
		}		
	}
	
	private static void createDir(String path) {
		File customDir = new File(path);
		customDir.mkdirs();
	}
	
}
