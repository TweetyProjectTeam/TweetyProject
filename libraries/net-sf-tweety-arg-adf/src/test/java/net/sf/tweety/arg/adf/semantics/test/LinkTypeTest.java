package net.sf.tweety.arg.adf.semantics.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import net.sf.tweety.arg.adf.sat.NativeMinisatSolver;
import net.sf.tweety.arg.adf.semantics.link.Link;
import net.sf.tweety.arg.adf.semantics.link.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.link.LinkType;
import net.sf.tweety.arg.adf.semantics.link.SatLinkStrategy;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.NegationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

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
