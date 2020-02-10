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

import net.sf.tweety.arg.bipolar.syntax.*;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import org.junit.BeforeClass;
import org.junit.Test;

public class DeductiveArgumentationTest {
    static BArgument a = new BArgument("a");
    static BArgument b = new BArgument("b");
    static BArgument c = new BArgument("c");
    static BArgument x = new BArgument("x");

    // Example used by C. Cayrol in "Bipolarity in argumentation graphs: Towards a better understanding" (2013)
    static DeductiveArgumentationFramework cayrolExample7;
    static DungTheory cayrolExample7_1;

    @BeforeClass
    public static void setUpBeforeClass() {
        cayrolExample7 = new DeductiveArgumentationFramework();
        cayrolExample7_1 = new DungTheory();

        cayrolExample7.add(a);
        cayrolExample7.add(b);
        cayrolExample7.add(c);
        cayrolExample7.add(x);

        cayrolExample7.addAttack(x, c);

        cayrolExample7.addSupport(a, x);
        cayrolExample7.addSupport(b, c);


        cayrolExample7_1.add(a);
        cayrolExample7_1.add(b);
        cayrolExample7_1.add(c);
        cayrolExample7_1.add(x);

        cayrolExample7_1.addAttack(a, b);
        cayrolExample7_1.addAttack(a, c);
        cayrolExample7_1.addAttack(x, b);
        cayrolExample7_1.addAttack(x, c);
    }

    @Test
    public void toDungTheory() {
        assertEquals(cayrolExample7_1, cayrolExample7.getCompleteAssociatedDungTheory());
    }
}
