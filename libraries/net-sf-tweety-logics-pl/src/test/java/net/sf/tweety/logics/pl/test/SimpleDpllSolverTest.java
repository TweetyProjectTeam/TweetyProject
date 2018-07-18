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
 *  Copyright 2016-2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.parser.DimacsParser;
import net.sf.tweety.logics.pl.sat.SimpleDpllSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;

public class SimpleDpllSolverTest {
	
	private String[] files = {
			"../../examples/pl/dimacs_ex1.cnf",
			"../../examples/pl/dimacs_ex2.cnf",
			"../../examples/pl/dimacs_ex3.cnf",
			"../../examples/pl/dimacs_ex4.cnf",
			"../../examples/pl/dimacs_ex5.cnf"
	};
	
	private boolean[] sat = {
			true,
			false,
			true,
			false,
			true
	};
	
	@Test
	public void SolverTest() throws FileNotFoundException, ParserException, IOException {
		DimacsParser parser = new DimacsParser();
		SimpleDpllSolver solver = new SimpleDpllSolver();
		for(int i = 0; i < this.files.length; i++) {
			PlBeliefSet bs = parser.parseBeliefBaseFromFile(this.files[i]);
			assertEquals(solver.isConsistent(bs), this.sat[i]);			
		}
	}
	
}
