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
package org.tweetyproject.logics.translators.aspnlp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.lp.asp.syntax.*;
import org.tweetyproject.lp.nlp.syntax.NLPProgram;
import org.tweetyproject.lp.nlp.syntax.NLPRule;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.translators.aspfol.AspFolTranslator;

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
	NLPRule toNLP(ASPRule rule) {
		NLPRule reval = new NLPRule();
		if (!(rule.getConclusion() instanceof ClassicalHead))
			throw new IllegalArgumentException("Non-classical heads (like aggregate heads) are not currently supported by this translator.");
		ClassicalHead h = (ClassicalHead) rule.getConclusion();
		if(h.size() == 1) {
			FolFormula conclusion = this.toFOL(h.get(0));
			reval.setConclusion(conclusion);
		} else if(h.size() > 1) {
			FolFormula conclusion = this.toFOL(h);
			reval.setConclusion(conclusion);
		}
		
		for(ASPBodyElement element : rule.getPremise()) {
			if(element instanceof ASPLiteral) {
				FolFormula trans = this.toFOL((ASPLiteral)element);
				reval.addPremise(trans);
			}
		}
		return reval;
	}
	/**
	 * 
	 * @param program program
	 * @return NLPProgram toNLP
	 */
	public NLPProgram toNLP(Program program) {
		NLPProgram reval = new NLPProgram();
		for(ASPRule rule : program) {
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
	 * @param rule some rule
	 * @return The ASP program that represens the rule given as parameter.
	 */
	public Program toASP(NLPRule rule) {
		Program reval = new Program();
		List<ClassicalHead> heads = new LinkedList<ClassicalHead>();
		
		// 1. create all possible heads
		FolFormula head = rule.getConclusion();
		if(head.isLiteral()) {
			heads.add(new ClassicalHead((ASPLiteral) this.toASP(head)));
		} else if(head instanceof Disjunction) {
			heads.add(this.toASP((Disjunction)head));
		} else if(head instanceof Conjunction) {
			heads.addAll(headsFromConjunction((Conjunction) head));
		}
		
		// 2. create all possible bodies.
		List<List<ASPBodyElement>> bodies = new LinkedList<List<ASPBodyElement>>();
		bodies.add(new LinkedList<ASPBodyElement>());
		for(FolFormula premise : rule.getPremise()) {
			bodiesFromFormula(premise, bodies);
		}
		
		// 3. create rules form heads and bodies:
		for(ClassicalHead h : heads) {
			for(List<ASPBodyElement> body : bodies) {
				reval.add(new ASPRule(h, body));
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
	private void bodiesFromFormula(FolFormula source, List<List<ASPBodyElement>> bodies) {
		if(source.isLiteral()) {
			ASPLiteral trans = (ASPLiteral) this.toASP(source);
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
	 * @param source a disjunction
	 * @param bodies  a list of list of body elements
	 */
	private void bodiesFromDisjunction(Disjunction source, List<List<ASPBodyElement>> bodies) {
		List<List<ASPBodyElement>> reval = new LinkedList<List<ASPBodyElement>>();
		
		// For every body so far a permutation per element of the
		// disjunction is created.
		for(List<ASPBodyElement> basis : bodies) {
			List<ASPBodyElement> curBody = new LinkedList<ASPBodyElement>(basis);
			
			for(RelationalFormula f : source) {
				FolFormula fol = (FolFormula)f;
				List<List<ASPBodyElement>> temporary = new LinkedList<List<ASPBodyElement>>();
				temporary.add(curBody);
				bodiesFromFormula(fol, temporary);
				reval.addAll(temporary);
			}
		}
		
		bodies.clear();
		bodies.addAll(reval);
	}
	
	private List<ClassicalHead> headsFromConjunction(Conjunction c) {
		List<ClassicalHead> reval = new LinkedList<ClassicalHead>();
		for(RelationalFormula formula : c){
			FolFormula fol = (FolFormula)formula;
			if(fol.isLiteral()) {
				ASPLiteral trans = (ASPLiteral)this.toASP(fol);
				reval.add(new ClassicalHead(trans));
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
