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

package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.learning.NiskanenTheorySynthesizer;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * *description missing*
 * @author Matthias Thimm
 *
 */
public class NiskanenExample {
	/**
	 * 
	 * @param args arguments
	 * @throws IOException Exception
	 * @throws NoSuchMethodException Exception
	 * @throws InvocationTargetException Exception
	 * @throws IllegalAccessException Exception
	 */
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // create graph to construct examples
        DungTheory dt = new DungTheory();
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        dt.add(b);
        dt.add(c);
        dt.add(d);
        dt.addAttack(b, c);
        dt.addAttack(c, d);
        dt.addAttack(d, c);

        // create positive examples
        Collection<Extension<DungTheory>> extensions = new SimpleStableReasoner().getModels(dt);
        Map<Extension<DungTheory>, Integer> posExamples = new HashMap<>();
        for (Extension<DungTheory> ext: extensions) {
            posExamples.put(ext, 2);
        }

        //create negative example
        Map<Extension<DungTheory>, Integer> negExamples = new HashMap<>();
        Extension<DungTheory> ext = new Extension<DungTheory>();
        ext.add(b);
        ext.add(c);
        negExamples.put(ext, 4);

        String solverLocation = "/home/lars/Nextcloud/local/open-wbo-master/open-wbo_static";
        NiskanenTheorySynthesizer nts = new NiskanenTheorySynthesizer(dt, Semantics.ST,solverLocation);

        DungTheory result = nts.learnExamples(posExamples, negExamples);
        System.out.println(result.prettyPrint());
    }
}

