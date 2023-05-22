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
package org.tweetyproject.arg.dung.reasoner.serialisable;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.tweetyproject.arg.dung.examples.SerialisableExtensionReasonerExample;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.syntax.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Tests to verify the code in the class {@link SerialisableExtensionReasoner}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
class SerialisableExtensionReasonerTest {

	/**
	 * This test, verifies that the method "getModels" works correctly, 
	 * by comparing its results with those of verified reasoners of the specified semantics.
	 * The verified reasoners are given by the class {@link AbstractExtensionReasoner}.
	 */
	@ParameterizedTest
	@EnumSource(names = {"ADM", "CO", "GR", "PR", "ST"}) 
	void getModels_knownSemantics_findSameExtensions(Semantics semantics) {
		
		//Arrange				
		var reasonerToTest = SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics);
		var reasonerVerified = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
		var frameworks = new DungTheory[3];
		frameworks[0] = SerialisableExtensionReasonerExample.buildExample1();
		frameworks[1] = SerialisableExtensionReasonerExample.buildExample2();
		frameworks[2] = SerialisableExtensionReasonerExample.buildExample3();
		
		for (int i = 0; i < frameworks.length; i++) {
			
			//Act	
			Collection<Extension<DungTheory>> extensionsActual = reasonerToTest.getModels(frameworks[i]);
			Collection<Extension<DungTheory>> extensionsExpected = reasonerVerified.getModels(frameworks[i]);
			
			System.out.println(semantics.description());
			System.out.println("Expected:" + extensionsExpected.toString());
			System.out.println("Actual:" + extensionsActual.toString());
			
			//Assert
			assertEquals(extensionsExpected, extensionsActual);
		}
		
	}
	
	/**
	 * This test, verifies that the method "getModelsSequences" works correctly, 
	 * by comparing its results with verified examples for complete semantics.
	 */
	@ParameterizedTest
	@EnumSource(names = {"CO"}) 
	void getModelsSequences_COSemantics_findSameSequences(Semantics semantics) {
		//Arrange
		var reasonerToTest = SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(semantics);
		var a0 = new Argument("a0");
		var a1 = new Argument("a1");
		var a2 = new Argument("a2");
		var a3 = new Argument("a3");
		var example1 = getExample1(a0, a1, a2, a3);
		var example2 = getExample2(a0, a1, a2, a3);
		var expectedSeqs1 = getSequencesForExample1(a0, a1, a2, a3);
		var expectedSeqs2 = getSequencesForExample2(a0, a1, a2, a3);
		
		//Act
		var actualSeqs1 = reasonerToTest.getModelsSequences(example1);
		var actualSeqs2 = reasonerToTest.getModelsSequences(example2);
		
		//Assert
		Assert.assertTrue(expectedSeqs1.equals(actualSeqs1));
		Assert.assertTrue(expectedSeqs2.equals(actualSeqs2));
	}
	
	private DungTheory getExample1(Argument a0, Argument a1, Argument a2, Argument a3) {
		var output = new DungTheory();
		output.add(a0);
		output.add(a1);
		output.add(a2);
		output.add(a3);
		
		output.add(new Attack(a1, a2));
		
		return output;
	}
	
	private DungTheory getExample2(Argument a0, Argument a1, Argument a2, Argument a3) {
		var output = new DungTheory();
		output.add(a0);
		output.add(a1);
		output.add(a2);
		output.add(a3);
		
		output.add(new Attack(a1, a2));
		output.add(new Attack(a2, a2));
		output.add(new Attack(a2, a3));
		
		return output;
	}
	
	private LinkedList<SerialisationSequence> getSequencesForExample1(Argument a0, Argument a1, Argument a2, Argument a3){ 
		var output = new LinkedList<SerialisationSequence>();
		
		var extEMPTY = new Extension<DungTheory>();
		var ext0 = new Extension<DungTheory>();
		ext0.add(a0);
		var ext1 = new Extension<DungTheory>();
		ext1.add(a1);
		var ext3 = new Extension<DungTheory>();
		ext3.add(a3);
		
		var seq1 = new SerialisationSequence();
		seq1.add(extEMPTY);
		seq1.add(ext0);
		seq1.add(ext1);
		seq1.add(ext3);
		output.add(seq1);
		
		var seq2 = new SerialisationSequence();
		seq2.add(extEMPTY);
		seq2.add(ext0);
		seq2.add(ext3);
		seq2.add(ext1);
		output.add(seq2);
		
		var seq3 = new SerialisationSequence();
		seq3.add(extEMPTY);
		seq3.add(ext1);
		seq3.add(ext0);
		seq3.add(ext3);
		output.add(seq3);
		
		var seq4 = new SerialisationSequence();
		seq4.add(extEMPTY);
		seq4.add(ext1);
		seq4.add(ext3);
		seq4.add(ext0);
		output.add(seq4);
		
		var seq5 = new SerialisationSequence();
		seq5.add(extEMPTY);
		seq5.add(ext3);
		seq5.add(ext0);
		seq5.add(ext1);
		output.add(seq5);
		
		var seq6 = new SerialisationSequence();
		seq6.add(extEMPTY);
		seq6.add(ext3);
		seq6.add(ext1);
		seq6.add(ext0);
		output.add(seq6);
		
		return output;
	}
	
	private LinkedList<SerialisationSequence> getSequencesForExample2(Argument a0, Argument a1, Argument a2, Argument a3){ 
		var output = new LinkedList<SerialisationSequence>();
		
		var extEMPTY = new Extension<DungTheory>();
		var ext0 = new Extension<DungTheory>();
		ext0.add(a0);
		var ext1 = new Extension<DungTheory>();
		ext1.add(a1);
		var ext3 = new Extension<DungTheory>();
		ext3.add(a3);
		
		var seq1 = new SerialisationSequence();
		seq1.add(extEMPTY);
		seq1.add(ext0);
		seq1.add(ext1);
		seq1.add(ext3);
		output.add(seq1);
		
		var seq2 = new SerialisationSequence();
		seq2.add(extEMPTY);
		seq2.add(ext1);
		seq2.add(ext0);
		seq2.add(ext3);
		output.add(seq2);
		
		var seq3 = new SerialisationSequence();
		seq3.add(extEMPTY);
		seq3.add(ext1);
		seq3.add(ext3);
		seq3.add(ext0);
		output.add(seq3);
		
		return output;
	}

}
