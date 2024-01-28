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
package org.tweetyproject.arg.dung.serialisability.graph;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationSequence;
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
		var extA2 = new Extension<DungTheory>();
		extA2.add(a);
		var extB = new Extension<DungTheory>();
		extB.add(b);
		var extC = new Extension<DungTheory>();
		extC.add(c);
		var extD = new Extension<DungTheory>();
		extD.add(d);
		var extE = new Extension<DungTheory>();
		extE.add(e);
		var extF = new Extension<DungTheory>();
		extF.add(f);
		
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
		

		var graph = new SerialisationGraph(extA, Semantics.ADM, new HashSet<Extension<DungTheory>>());
		
		graph.add(extAB);
		graph.add(new DirectedEdge<Extension<DungTheory>>(extA, extAB));
		
		graph.add(extABC);
		graph.add(new DirectedEdge<Extension<DungTheory>>(extAB, extABC));
		
		graph.add(extABCD);
		graph.add(new DirectedEdge<Extension<DungTheory>>(extABC, extABCD));
		
		graph.add(extABCDX);
		graph.add(new DirectedEdge<Extension<DungTheory>>(extABCD, extABCDX));
		
		graph.add(extAE);
		graph.add(new DirectedEdge<Extension<DungTheory>>(extA, extAE));
		
		graph.add(extAEF);
		graph.add(new DirectedEdge<Extension<DungTheory>>(extAE, extAEF));
		
		graph.add(extAEFY);
		graph.add(new DirectedEdge<Extension<DungTheory>>(extAEF, extAEFY));
		
		var expSeq1 = new SerialisationSequence();	
		expSeq1.add(extA2);
		expSeq1.add(extB);
		expSeq1.add(extC);
		expSeq1.add(extD);
		
		var expSeqs1 = new HashSet<SerialisationSequence>();
		expSeqs1.add(expSeq1);
		
		var expSeq2 = new SerialisationSequence();
		expSeq2.add(extA);
		expSeq2.add(extE);
		expSeq2.add(extF);
		var expSeqs2 = new HashSet<SerialisationSequence>();
		expSeqs2.add(expSeq2);
		
		//Act
		var actSeq1 = graph.getSerialisationSequences(extABCD);
		var actSeq2 = graph.getSerialisationSequences(extAEF);
		
		//Assert		
		Assert.assertEquals(expSeqs1, actSeq1);
		Assert.assertEquals(expSeqs2, actSeq2);
	}

}
