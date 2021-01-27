package org.tweetyproject.arg.adf.semantics.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.tweetyproject.arg.adf.sat.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.acc.NegationAcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

public class LinkTypeTest {

	public static final int DEFAULT_TIMEOUT = 2000;
	
	private final LinkStrategy linkStrategy = new SatLinkStrategy(new NativeMinisatSolver());
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testAttacking() {		
		Argument c = new Argument("c");
		Argument b = new Argument("b");
		Argument a = new Argument("a");
		
		Map<Argument,AcceptanceCondition> map = new HashMap<Argument,AcceptanceCondition>();
		map.put(c, AcceptanceCondition.TAUTOLOGY);
		map.put(b, AcceptanceCondition.TAUTOLOGY);
		map.put(a, new ImplicationAcceptanceCondition(b, c));
		AbstractDialecticalFramework adf = AbstractDialecticalFramework.fromMap(map).lazy(linkStrategy).build();
		
		Link baLink = adf.link(b, a);
		assertTrue(baLink.getType() == LinkType.ATTACKING);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testSupporting() {
		Argument c = new Argument("c");
		Argument b = new Argument("b");
		Argument a = new Argument("a");
		
		Map<Argument,AcceptanceCondition> map = new HashMap<Argument,AcceptanceCondition>();
		map.put(c, AcceptanceCondition.TAUTOLOGY);
		map.put(b, AcceptanceCondition.TAUTOLOGY);
		map.put(a, new ImplicationAcceptanceCondition(b, c));
		AbstractDialecticalFramework adf = AbstractDialecticalFramework.fromMap(map).lazy(linkStrategy).build();
		
		Link caLink = adf.link(c, a);
		assertTrue(caLink.getType() == LinkType.SUPPORTING);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testDependent() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		
		Map<Argument,AcceptanceCondition> map = new HashMap<Argument,AcceptanceCondition>();
		map.put(a, AcceptanceCondition.TAUTOLOGY);
		map.put(b, AcceptanceCondition.TAUTOLOGY);
		map.put(c, new EquivalenceAcceptanceCondition(a, b));
		AbstractDialecticalFramework adf = AbstractDialecticalFramework.fromMap(map).lazy(linkStrategy).build();
		
		Link acLink = adf.link(a, c);
		assertTrue(acLink.getType() == LinkType.DEPENDENT);
		
		Link bcLink = adf.link(b,c);
		assertTrue(bcLink.getType() == LinkType.DEPENDENT);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testRedundant() {	
		Argument a = new Argument("a");
		AbstractDialecticalFramework adf = AbstractDialecticalFramework.builder()
				.lazy(linkStrategy)
				.add(a, new DisjunctionAcceptanceCondition(a, new NegationAcceptanceCondition(a)))
				.build();
		
		Link aaLink = adf.link(a,a);
		assertTrue(aaLink.getType() == LinkType.REDUNDANT);
	}
	
}
