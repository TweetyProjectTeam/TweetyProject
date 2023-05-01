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

import java.util.HashSet;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.syntax.*;
import org.tweetyproject.arg.dung.syntax.Argument;
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
	void testIsEquivalentSerialisationGraphSerialisationGraph() {
		//Arrange
		var graphEQ1 = new SerialisationGraph(new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		var graphEQ2 = new SerialisationGraph(new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		var graphNotEQ1 = new SerialisationGraph(new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		var graphNotEQ2 = new SerialisationGraph(new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());

		createGraphsIsomorphic(graphEQ1, graphEQ2);
		createGraphsNotIsomorphic(graphNotEQ1, graphNotEQ2);

		var equivalence = new SerialisationEquivalenceByGraphIso();

		//Act
		//Assert
		Assert.assertTrue(equivalence.isEquivalent(graphEQ1, graphEQ2));
		Assert.assertFalse(equivalence.isEquivalent(graphNotEQ1, graphNotEQ2));
	}

	@Test
	void testIsEquivalentCollectionOfSerialisationGraph() {
		//Arrange
		var graphEQ1 = new SerialisationGraph(new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		var graphEQ2 = new SerialisationGraph(new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		var graphNotEQ1 = new SerialisationGraph(new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		var graphNotEQ2 = new SerialisationGraph(new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());

		var graphsEQ = new LinkedList<SerialisationGraph>();
		graphsEQ.add(graphEQ1);
		graphsEQ.add(graphEQ2);
		var graphsNotEQ = new LinkedList<SerialisationGraph>();
		graphsNotEQ.add(graphNotEQ1);
		graphsNotEQ.add(graphNotEQ2);

		createGraphsIsomorphic(graphEQ1, graphEQ2);
		createGraphsNotIsomorphic(graphNotEQ1, graphNotEQ2);

		var equivalence = new SerialisationEquivalenceByGraphIso();

		//Act
		//Assert
		Assert.assertTrue(equivalence.isEquivalent(graphsEQ));
		Assert.assertFalse(equivalence.isEquivalent(graphsNotEQ));
	}


	public static void createGraphsIsomorphic(SerialisationGraph out_graph1, SerialisationGraph out_graph2) {
		var a = new Extension<DungTheory>();
		a.add(new Argument("a"));
		var b = new Extension<DungTheory>();
		b.add(new Argument("b"));
		var c = new Extension<DungTheory>();
		c.add(new Argument("c"));
		var d = new Extension<DungTheory>();
		d.add(new Argument("d"));
		var e = new Extension<DungTheory>();
		e.add(new Argument("e"));
		var f = new Extension<DungTheory>();
		f.add(new Argument("f"));
	
		out_graph1.add(a);
		out_graph1.add(b);
		out_graph1.add(c);
		
		out_graph2.add(d);
		out_graph2.add(e);
		out_graph2.add(f);

		out_graph1.add(new DirectedEdge<Extension<DungTheory>>(out_graph1.getRoot(), a));
		out_graph1.add(new DirectedEdge<Extension<DungTheory>>(a, b));
		out_graph1.add(new DirectedEdge<Extension<DungTheory>>(b, c));
		
		out_graph2.add(new DirectedEdge<Extension<DungTheory>>(out_graph2.getRoot(), d));
		out_graph2.add(new DirectedEdge<Extension<DungTheory>>(d, e));
		out_graph2.add(new DirectedEdge<Extension<DungTheory>>(e, f));
	}

	public static void createGraphsNotIsomorphic(SerialisationGraph out_graph1, SerialisationGraph out_graph2) {
		var a = new Extension<DungTheory>();
		a.add(new Argument("a"));
		var b = new Extension<DungTheory>();
		b.add(new Argument("b"));
		var c = new Extension<DungTheory>();
		c.add(new Argument("c"));
	
		out_graph1.add(a);
		out_graph1.add(b);
		out_graph1.add(c);

		out_graph1.add(new DirectedEdge<Extension<DungTheory>>(out_graph1.getRoot(), a));
		out_graph1.add(new DirectedEdge<Extension<DungTheory>>(a, b));
		out_graph1.add(new DirectedEdge<Extension<DungTheory>>(b, c));
		
		out_graph2.add(a);
		out_graph2.add(new DirectedEdge<Extension<DungTheory>>(out_graph2.getRoot(), a));
		out_graph2.add(new DirectedEdge<Extension<DungTheory>>(a, a));
	}
}
