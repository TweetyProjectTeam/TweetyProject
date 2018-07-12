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
package net.sf.tweety.lp.asp.beliefdynamics.revision;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.beliefdynamics.CredibilityRevisionIterative;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.solver.DLV;
import net.sf.tweety.lp.asp.solver.Solver;
import net.sf.tweety.lp.asp.solver.SolverException;
import net.sf.tweety.lp.asp.syntax.DLPAtom;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;
import net.sf.tweety.lp.asp.syntax.DLPNeg;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The implementation orients on the diploma thesis of Mirja Boehmer
 * 
 * in this class a variant of the approach
 * "A Preference-Based Framework for Updating Logic Programs" by James P.
 * Delgrande, Torsten Schaub and Hans Tompits is implemented, respectively the
 * operator *1 is used; first step: defaultification of two given programs
 * second step: computation of the answer sets of the concatenation of the two
 * defaulticated programs third step: computation of the revised program with
 * the help of these answer sets last step: computation of the answer sets of
 * the revised program
 * 
 * We are only check for conflicting rules and remove this rules with lesser priority.
 * 
 * @author Tim Janus
 **/
public class PreferenceHandling extends CredibilityRevisionIterative<Rule> {
	
	/** reference to the logback logger instance */
	private Logger LOG = LoggerFactory.getLogger(PreferenceHandling.class);
	
	private int maxInt;
	
	private Solver solver;
	
	public PreferenceHandling(Solver solver) {
		this(solver, 5);
	}
	
	public PreferenceHandling(Solver solver, int maxInt)  {
		this.solver = solver;
		this.maxInt = maxInt;
	}
	
	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	@Override
	public Program revise(Collection<Rule> base, Collection<Rule> formulas) {
		Program p1 = null;
		Program p2 = null;
		if(base instanceof Program) {
			p1 = (Program)base;
		} else {
			p1 = new Program();
			p1.addAll(base);
		}
		
		if(formulas instanceof Program) {
			p2 = (Program)formulas;
		} else {
			p2 = new Program();
			p2.addAll(formulas);
		}
		
		Program combined = new Program();
		Program concat = new Program();
		
		// Defaultification of given programs.
		Program pd1 = Program.defaultification(p1);
		Program pd2 = Program.defaultification(p2);
		
		// list of conflicting rules of the defaultificated programs
		List<Pair<Rule, Rule>> conflictsDef = getConflictingRules(pd1, pd2);

		// Assumption: Index of rules in p equals index of rules pd.
		// TODO: Proof if this assumption is really true.
		List<Rule> pdr1 = new LinkedList<Rule>(pd1);
		List<Rule> pdr2 = new LinkedList<Rule>(pd2);
		List<Rule> pr1 = new LinkedList<Rule>(p1);
		List<Rule> pr2 = new LinkedList<Rule>(p2);
		Collections.sort(pdr1);
		Collections.sort(pdr2);
		Collections.sort(pr1);
		Collections.sort(pr2);
		
		List<Pair<Rule, Rule>> conflicts = new LinkedList<Pair<Rule,Rule>>();
		for(Pair<Rule, Rule> defConf : conflictsDef) {
			int index1 = pdr1.indexOf(defConf.getFirst());
			int index2 = pdr2.indexOf(defConf.getSecond());
			
			conflicts.add(new Pair<Rule, Rule>(pr1.get(index1), pr2.get(index2)));
		}
		
		// get answer sets of combined defaultificated programs.
		concat.add(pd1);
		concat.add(pd2);
		AnswerSetList asDefault;
		try {
			asDefault = solver.computeModels(concat, maxInt);
		} catch (SolverException e) {
			LOG.error("Cannot solve combined program:\n{}", concat.toString());
			e.printStackTrace();
			return null;
		}
		
		/*
		System.out.println("Default Program: " + concat);
		System.out.println("Default answer: " + asDefault);
		System.out.println("Conflicts: " + conflicts);
		*/
		
		// proof which rules can be removed from concat:
		// Let the rule R be the higher prioritized Rule in a conflict pair.
		// First test if the body of R is in the answer set but the head is not in the answer set.
		// If this is true mark the lower prioritized rule for remove.
		Set<Rule> toRemoveCollection = new HashSet<Rule>();
		for(Pair<Rule, Rule> conflict : conflicts) {
			for(AnswerSet as : asDefault) {
				Set<DLPLiteral> literals = new HashSet<DLPLiteral>();
				literals.addAll(conflict.getSecond().getLiterals());
				literals.removeAll(conflict.getSecond().getConclusion());
				
				if(	as.containsAll(literals) &&
					!as.containsAll(conflict.getSecond().getConclusion())) {
					toRemoveCollection.add(conflict.getFirst());
				}
			}
		}
		
		
	//	System.out.println("To Remove: " + toRemoveCollection);
		
		combined.add(p1);
		combined.add(p2);
		combined.removeAll(toRemoveCollection);
		
		return combined;
	}
	
