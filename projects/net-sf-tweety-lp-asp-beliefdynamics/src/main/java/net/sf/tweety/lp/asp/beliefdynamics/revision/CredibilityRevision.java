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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.beliefdynamics.CredibilityRevisionNonIterative;
import net.sf.tweety.lp.asp.semantics.AnswerSet;
import net.sf.tweety.lp.asp.semantics.AnswerSetList;
import net.sf.tweety.lp.asp.solver.Solver;
import net.sf.tweety.lp.asp.solver.SolverException;
import net.sf.tweety.lp.asp.syntax.Comparative;
import net.sf.tweety.lp.asp.syntax.DLPAtom;
import net.sf.tweety.lp.asp.syntax.DLPElement;
import net.sf.tweety.lp.asp.syntax.DLPLiteral;
import net.sf.tweety.lp.asp.syntax.DLPNeg;
import net.sf.tweety.lp.asp.syntax.DLPNot;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.NumberTerm;
import net.sf.tweety.logics.commons.syntax.Variable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the credibility revision approach for ASP described in
 * Kruempelmann et al. 2008.
 * It extends the NonIterativeRevision interface by providing methods
 * to receive the last calculated answer sets and it's projection.
 * Internally the prefixes 'c__' 'neg_c' are used to prefix constants and the
 * prefix 'p__' is used to prefix literals of the extended alphabet of the 
 * credibility program.
 * 
 * @author Tim Janus
 */
public class CredibilityRevision extends CredibilityRevisionNonIterative<Rule> {

	/**
	 * interface defines a method to process the extended answer sets of the 
	 * credibility revision to generate the new ELP.
	 * @author Tim Janus
	 */
	public static interface AnswersetProcessing {
		/**
		 * The method takes the list of ordered programs and the answer set of the
		 * credibility logic program and calculates a revided program
		 * @param orderedPrograms	List of programs used for revision process
		 * @param answersets		The answer sets processed by the credibility version of the program
		 * @return					The revided program
		 */
		Program process(List<Program> orderedPrograms, AnswerSetList answersets);
	}
	
	/**
	 * This class implements a default behavior for the processing of the answer sets
	 * of the credibility logic programs.
	 * @remark 	It does not support the generation of two different programs. If two programs
	 * 			are possible the first answer set is used for revision.
	 * @author Tim Janus
	 *
	 */
	public static class DefaultBehavior implements AnswersetProcessing {
		/** reference to the logback logger instance */
		private static Logger LOG = LoggerFactory.getLogger(DefaultBehavior.class);
		
		@Override
		public Program process(List<Program> orderedPrograms, AnswerSetList answersets) {
			if(answersets.size() > 1) {
				LOG.warn("The actual version of credibility revision for ASP does not support multiple program versions and uses the first answer set.");
			}
			AnswerSet as = answersets.get(0);
			
			// gather indicies of rule which are rejected:
			List<Integer> indicies = new LinkedList<Integer>();
			Set<DLPLiteral> literals = as.getLiteralsWithName("p__rej");
			for(DLPLiteral l : literals) {
				DLPAtom atom = (DLPAtom)l;
				NumberTerm nt = (NumberTerm)atom.getTerm(1);
				indicies.add(nt.get());
			}
			Collections.sort(indicies);

			// create the new program by adding all rules of all programs which are not rejected:
			int countIndex = 0;
			int indexIndex = 0;
			int removeRuleIndex = indexIndex < indicies.size() ? indicies.get(indexIndex) : -1;
			Program reval = new Program();
			for(Program p : orderedPrograms) {
				for(Rule r : p) {
					if(countIndex != removeRuleIndex) {
						reval.add(new Rule(r));
					} else {
						++indexIndex;
						removeRuleIndex = indexIndex < indicies.size() ? indicies.get(indexIndex) : -1;
					}
					++countIndex;
				}
			}
			
			return reval;
		}
		
	}
	
	private AnswersetProcessing processing = new DefaultBehavior();
	
	/** a map used during the creation of the credibility logic program */
	private Map<DLPLiteral, String> literalMap = new HashMap<DLPLiteral, String>();
	
	/** a prefix used to mark constants which represent the id of negative literals. */
	private String negConstantPrefix = "nc__";
	
	/** a prefix used to mark constants which represents the id of positive literals. */
	private String constantPrefix = "c__";
	
	/** a prefix used to mark the prioritzed version of predicates */
	private String predCredibilityPrefix = "p__";
	
	/** a rule index counter used during the creation of the credibility logic program */
	private int ruleIndex = 0;
	
	/** reference to the solver used for the answer set generation */
	private Solver solver = null;
	
	private AnswerSetList lastAnswersets;
	
	private AnswerSetList lastProjectedAnswersets;
	
	private int maxInt;
	
	/** Default Ctor: Do not forget to set the solver */
	public CredibilityRevision() {}
	
