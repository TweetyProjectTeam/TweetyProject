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

import org.tweetyproject.arg.dung.analysis.NonGroundedCountInconsistencyMeasure;
import org.tweetyproject.arg.dung.analysis.PreferredCountInconsistencyMeasure;
import org.tweetyproject.arg.dung.analysis.UnstableCountInconsistencyMeasure;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * @author Timothy Gillespie
 *
 */
public class ExtensionInconsistencyMeasure {

	static Argument a1 = new Argument("a1");
	static Argument a2 = new Argument("a2");
	static Argument a3 = new Argument("a3");
	static Argument a4 = new Argument("a4");
	static Argument a5 = new Argument("a5");
	static Argument a6 = new Argument("a6");
	static Argument a7 = new Argument("a7");
	static Argument a8 = new Argument("a8");
	
	// Example used by A. Hunter in "Measuring Inconsistency in Argument Graphs" (2017)
	static DungTheory hunterExample15 = new DungTheory();
	
	/**
	 * *description missing*
	 * @throws java.lang.Exception *description missing*
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		hunterExample15.add(a1);
		hunterExample15.add(a2);
		hunterExample15.add(a3);
		hunterExample15.add(a4);
		hunterExample15.add(a5);
		hunterExample15.add(a6);
		hunterExample15.add(a7);
		hunterExample15.add(a8);
		
		hunterExample15.addAttack(a1, a2);
		hunterExample15.addAttack(a2, a3);
		hunterExample15.addAttack(a3, a1);
		
		hunterExample15.addAttack(a4, a5);
		hunterExample15.addAttack(a5, a4);
		
		hunterExample15.addAttack(a6, a7);
		hunterExample15.addAttack(a7, a8);
		
	}

	/**
	 * *description missing*
	 */
	@Test
	public void preferredCountInconsistencyMeasure() {
		Double preferredCount = new PreferredCountInconsistencyMeasure<DungTheory>().inconsistencyMeasure(hunterExample15);
		// Deviation from the paper since it seems it is supposed to be 1 considering there are 2 preferred extensions from which we must subtract 1.
		assertEquals(1d, preferredCount, 0.0);
	}
	
	/**
	 * *description missing*
	 */
	@Test
	public void nonGroundedCountInconsistencyMeasure() {
		Double nonGroundedCount = new NonGroundedCountInconsistencyMeasure<DungTheory>().inconsistencyMeasure(hunterExample15);
		assertEquals(5d, nonGroundedCount, 0.0);
	}
	
	/**
	 * *description missing*
	 */
	@Test
	public void unstableCountInconsistencyMeasure() {
		Double unstableCount = new UnstableCountInconsistencyMeasure<DungTheory>().inconsistencyMeasure(hunterExample15);
		// Deviates from the paper since removing A3 will result in a stable extension with {A1, A4, A6, A8}
		assertEquals(1d, unstableCount, 0.0);
	}
	
	
	

}
