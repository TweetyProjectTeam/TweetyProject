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
package net.sf.tweety.arg.deductive.examples;

import java.io.IOException;

import net.sf.tweety.arg.deductive.DeductiveKnowledgeBase;
import net.sf.tweety.arg.deductive.SimpleReasoner;
import net.sf.tweety.arg.deductive.accumulator.SimpleAccumulator;
import net.sf.tweety.arg.deductive.categorizer.ClassicalCategorizer;
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * 
 * For testing purposes.
 * 
 * @author Matthias Thimm
 *
 */
public class DeductiveExample {

	public static void main(String[] args) throws ParserException, IOException{
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.TRACE;
		TweetyLogging.initLogging();
		SatSolver.setDefaultSolver(new Sat4jSolver());
		DeductiveKnowledgeBase kb = new DeductiveKnowledgeBase();
		
		PlParser parser = new PlParser();
		kb.add((PropositionalFormula)parser.parseFormula("s"));
		kb.add((PropositionalFormula)parser.parseFormula("!s || h"));
		kb.add((PropositionalFormula)parser.parseFormula("f"));		
		kb.add((PropositionalFormula)parser.parseFormula("!f || !h"));
		kb.add((PropositionalFormula)parser.parseFormula("v"));
		kb.add((PropositionalFormula)parser.parseFormula("!v || !h"));
		
		System.out.println(kb);
		
		BeliefBaseReasoner<DeductiveKnowledgeBase> reasoner = new SimpleReasoner(new ClassicalCategorizer(), new SimpleAccumulator());
		
		System.out.println(reasoner.query(kb,parser.parseFormula("h")).getAnswerDouble());
		
	}
	
}
