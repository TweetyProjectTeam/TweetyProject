package org.tweetyproject.arg.dung.serialisability.equivalence;

import java.util.HashSet;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.syntax.SerialisationGraph;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

class SerialisationEquivalenceByGraphNaivTest {

	@Test
	void testIsEquivalentSerialisationGraphSerialisationGraph() {

		//Arrange
		SerialisationGraph graphEQ1 = new SerialisationGraph(
				new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		SerialisationGraph graphEQ2 = new SerialisationGraph(
				new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		SerialisationGraph graphNotEQ1 = new SerialisationGraph(
				new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		SerialisationGraph graphNotEQ2 = new SerialisationGraph(
				new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());

		createGraphsEqNaiv(graphEQ1, graphEQ2);
		createGraphsNotEqNaiv(graphNotEQ1, graphNotEQ2);

		//Act
		SerialisationEquivalenceByGraphNaiv equivalence = new SerialisationEquivalenceByGraphNaiv();
		//Assert

		Assert.assertTrue(equivalence.isEquivalent(graphEQ1, graphEQ2));
		Assert.assertFalse(equivalence.isEquivalent(graphNotEQ1, graphNotEQ2));

	}

	@Test
	void testIsEquivalentCollectionOfSerialisationGraph() {
		//Arrange
		SerialisationGraph graphEQ1 = new SerialisationGraph(
				new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		SerialisationGraph graphEQ2 = new SerialisationGraph(
				new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		SerialisationGraph graphNotEQ1 = new SerialisationGraph(
				new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		SerialisationGraph graphNotEQ2 = new SerialisationGraph(
				new Extension<DungTheory>(), Semantics.ADM, new HashSet<Extension<DungTheory>>());
		
		LinkedList<SerialisationGraph> lstEQ = new LinkedList<SerialisationGraph>();
		lstEQ.add(graphEQ1);
		lstEQ.add(graphEQ2);
		LinkedList<SerialisationGraph> lstNotEQ = new LinkedList<SerialisationGraph>();
		lstNotEQ.add(graphNotEQ1);
		lstNotEQ.add(graphNotEQ2);

		createGraphsEqNaiv(graphEQ1, graphEQ2);
		createGraphsNotEqNaiv(graphNotEQ1, graphNotEQ2);

		//Act
		SerialisationEquivalenceByGraphNaiv equivalence = new SerialisationEquivalenceByGraphNaiv();
		//Assert

		Assert.assertTrue(equivalence.isEquivalent(lstEQ));
		Assert.assertFalse(equivalence.isEquivalent(lstNotEQ));
	}
	
	public static void createGraphsEqNaiv(SerialisationGraph out_graph1, SerialisationGraph out_graph2) {
		var a = new Extension<DungTheory>();
		a.add(new Argument("a"));
		var b = new Extension<DungTheory>();
		b.add(new Argument("b"));
		var c = new Extension<DungTheory>();
		c.add(new Argument("c"));
		var d = new Extension<DungTheory>();
		d.add(new Argument("a"));
		var e = new Extension<DungTheory>();
		e.add(new Argument("b"));
		var f = new Extension<DungTheory>();
		f.add(new Argument("c"));
	
		out_graph1.add(a);
		out_graph1.add(b);
		out_graph1.add(c);
		
		out_graph2.add(d);
		out_graph2.add(e);
		out_graph2.add(f);
		
	}
	
	public static void createGraphsNotEqNaiv(SerialisationGraph out_graph1, SerialisationGraph out_graph2) {
		var a = new Extension<DungTheory>();
		a.add(new Argument("a"));
		var b = new Extension<DungTheory>();
		b.add(new Argument("b"));
		var c = new Extension<DungTheory>();
		c.add(new Argument("c"));
	
		out_graph1.add(a);
		out_graph1.add(b);
		out_graph1.add(c);
		
		out_graph2.add(a);

	}

}
