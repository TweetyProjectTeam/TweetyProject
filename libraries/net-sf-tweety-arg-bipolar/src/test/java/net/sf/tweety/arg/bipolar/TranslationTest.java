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

/**
 * @author Lars Bengel
 *
 */
public class TranslationTest {
    static BArgument a = new BArgument("a");
    static BArgument b = new BArgument("b");
    static BArgument c = new BArgument("c");
    static BArgument d = new BArgument("d");
    static BArgument e = new BArgument("e");

    // Example from Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
    static NecessityArgumentationFramework polberg_example4;
    static EvidentialArgumentationFramework polberg_example4_1;
    static NecessityArgumentationFramework polberg_example4_2;

    @BeforeClass
    public static void setUpBeforeClass() {
        ArgumentSet aS1 = new ArgumentSet();
        aS1.add(b);
        aS1.add(d);

        BArgument eta = new EvidentialArgumentationFramework().getEta();

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

        polberg_example4_1 = new EvidentialArgumentationFramework();
        polberg_example4_1.add(a);
        polberg_example4_1.add(b);
        polberg_example4_1.add(c);
        polberg_example4_1.add(d);
        polberg_example4_1.add(e);

        polberg_example4_1.addAttack(b, a);
        polberg_example4_1.addAttack(e, a);
        polberg_example4_1.addAttack(c, d);

        polberg_example4_1.addSupport(a, c);
        polberg_example4_1.addSupport(b, b);
        polberg_example4_1.addSupport(b, e);
        polberg_example4_1.addSupport(d, e);

        polberg_example4_1.addPrimaFacie(a);
        polberg_example4_1.addPrimaFacie(d);

        polberg_example4_2 = new NecessityArgumentationFramework();
        polberg_example4_2.add(a);
        polberg_example4_2.add(b);
        polberg_example4_2.add(c);
        polberg_example4_2.add(d);
        polberg_example4_2.add(e);
        polberg_example4_2.add(eta);

        polberg_example4_2.addAttack(b, a);
        polberg_example4_2.addAttack(e, a);
        polberg_example4_2.addAttack(c, d);

        polberg_example4_2.addSupport(a, c);
        polberg_example4_2.addSupport(b, b);
        polberg_example4_2.addSupport(eta, a);
        polberg_example4_2.addSupport(eta, d);
        polberg_example4_2.addSupport(aS1, e);
    }

    @Test
    public void necessityToEvidential() {
        assertEquals(polberg_example4_1, polberg_example4.toEAF());
    }

    @Test
    public void evidentialToNecessity() {
        assertEquals(polberg_example4_2, polberg_example4_1.toNAF());
    }

    @Test
    public void necessityToEvidentialToNecessity() {
        assertEquals(polberg_example4_2, polberg_example4.toEAF().toNAF());
    }


}
