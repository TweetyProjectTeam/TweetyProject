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
package org.tweetyproject.arg.dung.serialisability.equivalence;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.serialisability.sequence.SerialisationSequenceTest;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationSequence;

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
		var seqDE = new SerialisationSequence();
		var seqED = new SerialisationSequence();

		SerialisationSequenceTest.CreateSequencesForTest(seqAB11, seqAB12, seqAB2, seqBC, seqAB11BC, seqAB12BC, seqAB2BC, seqDE, seqED);
		
		var seqsEQ1 = new HashSet<SerialisationSequence>();
		seqsEQ1.add(seqAB11);
		seqsEQ1.add(seqBC);
		seqsEQ1.add(seqDE);
		
		var seqsEQ2 = new HashSet<SerialisationSequence>();
		seqsEQ2.add(seqAB12);
		seqsEQ2.add(seqBC);
		seqsEQ2.add(seqDE);
		
		var seqsNotEQ1 = new HashSet<SerialisationSequence>();
		seqsNotEQ1.add(seqAB11BC);
		
		var seqsNotEQ2 = new HashSet<SerialisationSequence>();
		seqsNotEQ2.add(seqAB11);
		seqsNotEQ2.add(seqBC);
		seqsNotEQ2.add(seqED);
		
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

		SerialisationSequenceTest.CreateSequencesForTest(seqAB11X, seqAB12X, seqAB2X, seqBCX, seqAB11BCX, seqAB12BCX, seqAB2BCX, seqDEX, seqEDX);

		var seqsEQ3 = new HashSet<SerialisationSequence>();
		seqsEQ3.add(seqAB11X);
		seqsEQ3.add(seqBCX);
		seqsEQ3.add(seqDEX);
		
		
		//Act
		var equivalence = new SerialisationEquivalenceBySequenceNaiv();

		//Assert
		Assert.assertTrue(equivalence.isEquivalent(seqsEQ1, seqsEQ2));
		Assert.assertFalse(equivalence.isEquivalent(seqsEQ1, seqsNotEQ1));
		Assert.assertFalse(equivalence.isEquivalent(seqsEQ1, seqsNotEQ2));

		// compare same sequences, but with arguments being diff. objects
		Assert.assertTrue(equivalence.isEquivalent(seqsEQ1, seqsEQ3));
		
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
		var seqDE = new SerialisationSequence();
		var seqED = new SerialisationSequence();

		SerialisationSequenceTest.CreateSequencesForTest(seqAB11, seqAB12, seqAB2, seqBC, seqAB11BC, seqAB12BC, seqAB2BC, seqDE, seqED);

		var seqsEQ1 = new LinkedList<SerialisationSequence>();
		seqsEQ1.add(seqAB11);
		seqsEQ1.add(seqBC);
		seqsEQ1.add(seqDE);
		
		var seqsEQ2 = new LinkedList<SerialisationSequence>();
		seqsEQ2.add(seqAB12);
		seqsEQ2.add(seqBC);
		seqsEQ2.add(seqDE);
		
		var seqsNotEQ1= new LinkedList<SerialisationSequence>();
		seqsNotEQ1.add(seqAB11BC);
		
		var seqsNotEQ2 = new HashSet<SerialisationSequence>();
		seqsNotEQ2.add(seqAB11);
		seqsNotEQ2.add(seqBC);
		seqsNotEQ2.add(seqED);
		
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

		SerialisationSequenceTest.CreateSequencesForTest(seqAB11X, seqAB12X, seqAB2X, seqBCX, seqAB11BCX, seqAB12BCX, seqAB2BCX, seqDEX, seqEDX);
		
		var seqsEQ3 = new LinkedList<SerialisationSequence>();
		seqsEQ3.add(seqAB11X);
		seqsEQ3.add(seqBCX);
		seqsEQ3.add(seqDEX);
		
		var listSeqs1 = new LinkedList<Collection<SerialisationSequence>>();
		listSeqs1.add(seqsEQ1);
		listSeqs1.add(seqsEQ2);
		
		var listSeqs2 = new LinkedList<Collection<SerialisationSequence>>();
		listSeqs2.add(seqsEQ1);
		listSeqs2.add(seqsNotEQ1);
		
		var listSeqs3 = new LinkedList<Collection<SerialisationSequence>>();
		listSeqs3.add(seqsEQ1);
		listSeqs3.add(seqsNotEQ2);
		
		var listSeqs4 = new LinkedList<Collection<SerialisationSequence>>();
		listSeqs4.add(seqsEQ1);
		listSeqs4.add(seqsEQ3);

		//Act
		var equivalence = new SerialisationEquivalenceBySequenceNaiv();

		//Assert
		Assert.assertTrue(equivalence.isEquivalent(listSeqs1));
		Assert.assertFalse(equivalence.isEquivalent(listSeqs2));
		Assert.assertFalse(equivalence.isEquivalent(listSeqs3));

		// compare same sequences, but with arguments being diff. objects
		Assert.assertTrue(equivalence.isEquivalent(listSeqs4));
	}

}
