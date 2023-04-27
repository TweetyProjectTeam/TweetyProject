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
package org.tweetyproject.arg.dung.serialisibility.equivalence;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.serialisibility.sequence.SerialisationSequence;
import org.tweetyproject.arg.dung.serialisibility.sequence.SerialisationSequenceTest;

/**
 * Tests to verify the code in the class {@link SerialisationEquivalenceBySequenceNaiv}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
class SerialisationEquivalenceBySequenceNaivTest {

	@Test
	void testIsEquivalentSerialisationSequenceSerialisationSequence() {
		
		//Arrange
		var seqAB11 = new SerialisationSequence();
		var seqAB12 = new SerialisationSequence();
		var seqAB2 = new SerialisationSequence();
		var seqBC = new SerialisationSequence();
		var seqAB11BC = new SerialisationSequence();
		var seqAB12BC = new SerialisationSequence();
		var seqAB2BC = new SerialisationSequence();
		
		SerialisationSequenceTest.CreateSequencesForTest(seqAB11, seqAB12, seqAB2, seqBC, seqAB11BC, seqAB12BC, seqAB2BC);
		
		//create same Sequences on different arguments (different objects)
		var seqAB11X = new SerialisationSequence();
		var seqAB12X = new SerialisationSequence();
		var seqAB2X = new SerialisationSequence();
		var seqBCX = new SerialisationSequence();
		var seqAB11BCX = new SerialisationSequence();
		var seqAB12BCX = new SerialisationSequence();
		var seqAB2BCX = new SerialisationSequence();
		
		SerialisationSequenceTest.CreateSequencesForTest(seqAB11X, seqAB12X, seqAB2X, seqBCX, seqAB11BCX, seqAB12BCX, seqAB2BCX);
		
		//Act
		var equivalence = new SerialisationEquivalenceBySequenceNaiv();
		
		//Assert
				Assert.assertTrue(equivalence.isEquivalent(seqAB11, seqAB12));
				Assert.assertTrue(equivalence.isEquivalent(seqAB11, seqAB2));
				Assert.assertTrue(equivalence.isEquivalent(seqAB12, seqAB2));
				
				Assert.assertFalse(equivalence.isEquivalent(seqAB11, seqBC));
				Assert.assertFalse(equivalence.isEquivalent(seqAB12, seqBC));
				Assert.assertFalse(equivalence.isEquivalent(seqAB2, seqBC));
				
				Assert.assertTrue(equivalence.isEquivalent(seqAB11BC, seqAB12BC));
				Assert.assertTrue(equivalence.isEquivalent(seqAB11BC, seqAB2BC));
				Assert.assertTrue(equivalence.isEquivalent(seqAB12BC, seqAB2BC));
				
				Assert.assertFalse(equivalence.isEquivalent(seqAB11BC, seqBC));
				Assert.assertFalse(equivalence.isEquivalent(seqAB12BC, seqBC));
				Assert.assertFalse(equivalence.isEquivalent(seqAB2BC, seqBC));
				
				
				// compare same sequences, but with arguments being diff. objects
				Assert.assertTrue(equivalence.isEquivalent(seqAB11, seqAB11X));
				Assert.assertTrue(equivalence.isEquivalent(seqAB12, seqAB12X));
				Assert.assertTrue(equivalence.isEquivalent(seqAB2, seqAB2X));
				Assert.assertTrue(equivalence.isEquivalent(seqBC, seqBCX));
				Assert.assertTrue(equivalence.isEquivalent(seqAB11BC, seqAB11BCX));
				Assert.assertTrue(equivalence.isEquivalent(seqAB12BC, seqAB12BCX));
				Assert.assertTrue(equivalence.isEquivalent(seqAB2BC, seqAB2BCX));
	}

	@Test
	void testIsEquivalentCollectionOfSerialisationSequence() {
		//fail("Not yet implemented");
	}

}
