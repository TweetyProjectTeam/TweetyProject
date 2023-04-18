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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.tweetyproject.arg.dung.examples.SerialisableExtensionReasonerExample;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
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
			
			//Assert
			assertEquals(extensionsExpected, extensionsActual);
		}
		
	}

}
