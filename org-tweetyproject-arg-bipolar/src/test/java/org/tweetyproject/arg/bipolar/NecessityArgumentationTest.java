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

package org.tweetyproject.arg.bipolar;

import static org.junit.Assert.*;

import org.tweetyproject.arg.bipolar.reasoner.necessity.*;
import org.tweetyproject.arg.bipolar.syntax.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Lars Bengel
 *
 */
public class NecessityArgumentationTest {
    static BArgument a = new BArgument("a");
    static BArgument b = new BArgument("b");
    static BArgument c = new BArgument("c");
    static BArgument d = new BArgument("d");
    static BArgument e = new BArgument("e");

    // Example from Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
    static NecessityArgumentationFramework polberg_example4;

    static Collection<ArgumentSet> stableExtensions = new HashSet<>();
    static Collection<ArgumentSet> groundedExtensions = new HashSet<>();
    static Collection<ArgumentSet> preferredExtensions = new HashSet<>();
    static Collection<ArgumentSet> completeExtensions = new HashSet<>();
    static Collection<ArgumentSet> admissibleExtensions = new HashSet<>();

    @BeforeClass
    public static void setUpBeforeClass() {
        ArgumentSet aS1 = new ArgumentSet();
        aS1.add(b);
        aS1.add(d);

        polberg_example4 = new NecessityArgumentationFramework();
        polberg_example4.add(a);
        polberg_example4.add(b);
        polberg_example4.add(c);
        polberg_example4.add(d);
        polberg_example4.add(e);

        polberg_example4.addAttack(b, a);
        polberg_example4.addAttack(e, a);
        polberg_example4.addAttack(c, d);

        polberg_example4.addSupport(a, c);
        polberg_example4.addSupport(b, b);
        polberg_example4.addSupport(aS1, e);

        ArgumentSet set1 = new ArgumentSet();
        set1.add(a);
        set1.add(c);

        ArgumentSet set2 = new ArgumentSet();
        set2.add(d);
        set2.add(e);

        stableExtensions.add(set1);
        stableExtensions.add(set2);
        preferredExtensions.add(set1);
        preferredExtensions.add(set2);
        completeExtensions.add(set1);
        completeExtensions.add(set2);
        completeExtensions.add(new ArgumentSet());
        groundedExtensions.add(new ArgumentSet());
        admissibleExtensions.add(set1);
        admissibleExtensions.add(set2);
        admissibleExtensions.add(new ArgumentSet());
    }

    @Test
    public void StableReasoning() {
        Collection<ArgumentSet> extensions = new StableReasoner().getModels(polberg_example4);
        assertEquals(stableExtensions, extensions);
    }

    @Test
    public void CompleteReasoning() {
        Collection<ArgumentSet> extensions = new CompleteReasoner().getModels(polberg_example4);
        assertEquals(completeExtensions, extensions);
    }

    @Test
    public void AdmissibleReasoning() {
        Collection<ArgumentSet> extensions = new AdmissibleReasoner().getModels(polberg_example4);
        assertEquals(admissibleExtensions, extensions);
    }

    @Test
    public void GroundedReasoning() {
        Collection<ArgumentSet> extensions = new GroundedReasoner().getModels(polberg_example4);
        assertEquals(groundedExtensions, extensions);
    }

    @Test
    public void PreferredReasoning() {
        Collection<ArgumentSet> extensions = new PreferredReasoner().getModels(polberg_example4);
        assertEquals(preferredExtensions, extensions);
    }
}
