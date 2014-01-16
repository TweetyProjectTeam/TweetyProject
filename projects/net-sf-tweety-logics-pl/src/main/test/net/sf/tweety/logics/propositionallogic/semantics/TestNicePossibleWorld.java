package net.sf.tweety.logics.propositionallogic.semantics;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.propositionallogic.syntax.Proposition;
import net.sf.tweety.util.Pair;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestNicePossibleWorld {
	
	static private Set<Proposition> worldProps = new HashSet<Proposition>();
	
	static private Set<Proposition> signature = new HashSet<Proposition>();
	
	private NicePossibleWorld world;
	
	@BeforeClass
	public static void init() {
		worldProps.add(new Proposition("c"));
		
		signature.add(new Proposition("b"));
		signature.add(new Proposition("c"));
		signature.add(new Proposition("d"));
		
	}
	
	@Before
	public void initWorld() {
		world = new NicePossibleWorld(worldProps, signature);
	}
	
	@Test
	public void creation() {
		assertEquals(3, world.getRepresentationStructure().size());
		assertEquals(true, world.getRepresentationStructure().contains(
				new Pair<Proposition, Boolean>(new Proposition("b"), false)));
		assertEquals(true, world.getRepresentationStructure().contains(
				new Pair<Proposition, Boolean>(new Proposition("c"), true)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void creationFailure() {
		new NicePossibleWorld(worldProps, new HashSet<Proposition>());
	}
	
	@Test
	public void signatureGrow() {
		Set<Proposition> sig = new HashSet<Proposition>(signature);
		sig.add(new Proposition("a"));
		world.setSignature(sig);
		
		assertEquals(4, world.getRepresentationStructure().size());
		assertEquals(new Pair<Proposition, Boolean>(new Proposition("a"), false), 
				world.getRepresentationStructure().iterator().next());
		assertEquals(true, world.getRepresentationStructure().contains(
				new Pair<Proposition, Boolean>(new Proposition("c"), true)));
	}
	
	@Test
	public void signatureShrink() {
		Set<Proposition> sig = new HashSet<Proposition>(signature);
		sig.remove(new Proposition("b"));
		world.setSignature(sig);
		
		assertEquals(2, world.getRepresentationStructure().size());
		assertEquals(new Pair<Proposition, Boolean>(new Proposition("c"), true), 
				world.getRepresentationStructure().iterator().next());
	}
	
	@Test
	public void invalidSignature() {
		Set<Proposition> sig = new HashSet<Proposition>(signature);
		sig.remove(new Proposition("c"));
		
		assertEquals(false, world.setSignature(sig));
	}
}
