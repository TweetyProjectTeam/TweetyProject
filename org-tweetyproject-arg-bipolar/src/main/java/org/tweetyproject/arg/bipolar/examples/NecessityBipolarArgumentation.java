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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.bipolar.examples;

import org.tweetyproject.arg.bipolar.reasoner.SimpleNecessityReasoner;
import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.bipolar.syntax.Support;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Examples for necessity argumentation frameworks.
 * 
 * @author Lars Bengel
 */
public class NecessityBipolarArgumentation {

    /**
     * Example
     * @param args args
     */
    public static void main(String[] args) {
        // Modified Example from Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
        BipolarArgumentationFramework nt = new BipolarArgumentationFramework();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        nt.add(a,b,c,d,e);

        nt.addAttack(b,a);
        nt.addAttack(e,a);
        nt.addAttack(c,d);
        nt.addSupport(a,c);
        nt.addSupport(b,b);

        System.out.println("BAF:");
        System.out.println(nt.prettyPrint());

        DungTheory dt = nt.getAssociatedTheory(Support.Type.NECESSITY);

        System.out.println("Associated AF:");
        System.out.println(dt.prettyPrint());

        // Computing the preferred extensions directly or via the associated abstract argumentation framework should yield the same result
        System.out.println("Preferred extensions of at: " + new SimpleNecessityReasoner(Semantics.PR).getModels(nt));
        System.out.println("Preferred extensions of dt: " + new SimplePreferredReasoner().getModels(dt));
    }

    /**
     * Default constructor for the {@code NecessityBipolarArgumentation} class.
     * Initializes an instance of this class, though it currently has no specific initialization logic.
     */
    public NecessityBipolarArgumentation() {}
}
