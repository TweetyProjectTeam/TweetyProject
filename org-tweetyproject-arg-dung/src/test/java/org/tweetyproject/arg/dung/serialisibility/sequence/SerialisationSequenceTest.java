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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility.sequence;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Tests to verify the code in the class {@link SerialisationSequence}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
class SerialisationSequenceTest {

	@Test
	void testEquals() {
		
		//Arrange
		var a = new Argument("a");
		var b = new Argument("b");
		var c = new Argument("c");
		
		var argsAB = new HashSet<Argument>();
		argsAB.add(a);
		argsAB.add(b);
		var extAB1_1 = new Extension<DungTheory>(argsAB);
		
		var extAB1_2 = new Extension<DungTheory>(argsAB);
		
		var argsAB2 = new HashSet<Argument>();
		argsAB2.add(a);
		argsAB2.add(b);
		var extAB2 = new Extension<DungTheory>(argsAB2);
		
		var argsBC = new HashSet<Argument>();
		argsBC.add(b);
		argsBC.add(c);
		var extBC = new Extension<DungTheory>(argsBC);
		
		//Act
		var seqAB1_1 = new SerialisationSequence();
		seqAB1_1.add(extAB1_1);
		
		var seqAB1_2 = new SerialisationSequence();
		seqAB1_2.add(extAB1_2);
		
		var seqAB2 = new SerialisationSequence();
		seqAB2.add(extAB2);
		
		var seqBC = new SerialisationSequence();
		seqBC.add(extBC);
		
		var seqAB1_1_BC = new SerialisationSequence();
		seqAB1_1_BC.add(extAB1_1);
		seqAB1_1_BC.add(extBC);
		
		var seqAB1_2_BC = new SerialisationSequence();
		seqAB1_2_BC.add(extAB1_2);
		seqAB1_2_BC.add(extBC);
		
		var seqAB2_BC = new SerialisationSequence();
		seqAB2_BC.add(extAB2);
		seqAB2_BC.add(extBC);
		
		//Assert
		Assert.assertTrue(seqAB1_1.equals(seqAB1_2));
		Assert.assertTrue(seqAB1_1.equals(seqAB2));
		Assert.assertTrue(seqAB1_2.equals(seqAB2));
		
		Assert.assertFalse(seqAB1_1.equals(seqBC));
		Assert.assertFalse(seqAB1_2.equals(seqBC));
		Assert.assertFalse(seqAB2.equals(seqBC));
		
		Assert.assertTrue(seqAB1_1_BC.equals(seqAB1_2_BC));
		Assert.assertTrue(seqAB1_1_BC.equals(seqAB2_BC));
		Assert.assertTrue(seqAB1_2_BC.equals(seqAB2_BC));
		
		Assert.assertFalse(seqAB1_1_BC.equals(seqBC));
		Assert.assertFalse(seqAB1_2_BC.equals(seqBC));
		Assert.assertFalse(seqAB2_BC.equals(seqBC));
	}

}
