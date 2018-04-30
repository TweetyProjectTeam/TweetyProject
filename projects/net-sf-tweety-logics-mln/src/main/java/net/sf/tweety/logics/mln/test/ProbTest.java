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
package net.sf.tweety.logics.mln.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.mln.MarkovLogicNetwork;
import net.sf.tweety.logics.mln.NaiveMlnReasoner;
import net.sf.tweety.logics.mln.syntax.MlnFormula;

public class ProbTest {

	public static void main(String[] args) throws ParserException, IOException{
		Predicate a = new Predicate("a",1);
		Predicate b = new Predicate("b",1);
		Predicate c = new Predicate("c",1);
		Predicate d = new Predicate("d",1);
		
		FolSignature sig = new FolSignature();
		sig.add(a);
		sig.add(b);
		sig.add(c);
		sig.add(d);
		
		sig.add(new Constant("d1"));
		
		FolParser parser = new FolParser();
		parser.setSignature(sig);
		
		MarkovLogicNetwork mln = new MarkovLogicNetwork();
		
		FolFormula f = (FolFormula)parser.parseFormula("a(X) && b(X) || c(X) && d(X)");
		double p = 0.1;		
		
		double factor = f.getSatisfactionRatio(); 
		
		double w = Math.log(p/(1-p)*factor);
		
		mln.add(new MlnFormula(f, w));
		
		NaiveMlnReasoner reasoner = new NaiveMlnReasoner();
		reasoner.setTempDirectory("/Users/mthimm/Desktop/tmp");
		
		System.out.println(w + "\t\t" + reasoner.query(mln,(FolFormula)parser.parseFormula("a(d1) && b(d1) || c(d1) && d(d1)"),sig).getAnswerDouble());
		

	}
}
