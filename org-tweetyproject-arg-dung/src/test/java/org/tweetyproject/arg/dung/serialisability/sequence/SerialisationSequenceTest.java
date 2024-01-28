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
package org.tweetyproject.arg.dung.serialisability.sequence;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Tests to verify the code in the class {@link SerialisationSequence}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationSequenceTest {

	@Test
	void testEquals() {

		//Arrange
		var seqAB11 = new SerialisationSequence();
		var seqAB12 = new SerialisationSequence();
		var seqAB2 = new SerialisationSequence();
		var seqBC = new SerialisationSequence();
		var seqAB11BC = new SerialisationSequence();
		var seqAB12BC = new SerialisationSequence();
		var seqAB2BC = new SerialisationSequence();
		var seqDE = new SerialisationSequence();
		var seqED = new SerialisationSequence();

		CreateSequencesForTest(seqAB11, seqAB12, seqAB2, seqBC, seqAB11BC, seqAB12BC, seqAB2BC, seqDE, seqED);

		//create same Sequences on different arguments (different objects)
		var seqAB11X = new SerialisationSequence();
		var seqAB12X = new SerialisationSequence();
		var seqAB2X = new SerialisationSequence();
		var seqBCX = new SerialisationSequence();
		var seqAB11BCX = new SerialisationSequence();
		var seqAB12BCX = new SerialisationSequence();
		var seqAB2BCX = new SerialisationSequence();
		var seqDEX = new SerialisationSequence();
		var seqEDX = new SerialisationSequence();

		CreateSequencesForTest(seqAB11X, seqAB12X, seqAB2X, seqBCX, seqAB11BCX, seqAB12BCX, seqAB2BCX, seqDEX, seqEDX);

		//Act
		//Assert
		Assert.assertTrue(seqAB11.equals(seqAB12));
		Assert.assertTrue(seqAB11.equals(seqAB2));
		Assert.assertTrue(seqAB12.equals(seqAB2));

		Assert.assertFalse(seqAB11.equals(seqBC));
		Assert.assertFalse(seqAB12.equals(seqBC));
		Assert.assertFalse(seqAB2.equals(seqBC));

		Assert.assertTrue(seqAB11BC.equals(seqAB12BC));
		Assert.assertTrue(seqAB11BC.equals(seqAB2BC));
		Assert.assertTrue(seqAB12BC.equals(seqAB2BC));

		Assert.assertFalse(seqAB11BC.equals(seqBC));
		Assert.assertFalse(seqAB12BC.equals(seqBC));
		Assert.assertFalse(seqAB2BC.equals(seqBC));
		
		Assert.assertFalse(seqDE.equals(seqED));
		Assert.assertFalse(seqED.equals(seqDE));
		
		//compare same sequences, but with arguments being two different objects
		Assert.assertTrue(seqAB11.equals(seqAB11X));
		Assert.assertTrue(seqAB12.equals(seqAB12X));
		Assert.assertTrue(seqAB2.equals(seqAB2X));
		Assert.assertTrue(seqBC.equals(seqBCX));
		Assert.assertTrue(seqAB11BC.equals(seqAB11BCX));
		Assert.assertTrue(seqAB12BC.equals(seqAB12BCX));
		Assert.assertTrue(seqAB2BC.equals(seqAB2BCX));
		Assert.assertTrue(seqDE.equals(seqDEX));
		Assert.assertTrue(seqED.equals(seqEDX));
	}


	@SuppressWarnings("javadoc")
	public static void CreateSequencesForTest(
			SerialisationSequence out_seqAB11, 
			SerialisationSequence out_seqAB12, 
			SerialisationSequence out_seqAB2, 
			SerialisationSequence out_seqBC, 
			SerialisationSequence out_seqAB11BC, 
			SerialisationSequence out_seqAB12BC, 
			SerialisationSequence out_seqAB2BC, 
			SerialisationSequence out_seqDE,
			SerialisationSequence out_seqED) {
		var a = new Argument("a");
		var b = new Argument("b");
		var c = new Argument("c");
		var d = new Argument("d");
		var e = new Argument("e");

		var argsAB = new HashSet<Argument>();
		argsAB.add(a);
		argsAB.add(b);
		var extAB11 = new Extension<DungTheory>(argsAB);

		var extAB12 = new Extension<DungTheory>(argsAB);

		var argsAB2 = new HashSet<Argument>();
		argsAB2.add(a);
		argsAB2.add(b);
		var extAB2 = new Extension<DungTheory>(argsAB2);

		var argsBC = new HashSet<Argument>();
		argsBC.add(b);
		argsBC.add(c);
		var extBC = new Extension<DungTheory>(argsBC);
		
		var extD = new Extension<DungTheory>();
		extD.add(d);
		
		var extE = new Extension<DungTheory>();
		extE.add(e);

		out_seqAB11.add(extAB11);

		out_seqAB12.add(extAB12);

		out_seqAB2.add(extAB2);

		out_seqBC.add(extBC);

		out_seqAB11BC.add(extAB11);
		out_seqAB11BC.add(extBC);

		out_seqAB12BC.add(extAB12);
		out_seqAB12BC.add(extBC);

		out_seqAB2BC.add(extAB2);
		out_seqAB2BC.add(extBC);
		
		out_seqDE.add(extD);
		out_seqDE.add(extE);
		
		out_seqED.add(extE);
		out_seqED.add(extD);
	}
}
