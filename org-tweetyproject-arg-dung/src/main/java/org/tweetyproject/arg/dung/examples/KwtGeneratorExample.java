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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.tweetyproject.arg.dung.reasoner.AbstractAcceptabilityReasoner;
import org.tweetyproject.arg.dung.reasoner.IaqAcceptabilityReasoner;
import org.tweetyproject.arg.dung.reasoner.ProboReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.KwtDungTheoryGenerator;
import org.tweetyproject.arg.dung.writer.TgfWriter;
import org.tweetyproject.commons.InferenceMode;

/**
 * 
 * Example code for generating a lot of KWT graphs.
 * 
 * @author Matthias Thimm
 *
 */
public class KwtGeneratorExample {
	public static void main(String[] args) throws IOException {
		String path = args[0]; // enter path where graphs and solutions should be stored
		// add path of any ICCMA-compliant solver supporting DS-PR
		AbstractAcceptabilityReasoner reasoner = new IaqAcceptabilityReasoner(new ProboReasoner("/Users/mthimm/Documents/software/misc_bins/mu-toksia-glucose-2020-03-12", Semantics.PR),InferenceMode.SKEPTICAL);
		int[] i_num_arguments = {150};
		double[] i_num_skept_arguments = {0.4,0.5,0.6};
		double[] i_size_ideal_extension = {0.5};
		double[] i_num_cred_arguments = {0.1,0.4};
		int[] i_num_pref_exts = {100,200};
		
		double[] i_p_ideal_attacked = {0.3,0.5};
		double[] i_p_ideal_attack_back = {0.2,0.4};
		double[] i_p_other_skept_args_attacked = {0.3,0.5};
		double[] i_p_other_skept_args_attack_back = {0.2,0.4};
		double[] i_p_cred_args_attacked = {0.3,0.5};
		double[] i_p_cred_args_attack_back = {0.2,0.4};
		double[] i_p_other_attacks = {0.2,0.3};
		
		int num = 1;
		int offset = 1;
		
		TgfWriter writer = new TgfWriter();
		for(int i: i_num_arguments)
			for(double j: i_num_skept_arguments)
				for(double k: i_size_ideal_extension)
						for(double m: i_num_cred_arguments)
							for(int n: i_num_pref_exts)
								for(double o: i_p_ideal_attacked)
									for(double p: i_p_ideal_attack_back)
										for(double q: i_p_other_skept_args_attacked)
											for(double r: i_p_other_skept_args_attack_back)
												for(double s: i_p_cred_args_attacked)
													for(double t: i_p_cred_args_attack_back)
														for(double u: i_p_other_attacks) {
															int num_arguments = i;
															int num_skept_arguments = (int) Math.round(j*i);
															int size_ideal_extension = (int) Math.round(k*num_skept_arguments);
															int num_cred_arguments = (int) Math.round(m*i);;
															int num_pref_exts = n;
															double p_ideal_attacked = o;
															double p_ideal_attack_back = p;
															double p_other_skept_args_attacked = q;
															double p_other_skept_args_attack_back = r;
															double p_cred_args_attacked = s;
															double p_cred_args_attack_back = t;
															double p_other_attacks = u;
															KwtDungTheoryGenerator gen = new KwtDungTheoryGenerator(
																	num_arguments,
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
																	p_other_attacks
																);
															for(int v = 0; v < num; v++) {
																DungTheory af = gen.next();
																String filename =path+"kwt_"+
																		num_arguments + "_" + 
																		num_skept_arguments + "_" +
																		size_ideal_extension + "_" +
																		num_cred_arguments + "_" +
																		num_pref_exts + "_" +
																		p_ideal_attacked + "_" +
																		p_ideal_attack_back + "_" +
																		p_other_skept_args_attacked + "_" +
																		p_other_skept_args_attack_back + "_" +
																		p_cred_args_attacked + "_" +
																		p_cred_args_attack_back + "_" +
																		p_other_attacks + "__" +
																		(v+offset) + ".tgf";
																System.out.println(filename);
																writer.write(af, new File(filename));
																Collection<Argument> c = reasoner.getAcceptableArguments(af);
																File outputFile = new File(filename+".espr");
																String output = TgfWriter.writeArguments(c);
																FileWriter writer2 = new FileWriter(outputFile);
																writer2.write(output);
																writer2.close();
															}
														}
	}
}
