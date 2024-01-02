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
package org.tweetyproject.arg.weighted.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.weighted.syntax.WeightedArgumentationFramework;

/**
 *  * Writes an weighted abstract argumentation framework into a file of the
 * APX format including the weight value of each attack. Weights on attacks are specified by the suffix :- followed by the weight value. 
 * 
 * @author Sandra Hoffmann
 *
 */
public class WeightedApxWriter {

	public void write(WeightedArgumentationFramework waf, File f) throws IOException {
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		for(Argument a: waf)
			writer.println("arg(" + a.getName() + ").");
		for(Attack att: waf.getAttacks())
			writer.println("att(" + att.getAttacker().getName() + "," + att.getAttacked().getName() + "):-" + waf.getWeight(att) +".");		
		writer.close();	
		
	}

}
