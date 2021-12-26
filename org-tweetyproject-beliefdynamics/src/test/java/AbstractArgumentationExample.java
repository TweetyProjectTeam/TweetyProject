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


import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.tweetyproject.agents.Agent;
import org.tweetyproject.agents.DummyAgent;
import org.tweetyproject.arg.dung.reasoner.SatCompleteReasoner;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.beliefdynamics.DefaultMultipleBaseExpansionOperator;
import org.tweetyproject.beliefdynamics.LeviMultipleBaseRevisionOperator;
import org.tweetyproject.beliefdynamics.kernels.KernelContractionOperator;
import org.tweetyproject.beliefdynamics.kernels.RandomIncisionFunction;
import org.tweetyproject.beliefdynamics.mas.CrMasBeliefSet;
import org.tweetyproject.beliefdynamics.mas.CrMasRevisionWrapper;
import org.tweetyproject.beliefdynamics.mas.InformationObject;
import org.tweetyproject.beliefdynamics.operators.CrMasArgumentativeRevisionOperator;
import org.tweetyproject.beliefdynamics.operators.CrMasSimpleRevisionOperator;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.graphs.orders.Order;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * Example code for applying belief dynamics on abstract argumentation frameworks.
 * 
 * @author Matthias Thimm
 *
 */
public class AbstractArgumentationExample {
	@Test
	public void abstractArgEx(){
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");		
		theory.add(a);
		theory.add(b);
		theory.add(c);		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,c));
		theory.add(new Attack(c,b));
		theory.add(new Attack(c,a));
		
		SatCompleteReasoner reasoner = new SatCompleteReasoner(SatSolver.getDefaultSolver());
		

				
		PlBeliefSet beliefSet = reasoner.getPropositionalCharacterisation(theory); 
		System.out.println(beliefSet);
		System.out.println();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds((PlSignature)beliefSet.getMinimalSignature())){
			if(w.satisfies(beliefSet))
				assertTrue(w.toString().equals("[undec_b, undec_c, undec_a]") || 
						w.toString().equals("[out_b, out_a, in_c]"));
		}


	
				
	}
	
	@Test
	public void crmasTest() throws ParserException, IOException{		
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
		
		assertTrue(rev.revise(bs, newInformation).toString().equals("[A3:a, A2:!c, A3:!a||c, A3:!b||!a]"));
		
		// simple non-prioritized revision (with credibilities)
		CrMasSimpleRevisionOperator rev2 = new CrMasSimpleRevisionOperator();
		assertTrue(rev2.revise(bs, newInformation).toString().equals("[A3:a, A2:!c, A3:!a||c, A3:!b||!a]"));
		
		// credibility-based argumentative revision
		CrMasArgumentativeRevisionOperator theRevision = new CrMasArgumentativeRevisionOperator();		
		System.out.println(theRevision.revise(bs, newInformation).toString());
		assertTrue(theRevision.revise(bs, newInformation).toString().equals("[A2:!c, A3:!b||!a, A3:!a||c, A3:b]"));
		
	}
}
