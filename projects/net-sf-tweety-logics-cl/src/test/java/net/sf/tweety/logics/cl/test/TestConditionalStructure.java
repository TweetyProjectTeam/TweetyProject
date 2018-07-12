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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.cl.test;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.logics.cl.semantics.ConditionalStructure;
import net.sf.tweety.logics.cl.semantics.ConditionalStructure.Generator;
import net.sf.tweety.logics.cl.syntax.Conditional;
import net.sf.tweety.logics.pl.semantics.NicePossibleWorld;
import net.sf.tweety.logics.pl.syntax.Proposition;

import org.junit.Test;

/**
 * 
 * @author Tim Janus
 */
public class TestConditionalStructure {
	private static Proposition a = new Proposition("a");
	private static Proposition b = new Proposition("b");
	
	@Test
	public void addConditionalSignature() {
		ConditionalStructure cs = new ConditionalStructure();
		Conditional cond = new Conditional(a, b);
		cs.addConditional(cond);
		
		// test if signature is created correctly:
		assertEquals(2, cs.getSignature().size());
		assertEquals(true, cs.getSignature().contains(a));
		assertEquals(true, cs.getSignature().contains(b));
	}
	
	@Test
	public void addConditionalConditionalGenerators() {
		ConditionalStructure cs = new ConditionalStructure();
		Conditional cond = new Conditional(a, b);
		cs.addConditional(cond);
		
		Map<NicePossibleWorld, Generator> map = cs.getConditionalGenerators(cond);
		
		Set<Proposition> set = new HashSet<Proposition>();
		set.add(a);
		NicePossibleWorld falsifies = new NicePossibleWorld(set, cs.getSignature());
		set.add(b);
		NicePossibleWorld verifies = new NicePossibleWorld(set, cs.getSignature());
		
		// Check if conditional data is created correctly:
		assertEquals(true, map.keySet().contains(verifies));
		assertEquals(true, map.keySet().contains(falsifies));
		
		assertEquals(map.get(verifies), Generator.CG_PLUS);
		assertEquals(map.get(falsifies), Generator.CG_MINUS);
	}
	
	@Test
	public void addConditionalWorldGenerators() {
		ConditionalStructure cs = new ConditionalStructure();
		Conditional cond = new Conditional(a, b);
		cs.addConditional(cond);
		
		Set<Proposition> set = new HashSet<Proposition>();
		set.add(a);
		NicePossibleWorld falsifies = new NicePossibleWorld(set, cs.getSignature());
		set.add(b);
		NicePossibleWorld verifies = new NicePossibleWorld(set, cs.getSignature());
		
		// test if world data is created correctly
		Map<Conditional, Generator> map = cs.getWorldGenerators(falsifies);
		assertEquals(1, map.size());
		assertEquals(Generator.CG_MINUS, map.get(cond));
		
		map = cs.getWorldGenerators(verifies);
		assertEquals(1, map.size());
		assertEquals(Generator.CG_PLUS, map.get(cond));
		
		NicePossibleWorld npw = new NicePossibleWorld(new HashSet<Proposition>(), cs.getSignature());
		assertEquals(0, cs.getWorldGenerators(npw).size());
	}
}