	/**
	 * Helper method: Finds all pairs of conflicting rules in program p1 and p2.
	 * A conflicting rule is a pair(r1, r2 | r1 in p1, r2 in p2, H(r1) = -H(r2)) 
	 * @param p1	The first program 
	 * @param p2	The second program
	 * @return		A list of all pairs representing the conflicting rules in p1 and p2.
	 */
	protected static List<Pair<Rule, Rule>> getConflictingRules(Program p1, Program p2) {
		List<Pair<Rule, Rule>> reval = new LinkedList<Pair<Rule,Rule>>();
		
		Iterator<Rule> p1It = p1.iterator();
		while(p1It.hasNext()) {
			Rule r1 = p1It.next();
			if(r1.isConstraint())
				continue;
			
			// Create negated head of rule 1.
			DLPLiteral head1 = r1.getConclusion().iterator().next();
			DLPLiteral negHead1 = null;
			if(head1 instanceof DLPAtom) {
				negHead1 = new DLPNeg(head1.getAtom());
			} else if(head1 instanceof DLPNeg) {
				negHead1 = head1.getAtom();
			} else {
				throw new RuntimeException("Head Atom must be normal or strict negated.");
			}
			
			// try to find the negated head in the rules of the other program.
			Iterator<Rule> p2it = p2.iterator();
			while(p2it.hasNext()) {
				Rule r2 = p2it.next();
				if(r2.isConstraint())
					continue;
				if(r2.getConclusion().iterator().next().equals(negHead1)) {
					reval.add(new Pair<Rule, Rule>(r1, r2));
				}
			}
		}
		
		return reval;
	}
	
	/*
	 * Temporary functional test method.
	 */
	public static void main(String [] args) throws SolverException, ParseException {
		// Example from Mirja diplom thesis.
		String program1 = "a.\nb:-not c.";
		String program2 = "-a:-not b.";
		program1 = "sleep:-not tv_on.\nnight.\ntv_on.\nwatch_tv:-tv_on.";
		program2 = "-tv_on:-power_failure.\npower_failure.";
		
		DLV clingo = new DLV("/home/janus/workspace/angerona/software/test/src/main/tools/solver/asp/dlv/dlv.bin");
		
		InstantiateVisitor visitor = new InstantiateVisitor();
		ASPParser parser = new ASPParser(new StringReader(program1));
		Program p1 = visitor.visit(parser.Program(), null);
		parser.ReInit(new StringReader(program2));
		Program p2 = visitor.visit(parser.Program(), null);
		
		System.out.println("P1:");
		System.out.println(p1.toString()+"\n" + clingo.computeModels(p1, 5) + "\n");
		
		System.out.println("P2:");
		System.out.println(p2.toString()+"\n" + clingo.computeModels(p2, 5) + "\n");
		
		PreferenceHandling ph = new PreferenceHandling(clingo);
		Program r = ph.revise(p1, p2);		

		System.out.println("Revised:");
		System.out.println(r.toString()+"\n\n");
		
		System.out.println(clingo.computeModels(r, 5));
	}
}
