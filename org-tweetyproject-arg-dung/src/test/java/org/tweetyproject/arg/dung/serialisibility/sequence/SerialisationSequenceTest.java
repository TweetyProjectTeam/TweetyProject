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
		var ext1 = new Extension<DungTheory>(argsAB);
		
		var ext2 = new Extension<DungTheory>(argsAB);
		
		var argsAB2 = new HashSet<Argument>();
		argsAB2.add(a);
		argsAB2.add(b);
		var ext3 = new Extension<DungTheory>(argsAB2);
		
		var argsBC = new HashSet<Argument>();
		argsBC.add(b);
		argsBC.add(c);
		var extX = new Extension<DungTheory>(argsBC);
		
		//Act
		var seq1 = new SerialisationSequence();
		seq1.add(ext1);
		
		var seq2 = new SerialisationSequence();
		seq2.add(ext2);
		
		var seq3 = new SerialisationSequence();
		seq3.add(ext3);
		
		var seqX = new SerialisationSequence();
		seqX.add(extX);
		
		var seq1X = new SerialisationSequence();
		seq1X.add(ext1);
		seq1X.add(extX);
		
		var seq2X = new SerialisationSequence();
		seq2X.add(ext2);
		seq2X.add(extX);
		
		var seq3X = new SerialisationSequence();
		seq3X.add(ext3);
		seq3X.add(extX);
		
		//Assert
		Assert.assertTrue(seq1.equals(seq2));
		Assert.assertTrue(seq1.equals(seq3));
		Assert.assertTrue(seq2.equals(seq3));
		
		Assert.assertFalse(seq1.equals(seqX));
		Assert.assertFalse(seq2.equals(seqX));
		Assert.assertFalse(seq3.equals(seqX));
		
		Assert.assertTrue(seq1X.equals(seq2X));
		Assert.assertTrue(seq1X.equals(seq3X));
		Assert.assertTrue(seq2X.equals(seq3X));
		
		Assert.assertFalse(seq1X.equals(seqX));
		Assert.assertFalse(seq2X.equals(seqX));
		Assert.assertFalse(seq3X.equals(seqX));
	}

}
