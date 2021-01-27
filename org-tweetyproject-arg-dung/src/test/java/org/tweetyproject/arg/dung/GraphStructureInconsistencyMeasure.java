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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import org.tweetyproject.arg.dung.analysis.CycleCountInconsistencyMeasure;
import org.tweetyproject.arg.dung.analysis.DrasticInconsistencyMeasure;
import org.tweetyproject.arg.dung.analysis.InSumInconsistencyMeasure;
import org.tweetyproject.arg.dung.analysis.WeightedComponentCountInconsistencyMeasure;
import org.tweetyproject.arg.dung.analysis.WeightedCycleCountInconsistencyMeasure;
import org.tweetyproject.arg.dung.analysis.WeightedInSumInconsistencyMeasure;
import org.tweetyproject.arg.dung.analysis.WeightedOutSumInconsistencyMeasure;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * @author Timothy Gillespie
 *
 */
public class GraphStructureInconsistencyMeasure {

	static Argument a = new Argument("a");
	static Argument b = new Argument("b");
	static Argument c = new Argument("c");
	static Argument d = new Argument("d");
	static Argument e = new Argument("e");
	static Argument f = new Argument("f");
	static Argument g = new Argument("g");
	
	static DungTheory attackFreeFramework = new DungTheory();
	static DungTheory twoComponentFramework = new DungTheory();
	
	// Examples used by A. Hunter in "Measuring Inconsistency in Argument Graphs" (2017)
	static DungTheory hunterExample10 = new DungTheory();
	
	static DungTheory hunterExample13G1 = new DungTheory();
	static DungTheory hunterExample13G2 = new DungTheory();
	
	static DungTheory hunterExample14G1 = new DungTheory();
	static DungTheory hunterExample14G2 = new DungTheory();
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		attackFreeFramework.add(a);
		attackFreeFramework.add(b);
		attackFreeFramework.add(c);
		
		
		twoComponentFramework.add(a);
		twoComponentFramework.add(b);
		twoComponentFramework.add(c);
		
		twoComponentFramework.add(d);
		twoComponentFramework.add(e);
		twoComponentFramework.add(f);
		twoComponentFramework.add(g);
		
		twoComponentFramework.addAttack(a, b);
		twoComponentFramework.addAttack(b, c);
		
		twoComponentFramework.addAttack(d, e);
		twoComponentFramework.addAttack(f, e);
		twoComponentFramework.addAttack(f, g);
		twoComponentFramework.addAttack(e, g);
		
		
		hunterExample10.add(a);
		hunterExample10.add(b);
		hunterExample10.add(c);
		
		hunterExample10.addAttack(a, a);
		hunterExample10.addAttack(b, b);
		hunterExample10.addAttack(c, c);
		
		hunterExample10.addAttack(a, b);
		hunterExample10.addAttack(a, c);
		
		hunterExample10.addAttack(b, a);
		hunterExample10.addAttack(b, c);
		
		hunterExample10.addAttack(c, a);
		hunterExample10.addAttack(c, b);
		
		
		hunterExample13G1.add(a);
		hunterExample13G1.add(b);
		hunterExample13G1.add(c);
		hunterExample13G1.add(d);
		
		hunterExample13G1.addAttack(a, d);
		hunterExample13G1.addAttack(b, d);
		hunterExample13G1.addAttack(c, d);
	
	
		hunterExample13G2.add(a);
		hunterExample13G2.add(b);
		hunterExample13G2.add(c);
		
		hunterExample13G2.addAttack(a, c);
		hunterExample13G2.addAttack(b, c);
		
		
		hunterExample14G1.add(a);
		hunterExample14G1.add(b);
		hunterExample14G1.add(c);
		hunterExample14G1.add(d);
		
		hunterExample14G1.addAttack(a, b);
		hunterExample14G1.addAttack(b, c);
		hunterExample14G1.addAttack(c, d);
		hunterExample14G1.addAttack(d, a);
		
		
		hunterExample14G2.add(a);
		hunterExample14G2.add(b);
		hunterExample14G2.add(c);
		
