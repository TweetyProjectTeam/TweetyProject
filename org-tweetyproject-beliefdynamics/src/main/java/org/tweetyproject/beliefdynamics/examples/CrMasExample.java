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
package org.tweetyproject.beliefdynamics.examples;

import java.io.*;
import java.util.*;

import org.tweetyproject.agents.*;
import org.tweetyproject.beliefdynamics.*;
import org.tweetyproject.beliefdynamics.kernels.*;
import org.tweetyproject.beliefdynamics.mas.*;
import org.tweetyproject.beliefdynamics.operators.*;
import org.tweetyproject.commons.*;
import org.tweetyproject.graphs.orders.*;
import org.tweetyproject.logics.pl.parser.*;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.syntax.*;

/**
 * Example code on using belief operators in multi-agent settings.
 * @author Matthias Thimm
 *
 */
public class CrMasExample {

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
		CrMasBeliefSet<PlFormula,PlSignature> bs = new CrMasBeliefSet<PlFormula,PlSignature>(credOrder, new PlSignature());
		bs.add(new InformationObject<PlFormula>((PlFormula) parser.parseFormula("!c"), agents.get(1)));
		bs.add(new InformationObject<PlFormula>((PlFormula) parser.parseFormula("b"), agents.get(2)));
		bs.add(new InformationObject<PlFormula>((PlFormula) parser.parseFormula("!b||!a"), agents.get(2)));
		
		// some new information
		Collection<InformationObject<PlFormula>> newInformation = new HashSet<InformationObject<PlFormula>>();
		newInformation.add(new InformationObject<PlFormula>((PlFormula) parser.parseFormula("a"), agents.get(2)));
		newInformation.add(new InformationObject<PlFormula>((PlFormula) parser.parseFormula("!a||c"), agents.get(2)));		
		
		System.out.println(bs + " * " + newInformation);
		System.out.println();
				
		// simple prioritized revision (without considering credibilities)
		CrMasRevisionWrapper<PlFormula> rev = new CrMasRevisionWrapper<PlFormula>(
				new LeviMultipleBaseRevisionOperator<PlFormula>(
						new KernelContractionOperator<PlFormula>(new RandomIncisionFunction<PlFormula>(), new SimplePlReasoner()),
						new DefaultMultipleBaseExpansionOperator<PlFormula>()
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
