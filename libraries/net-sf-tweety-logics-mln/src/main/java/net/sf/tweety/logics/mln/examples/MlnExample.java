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
package net.sf.tweety.logics.mln.examples;

import java.io.IOException;
import java.util.*;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.mln.analysis.*;
import net.sf.tweety.logics.mln.reasoner.SimpleMlnReasoner;
import net.sf.tweety.logics.mln.syntax.MarkovLogicNetwork;
import net.sf.tweety.logics.mln.syntax.MlnFormula;
import net.sf.tweety.math.func.AverageAggregator;
import net.sf.tweety.math.func.MaxAggregator;
import net.sf.tweety.math.func.MinAggregator;
import net.sf.tweety.math.norm.AggregatingNorm;
import net.sf.tweety.math.norm.PNorm;

/**
 * Shows how MLNs can be constructed programmatically and shows how coherence measures can be used.
 * @author Matthias Thimm
 *
 */
public class MlnExample {
	
	public static Pair<MarkovLogicNetwork,FolSignature> SmokersExample(int domain_size) throws ParserException, IOException{
		// see [Richardson:2006]
		Predicate friends = new Predicate("friends",2);
		Predicate smoker = new Predicate("smokes",1);
		Predicate cancer = new Predicate("cancer",1);
		
		FolSignature sig = new FolSignature();
		sig.add(friends);
		sig.add(smoker);
		sig.add(cancer);
		
		for(int i = 0; i< domain_size; i++)
			sig.add(new Constant("d"+i));
		
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		MarkovLogicNetwork mln = new MarkovLogicNetwork();
		
		//friends of friends are friends
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!friends(X,Y) || !friends(Y,Z) || friends(X,Z)"), ((double)0.7)));
		//friendless people smoke
		//mln.add(new MlnFormula((FolFormula)parser.parseFormula("(exists Y: (friends(X,Y))) || smokes(X)"), ((double)2.3)));
		//smoking causes cancer
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!smokes(X) ||  cancer(X)"), ((double)1.5)));
		//smoking behavior of friends is the same
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!friends(X,Y) || ((smokes(X) && smokes(Y))||(!smokes(X) && !smokes(Y)))"), ((double)1.1)));
		// friends relationship is symmetric (strict formula)
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("(friends(X,Y) && friends(Y,X))||(!friends(X,Y) && !friends(Y,X))")));
		
		return new Pair<MarkovLogicNetwork,FolSignature>(mln,sig);
	}
	
	public static Pair<MarkovLogicNetwork,FolSignature> ElephantZooExample(int domain_size) throws ParserException, IOException{
		// see [Finthammer:2012]
		Predicate likes = new Predicate("likes",2);
		Predicate elephant = new Predicate("elephant",1);
		Predicate keeper = new Predicate("keeper",1);
		
		Constant fred = new Constant("fred");
		Constant clyde = new Constant("clyde");
		
		FolSignature sig = new FolSignature();
		sig.add(likes);
		sig.add(elephant);
		sig.add(keeper);
		sig.add(fred);
		sig.add(clyde);
		
		for(int i = 0; i< domain_size; i++)
			sig.add(new Constant("d"+i));
		
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		MarkovLogicNetwork mln = new MarkovLogicNetwork();
		
		// Clyde is an elephant, Fred is a keeper (strict formulas)
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("elephant(clyde)"))); //p=1
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("keeper(fred)"))); //p=1
		// elephants are not keepers (strict formula)
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("(!keeper(X) && elephant(X))||(keeper(X) && !elephant(X))"))); //p=1		
		//elephants like keepers
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!elephant(X) || !keeper(Y) || likes(X,Y)"), ((double)2.1972))); //p=0.9
		// elephants do not like Fred
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!elephant(X) || likes(X,fred)"), ((double)-0.8573))); //p=0.3
		// Clyde likes Fred
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("likes(clyde,fred)"))); //p=1
				
		return new Pair<MarkovLogicNetwork,FolSignature>(mln,sig);
	}
	
	public static Pair<MarkovLogicNetwork,FolSignature> CommonColdExample(int domain_size) throws ParserException, IOException{
		// see [Fisseler:2008]
		Predicate contact = new Predicate("contact",2);
		Predicate cold = new Predicate("cold",1);
		Predicate susceptible = new Predicate("susceptible",1);
		
		FolSignature sig = new FolSignature();
		sig.add(contact);
		sig.add(cold);
		sig.add(susceptible);
		
		for(int i = 0; i< domain_size; i++)
			sig.add(new Constant("d"+i));
		
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		MarkovLogicNetwork mln = new MarkovLogicNetwork();
		
		// general probability of infection
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("cold(X)"), ((double)-4.5951198501))); //p=0.01
		// infection if susceptible
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!susceptible(X) || cold(X)"), ((double)-2.1972245773))); //p=0.1
		// infection by contact
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!contact(X,Y) || !cold(Y) || cold(X)"), ((double)0.4054651081))); //p=0.6
		// contact relationship is symmetric
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("!contact(X,Y) || contact(Y,X)")));
		
		return new Pair<MarkovLogicNetwork,FolSignature>(mln,sig);
	}
	
	public static Pair<MarkovLogicNetwork,FolSignature> SimpleExample(int domain_size) throws ParserException, IOException{
		Predicate a = new Predicate("A",1);
		Constant c1 = new Constant("c1");
		
		FolSignature sig = new FolSignature();
		sig.add(a);
		sig.add(c1);
		for(int i = 0; i< domain_size; i++)
			sig.add(new Constant("d"+i));
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		MarkovLogicNetwork mln = new MarkovLogicNetwork();
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("A(X)"), ((double)2)));
		mln.add(new MlnFormula((FolFormula)parser.parseFormula("A(c1)"), ((double)-5)));
		
		return new Pair<MarkovLogicNetwork,FolSignature>(mln,sig);
	}
	
	public static Pair<MarkovLogicNetwork,FolSignature> iterateExamples(int exNum, int domain_size) throws ParserException, IOException{
		if(exNum == 0)
			return SimpleExample(domain_size);
		if(exNum == 1)
			return SmokersExample(domain_size);
		if(exNum == 2)
			return CommonColdExample(domain_size);
		return ElephantZooExample(domain_size);		
	}
	
	public static void main(String[] args) throws ParserException, IOException{
		String param = "/Users/mthimm/Desktop/tmp";//"/home/share/mln/tmp";//"/home/share/mln/infer";//"/Users/mthimm/Desktop/infer"
					
		List<AggregatingCoherenceMeasure> cohMeasures = new ArrayList<AggregatingCoherenceMeasure>();
		cohMeasures.add(new AggregatingCoherenceMeasure(new PNorm(2),new MaxAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new PNorm(2),new MinAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new AggregatingNorm(new AverageAggregator()),new MaxAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new AggregatingNorm(new MaxAggregator()),new AverageAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new AggregatingNorm(new MinAggregator()),new MinAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new AggregatingNorm(new MinAggregator()),new MaxAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new AggregatingNorm(new MaxAggregator()),new MinAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new AggregatingNorm(new MaxAggregator()),new MaxAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new AggregatingNorm(new AverageAggregator()),new MinAggregator()));
		cohMeasures.add(new AggregatingCoherenceMeasure(new AggregatingNorm(new MaxAggregator()),new MaxAggregator()));
		
		for(int i = 0; i < 1; i++){
			for(int dsize = 3; dsize < 14; dsize++){
				Pair<MarkovLogicNetwork,FolSignature> ex = MlnExample.iterateExamples(i, dsize);
				MarkovLogicNetwork mln = ex.getFirst();
				FolSignature sig = ex.getSecond();
				//AlchemyMlnReasoner reasoner = new AlchemyMlnReasoner(mln,sig);
				//reasoner.setAlchemyInferenceCommand(param);
				SimpleMlnReasoner reasoner = new SimpleMlnReasoner();
				reasoner.setTempDirectory(param);
				for(AggregatingCoherenceMeasure measure: cohMeasures){				
						System.out.println("Example " + i + ", domain size " + dsize + ", measure " + measure.toString() + ", coherence value " + measure.coherence(mln, reasoner, sig));
				}
			}
		}
	}	
}
