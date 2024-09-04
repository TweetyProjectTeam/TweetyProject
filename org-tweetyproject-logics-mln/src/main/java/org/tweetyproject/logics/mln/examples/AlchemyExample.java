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
package org.tweetyproject.logics.mln.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.mln.reasoner.AlchemyMlnReasoner;
import org.tweetyproject.logics.mln.syntax.MarkovLogicNetwork;

/**
 * Example code illustrating the use of the Alchemy reasoner.
 * @author Matthias Thimm
 *
 */
public class AlchemyExample {


	/**
	 * Default constructor for {@code AlchemyExample}.
	 * <p>
	 * This constructor initializes the example for running the Alchemy reasoner
	 * with a predefined MLN setup.
	 * </p>
	 */
	public AlchemyExample() {
		// Default constructor, no specific initialization required
	}

	/**
	 * The main method to run the Alchemy reasoner example.
	 * <p>
	 * This method sets up an MLN with smokers and cancer, parses a query, and
	 * invokes the Alchemy reasoner to compute the result. It prints the result
	 * of the query to the console.
	 * </p>
	 *
	 * @param args command-line arguments (not used in this example)
	 * @throws ParserException if there is an error during parsing
	 * @throws IOException if there is an I/O error
	 */
	public static void main(String[] args) throws ParserException, IOException{
		Pair<MarkovLogicNetwork,FolSignature> exp1 = MlnExample.SmokersExample(3);
		AlchemyMlnReasoner reasoner = new AlchemyMlnReasoner();
		FolParser parser = new FolParser();
		parser.setSignature(exp1.getSecond());
		FolFormula query = (FolFormula) parser.parseFormula("cancer(d0)");
		reasoner.setAlchemyInferenceCommand("/Users/mthimm/Projects/misc_bins/alchemy/infer");
		System.out.println(reasoner.query(exp1.getFirst(),query,exp1.getSecond()));
	}
}
