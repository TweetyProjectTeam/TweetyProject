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
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.syntax.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisibility.syntax.TransitionState;
import org.tweetyproject.arg.dung.serialisibility.syntax.TransitionStateNode;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;

/**
 * Tests to verify the code in the class {@link SerialisationEquivalenceByGraphIso}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
class SerialisationEquivalenceByGraphIsoTest {

	@Test
	void testIsEquivalentDungTheoryDungTheory() {
		//Arrange
		var frameEQ1 = new SerialisationGraph(new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>())), Semantics.ADM);
		var frameEQ2 = new SerialisationGraph(new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>())), Semantics.ADM);
		var frameNotEQ1 = new SerialisationGraph(new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>())), Semantics.ADM);
		var frameNotEQ2 = new SerialisationGraph(new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>())), Semantics.ADM);

		createGraphsIsomorphic(frameEQ1, frameEQ2);
		createGraphsNotIsomorphic(frameNotEQ1, frameNotEQ2);

		var equivalence = new SerialisationEquivalenceByGraphIso();

		//Act
		//Assert
		Assert.assertTrue(equivalence.isEquivalent(frameEQ1, frameEQ2));
		Assert.assertFalse(equivalence.isEquivalent(frameNotEQ1, frameNotEQ2));
	}

	@Test
	void testIsEquivalentCollectionOfDungTheory() {
		//Arrange
		var frameEQ1 = new SerialisationGraph(new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>())), Semantics.ADM);
		var frameEQ2 = new SerialisationGraph(new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>())), Semantics.ADM);
		var frameNotEQ1 = new SerialisationGraph(new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>())), Semantics.ADM);
		var frameNotEQ2 = new SerialisationGraph(new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>())), Semantics.ADM);

		var framesEQ = new LinkedList<SerialisationGraph>();
		framesEQ.add(frameEQ1);
		framesEQ.add(frameEQ2);
		var framesNotEQ = new LinkedList<SerialisationGraph>();
		framesNotEQ.add(frameNotEQ1);
		framesNotEQ.add(frameNotEQ2);

		createGraphsIsomorphic(frameEQ1, frameEQ2);
		createGraphsNotIsomorphic(frameNotEQ1, frameNotEQ2);

		var equivalence = new SerialisationEquivalenceByGraphIso();

		//Act
		//Assert
		Assert.assertTrue(equivalence.isEquivalent(framesEQ));
		Assert.assertFalse(equivalence.isEquivalent(framesNotEQ));
	}


	private void createGraphsIsomorphic(SerialisationGraph out_framework1, SerialisationGraph out_framework2) {
		var a = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
		var b = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
		var c = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
		var d = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
		var e = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
		var f = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
	
		out_framework1.add(a);
		out_framework1.add(b);
		out_framework1.add(c);
		
		out_framework2.add(d);
		out_framework2.add(e);
		out_framework2.add(f);

		out_framework1.add(new DirectedEdge<TransitionStateNode>(a, b));
		out_framework1.add(new DirectedEdge<TransitionStateNode>(b, c));
		
		out_framework2.add(new DirectedEdge<TransitionStateNode>(d, e));
		out_framework2.add(new DirectedEdge<TransitionStateNode>(e, f));
	}

	private void createGraphsNotIsomorphic(SerialisationGraph out_framework1, SerialisationGraph out_framework2) {
		var a = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
		var b = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
		var c = new TransitionStateNode(new TransitionState(new DungTheory(), new Extension<DungTheory>()));
	
		out_framework1.add(a);
		out_framework1.add(b);
		out_framework1.add(c);

		out_framework1.add(new DirectedEdge<TransitionStateNode>(a, b));
		out_framework1.add(new DirectedEdge<TransitionStateNode>(b, c));
	}
}
