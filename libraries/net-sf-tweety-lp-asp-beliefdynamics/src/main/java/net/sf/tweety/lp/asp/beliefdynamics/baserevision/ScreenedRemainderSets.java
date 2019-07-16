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
package net.sf.tweety.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.reasoner.ASPSolver;
import net.sf.tweety.lp.asp.reasoner.DLVSolver;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.ASPRule;


/**
 * This class represents the set of Screened Consistent Remainder Sets as 
 * defined in [1]. A screened remainder set X of P regarding a 
 * set of sentences R \subseteq P is a set X s.t. 
 *  (1) R \subseteq X \subseteq P,
 *  (2) X is consistent and
 *  (3) there is no proper superset X' of X in P that is also consistent.
 * 
 * [1] Kruempelmann, Patrick und Gabriele Kern-Isberner: 
 * 	Belief Base Change Operations for Answer Set Programming. 
 *  In: Cerro, Luis Farinas, Andreas Herzig und Jerome Mengin (Herausgeber):
 *  Proceedings of the 13th European conference on Logics in Artificial 
 *  Intelligence, Band 7519, Seiten 294-306, Toulouse, France, 2012. 
 *  Springer Berlin Heidelberg.
 *  
 * @author Sebastian Homann
 *
 */
public class ScreenedRemainderSets extends RemainderSets<ASPRule> {
	private static final long serialVersionUID = -9146903242327808522L;
	
	private Program program;
	private Program screen;
	private ASPSolver solver;
	
	/**
	 * Creates a new set of screened remainder sets of program p that all contain
	 * the screened rules r. An asp-solver is used to calculate the consistency
	 * of remainder set candidates.
	 * 
	 * @param p an asp-program for which the screened remainder sets are calculated
	 * @param r an asp-program representing the set of rules, that have to be contained in every remainder set. Has to be a subset of p
	 * @param solver an asp-solver
	 */
	public ScreenedRemainderSets(Program p, Program r, ASPSolver solver) {
		if(!p.containsAll(r)) {
			throw new IllegalArgumentException("r has to be a subset of p");
		}
		this.program = p.clone();
		this.screen = r.clone();
		this.solver = solver;
		if(!isConsistent(r)) {
			return;
		}
		Set<Program> candidates = calculateRemainderSetCandidates(p);
		// remove non-maximal candidates
		for(Program candidate : candidates) {
			boolean isMaximal = true;
			for(Program check : candidates) {
				if(isProperSubset(candidate, check)) {
					isMaximal = false;
					break;
				}
			}
			if(isMaximal) {
				this.add(candidate);
			}
		}
	}
	
	/**
	 * Returns the original program p for which this set represents the
	 * set of remainder sets.
	 * 
	 * @return an elp program.
	 */
	public Program getSourceBeliefBase() {
		return program;
	}
	
	/**
	 * Returns the screened rules that are contained in every remainder set.
	 * 
	 * @return an elp program.
	 */
	public Program getScreen() {
		return screen;
	}
	
	/**
	 * Returns true iff program p is a proper (strict) subset of program q
	 * @param p a program
	 * @param q another program
	 * @return true iff program p is a proper (strict) subset of program q
	 */
	private boolean isProperSubset(Program p, Program q) {
		return q.containsAll(p) && (! p.containsAll(q) );
	}
	
	/**
	 * Recursively calculates consistent subsets of p. This is slightly faster
	 * than bruteforce calculating all possible combinations, as consistent subsets are
	 * pruned.
	 * @param p a program
	 */
	private Set<Program> calculateRemainderSetCandidates(Program p) {
		Set<Program> result = new HashSet<Program>();
		if(isConsistent(p)) {
			result.add(p);
			return result;
		}
		
		Program toRemove = p.clone();
		toRemove.removeAll(screen);
		for(ASPRule remove : toRemove) {
			Program candidate = p.clone();
			candidate.remove(remove);
			result.addAll(calculateRemainderSetCandidates(candidate));
		}
		return result;
	}
	
	private boolean isConsistent(Program p) {
		return !solver.getModels(p).isEmpty();
	}
	
	/**
	 * Returns this set of remainder sets as a collection of programs where
	 * each program contains exactly the rules of one remainder set.
	 * 
	 * @return a collection of programs.
	 */
	public Collection<Program> asPrograms() {
		Set<Program> result = new HashSet<Program>();
		for(Collection<ASPRule> remainder : this) {
			result.add(new Program(remainder));
		}
		return result;
	}
	
	/**
	 * Simple test case taken from [1]
	 * @param args some arguments
	 * @throws ParseException if parsing failed
	 */
	public static void main(String[] args) throws ParseException {
		String input = "a :- b.\n -a. \n b. \n :- not -a, not b.";
		
		//TODO: replace
		String pathToSolver = "";
		ASPSolver solver = new DLVSolver(pathToSolver);
		
		Program p = ASPCore2Parser.parseProgram(input); //TODO test with new parser
		ScreenedRemainderSets srs = new ScreenedRemainderSets(p, new Program(), solver);
		System.out.println("P = " + p + "\n\nScreened Remainder Sets: " + srs.size());
		int i = 1;
		for(Collection<ASPRule> remainder : srs) {
			System.out.println("\n" + i++ + ". Remainder Set:\n" + new Program(remainder));
		}
	}
}
