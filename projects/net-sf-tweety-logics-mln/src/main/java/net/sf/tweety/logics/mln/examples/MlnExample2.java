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
package net.sf.tweety.logics.mln.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.mln.*;
import net.sf.tweety.logics.mln.analysis.*;
import net.sf.tweety.logics.mln.syntax.MlnFormula;
import net.sf.tweety.math.func.MaxAggregator;
import net.sf.tweety.math.norm.AggregatingNorm;
import net.sf.tweety.math.probability.Probability;

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
		NaiveMlnReasoner reasoner = new NaiveMlnReasoner();
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
