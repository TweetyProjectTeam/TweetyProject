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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.bipolar;

import static org.junit.Assert.*;

import org.tweetyproject.arg.bipolar.syntax.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Lars Bengel
 *
 */
public class BasicFunctionalityTest {
    static BArgument a = new BArgument("a");
    static BArgument b = new BArgument("b");
    static BArgument c = new BArgument("c");
    static BArgument x = new BArgument("x");

    // Example used by C. Cayrol in "Bipolarity in argumentation graphs: Towards a better understanding" (2013)
    static DeductiveArgumentationFramework cayrolExample7;
    static DeductiveArgumentationFramework cayrolExample7_1;
    static DeductiveArgumentationFramework cayrolExample7_2;
    static DeductiveArgumentationFramework cayrolExample7_3;

    @BeforeClass
    public static void setUpBeforeClass() {
        cayrolExample7_1 = new DeductiveArgumentationFramework();
        cayrolExample7_2 = new DeductiveArgumentationFramework();
        cayrolExample7_3 = new DeductiveArgumentationFramework();

        cayrolExample7_1.add(b);
        cayrolExample7_1.add(c);
        cayrolExample7_1.add(x);

        cayrolExample7_1.addAttack(x, c);

        cayrolExample7_1.addSupport(b, c);

        cayrolExample7_2.add(a);
        cayrolExample7_2.add(b);
        cayrolExample7_2.add(c);
        cayrolExample7_2.add(x);

        cayrolExample7_2.addSupport(a, x);
        cayrolExample7_2.addSupport(b, c);

        cayrolExample7_3.add(a);
        cayrolExample7_3.add(b);
        cayrolExample7_3.add(c);
        cayrolExample7_3.add(x);

        cayrolExample7_3.addAttack(x, c);

        cayrolExample7_3.addSupport(a, x);
    }

    @Before
    public void setUpBefore() {
        cayrolExample7 = new DeductiveArgumentationFramework();

        cayrolExample7.add(a);
        cayrolExample7.add(b);
        cayrolExample7.add(c);
        cayrolExample7.add(x);

        cayrolExample7.addAttack(x, c);

        cayrolExample7.addSupport(a, x);
        cayrolExample7.addSupport(b, c);

    }

    @Test
    public void removeArgument() {
        assertTrue(cayrolExample7.remove(a));
        assertFalse(cayrolExample7.remove(a));
        assertEquals(cayrolExample7_1, cayrolExample7);
    }

    @Test
    public void removeAttack() {
        Attack att = new BinaryAttack(x, c);
        assertTrue(cayrolExample7.remove(att));
        assertFalse(cayrolExample7.remove(att));
        assertEquals(cayrolExample7_2, cayrolExample7);
    }

    @Test
    public void removeSupport() {
        Support supp = new BinarySupport(b, c);
        assertTrue(cayrolExample7.remove(supp));
        assertFalse(cayrolExample7.remove(supp));
        assertEquals(cayrolExample7_3, cayrolExample7);
    }

}

