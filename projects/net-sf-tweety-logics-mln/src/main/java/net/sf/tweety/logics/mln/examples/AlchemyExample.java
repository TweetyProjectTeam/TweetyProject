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
package net.sf.tweety.logics.mln.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.mln.AlchemyMlnReasoner;
import net.sf.tweety.logics.mln.MarkovLogicNetwork;

/**
 * Example code illustrating the use of the Alchemy reasoner.
 * @author Matthias Thimm
 *
 */
public class AlchemyExample {
	public static void main(String[] args) throws ParserException, IOException{
		Pair<MarkovLogicNetwork,FolSignature> exp1 = MlnExample.SmokersExample(3);
		AlchemyMlnReasoner reasoner = new AlchemyMlnReasoner();
		FolParser parser = new FolParser();
		parser.setSignature(exp1.getSecond());
		FolFormula query = (FolFormula) parser.parseFormula("cancer(d0)");
		reasoner.setAlchemyInferenceCommand("/Users/mthimm/Projects/misc_bins/alchemy/infer");
		System.out.println(reasoner.query(exp1.getFirst(),query,exp1.getSecond()).getAnswerDouble());
	}
}