	/**
	 * Ctor: setting the solver at construction time
	 * @param solver	Reference to the solver used for answer set generation.
	 */
	public CredibilityRevision(Solver solver) {
		this(solver, 10);
	}
	
	public CredibilityRevision(Solver solver, int maxInt) {
		this.solver = solver;
		this.maxInt = maxInt;
	}
	
	public AnswerSetList getLastAnswerset() {
		return lastAnswersets;
	}
	
	public AnswerSetList getLastProjectedAnswerSet() {
		return lastProjectedAnswersets;
	}
	
	public void setAnswersetProcessing(AnswersetProcessing processing) {
		if(processing == null)
			throw new IllegalArgumentException("An answer set processing must be used.");
		this.processing = processing;
	}
	
	@Override
	public Program revise(List<Collection<Rule>> ol) {
		// cast to program:
		List<Program> orderedList = new LinkedList<Program>();
		for(Collection<Rule> c : ol) {
			if(c instanceof Program) {
				orderedList.add((Program)c);
			} else {
				Program p = new Program();
				p.addAll(c);
				orderedList.add(p);
			}
		}
		
		// Check if the input programs are legal
		for(Program p: orderedList) {
			if(!p.isExtendedProgram())
				throw new IllegalArgumentException("The revision only support extended logic programs.");
		}
		
		// translate the programs to the credibility logic program.
		Program credProgram = translate(orderedList);
		
		// compute the answer set of the extended alphabet
		try {
			lastAnswersets = solver.computeModels(credProgram, maxInt);
		} catch (SolverException e) {
			e.printStackTrace();
			return null;
		}
		
		projectAnswerSet();
		
		return processing.process(orderedList, lastAnswersets);
	}

	/**
	 * Projects the answer set of the credibility program to the original alphabet
	 */
	private void projectAnswerSet() {
		lastProjectedAnswersets = (AnswerSetList)lastAnswersets.clone();
		for(AnswerSet as : lastProjectedAnswersets) {
			Set<DLPLiteral> toRemove = new HashSet<DLPLiteral>();
			for(DLPLiteral literal : as) {
				if(literal.getName().startsWith(predCredibilityPrefix)) {
					toRemove.add(literal);
				}
			}	
			as.removeAll(toRemove);
		}
	}

	/**
	 * Translates the given list of programs to the credibility logic program.
	 * The index of the program in the list represents its credibility, this means
	 * the first program in the list has lesser credibility than the second.
	 * @param orderedList	The ordered list of extended logic programs which shall be translated.
	 * @return		The credibility logic program representing the revision of the given ELPs.
	 */
	public Program translate(List<Program> orderedList) {
		// make one credibility version of the given program list
		Program reval = new Program();
		for(int i=0; i<orderedList.size(); ++i) {
			// add credibility version of program:
			reval.add(translate(orderedList.get(i), i));
		}
		
		// clear helper variables
		ruleIndex = 0;
		literalMap.clear();
		return reval;
	}
	
