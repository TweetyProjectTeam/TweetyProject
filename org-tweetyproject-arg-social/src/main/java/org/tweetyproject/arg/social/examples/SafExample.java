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
 package org.tweetyproject.arg.social.examples;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.social.reasoner.IssReasoner;
import org.tweetyproject.arg.social.semantics.SimpleProductSemantics;
import org.tweetyproject.arg.social.syntax.SocialAbstractArgumentationFramework;

/**
 * Example code for using social abstract argumentation.
 * @author Matthias Thimm
 *
 */
public class SafExample {

	/**
     * Default constructor for creating an instance of SimulationParameters.
     * This constructor initializes a new instance of the Hashtable.
     */
    public SafExample() {
        super();
    }


	/**
	 * Demonstrates the use of a social abstract argumentation framework (SAF) and
	 * reasoning over it using social semantics.
	 *
	 *
	 * @param args command-line arguments (not used in this example).
	 */
	public static void main(String[] args){
		SocialAbstractArgumentationFramework saf = new SocialAbstractArgumentationFramework();
		Argument a = new Argument("A");
		Argument b = new Argument("B");
		Argument c = new Argument("C");
		Argument d = new Argument("D");
		saf.add(a);
		saf.add(b);
		saf.add(c);
		saf.add(d);
		saf.add(new Attack(a,b));
		saf.add(new Attack(b,c));
		saf.add(new Attack(c,b));
		saf.add(new Attack(c,d));

		saf.voteUp(a,3);
		saf.voteUp(b,2);
		saf.voteUp(c,2);
		saf.voteUp(d,2);
		saf.voteDown(a);
		saf.voteDown(c,5);
		saf.voteDown(d,1);

		System.out.println(saf);

		IssReasoner reasoner = new IssReasoner(new SimpleProductSemantics(0.01),0.001);

		System.out.println(reasoner.getModel(saf));
	}
}
