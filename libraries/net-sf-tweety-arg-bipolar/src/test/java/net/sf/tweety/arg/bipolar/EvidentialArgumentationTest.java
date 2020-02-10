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

package net.sf.tweety.arg.bipolar;

import static org.junit.Assert.*;

import net.sf.tweety.arg.bipolar.reasoner.evidential.*;
import net.sf.tweety.arg.bipolar.syntax.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Lars Bengel
 *
 */
public class EvidentialArgumentationTest {
    static BArgument a = new BArgument("a");
    static BArgument b = new BArgument("b");
    static BArgument c = new BArgument("c");
    static BArgument d = new BArgument("d");
    static BArgument e = new BArgument("e");
    static BArgument f = new BArgument("f");

    // Example from Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
    static EvidentialArgumentationFramework polberg_example3;
    static EvidentialArgumentationFramework polberg_example3_1;

    static Collection<ArgumentSet> stableExtensions = new HashSet<>();
    static Collection<ArgumentSet> groundedExtensions = new HashSet<>();
    static Collection<ArgumentSet> preferredExtensions = new HashSet<>();
    static Collection<ArgumentSet> completeExtensions = new HashSet<>();

    @BeforeClass
    public static void setUpBeforeClass() {
        polberg_example3_1 = new EvidentialArgumentationFramework();
        polberg_example3_1.add(a);
        polberg_example3_1.add(b);
        polberg_example3_1.add(c);
        polberg_example3_1.add(d);
        polberg_example3_1.add(e);
        polberg_example3_1.add(f);

        polberg_example3_1.addAttack(b, a);
        polberg_example3_1.addAttack(b, c);
        polberg_example3_1.addAttack(c, b);
        polberg_example3_1.addAttack(c, d);
        polberg_example3_1.addAttack(d, f);
        polberg_example3_1.addAttack(f, f);

        polberg_example3_1.addSupport(d, e);

        polberg_example3_1.addPrimaFacie(a);
        polberg_example3_1.addPrimaFacie(e);

        ArgumentSet set1 = new ArgumentSet();
        set1.add(b);
        set1.add(d);
        set1.add(e);
        set1.add(polberg_example3_1.getEta());

        ArgumentSet set2 = new ArgumentSet();
        set2.add(c);
        set2.add(polberg_example3_1.getEta());

        ArgumentSet set3 = new ArgumentSet();
        set3.add(polberg_example3_1.getEta());

        stableExtensions.add(set1);
        preferredExtensions.add(set1);
        preferredExtensions.add(set2);
        completeExtensions.add(set1);
        completeExtensions.add(set2);
        completeExtensions.add(set3);
        groundedExtensions.add(set3);
    }

    @Before
    public void setUpBefore() {
        polberg_example3 = new EvidentialArgumentationFramework();
        polberg_example3.add(a);
        polberg_example3.add(b);
        polberg_example3.add(c);
        polberg_example3.add(d);
        polberg_example3.add(e);
        polberg_example3.add(f);

        polberg_example3.addAttack(b, a);
        polberg_example3.addAttack(b, c);
        polberg_example3.addAttack(c, b);
        polberg_example3.addAttack(c, d);
        polberg_example3.addAttack(d, f);
        polberg_example3.addAttack(f, f);

        polberg_example3.addSupport(d, e);

        polberg_example3.addPrimaFacie(b);
        polberg_example3.addPrimaFacie(c);
        polberg_example3.addPrimaFacie(d);
        polberg_example3.addPrimaFacie(f);

    }

    @Test
    public void primaFacie() {
        polberg_example3.removePrimaFacie(b);
        polberg_example3.removePrimaFacie(c);
        polberg_example3.removePrimaFacie(d);
        polberg_example3.removePrimaFacie(f);
        polberg_example3.addPrimaFacie(a);
        polberg_example3.addPrimaFacie(e);
        assertEquals(polberg_example3, polberg_example3_1);
    }

    @Test
    public void StableReasoning() {
        Collection<ArgumentSet> extensions = new StableReasoner().getModels(polberg_example3);
        assertEquals(stableExtensions, extensions);
    }

    @Test
    public void CompleteReasoning() {
        Collection<ArgumentSet> extensions = new CompleteReasoner().getModels(polberg_example3);
        assertEquals(completeExtensions, extensions);
    }

    @Test
    public void GroundedReasoning() {
        Collection<ArgumentSet> extensions = new GroundedReasoner().getModels(polberg_example3);
        assertEquals(groundedExtensions, extensions);
    }

    @Test
    public void PreferredReasoning() {
        Collection<ArgumentSet> extensions = new PreferredReasoner().getModels(polberg_example3);
        assertEquals(preferredExtensions, extensions);
    }

}
