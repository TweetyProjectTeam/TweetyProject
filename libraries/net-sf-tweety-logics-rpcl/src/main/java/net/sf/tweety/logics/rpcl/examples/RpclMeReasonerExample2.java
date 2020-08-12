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
package net.sf.tweety.logics.rpcl.examples;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.rpcl.parser.RpclParser;
import net.sf.tweety.logics.rpcl.reasoner.RpclMeReasoner;
import net.sf.tweety.logics.rpcl.semantics.AggregatingSemantics;
import net.sf.tweety.logics.rpcl.syntax.RpclBeliefSet;

/**
 *  Example code illustrating relational probabilistic conditional logic and reasoning with it.
 */
public class RpclMeReasonerExample2 {

	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		RpclParser parser = new RpclParser();
		RpclBeliefSet bs = parser.parseBeliefBaseFromFile(RpclMeReasonerExample2.class.getResource("/cold.rpcl").getFile());
		System.out.println(bs);
		FolParser folParser = new FolParser();
		folParser.setSignature((FolSignature)bs.getMinimalSignature());
		FolFormula query = (FolFormula)folParser.parseFormula("cold(anna)");
		RpclMeReasoner reasoner = new RpclMeReasoner(new AggregatingSemantics(), RpclMeReasoner.STANDARD_INFERENCE);
		System.out.println(reasoner.query(bs,query));		
	}
}
