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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;


import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * 
 * @author Sebastian Franke
 *
 */

public class ResolutionbasedSolverTest {

	public static void main(String[] args) {
        DungTheory ex1 = new DungTheory();


        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");

        ex1.add(a);
        ex1.add(b);
        ex1.add(c);
        ex1.add(d);
        ex1.addAttack(a, b);
        ex1.addAttack(b, a);
        ex1.addAttack(c, d);
        ex1.addAttack(d, c);
        ex1.addAttack(a, c);


       // SimpleResolutionBasedReasoner re= new SimpleResolutionBasedReasoner(new SimplePreferredReasoner());
//SimpleAdmissibleReasoner ad = new SimpleAdmissibleReasoner();
        //System.out.println(re.getModels(ex1));
       // System.out.println(ad.getModels(ex1));
        


	}
}
