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
import net.sf.tweety.lp.asp.parser.ASPCore2Parser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.reasoner.ASPSolver;
import net.sf.tweety.lp.asp.reasoner.DLVSolver;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.ASPAtom;
import net.sf.tweety.lp.asp.syntax.ASPLiteral;
import net.sf.tweety.lp.asp.syntax.StrictNegation;
import net.sf.tweety.lp.asp.syntax.ASPRule;
import net.sf.tweety.lp.asp.syntax.AggregateHead;
import net.sf.tweety.lp.asp.syntax.ClassicalHead;

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
public class PreferenceHandling extends CredibilityRevisionIterative<ASPRule> {
	
	/** reference to the logback logger instance */
	private Logger LOG = LoggerFactory.getLogger(PreferenceHandling.class);
	
	private int maxInt;
	
	private ASPSolver solver;
	
	public PreferenceHandling(ASPSolver solver) {
		this(solver, 5);
	}
	
	public PreferenceHandling(ASPSolver solver, int maxInt)  {
		this.solver = solver;
		this.maxInt = maxInt;
	}
	
	public void setSolver(ASPSolver solver) {
		this.solver = solver;
	}
	
	@Override
	public Program revise(Collection<ASPRule> base, Collection<ASPRule> formulas) {
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
		Program pd1 = Program.getDefaultification(p1);
		Program pd2 = Program.getDefaultification(p2);
		
		// list of conflicting rules of the defaultificated programs
		List<Pair<ASPRule, ASPRule>> conflictsDef = getConflictingRules(pd1, pd2);

		// Assumption: Index of rules in p equals index of rules pd.
		// TODO: Proof if this assumption is really true.
		List<ASPRule> pdr1 = new LinkedList<ASPRule>(pd1);
		List<ASPRule> pdr2 = new LinkedList<ASPRule>(pd2);
		List<ASPRule> pr1 = new LinkedList<ASPRule>(p1);
		List<ASPRule> pr2 = new LinkedList<ASPRule>(p2);
		Collections.sort(pdr1);
		Collections.sort(pdr2);
		Collections.sort(pr1);
		Collections.sort(pr2);
		
		List<Pair<ASPRule, ASPRule>> conflicts = new LinkedList<Pair<ASPRule,ASPRule>>();
		for(Pair<ASPRule, ASPRule> defConf : conflictsDef) {
			int index1 = pdr1.indexOf(defConf.getFirst());
			int index2 = pdr2.indexOf(defConf.getSecond());
			
			conflicts.add(new Pair<ASPRule, ASPRule>(pr1.get(index1), pr2.get(index2)));
		}
		
		// get answer sets of combined defaultificated programs.
		concat.add(pd1);
		concat.add(pd2);
		Collection<AnswerSet> asDefault;
		try {
			asDefault = solver.getModels(concat, maxInt);
		} catch (Exception e) {
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
		Set<ASPRule> toRemoveCollection = new HashSet<ASPRule>();
		for(Pair<ASPRule, ASPRule> conflict : conflicts) {
			for(AnswerSet as : asDefault) {
				Set<ASPLiteral> literals = new HashSet<ASPLiteral>();
				literals.addAll(conflict.getSecond().getLiterals());
				literals.removeAll((Collection<?>) conflict.getSecond().getConclusion());
				
				if(	as.containsAll(literals) &&
					!as.containsAll((Collection<?>) conflict.getSecond().getConclusion())) {
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
	protected static List<Pair<ASPRule, ASPRule>> getConflictingRules(Program p1, Program p2) {
		List<Pair<ASPRule, ASPRule>> reval = new LinkedList<Pair<ASPRule,ASPRule>>();
		
		Iterator<ASPRule> p1It = p1.iterator();
		while(p1It.hasNext()) {
			ASPRule r1 = p1It.next();
			if(r1.isConstraint())
				continue;
			
			// Create negated head of rule 1.
			if (r1.getConclusion() instanceof AggregateHead)
				throw new IllegalArgumentException("Only literals are allowed as rule heads in this module.");
			ASPLiteral head1 = ((ClassicalHead)r1.getConclusion()).iterator().next();
			ASPLiteral negHead1 = null;
			if(head1 instanceof ASPAtom) {
				negHead1 = new StrictNegation(head1.getAtom());
			} else if(head1 instanceof StrictNegation) {
				negHead1 = head1.getAtom();
			} else {
				throw new RuntimeException("Head Atom must be normal or strict negated.");
			}
			
			// try to find the negated head in the rules of the other program.
			Iterator<ASPRule> p2it = p2.iterator();
			while(p2it.hasNext()) {
				ASPRule r2 = p2it.next();
				if(r2.isConstraint())
					continue;
				if (r2.getConclusion() instanceof AggregateHead)
					throw new IllegalArgumentException("Only literals are allowed as rule heads in this module.");
				if(((ClassicalHead)r2.getConclusion()).iterator().next().equals(negHead1)) {
					reval.add(new Pair<ASPRule, ASPRule>(r1, r2));
				}
			}
		}
		
		return reval;
	}
	
	/*
	 * Temporary functional test method.
	 */
	public static void main(String [] args) throws ParseException {
		// Example from Mirja diplom thesis.
		String program1 = "a.\nb:-not c.";
		String program2 = "-a:-not b.";
		program1 = "sleep:-not tv_on.\nnight.\ntv_on.\nwatch_tv:-tv_on.";
		program2 = "-tv_on:-power_failure.\npower_failure.";
		
		DLVSolver dlv = new DLVSolver("/home/janus/workspace/angerona/software/test/src/main/tools/solver/asp/dlv/dlv.bin");
		
		InstantiateVisitor visitor = new InstantiateVisitor();
		ASPCore2Parser parser = new ASPCore2Parser(new StringReader(program1)); //TODO test with new parser
		Program p1 = visitor.visit(parser.Program(), null);
		parser.ReInit(new StringReader(program2));
		Program p2 = visitor.visit(parser.Program(), null);
		
		System.out.println("P1:");
		System.out.println(p1.toString()+"\n" + dlv.getModels(p1, 5) + "\n");
		
		System.out.println("P2:");
		System.out.println(p2.toString()+"\n" + dlv.getModels(p2, 5) + "\n");
		
		PreferenceHandling ph = new PreferenceHandling(dlv);
		Program r = ph.revise(p1, p2);		

		System.out.println("Revised:");
		System.out.println(r.toString()+"\n\n");
		
		System.out.println(dlv.getModels(r, 5));
	}
}
