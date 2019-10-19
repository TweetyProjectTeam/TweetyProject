package net.sf.tweety.arg.adf.semantics.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.LinkType;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.TautologyAcceptanceCondition;

public class LinkTypeTest {

	public static final int DEFAULT_TIMEOUT = 2000;
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testAttacking() {		
		Argument c = new Argument("c");
		Argument b = new Argument("b");
		Argument a = new Argument("a");
		
		Map<Argument,AcceptanceCondition> map = new HashMap<Argument,AcceptanceCondition>();
		map.put(c, new TautologyAcceptanceCondition());
		map.put(b, new TautologyAcceptanceCondition());
		map.put(a, new ImplicationAcceptanceCondition(b, c));
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework(map);
		
		Link baLink = adf.link(b, a);
		assertTrue(baLink.getLinkType() == LinkType.ATTACKING);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testSupporting() {
		Argument c = new Argument("c");
		Argument b = new Argument("b");
		Argument a = new Argument("a");
		
		Map<Argument,AcceptanceCondition> map = new HashMap<Argument,AcceptanceCondition>();
		map.put(c, new TautologyAcceptanceCondition());
		map.put(b, new TautologyAcceptanceCondition());
		map.put(a, new ImplicationAcceptanceCondition(b, c));
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework(map);
		
		Link caLink = adf.link(c, a);
		assertTrue(caLink.getLinkType() == LinkType.SUPPORTING);
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testDependent() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		
		Map<Argument,AcceptanceCondition> map = new HashMap<Argument,AcceptanceCondition>();
		map.put(a, new TautologyAcceptanceCondition());
		map.put(b, new TautologyAcceptanceCondition());
		map.put(c, new EquivalenceAcceptanceCondition(a, b));
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework(map);
		
		Link acLink = adf.link(a, c);
		assertTrue(acLink.getLinkType() == LinkType.DEPENDENT);
		
		Link bcLink = adf.link(b,c);
		assertTrue(bcLink.getLinkType() == LinkType.DEPENDENT);
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testRedundant() {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		
		Map<Argument,AcceptanceCondition> map = new HashMap<Argument,AcceptanceCondition>();
		map.put(a, new TautologyAcceptanceCondition());
		map.put(b, new TautologyAcceptanceCondition());
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework(map);
		
		Link baLink = adf.link(b,a);
		assertTrue(baLink.getLinkType() == LinkType.REDUNDANT);
	}
	
}
