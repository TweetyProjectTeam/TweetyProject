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
