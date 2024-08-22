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
package org.tweetyproject.logics.translators.clnlp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.cl.syntax.ClBeliefSet;
import org.tweetyproject.logics.cl.syntax.Conditional;
import org.tweetyproject.logics.commons.error.LanguageException;
import org.tweetyproject.logics.commons.error.LanguageException.LanguageExceptionReason;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Tautology;
import org.tweetyproject.logics.translators.Translator;
import org.tweetyproject.logics.translators.folprop.FOLPropTranslator;
import org.tweetyproject.lp.nlp.syntax.NLPProgram;
import org.tweetyproject.lp.nlp.syntax.NLPRule;

/**
 * Translates between propositional Conditionals and NLP.
 *
 * @author Sebastian Homann
 */
public class ClNLPTranslator extends Translator
{
	private FOLPropTranslator fol2pl = new FOLPropTranslator();
	/**
	 * Translates the given conditional to a NLP-rule.
	 * @param cond	The conditional that is going to be translated.
	 * @return		The translated NLP rule
	 */
	NLPRule toNLP(Conditional cond) {
		NLPRule reval = new NLPRule();
		PlFormula plPremise = cond.getPremise().iterator().next();
		PlFormula plConclusion = cond.getConclusion();

		FolFormula folPremise = fol2pl.toFOL(plPremise);
		FolFormula folConclusion = fol2pl.toFOL(plConclusion);

		reval.addPremise(folPremise);
		reval.setConclusion(folConclusion);
		return reval;
	}

	/**
	 * Translates the given NLP-rule into a conditional. This only works
	 * for unquantified ground nlp-rules. In all other cases, an exception
	 * is thrown.
	 *
	 * @param rule some rule
	 * @return a conditional
	 */
	public Conditional toCl(NLPRule rule) {
		if(!rule.isGround()) {
			throw new LanguageException("NLPRule rule has to be ground for conversion to propositional conditional.", LanguageExceptionReason.LER_TERM_TYPE_NOT_SUPPORTED);
		}
		FolFormula folConclusion = rule.getConclusion();
		if(folConclusion.containsQuantifier()) {
			throw new LanguageException("Cannot translate a NLPRule containing quantifiers to propositional conditional.", LanguageExceptionReason.LER_QUANTIFICATION_NOT_SUPPORTED);
		}
		Collection<FolFormula> folPremises = rule.getPremise();
		PlFormula plPremise = new Tautology();
		for(FolFormula folPremiseFormula : folPremises) {
			if(folPremiseFormula.containsQuantifier()) {
				throw new LanguageException("Cannot translate a NLPRule containing quantifiers to propositional conditional.", LanguageExceptionReason.LER_QUANTIFICATION_NOT_SUPPORTED);
			}
			plPremise.combineWithAnd(fol2pl.toPropositional(folPremiseFormula));
		}
		PlFormula plConclusion = fol2pl.toPropositional(folConclusion);

		Conditional result = new Conditional(plPremise, plConclusion);
		return result;
	}

	/**
	 * Translate the given NLP-program into a conditional belief set.
	 * @param program	The NLP-program that is translated
	 * @return			A conditional belief set representing the given NLP-program.
	 */
	public ClBeliefSet toCl(NLPProgram program) {
		ClBeliefSet result = new ClBeliefSet();
		for(NLPRule nlprule : program) {
			result.add(toCl(nlprule));
		}
		return result;
	}

	/**
	 * Translate the given conditional belief set into a NLP-program.
	 *
	 * @param conditionals The set of conditionals that is translated
	 * @return A NLP-program representing the given set of conditionals
	 */
	public NLPProgram toNLP(ClBeliefSet conditionals) {
		NLPProgram result = new NLPProgram();
		for(Conditional cond : conditionals) {
			result.add(toNLP(cond));
		}
		return result;
	}

	@Override
	protected Map<Class<?>, Pair<Integer, Class<?>>> createTranslateMap() {
		Map<Class<?>, Pair<Integer, Class<?>>> tmap = new HashMap<Class<?>, Pair<Integer,Class<?>>>();
		return tmap;
	}


    /** Default Constructor */
    public ClNLPTranslator(){}
}
