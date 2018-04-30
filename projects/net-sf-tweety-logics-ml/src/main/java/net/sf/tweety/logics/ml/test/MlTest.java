/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
 package net.sf.tweety.logics.ml.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.ml.ModalBeliefSet;
import net.sf.tweety.logics.ml.reasoner.NaiveModalReasoner;
import net.sf.tweety.logics.ml.parser.ModalParser;
import net.sf.tweety.logics.ml.writer.ModalWriter;

/**
 * Some examples for testing ModalParser, ModalWriter and NaiveModalReasoner
 * @author Anna Gessler
 */
public class MlTest {
	
	public static void main(String[] args) throws ParserException, IOException {
		//Parse BeliefBase
		ModalParser parser = new ModalParser();		
		ModalBeliefSet b = parser.parseBeliefBase("Animal = {duffy,martin} \n type(Flies(Animal)) \n (Flies(martin))");
		System.out.println(b);
		
		//NaiveModalReasoner
		NaiveModalReasoner reasoner = new NaiveModalReasoner();
		System.out.println(reasoner.query(b,parser.parseFormula("(Flies(duffy)) || (!(Flies(duffy)))")));
	
		//Write BeliefBase to file
		ModalWriter writer = new ModalWriter(b);
		writer = new ModalWriter(b);
		//writer.writeToFile("path");
	}

}
