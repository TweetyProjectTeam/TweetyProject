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
package org.tweetyproject.logics.rcl.examples;

import java.io.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.fol.parser.*;
import org.tweetyproject.logics.fol.syntax.*;
import org.tweetyproject.logics.rcl.parser.*;
import org.tweetyproject.logics.rcl.reasoner.SimpleRelationalCReasoner;
import org.tweetyproject.logics.rcl.semantics.*;
import org.tweetyproject.logics.rcl.syntax.RclBeliefSet;

/**
 * Example code illustrating the use of working with relational
 * conditionals and using c reasoning.
 * @author Matthias Thimm
 *
 */
public class RclExample {

	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.DEBUG;
		TweetyLogging.initLogging();
		
		RclParser parser = new RclParser();
		RclBeliefSet bs = (RclBeliefSet) parser.parseBeliefBaseFromFile(args[0]);
		System.out.println("Knowledge base:\n " + bs);
		
		RelationalRankingFunction kappa = new SimpleRelationalCReasoner(true).getModel(bs,parser.getSignature());
		System.out.println("Simple c-representation:\n" + kappa);		
		
		System.out.println();
		System.out.println("Queries: ");
		
		FolParser pars = new FolParser();
		pars.setSignature(parser.getSignature());
		
		if(args[1] != null && !args[1].equals("")){
			BufferedReader in = new BufferedReader(new FileReader(args[1]));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line + "\t:\t" + kappa.rank((FolFormula)pars.parseFormula(line)));				
			}
			in.close();
		}
	}
}
