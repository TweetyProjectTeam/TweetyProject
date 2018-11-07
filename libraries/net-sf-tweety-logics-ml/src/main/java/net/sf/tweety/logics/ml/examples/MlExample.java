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
 package net.sf.tweety.logics.ml.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.ml.reasoner.SimpleModalReasoner;
import net.sf.tweety.logics.ml.syntax.ModalBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.ml.parser.ModalParser;

/**
 * Some examples for testing ModalParser and NaiveModalReasoner
 * @author Anna Gessler
 */
public class MlExample {
	
	public static void main(String[] args) throws ParserException, IOException {
		//Parse BeliefBase
		ModalParser parser = new ModalParser();		
		ModalBeliefSet b = parser.parseBeliefBase("Animal = {duffy,martin} \n type(Flies(Animal)) \n (Flies(martin))");
		System.out.println(b);
		
		//NaiveModalReasoner
		SimpleModalReasoner reasoner = new SimpleModalReasoner();
		System.out.println(reasoner.query(b,(FolFormula) parser.parseFormula("(Flies(duffy)) || (!(Flies(duffy)))")));
	}

}
