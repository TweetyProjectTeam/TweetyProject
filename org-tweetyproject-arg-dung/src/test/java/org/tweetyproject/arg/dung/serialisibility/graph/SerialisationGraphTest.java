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
package org.tweetyproject.arg.dung.serialisibility.graph;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.TransitionState;
import org.tweetyproject.arg.dung.serialisibility.sequence.SerialisationSequence;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.*;

/**
 * Tests to verify the code in the class {@link SerialisationGraph}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
class SerialisationGraphTest {

	@Test
	void testGetSerialisationSequence() {
		
		//Arrange
		var framework = new DungTheory();
		var a = new Argument("a");
		var b = new Argument("b");
		var c = new Argument("c");
		var d = new Argument("d");
		var e = new Argument("e");
		var f = new Argument("f");
		var x = new Argument("x");
		var y = new Argument("y");
		var argsA = new HashSet<Argument>();
		argsA.add(a);
		var extA = new Extension<DungTheory>(argsA);
		var argsAB = new HashSet<Argument>();
		argsAB.add(a);
		argsAB.add(b);
		var extAB = new Extension<DungTheory>(argsAB);
		var argsABC = new HashSet<Argument>();
		argsABC.add(a);
		argsABC.add(b);
		argsABC.add(c);
		var extABC = new Extension<DungTheory>(argsABC);
		var argsABCD = new HashSet<Argument>();
		argsABCD.add(a);
		argsABCD.add(b);
		argsABCD.add(c);
		argsABCD.add(d);
		var extABCD = new Extension<DungTheory>(argsABCD);
		var argsABCDX = new HashSet<Argument>();
		argsABCDX.add(a);
		argsABCDX.add(b);
		argsABCDX.add(c);
		argsABCDX.add(d);
		argsABCDX.add(x);
		var extABCDX = new Extension<DungTheory>(argsABCDX);
		
		var argsAE = new HashSet<Argument>();
		argsAE.add(a);
		argsAE.add(e);
		var extAE = new Extension<DungTheory>(argsAE);
		var argsAEF = new HashSet<Argument>();
		argsAEF.add(a);
		argsAEF.add(e);
		argsAEF.add(f);
		var extAEF = new Extension<DungTheory>(argsAEF);
		var argsAEFY = new HashSet<Argument>();
		argsAEFY.add(a);
		argsAEFY.add(e);
		argsAEFY.add(f);
		argsAEFY.add(y);
		var extAEFY = new Extension<DungTheory>(argsAEFY);
		
		var rootA = new TransitionStateNode(new TransitionState(framework, extA));
		var graph = new SerialisationGraph(rootA, Semantics.ADM);
		
		var nodeAB = new TransitionStateNode(new TransitionState(framework, extAB));
		graph.add(nodeAB);
		graph.add(new DirectedEdge<TransitionStateNode>(rootA, nodeAB));
		
		var nodeABC = new TransitionStateNode(new TransitionState(framework, extABC));
		graph.add(nodeABC);
		graph.add(new DirectedEdge<TransitionStateNode>(nodeAB, nodeABC));
		
		var nodeABCD = new TransitionStateNode(new TransitionState(framework, extABCD));
		graph.add(nodeABCD);
		graph.add(new DirectedEdge<TransitionStateNode>(nodeABC, nodeABCD));
		
		var nodeABCDX = new TransitionStateNode(new TransitionState(framework, extABCDX));
		graph.add(nodeABCDX);
		graph.add(new DirectedEdge<TransitionStateNode>(nodeABCD, nodeABCDX));
		
		
		var nodeAE = new TransitionStateNode(new TransitionState(framework, extAE));
		graph.add(nodeAE);
		graph.add(new DirectedEdge<TransitionStateNode>(rootA, nodeAE));
		
		var nodeAEF = new TransitionStateNode(new TransitionState(framework, extAEF));
		graph.add(nodeAEF);
		graph.add(new DirectedEdge<TransitionStateNode>(nodeAE, nodeAEF));
		
		var nodeAEFY = new TransitionStateNode(new TransitionState(framework, extAEFY));
		graph.add(nodeAEFY);
		graph.add(new DirectedEdge<TransitionStateNode>(nodeAEF, nodeAEFY));
		
		var expSeq1 = new SerialisationSequence();
		expSeq1.add(extA);
		expSeq1.add(extAB);
		expSeq1.add(extABC);
		expSeq1.add(extABCD);
		
		var expSeq2 = new SerialisationSequence();
		expSeq2.add(extA);
		expSeq2.add(extAE);
		expSeq2.add(extAEF);
		
		//Act
		var actSeq1 = graph.getSerialisationSequence(nodeABCD);
		var actSeq2 = graph.getSerialisationSequence(nodeAEF);
		
		//Assert		
		Assert.assertEquals(expSeq1, actSeq1);
		Assert.assertEquals(expSeq2, actSeq2);
	}

}
