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
package net.sf.tweety.beliefdynamics.test;

import java.io.*;
import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.beliefdynamics.*;
import net.sf.tweety.beliefdynamics.kernels.*;
import net.sf.tweety.beliefdynamics.mas.*;
import net.sf.tweety.beliefdynamics.operators.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.graphs.orders.*;
import net.sf.tweety.logics.pl.ClassicalEntailment;
import net.sf.tweety.logics.pl.parser.*;
import net.sf.tweety.logics.pl.syntax.*;

public class CrMasTest {

	public static void main(String[] args) throws ParserException, IOException{		
		//TweetyLogging.logLevel = TweetyConfiguration.LogLevel.TRACE;
		//TweetyLogging.initLogging();
		PlParser parser = new PlParser();
		
		// some agents
		List<Agent> agents = new ArrayList<Agent>();
		agents.add(new DummyAgent("A1"));
		agents.add(new DummyAgent("A2"));
		agents.add(new DummyAgent("A3"));
		
		// some credibility order A3 < A2 < A1 (A1 is most credible)
		Order<Agent> credOrder = new Order<Agent>(agents);
		credOrder.setOrderedBefore(agents.get(0), agents.get(1));
		credOrder.setOrderedBefore(agents.get(1), agents.get(2));
		
		// a belief base (we use propositional logic)
		CrMasBeliefSet<PropositionalFormula> bs = new CrMasBeliefSet<PropositionalFormula>(credOrder);
		bs.add(new InformationObject<PropositionalFormula>((PropositionalFormula) parser.parseFormula("!c"), agents.get(1)));
		bs.add(new InformationObject<PropositionalFormula>((PropositionalFormula) parser.parseFormula("b"), agents.get(2)));
		bs.add(new InformationObject<PropositionalFormula>((PropositionalFormula) parser.parseFormula("!b||!a"), agents.get(2)));
		
		// some new information
		Collection<InformationObject<PropositionalFormula>> newInformation = new HashSet<InformationObject<PropositionalFormula>>();
		newInformation.add(new InformationObject<PropositionalFormula>((PropositionalFormula) parser.parseFormula("a"), agents.get(2)));
		newInformation.add(new InformationObject<PropositionalFormula>((PropositionalFormula) parser.parseFormula("!a||c"), agents.get(2)));		
		
		System.out.println(bs + " * " + newInformation);
		System.out.println();
				
		// simple prioritized revision (without considering credibilities)
		CrMasRevisionWrapper<PropositionalFormula> rev = new CrMasRevisionWrapper<PropositionalFormula>(
				new LeviMultipleBaseRevisionOperator<PropositionalFormula>(
						new KernelContractionOperator<PropositionalFormula>(new RandomIncisionFunction<PropositionalFormula>(), new ClassicalEntailment()),
						new DefaultMultipleBaseExpansionOperator<PropositionalFormula>()
						));
		System.out.println("PRIO       :\t " + rev.revise(bs, newInformation));
		
		// simple non-prioritized revision (with credibilities)
		CrMasSimpleRevisionOperator rev2 = new CrMasSimpleRevisionOperator();
		System.out.println("N-PRIO CRED:\t " + rev2.revise(bs, newInformation));
		
		// credibility-based argumentative revision
		CrMasArgumentativeRevisionOperator theRevision = new CrMasArgumentativeRevisionOperator();		
		System.out.println("ARG        :\t " + theRevision.revise(bs, newInformation));
		
	}
}