	/**
	 * Helper method: Translates the given program
	 * @param p						Reference to the program
	 * @param programCredibility	The credibility value for the program
	 * @return	The credibility logic program of the given extendend logic program
	 */
	private Program translate(Program p, int programCredibility) {
		Program reval = new Program();
		String pcp = predCredibilityPrefix;
		
		for(Rule r : p) {
			Variable cred = new Variable("Cred");
			NumberTerm indexTerm = new NumberTerm(ruleIndex);
			
			DLPLiteral originalHead = r.getConclusion().iterator().next();
			List<DLPElement> originalBody = new LinkedList<DLPElement>();
			for(DLPElement pe : r.getPremise()) {
				DLPElement newBodyElement = (DLPElement)pe.clone();
				originalBody.add(newBodyElement);
			}
			
			String id = translateLiteral(originalHead);
			Constant headId = new Constant(id);
			DLPLiteral tHead = new DLPAtom(pcp+"lit", headId);
			
			String negId = translateLiteral((DLPLiteral)originalHead.complement());
			Constant negHeadId = new Constant(negId);
			DLPLiteral tHeadNeg = new DLPAtom(pcp+"lit", negHeadId);
			
			Variable credLow = new Variable("CredLow");
			Variable credHigh = new Variable("CredHigh");
			List<DLPElement> newBody = new LinkedList<DLPElement>();
			
			// add H(u) :- B, minr(j, Cred), not exLowerMinr(j, Cred).
			newBody.addAll(originalBody);
			newBody.add(new DLPAtom(pcp+"minr", indexTerm, cred));
			newBody.add(new DLPNot(new DLPAtom(pcp+"exLowerMinr", indexTerm, cred)));
			reval.add(new Rule(tHead.cloneWithAddedTerm(cred), newBody));
			
			// add exLowerMin(j, Cred) :- minr(j, Cred), minr(j, CredLower), Prec(CredLower, Cred).
			newBody = new LinkedList<DLPElement>();
			newBody.add(new DLPAtom(pcp+"minr", indexTerm, cred));
			newBody.add(new DLPAtom(pcp+"minr", indexTerm, credLow));
			newBody.add(new Comparative("<", credLow, cred));
			reval.add(new Rule(new DLPAtom(pcp+"exLowerMinr", indexTerm, cred), newBody));
			
			// add H :- H(u), not rej(name).
			newBody = new LinkedList<DLPElement>();
			newBody.add(tHead.cloneWithAddedTerm(cred));
			newBody.add(new DLPNot(new DLPAtom(pcp+"rej", headId, indexTerm, cred)));
			reval.add(new Rule(originalHead, newBody));
			
			
			// add minr(index, programCredibility) :- .
			reval.addFact(new DLPAtom(pcp+"minr", indexTerm, new NumberTerm(programCredibility)));
			
			// add minr(index, Cred) :- B(r), L(name, Cred), not exHigherL(Cred).
			for(DLPElement re : originalBody) {
				if(re instanceof DLPLiteral) {
					String bodyLitId = translateLiteral((DLPLiteral)re);
					DLPAtom translated = new DLPAtom(pcp + "lit", new Constant(bodyLitId));
					newBody = new LinkedList<DLPElement>();
					newBody.addAll(originalBody);
					newBody.add(translated.cloneWithAddedTerm(cred));
					newBody.add(new DLPNot(new DLPAtom(pcp+"exHigher", headId, indexTerm, cred)));
					reval.add(new Rule(new DLPAtom(pcp+"minr", indexTerm, cred), newBody));
				}
			}
			
			
			// add exHigher(name, index, Cred) :- lit(name, Cred), lit(name, CredHigh), Prec(Cred, CredHigh).
			newBody = new LinkedList<DLPElement>();
			newBody.add(tHead.cloneWithAddedTerm(cred));
			newBody.add(tHead.cloneWithAddedTerm(credHigh));
			newBody.add(new Comparative("<", cred, credHigh));
			reval.add(new Rule( new DLPAtom(pcp+"exHigher", headId, indexTerm, cred), newBody));
			
			// add rej(name, index, Cred) :- lit(name, Cred), -lit(name, CredHigh), Preceq(Cred, CredHigh).
			newBody = new LinkedList<DLPElement>();
			newBody.add(tHead.cloneWithAddedTerm(cred));
			newBody.add(tHeadNeg.cloneWithAddedTerm(credHigh));
			newBody.add(new Comparative("<=", cred, credHigh));
			newBody.add(originalHead.complement());
			reval.add(new Rule( new DLPAtom(pcp+"rej", headId, indexTerm, cred), newBody));
			
			++ruleIndex;
		}
		
		return reval;
	}
	
	/**
	 * Helper method: creates a string representing the literal and an literal
	 * representing the prioritzed version of the literal.
	 * @param lit	The unprioritzed version of the literal.
	 * @return	A pair containing the string id representing the prioritzed literal and the prioritzed literal.
	 */
	private String translateLiteral(DLPLiteral lit) {
		if(literalMap.containsKey(lit)) {
			return literalMap.get(lit);
		}
		
		String litId = lit.toString();
		litId = litId.replaceAll(" ", "").replaceAll("-", "").replaceAll(",", "_");
		
		if (lit instanceof DLPAtom) {
			litId = constantPrefix + litId;
		} else if (lit instanceof DLPNeg) {
			litId = negConstantPrefix + litId;
		} else if(! (lit instanceof DLPAtom)){
			throw new IllegalArgumentException("Parameter 'lit' has to be an Atom or a Neg.");
		}
		literalMap.put(lit, litId);
		return litId;
	}
	
	public static void main(String [] args) {
		Program p1 = new Program();
		p1.addFact(new DLPAtom("b"));
		
		Program p2 = new Program();
		Rule r = new Rule();
		r.setConclusion(new DLPNeg(new DLPAtom("a")));
		p2.add(new Rule(new DLPNeg("a")));
		
		Program p3 = new Program();
		r = new Rule();
		r.setConclusion(new DLPAtom("a"));
		r.addPremise(new DLPAtom("b"));
		p3.add(r);
		
		List<Program> programs = new LinkedList<Program>();
		programs.add(p1);
		programs.add(p2);
		programs.add(p3);
		
		CredibilityRevision revision = new CredibilityRevision();
		Program cp = revision.translate(programs);
		
		System.out.println(cp);
		
		p1 = new Program();
		p1.addFact(new DLPAtom("b"));
		p1.addFact(new DLPAtom("c"));
		
		p2 = new Program();
		p2.add(new Rule(new DLPNeg("a"), new DLPAtom("b")));
		p2.add(new Rule(new DLPAtom("a"), new DLPAtom("c")));
		
		programs.clear();
		programs.add(p1);
		programs.add(p2);
		cp = revision.translate(programs);
		
		System.out.println("\n\n\n\n" + cp);
	}
}
