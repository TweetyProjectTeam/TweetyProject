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
package org.tweetyproject.lp.asp.beliefdynamics.revision;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.beliefdynamics.CredibilityRevisionNonIterative;
import org.tweetyproject.lp.asp.reasoner.ASPSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.syntax.ASPAtom;
import org.tweetyproject.lp.asp.syntax.ASPLiteral;
import org.tweetyproject.lp.asp.syntax.StrictNegation;
import org.tweetyproject.lp.asp.syntax.ASPOperator;
import org.tweetyproject.lp.asp.syntax.ASPBodyElement;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.syntax.AggregateHead;
import org.tweetyproject.lp.asp.syntax.ClassicalHead;
import org.tweetyproject.lp.asp.syntax.ComparativeAtom;
import org.tweetyproject.lp.asp.syntax.DefaultNegation;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.NumberTerm;
import org.tweetyproject.logics.commons.syntax.Variable;


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
public class CredibilityRevision extends CredibilityRevisionNonIterative<ASPRule> {

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
		Program process(List<Program> orderedPrograms, Collection<AnswerSet> answersets);
	}

	/**
	 * This class implements a default behavior for the processing of the answer sets
	 * of the credibility logic programs.
	 * Remark: 	It does not support the generation of two different programs. If two programs
	 * 			are possible the first answer set is used for revision.
	 * @author Tim Janus
	 *
	 */
	public static class DefaultBehavior implements AnswersetProcessing {


		@Override
		public Program process(List<Program> orderedPrograms, Collection<AnswerSet> answersets) {
			if(answersets.size() > 1) {

			}
			AnswerSet as = answersets.iterator().next();

			// gather indicies of rule which are rejected:
			List<Integer> indicies = new LinkedList<Integer>();
			Set<ASPLiteral> literals = as.getLiteralsWithName("p__rej");
			for(ASPLiteral l : literals) {
				ASPAtom atom = (ASPAtom)l;
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
				for(ASPRule r : p) {
					if(countIndex != removeRuleIndex) {
						reval.add(new ASPRule(r));
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
	private Map<ASPLiteral, String> literalMap = new HashMap<ASPLiteral, String>();

	/** a prefix used to mark constants which represent the id of negative literals. */
	private String negConstantPrefix = "nc__";

	/** a prefix used to mark constants which represents the id of positive literals. */
	private String constantPrefix = "c__";

	/** a prefix used to mark the prioritzed version of predicates */
	private String predCredibilityPrefix = "p__";

	/** a rule index counter used during the creation of the credibility logic program */
	private int ruleIndex = 0;

	/** reference to the solver used for the answer set generation */
	private ASPSolver solver = null;

	private Collection<AnswerSet> lastAnswersets;

	private Collection<AnswerSet> lastProjectedAnswersets;

	private int maxInt;

	/** Default Ctor: Do not forget to set the solver */
	public CredibilityRevision() {}

	/**
	 * Ctor: setting the solver at construction time
	 * @param solver	Reference to the solver used for answer set generation.
	 */
	public CredibilityRevision(ASPSolver solver) {
		this(solver, 10);
	}

	public CredibilityRevision(ASPSolver solver, int maxInt) {
		this.solver = solver;
		this.maxInt = maxInt;
	}

	public Collection<AnswerSet> getLastAnswerset() {
		return lastAnswersets;
	}

	public Collection<AnswerSet> getLastProjectedAnswerSet() {
		return lastProjectedAnswersets;
	}

	public void setAnswersetProcessing(AnswersetProcessing processing) {
		if(processing == null)
			throw new IllegalArgumentException("An answer set processing must be used.");
		this.processing = processing;
	}

	@Override
	public Program revise(List<Collection<ASPRule>> ol) {
		// cast to program:
		List<Program> orderedList = new LinkedList<Program>();
		for(Collection<ASPRule> c : ol) {
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
			lastAnswersets = solver.getModels(credProgram, maxInt);
		} catch (Exception e) {
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
		lastProjectedAnswersets.clear();
		for (AnswerSet as : lastAnswersets)
			lastProjectedAnswersets.add(as);
		for(AnswerSet as : lastProjectedAnswersets) {
			Set<ASPLiteral> toRemove = new HashSet<ASPLiteral>();
			for(ASPLiteral literal : as) {
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

		for(ASPRule r : p) {
			Variable cred = new Variable("Cred");
			NumberTerm indexTerm = new NumberTerm(ruleIndex);

			if (r.getConclusion() instanceof AggregateHead)
				throw new IllegalArgumentException("Only literals are allowed as rule heads in this module.");
			ASPLiteral originalHead = ((ClassicalHead)r.getConclusion()).iterator().next();
			List<ASPBodyElement> originalBody = new LinkedList<ASPBodyElement>();
			for(ASPBodyElement pe : r.getPremise()) {
				ASPBodyElement newBodyElement = (ASPBodyElement)pe.clone();
				originalBody.add(newBodyElement);
			}

			String id = translateLiteral(originalHead);
			Constant headId = new Constant(id);
			ASPLiteral tHead = new ASPAtom(pcp+"lit", headId);

			String negId = translateLiteral((ASPLiteral)originalHead.complement());
			Constant negHeadId = new Constant(negId);
			ASPLiteral tHeadNeg = new ASPAtom(pcp+"lit", negHeadId);

			Variable credLow = new Variable("CredLow");
			Variable credHigh = new Variable("CredHigh");
			List<ASPBodyElement> newBody = new LinkedList<ASPBodyElement>();

			// add H(u) :- B, minr(j, Cred), not exLowerMinr(j, Cred).
			newBody.addAll(originalBody);
			newBody.add(new ASPAtom(pcp+"minr", indexTerm, cred));
			newBody.add(new DefaultNegation(new ASPAtom(pcp+"exLowerMinr", indexTerm, cred)));
			reval.add(new ASPRule(tHead.cloneWithAddedTerm(cred), newBody));

			// add exLowerMin(j, Cred) :- minr(j, Cred), minr(j, CredLower), Prec(CredLower, Cred).
			newBody = new LinkedList<ASPBodyElement>();
			newBody.add(new ASPAtom(pcp+"minr", indexTerm, cred));
			newBody.add(new ASPAtom(pcp+"minr", indexTerm, credLow));
			newBody.add(new ComparativeAtom(ASPOperator.BinaryOperator.LT, credLow, cred));
			reval.add(new ASPRule(new ASPAtom(pcp+"exLowerMinr", indexTerm, cred), newBody));

			// add H :- H(u), not rej(name).
			newBody = new LinkedList<ASPBodyElement>();
			newBody.add(tHead.cloneWithAddedTerm(cred));
			newBody.add(new DefaultNegation(new ASPAtom(pcp+"rej", headId, indexTerm, cred)));
			reval.add(new ASPRule(originalHead, newBody));


			// add minr(index, programCredibility) :- .
			reval.addFact(new ASPAtom(pcp+"minr", indexTerm, new NumberTerm(programCredibility)));

			// add minr(index, Cred) :- B(r), L(name, Cred), not exHigherL(Cred).
			for(ASPBodyElement re : originalBody) {
				if(re instanceof ASPLiteral) {
					String bodyLitId = translateLiteral((ASPLiteral)re);
					ASPAtom translated = new ASPAtom(pcp + "lit", new Constant(bodyLitId));
					newBody = new LinkedList<ASPBodyElement>();
					newBody.addAll(originalBody);
					newBody.add(translated.cloneWithAddedTerm(cred));
					newBody.add(new DefaultNegation(new ASPAtom(pcp+"exHigher", headId, indexTerm, cred)));
					reval.add(new ASPRule(new ASPAtom(pcp+"minr", indexTerm, cred), newBody));
				}
			}


			// add exHigher(name, index, Cred) :- lit(name, Cred), lit(name, CredHigh), Prec(Cred, CredHigh).
			newBody = new LinkedList<ASPBodyElement>();
			newBody.add(tHead.cloneWithAddedTerm(cred));
			newBody.add(tHead.cloneWithAddedTerm(credHigh));
			newBody.add(new ComparativeAtom(ASPOperator.BinaryOperator.LT, cred, credHigh));
			reval.add(new ASPRule( new ASPAtom(pcp+"exHigher", headId, indexTerm, cred), newBody));

			// add rej(name, index, Cred) :- lit(name, Cred), -lit(name, CredHigh), Preceq(Cred, CredHigh).
			newBody = new LinkedList<ASPBodyElement>();
			newBody.add(tHead.cloneWithAddedTerm(cred));
			newBody.add(tHeadNeg.cloneWithAddedTerm(credHigh));
			newBody.add(new ComparativeAtom(ASPOperator.BinaryOperator.LEQ, cred, credHigh));
			newBody.add(originalHead.complement());
			reval.add(new ASPRule( new ASPAtom(pcp+"rej", headId, indexTerm, cred), newBody));

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
	private String translateLiteral(ASPLiteral lit) {
		if(literalMap.containsKey(lit)) {
			return literalMap.get(lit);
		}

		String litId = lit.toString();
		litId = litId.replaceAll(" ", "").replaceAll("-", "").replaceAll(",", "_");

		if (lit instanceof ASPAtom) {
			litId = constantPrefix + litId;
		} else if (lit instanceof StrictNegation) {
			litId = negConstantPrefix + litId;
		} else if(! (lit instanceof ASPAtom)){
			throw new IllegalArgumentException("Parameter 'lit' has to be an Atom or a Neg.");
		}
		literalMap.put(lit, litId);
		return litId;
	}

	public static void main(String [] args) {
		Program p1 = new Program();
		p1.addFact(new ASPAtom("b"));

		Program p2 = new Program();
		ASPRule r = new ASPRule();
		r.setConclusion(new StrictNegation(new ASPAtom("a")));
		p2.add(new ASPRule(new StrictNegation("a")));

		Program p3 = new Program();
		r = new ASPRule();
		r.setConclusion(new ASPAtom("a"));
		r.addPremise(new ASPAtom("b"));
		p3.add(r);

		List<Program> programs = new LinkedList<Program>();
		programs.add(p1);
		programs.add(p2);
		programs.add(p3);

		CredibilityRevision revision = new CredibilityRevision();
		Program cp = revision.translate(programs);

		System.out.println(cp);

		p1 = new Program();
		p1.addFact(new ASPAtom("b"));
		p1.addFact(new ASPAtom("c"));

		p2 = new Program();
		p2.add(new ASPRule(new StrictNegation("a"), new ASPAtom("b")));
		p2.add(new ASPRule(new ASPAtom("a"), new ASPAtom("c")));

		programs.clear();
		programs.add(p1);
		programs.add(p2);
		cp = revision.translate(programs);

		System.out.println("\n\n\n\n" + cp);
	}
}
