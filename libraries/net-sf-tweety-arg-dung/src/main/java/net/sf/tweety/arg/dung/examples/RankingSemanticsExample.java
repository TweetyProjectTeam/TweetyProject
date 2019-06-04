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
package net.sf.tweety.arg.dung.examples;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.tweety.arg.dung.reasoner.CategorizerRankingReasoner;
import net.sf.tweety.arg.dung.semantics.ArgumentRanking;
import net.sf.tweety.arg.dung.semantics.NumericalArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.DungTheory;


/**
 * Example code for ranking semantics.
 * 
 * @author Anna Gessler
 */
public class RankingSemanticsExample {
	public static void main(String[] args){
		//Categorizer ranking semantics 
		CategorizerRankingReasoner reasoner = new CategorizerRankingReasoner();
		
		//Example 1, taken from [Bonzon, Delobelle, Konieczny, Maudet. A Comparative Study
		//of Ranking-Based Semantics for Abstract Argumentation]
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
		theory.add(new Attack(a,e));
		theory.add(new Attack(d,a));
		theory.add(new Attack(e,d));
		theory.add(new Attack(c,e));
		theory.add(new Attack(b,c));
		theory.add(new Attack(b,a));		
		
		System.out.println(roundRanking(reasoner.getModel(theory),2));
		
		//Example 2, taken from 
		//[Baumeister, Neugebauer, Rothe. Argumentation Meets Computational Social Choice. Tutorial. 2018]
		DungTheory theory2 = new DungTheory();
		Argument f = new Argument("f");
		theory2.add(a);
		theory2.add(b);
		theory2.add(c);
		theory2.add(d);
		theory2.add(e);
		theory2.add(f);
		theory2.add(new Attack(a,b));
		theory2.add(new Attack(b,c));
		theory2.add(new Attack(d,e));
		theory2.add(new Attack(e,d));
		theory2.add(new Attack(c,f));
		theory2.add(new Attack(e,c));
		System.out.println(roundRanking(reasoner.getModel(theory2),3));

	}
	
	/**
	 * Rounds a double value to n decimals.
	 * @param value
	 * @param n number of decimals
	 * @return value rounded to n decimals
	 */
	private static double round(double value, int n) {
	    if (n < 0) 
	    	throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(Double.toString(value));
	    bd = bd.setScale(n, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Rounds values in the given numerical argument ranking to 
	 * n decimals.
	 * @param ranking a NumericalArgumentRanking
	 * @param n decimals
	 * @return rounded NumericalArgumentRanking
	 */
	private static ArgumentRanking roundRanking(NumericalArgumentRanking ranking, int n) {
		Iterator<Entry<Argument, Double>> it = ranking.entrySet().iterator();
		NumericalArgumentRanking reval = new NumericalArgumentRanking();
		while (it.hasNext()) {
	        Map.Entry<Argument,Double> pair = (Map.Entry<Argument,Double>)it.next();
	        Argument a  = pair.getKey();
	        reval.put(a, round(pair.getValue(), n));
	        it.remove(); 
	    }
		return reval;
	}
}