		hunterExample14G2.addAttack(a, b);
		hunterExample14G2.addAttack(b, c);
		hunterExample14G2.addAttack(c, a);
		
	}

	@Test
	public void drasticWithAttackReturnsOne() {
		
		DrasticInconsistencyMeasure<DungTheory> tester = new DrasticInconsistencyMeasure<DungTheory>();
		
		Double drasticActual = tester.inconsistencyMeasure(hunterExample13G1);
		assertEquals(1.0d, drasticActual, 0.0);
	}
	
	@Test
	public void drasticWithNoAttackReturnsZero() {
		
		DrasticInconsistencyMeasure<DungTheory> tester = new DrasticInconsistencyMeasure<DungTheory>();
		
		Double drasticActual = tester.inconsistencyMeasure(attackFreeFramework);
		assertEquals(0.0d, drasticActual, 0.0);
	}
	
	@Test
	public void inSumWithThreeEdgesReturnsThree() {
		
		InSumInconsistencyMeasure<DungTheory> tester = new InSumInconsistencyMeasure<DungTheory>();
		
		Double inSumActual = tester.inconsistencyMeasure(hunterExample13G1);
		assertEquals(3.0d, inSumActual, 0.0);
	}
	
	@Test
	public void inSumWithNoAttackReturnsZero() {
		
		InSumInconsistencyMeasure<DungTheory> tester = new InSumInconsistencyMeasure<DungTheory>();
		
		Double inSumActual = tester.inconsistencyMeasure(attackFreeFramework);
		assertEquals(0.0d, inSumActual, 0.0);
	}
	
	@Test
	public void inSumWorksWithCycles() {
		
		InSumInconsistencyMeasure<DungTheory> tester = new InSumInconsistencyMeasure<DungTheory>();
		
		Double inSumActual = tester.inconsistencyMeasure(hunterExample14G1);
		assertEquals(4.0d, inSumActual, 0.0);
	}
	
	@Test
	public void weightedInSumExample1() {
		
		WeightedInSumInconsistencyMeasure<DungTheory> tester = new WeightedInSumInconsistencyMeasure<DungTheory>();
		
		Double weightedInSumActual = tester.inconsistencyMeasure(hunterExample13G1);
		assertEquals((1.0 / 3.0) , weightedInSumActual, 0.0);
	}
	
	@Test
	public void weightedInSumExample2() {
		
		WeightedInSumInconsistencyMeasure<DungTheory> tester = new WeightedInSumInconsistencyMeasure<DungTheory>();
		
		Double weightedInSumActual = tester.inconsistencyMeasure(hunterExample13G2);
		assertEquals((1.0 / 2.0) , weightedInSumActual, 0.0);
	}
	
	@Test
	public void weightedInSumWorkedWithCycles() {
		
		WeightedInSumInconsistencyMeasure<DungTheory> tester = new WeightedInSumInconsistencyMeasure<DungTheory>();
		
		Double weightedInSumActual = tester.inconsistencyMeasure(hunterExample14G1);
		assertEquals(4d , weightedInSumActual, 0.0);
	}

	@Test
	public void weightedOutSumExample1() {
		
		WeightedOutSumInconsistencyMeasure<DungTheory> tester = new WeightedOutSumInconsistencyMeasure<DungTheory>();
		
		Double weightedOutSumActual = tester.inconsistencyMeasure(hunterExample13G1);
		assertEquals(3d, weightedOutSumActual, 0.0);
	}
	
	@Test
	public void weightedOutSumExample2() {
		
		WeightedOutSumInconsistencyMeasure<DungTheory> tester = new WeightedOutSumInconsistencyMeasure<DungTheory>();
		
		Double weightedOutSumActual = tester.inconsistencyMeasure(hunterExample13G2);
		assertEquals(2d, weightedOutSumActual, 0.0);
	}
	
	@Test
	public void weightedOutSumWorkedWithCycles() {
		
		WeightedOutSumInconsistencyMeasure<DungTheory> tester = new WeightedOutSumInconsistencyMeasure<DungTheory>();
		
		Double weightedOutSumActual = tester.inconsistencyMeasure(hunterExample14G1);
		assertEquals(4d, weightedOutSumActual, 0.0);
	}
	
	@Test
	public void weightedComponentCountExample1() {
		
		WeightedComponentCountInconsistencyMeasure<DungTheory> tester = new WeightedComponentCountInconsistencyMeasure<DungTheory>();
		
		Double weightedComponentCount = tester.inconsistencyMeasure(hunterExample13G1);
		assertEquals(9d, weightedComponentCount, 0.0);
	}
	
	@Test
	public void weightedComponentCountExample2() {
			
			WeightedComponentCountInconsistencyMeasure<DungTheory> tester = new WeightedComponentCountInconsistencyMeasure<DungTheory>();
			
			Double weightedComponentCount = tester.inconsistencyMeasure(hunterExample13G2);
			assertEquals(4d, weightedComponentCount, 0.0);
		}
	
	@Test
	public void weightedComponentCountWorksWithCycles() {
		
		WeightedComponentCountInconsistencyMeasure<DungTheory> tester = new WeightedComponentCountInconsistencyMeasure<DungTheory>();
		
		Double weightedComponentCount = tester.inconsistencyMeasure(hunterExample14G1);
		assertEquals(9d, weightedComponentCount, 0.0);
	}
	
	@Test
	public void weightedComponentCountWorksWithTwoComponent() {
		
		WeightedComponentCountInconsistencyMeasure<DungTheory> tester = new WeightedComponentCountInconsistencyMeasure<DungTheory>();
		
		Double weightedComponentCount = tester.inconsistencyMeasure(twoComponentFramework);
		assertEquals(13d, weightedComponentCount, 0.0);
	}
	
	@Test
	public void cycleCountExample1() {
		
		CycleCountInconsistencyMeasure<DungTheory> tester = new CycleCountInconsistencyMeasure<DungTheory>();
		
		Double cycleCount = tester.inconsistencyMeasure(hunterExample13G1);
		assertEquals(0d, cycleCount, 0.0);
	}
	
	@Test
	public void cycleCountExample2() {
		
		CycleCountInconsistencyMeasure<DungTheory> tester = new CycleCountInconsistencyMeasure<DungTheory>();
		
		Double cycleCount = tester.inconsistencyMeasure(hunterExample13G2);
		assertEquals(0d, cycleCount, 0.0);
	}
	
	@Test
	public void cycleCountExample3() {
		
		CycleCountInconsistencyMeasure<DungTheory> tester = new CycleCountInconsistencyMeasure<DungTheory>();
		
		Double cycleCount = tester.inconsistencyMeasure(hunterExample14G1);
		assertEquals(1d, cycleCount, 0.0);
	}
	
	@Test
	public void cycleCountExample4() {
		
		CycleCountInconsistencyMeasure<DungTheory> tester = new CycleCountInconsistencyMeasure<DungTheory>();
		
		Double cycleCount = tester.inconsistencyMeasure(hunterExample14G2);
		assertEquals(1d, cycleCount, 0.0);
	}
	
	@Test
	public void cycleCountExample5() {
		
		CycleCountInconsistencyMeasure<DungTheory> tester = new CycleCountInconsistencyMeasure<DungTheory>();
		
		Double cycleCount = tester.inconsistencyMeasure(hunterExample10);
		assertEquals(7d, cycleCount, 0.0);
	}
	
	@Test
	public void cycleCountWorksWithTwoComponents() {
		
		CycleCountInconsistencyMeasure<DungTheory> tester = new CycleCountInconsistencyMeasure<DungTheory>();
		
		Double cycleCount = tester.inconsistencyMeasure(twoComponentFramework);
		assertEquals(0d, cycleCount, 0.0);
	}
	
	@Test
	public void weightedCycleCountExample1() {
		
		WeightedCycleCountInconsistencyMeasure<DungTheory> tester = new WeightedCycleCountInconsistencyMeasure<DungTheory>();
		
		Double weightedCycleCount = tester.inconsistencyMeasure(hunterExample13G1);
		assertEquals(0d, weightedCycleCount, 0.0);
	}
	
	@Test
	public void weightedCycleCountExample2() {
		
		WeightedCycleCountInconsistencyMeasure<DungTheory> tester = new WeightedCycleCountInconsistencyMeasure<DungTheory>();
		
		Double weightedCycleCount = tester.inconsistencyMeasure(hunterExample13G2);
		assertEquals(0d, weightedCycleCount, 0.0);
	}
	
	@Test
	public void weightedCycleCountExample3() {
		
		WeightedCycleCountInconsistencyMeasure<DungTheory> tester = new WeightedCycleCountInconsistencyMeasure<DungTheory>();
		
		Double weightedCycleCount = tester.inconsistencyMeasure(hunterExample14G1);
		assertEquals(1.0/4.0, weightedCycleCount, 0.0);
	}
	
	@Test
	public void weightedCycleCountExample4() {
		
		WeightedCycleCountInconsistencyMeasure<DungTheory> tester = new WeightedCycleCountInconsistencyMeasure<DungTheory>();
		
		Double weightedCycleCount = tester.inconsistencyMeasure(hunterExample14G2);
		assertEquals(1.0/3.0, weightedCycleCount, 0.0);
	}
	
	@Test
	public void weightedCycleCountExample5() {
		
		WeightedCycleCountInconsistencyMeasure<DungTheory> tester = new WeightedCycleCountInconsistencyMeasure<DungTheory>();
		
		Double weightedCycleCount = tester.inconsistencyMeasure(hunterExample10);
		assertEquals((29.0/6.0), weightedCycleCount, 0.0);
	}
	
	@Test
	public void weightedCycleCountWorksWithTwoComponents() {
		
		WeightedCycleCountInconsistencyMeasure<DungTheory> tester = new WeightedCycleCountInconsistencyMeasure<DungTheory>();
		
		Double weightedCycleCount = tester.inconsistencyMeasure(twoComponentFramework);
		assertEquals(0d, weightedCycleCount, 0.0);
	}
}
