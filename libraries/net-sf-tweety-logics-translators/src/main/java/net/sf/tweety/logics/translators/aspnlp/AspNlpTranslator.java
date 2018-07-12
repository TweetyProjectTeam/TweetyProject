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
package net.sf.tweety.logics.translators.aspnlp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.lp.asp.syntax.*;
import net.sf.tweety.lp.nlp.syntax.NLPProgram;
import net.sf.tweety.lp.nlp.syntax.NLPRule;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.translators.aspfol.AspFolTranslator;

/**
 * Translates between DLP under the answer set semantics (ASP) and NLP.
 * Because NLP is a kind of superset of FOL the Translator extends the
 * AspFolTranslator.
 * 
 * @author Tim Janus
 */
public class AspNlpTranslator extends AspFolTranslator
{	
	/**
	 * Translates the given ASP rule to a NLP-rule. Every ASP rule is
	 * expressible as NLP rule therefore there is no special behavior.
	 * @param rule	The ASP rule that is going to be translated.
	 * @return		The translated NLP rule
	 */
	NLPRule toNLP(Rule rule) {
		NLPRule reval = new NLPRule();
		if(rule.getConclusion().size() == 1) {
			FolFormula conclusion = this.toFOL(rule.getConclusion().get(0));
			reval.setConclusion(conclusion);
		} else if(rule.getConclusion().size() > 1) {
			FolFormula conclusion = this.toFOL(rule.getConclusion());
			reval.setConclusion(conclusion);
		}
		
		for(DLPElement element : rule.getPremise()) {
			if(element instanceof DLPLiteral) {
				FolFormula trans = this.toFOL((DLPLiteral)element);
				reval.addPremise(trans);
			}
		}
		return reval;
	}
	
	public NLPProgram toNLP(Program program) {
		NLPProgram reval = new NLPProgram();
		for(Rule rule : program) {
			reval.add(toNLP(rule));
		}
		return reval;
	}
	
	/**
	 * Translates the given NLP-rule into an ASP Program.
	 * A NLP rule might only be expressible by ASP with multiple rules.
	 * If the NLP rule is a :- b v c. Then the two ASP rules a :- b. and
	 * a :- c.  Are generated. If Conjunctions exist in the head then a rule
	 * for every element of the conjunction in the head is generated. Those
	 * rules share the same body.
	 * @param rule
	 * @return The ASP program that represens the rule given as parameter.
	 */
	public Program toASP(NLPRule rule) {
		Program reval = new Program();
		List<DLPHead> heads = new LinkedList<DLPHead>();
		
		// 1. create all possible heads
		FolFormula head = rule.getConclusion();
		if(head.isLiteral()) {
			heads.add(new DLPHead((DLPLiteral) this.toASP(head)));
		} else if(head instanceof Disjunction) {
			heads.add(this.toASP((Disjunction)head));
		} else if(head instanceof Conjunction) {
			heads.addAll(headsFromConjunction((Conjunction) head));
		}
		
		// 2. create all possible bodies.
		List<List<DLPElement>> bodies = new LinkedList<List<DLPElement>>();
		bodies.add(new LinkedList<DLPElement>());
		for(FolFormula premise : rule.getPremise()) {
			bodiesFromFormula(premise, bodies);
		}
		
		// 3. create rules form heads and bodies:
		for(DLPHead h : heads) {
			for(List<DLPElement> body : bodies) {
				reval.add(new Rule(h, body));
			}
		}
		
		return reval;
	}
	
	/**
	 * Translate the given NLP-program into an ASP-program.
	 * @param program	The NLP-program that is translated
	 * @return			A ASP-program representing the given NLP-program.
	 */
	public Program toASP(NLPProgram program) {
		Program reval = new Program();
		for(NLPRule rule : program) {
			reval.addAll(toASP(rule));
		}
		return reval;
	}
	
	/**
	 * Helper method: Generates a list of possible bodies for the given FOL-Formula.
	 * It handles Literals by adding them to every body, Conjunctions by recursively
	 * calling this method and Disjunctions by delegating the work to the bodiesFromDisjunction()
	 * method.
	 * 
	 * @param source	The FOL formula that is used to generate the bodies
	 * @param bodies	A list of bodies which is used as input and output parameter.
	 */
	private void bodiesFromFormula(FolFormula source, List<List<DLPElement>> bodies) {
		if(source.isLiteral()) {
			DLPLiteral trans = (DLPLiteral) this.toASP(source);
			for(int i=0; i<bodies.size(); ++i) {
				bodies.get(i).add(trans);
			}
		} else if(source instanceof Disjunction) {
			bodiesFromDisjunction((Disjunction)source, bodies);
		} else if(source instanceof Conjunction) {
			for(RelationalFormula f : (Conjunction)source) {
				bodiesFromFormula((FolFormula)f, bodies);
			}
		}
	}
	
	/**
	 * Helper method:
	 * @param source
	 * @param bodies
	 */
	private void bodiesFromDisjunction(Disjunction source, List<List<DLPElement>> bodies) {
		List<List<DLPElement>> reval = new LinkedList<List<DLPElement>>();
		
		// For every body so far a permutation per element of the
		// disjunction is created.
		for(List<DLPElement> basis : bodies) {
			List<DLPElement> curBody = new LinkedList<DLPElement>(basis);
			
			for(RelationalFormula f : source) {
				FolFormula fol = (FolFormula)f;
				List<List<DLPElement>> temporary = new LinkedList<List<DLPElement>>();
				temporary.add(curBody);
				bodiesFromFormula(fol, temporary);
				reval.addAll(temporary);
			}
		}
		
		bodies.clear();
		bodies.addAll(reval);
	}
	
	private List<DLPHead> headsFromConjunction(Conjunction c) {
		List<DLPHead> reval = new LinkedList<DLPHead>();
		for(RelationalFormula formula : c){
			FolFormula fol = (FolFormula)formula;
			if(fol.isLiteral()) {
				DLPLiteral trans = (DLPLiteral)this.toASP(fol);
				reval.add(new DLPHead(trans));
			} else if(fol instanceof Disjunction) {
				reval.add(this.toASP((Disjunction)fol));
			} else if(fol instanceof Conjunction) {
				reval.addAll(headsFromConjunction((Conjunction) fol));
			}
		}
		return reval;
	}
	
	@Override
	protected Map<Class<?>, Pair<Integer, Class<?>>> createTranslateMap() {
		Map<Class<?>, Pair<Integer, Class<?>>> tmap = new HashMap<Class<?>, Pair<Integer,Class<?>>>();
		return tmap;
	}

}
