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
 package test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.el.ModalBeliefSet;
import net.sf.tweety.logics.el.NaiveModalReasoner;
import net.sf.tweety.logics.el.parser.ModalParser;
import net.sf.tweety.logics.el.writer.ModalWriter;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * Some examples for testing ModalParser, ModalWriter and NaiveModalReasoner
 * @author Anna Gessler
 */
public class ElTest {
	
	public static void main(String[] args) throws ParserException, IOException {
		//Parse BeliefBase
		ModalParser parser = new ModalParser();		
		ModalBeliefSet b = parser.parseBeliefBase("Animal = {duffy,martin} \n type(Flies(Animal)) \n (Flies(martin))");
		System.out.println(b);
		
		//NaiveModalReasoner
		NaiveModalReasoner reasoner = new NaiveModalReasoner(b);
		System.out.println(reasoner.query(parser.parseFormula("(Flies(duffy)) || (!(Flies(duffy)))")));
	
		//Write BeliefBase to file
		ModalWriter writer = new ModalWriter(b);
		writer = new ModalWriter(b);
		//writer.writeToFile("path");
	}

}
