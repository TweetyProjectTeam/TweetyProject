/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package test;

import net.sf.tweety.logicprogramming.asplibrary.solver.DLVComplex;
import net.sf.tweety.logicprogramming.asplibrary.syntax.Atom;
import net.sf.tweety.logicprogramming.asplibrary.syntax.StdTerm;
import siwo.simulator.*;
import siwo.syntax.*;

/**
 * test application, showing transformation of
 * a siwo program to an ordinary disjunctive elp.
 * 
 * @author Thomas Vengels
 *
 */
public class SiWoExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		// asp solver
		// you should set up the solver path correctly
		// in order to get this working
		// note: not required here
		DLVComplex dlx = new DLVComplex("c:/logic/dlx.exe");
				
		// simulator used to calculate transition
		DLVSimulator sim = new DLVSimulator(dlx);
		
		// action handler
		ActionHandler ah = new SingleActionHandler();
		sim.addObserver(ah);

		// test world 1		
		WorldState w = WorldState.loadFrom("./examples/example1.siwo");
		ah.addAction("alice", new Atom("goto(2)"));
		ah.addAction("bob", new Atom("goto(2)"));
		sim.getProgram(w).saveTo("./examples/example1.dlv.lp");
				
		// test world 2
		// no actions here
		ah.clearAcions();
		WorldState w2 = WorldState.loadFrom("./examples/example2.siwo");
		sim.getProgram(w2).saveTo("./examples/example2.dlv.lp");
	}
}
