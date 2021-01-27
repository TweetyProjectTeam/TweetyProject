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
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.*;
import org.tweetyproject.logics.mln.analysis.*;
import org.tweetyproject.logics.mln.reasoner.SimpleMlnReasoner;
import org.tweetyproject.logics.mln.syntax.MarkovLogicNetwork;
import org.tweetyproject.logics.mln.syntax.MlnFormula;
import org.tweetyproject.math.func.MaxAggregator;
import org.tweetyproject.math.norm.AggregatingNorm;
import org.tweetyproject.math.probability.Probability;

/**
 * Gives further examples on how MLNs can be constructed programmatically and how coherence measures can be used.
 * @author Matthias Thimm
 */
public class MlnExample2 {

	public static Pair<MarkovLogicNetwork,FolSignature> Nixon1() throws ParserException, IOException{
		Predicate quaker = new Predicate("quaker",1);
		Predicate republican = new Predicate("republican",1);
		Predicate pacifist = new Predicate("pacifist",1);
		
		FolSignature sig = new FolSignature();
		sig.add(quaker);
		sig.add(republican);
		sig.add(pacifist);
		
		sig.add(new Constant("d1"));
		
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		MarkovLogicNetwork mln = new MarkovLogicNetwork();
				
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!quaker(X)|| pacifist(X)"), new Probability(0.95)));		
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!republican(X) || !pacifist(X)"), new Probability(0.95))); 

		return new Pair<MarkovLogicNetwork,FolSignature>(mln,sig);
	}

	public static Pair<MarkovLogicNetwork,FolSignature> Nixon2() throws ParserException, IOException{
		Predicate quaker = new Predicate("quaker",1);
		Predicate republican = new Predicate("republican",1);
		Predicate president = new Predicate("president",1);
		
		FolSignature sig = new FolSignature();
		sig.add(quaker);
		sig.add(republican);
		sig.add(president);
		
		sig.add(new Constant("nixon"));
		
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		MarkovLogicNetwork mln = new MarkovLogicNetwork();
		
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("quaker(nixon) && republican(nixon) && president(nixon)"))); // p = 1
		 

		return new Pair<MarkovLogicNetwork,FolSignature>(mln,sig);
	}
	
	public static Pair<MarkovLogicNetwork,FolSignature> Nixon3() throws ParserException, IOException{
		Predicate actor = new Predicate("actor",1);
		Predicate president = new Predicate("president",1);
		
		FolSignature sig = new FolSignature();
		sig.add(actor);
		sig.add(president);
		
		sig.add(new Constant("reagan"));
		
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		MarkovLogicNetwork mln = new MarkovLogicNetwork();
		
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!president(X) || actor(X)"), new Probability(0.9)));		
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("president(reagan) && actor(reagan)"))); // p = 1
		 

		return new Pair<MarkovLogicNetwork,FolSignature>(mln,sig);
	}
	
	public static void main(String[] args) throws ParserException, IOException{
		AggregatingCoherenceMeasure measure = new AggregatingCoherenceMeasure(new AggregatingNorm(new MaxAggregator()),new MaxAggregator());
		Pair<MarkovLogicNetwork,FolSignature> ex1 = Nixon1();
		Pair<MarkovLogicNetwork,FolSignature> ex2 = Nixon2();
		Pair<MarkovLogicNetwork,FolSignature> ex3 = Nixon3();
		MarkovLogicNetwork mln1 = ex1.getFirst();
		FolSignature sig1 = ex1.getSecond();
		MarkovLogicNetwork mln2 = ex2.getFirst();
		FolSignature sig2 = ex2.getSecond();
		MarkovLogicNetwork mln3 = ex3.getFirst();
		FolSignature sig3 = ex3.getSecond();
		SimpleMlnReasoner reasoner = new SimpleMlnReasoner();
		reasoner.setTempDirectory("/Users/mthimm/Desktop/tmp");
		System.out.println("#1: Measure " + measure.toString() + ", coherence value " + measure.coherence(mln1, reasoner, sig1));
		System.out.println("#2: Measure " + measure.toString() + ", coherence value " + measure.coherence(mln2, reasoner, sig2));
		System.out.println("#3: Measure " + measure.toString() + ", coherence value " + measure.coherence(mln3, reasoner, sig3));
		System.out.println();
		
		MarkovLogicNetwork mergedMln = new MarkovLogicNetwork();
		mergedMln.addAll(mln1);
		mergedMln.addAll(mln2);
		//mergedMln.addAll(mln3);
		FolSignature mergedSig = new FolSignature();
		mergedSig.addSignature(sig1);
		mergedSig.addSignature(sig2);
		//mergedSig.addSignature(sig3);
		System.out.println("Merged: Measure " + measure.toString() + ", coherence value " + measure.coherence(mergedMln, reasoner, mergedSig));
	}
}
