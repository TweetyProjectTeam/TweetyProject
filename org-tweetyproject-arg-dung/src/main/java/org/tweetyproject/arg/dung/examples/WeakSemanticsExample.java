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

import org.tweetyproject.arg.dung.reasoner.WeaklyAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyPreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * Example usage of the reasoners for weak semantics
 *
 * @author Lars Bengel
 */
public class WeakSemanticsExample {
	/**
	 * Execute the example
	 * @param args cmdline arguments
	 */
    public static void main(String[] args) {
        //initialize example
        DungTheory ex1 = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        ex1.add(a,b,c,d,e);
        ex1.addAttack(a, b);
        ex1.addAttack(b, a);
        ex1.addAttack(b, c);
        ex1.addAttack(c, d);
        ex1.addAttack(d, e);
        ex1.addAttack(e, c);

        // Compute extensions for all weak semantics
        System.out.println(ex1.prettyPrint());
        Collection<Extension<DungTheory>> exts_wad = new WeaklyAdmissibleReasoner().getModels(ex1);
        Collection<Extension<DungTheory>> exts_wco = new WeaklyCompleteReasoner().getModels(ex1);
        Collection<Extension<DungTheory>> exts_wgr = new WeaklyGroundedReasoner().getModels(ex1);
        Collection<Extension<DungTheory>> exts_wpr = new WeaklyPreferredReasoner().getModels(ex1);
        System.out.println("w-admissible Extensions: " + exts_wad);
        System.out.println("w-complete Extensions: " + exts_wco);
        System.out.println("w-grounded Extensions: " + exts_wgr);
        System.out.println("w-preferred Extensions: " + exts_wpr);
    }
}
