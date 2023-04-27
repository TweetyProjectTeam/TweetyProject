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

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.serialisibility.sequence.SerialisationSequenceTest;
import org.tweetyproject.arg.dung.serialisibility.syntax.SerialisationSequence;

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
		
		var seqs1 = new LinkedList<SerialisationSequence>();
		seqs1.add(seqAB11);
		seqs1.add(seqAB12);
		var seqs2 = new LinkedList<SerialisationSequence>();
		seqs2.add(seqAB11);
		seqs2.add(seqAB2);
		var seqs3 = new LinkedList<SerialisationSequence>();
		seqs3.add(seqAB11);
		seqs3.add(seqAB2);
		
		var seqs4 = new LinkedList<SerialisationSequence>();
		seqs4.add(seqAB11);
		seqs4.add(seqBC);
		var seqs5 = new LinkedList<SerialisationSequence>();
		seqs5.add(seqAB12);
		seqs5.add(seqBC);
		var seqs6 = new LinkedList<SerialisationSequence>();
		seqs6.add(seqAB2);
		seqs6.add(seqBC);
		
		var seqs7 = new LinkedList<SerialisationSequence>();
		seqs7.add(seqAB11BC);
		seqs7.add(seqAB12BC);
		var seqs8 = new LinkedList<SerialisationSequence>();
		seqs8.add(seqAB11BC);
		seqs8.add(seqAB2BC);
		var seqs9 = new LinkedList<SerialisationSequence>();
		seqs9.add(seqAB12BC);
		seqs9.add(seqAB2BC);
		
		var seqs10 = new LinkedList<SerialisationSequence>();
		seqs10.add(seqAB11BC);
		seqs10.add(seqBC);
		var seqs11 = new LinkedList<SerialisationSequence>();
		seqs11.add(seqAB12BC);
		seqs11.add(seqBC);
		var seqs12 = new LinkedList<SerialisationSequence>();
		seqs12.add(seqAB2BC);
		seqs12.add(seqBC);
		
		var seqs13 = new LinkedList<SerialisationSequence>();
		seqs13.add(seqAB11);
		seqs13.add(seqAB11X);
		var seqs14 = new LinkedList<SerialisationSequence>();
		seqs14.add(seqAB12);
		seqs14.add(seqAB12X);
		var seqs15 = new LinkedList<SerialisationSequence>();
		seqs15.add(seqAB2);
		seqs15.add(seqAB2X);
		var seqs16 = new LinkedList<SerialisationSequence>();
		seqs16.add(seqBC);
		seqs16.add(seqBCX);
		var seqs17 = new LinkedList<SerialisationSequence>();
		seqs17.add(seqAB11BC);
		seqs17.add(seqAB11BCX);
		var seqs18 = new LinkedList<SerialisationSequence>();
		seqs18.add(seqAB12BC);
		seqs18.add(seqAB12BCX);
		var seqs19 = new LinkedList<SerialisationSequence>();
		seqs19.add(seqAB2BC);
		seqs19.add(seqAB2BCX);

		//Act
		var equivalence = new SerialisationEquivalenceBySequenceNaiv();

		//Assert
		Assert.assertTrue(equivalence.isEquivalent(seqs1));
		Assert.assertTrue(equivalence.isEquivalent(seqs2));
		Assert.assertTrue(equivalence.isEquivalent(seqs3));

		Assert.assertFalse(equivalence.isEquivalent(seqs4));
		Assert.assertFalse(equivalence.isEquivalent(seqs5));
		Assert.assertFalse(equivalence.isEquivalent(seqs6));

		Assert.assertTrue(equivalence.isEquivalent(seqs7));
		Assert.assertTrue(equivalence.isEquivalent(seqs8));
		Assert.assertTrue(equivalence.isEquivalent(seqs9));

		Assert.assertFalse(equivalence.isEquivalent(seqs10));
		Assert.assertFalse(equivalence.isEquivalent(seqs11));
		Assert.assertFalse(equivalence.isEquivalent(seqs12));


		// compare same sequences, but with arguments being diff. objects
		Assert.assertTrue(equivalence.isEquivalent(seqs13));
		Assert.assertTrue(equivalence.isEquivalent(seqs14));
		Assert.assertTrue(equivalence.isEquivalent(seqs15));
		Assert.assertTrue(equivalence.isEquivalent(seqs16));
		Assert.assertTrue(equivalence.isEquivalent(seqs17));
		Assert.assertTrue(equivalence.isEquivalent(seqs18));
		Assert.assertTrue(equivalence.isEquivalent(seqs19));
	}

}
