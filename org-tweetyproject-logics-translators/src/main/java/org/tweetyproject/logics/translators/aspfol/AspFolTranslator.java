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
package org.tweetyproject.logics.translators.aspfol;

import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.lp.asp.syntax.*;
import org.tweetyproject.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import org.tweetyproject.logics.translators.Translator;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.Negation;

/**
 * This Translator can translate between FOL and ASP literals (without default
 * negated literals).
 *
 * @author Tim Janus
 */
public class AspFolTranslator extends Translator {


	/** Constant representing translation type for negation */
	public static final int TT_NEGATION = 1;

	/** Default constructor for the AspFolTranslator class */
	public AspFolTranslator() {}

	/**
	 * Translates an ASPAtom to its corresponding FolAtom.
	 *
	 * @param source the ASPAtom to be translated
	 * @return the translated FolAtom
	 */
	public FolAtom toFOL(ASPAtom source) {
		return (FolAtom) this.translateAtom(source, FolAtom.class);
	}

	/**
	 * Translates a FolAtom to its corresponding ASPAtom.
	 *
	 * @param source the FolAtom to be translated
	 * @return the translated ASPAtom
	 */
	public ASPAtom toASP(FolAtom source) {
		return (ASPAtom) this.translateAtom(source, ASPAtom.class);
	}

	/**
	 * Translates a FolFormula to an ASPLiteral. If the formula is an atom or negation, it will translate accordingly.
	 *
	 * @param source the FolFormula to be translated
	 * @return the translated ASPLiteral
	 */
	public ASPLiteral toASP(FolFormula source) {
		if (source instanceof FolAtom) {
			return toASP((FolAtom) source);
		} else if (source instanceof Negation) {
			return toASP((Negation) source);
		}
		return null;
	}

	/**
	 * Translates a StrictNegation into a Negation for FOL.
	 *
	 * @param source the StrictNegation to be translated
	 * @return the translated Negation
	 */
	public Negation toFOL(StrictNegation source) {
		return new Negation((FolAtom) this.translateAtom(source.getAtom(), FolAtom.class));
	}

	/**
	 * Translates a Negation from FOL into a StrictNegation for ASP.
	 *
	 * @param source the Negation to be translated
	 * @return the translated StrictNegation
	 */
	public StrictNegation toASP(Negation source) {
		return new StrictNegation((ASPAtom) this.translateAtom(source.getAtoms().iterator().next(), ASPAtom.class));
	}

	/**
	 * Translates an ASPLiteral to a FolFormula. If the literal is an atom or negation, it will translate accordingly.
	 *
	 * @param source the ASPLiteral to be translated
	 * @return the translated FolFormula
	 */
	public FolFormula toFOL(ASPLiteral source) {
		if (source instanceof ASPAtom) {
			return toFOL((ASPAtom) source);
		} else if (source instanceof StrictNegation) {
			return toFOL((StrictNegation) source);
		}
		return null;
	}

	/**
	 * Translates a ClassicalHead (ASP disjunction) to a FOL Disjunction.
	 *
	 * @param source the ClassicalHead to be translated
	 * @return the translated Disjunction
	 */
	public Disjunction toFOL(ClassicalHead source) {
		return (Disjunction) this.translateAssociative(source, Disjunction.class);
	}

	/**
	 * Translates a FOL Disjunction into an ASP ClassicalHead.
	 *
	 * @param source the Disjunction to be translated
	 * @return the translated ClassicalHead
	 */
	public ClassicalHead toASP(Disjunction source) {
		return (ClassicalHead) this.translateAssociative(source, ClassicalHead.class);
	}

	/**
	 * Translates the given formula using the translation map, handling custom logic for negations if needed.
	 *
	 * @param source the source formula to be translated
	 * @return the translated SimpleLogicalFormula or null if no translation could be performed
	 */
	@Override
	public SimpleLogicalFormula translateUsingMap(SimpleLogicalFormula source) {
		SimpleLogicalFormula reval = super.translateUsingMap(source);
		if (reval == null) {
			Pair<Integer, Class<?>> translateInfo = getTranslateInfo(source.getClass());
			switch (translateInfo.getFirst()) {
				case TT_NEGATION:
					return translateInfo.getSecond() == Negation.class ? toFOL((StrictNegation) source) : toASP((FolAtom) source);
			}
		}
		return reval;
	}

	/**
	 * Creates the mapping between ASP and FOL types for this translator.
	 *
	 * @return the map that links ASP and FOL classes for translation
	 */
	@Override
	protected Map<Class<?>, Pair<Integer, Class<?>>> createTranslateMap() {
		Map<Class<?>, Pair<Integer, Class<?>>> tmap = new HashMap<>();

		tmap.put(ASPAtom.class, new Pair<>(TT_ATOM, FolAtom.class));
		tmap.put(FolAtom.class, new Pair<>(TT_ATOM, ASPAtom.class));

		tmap.put(ClassicalHead.class, new Pair<>(TT_ASSOC, Disjunction.class));
		tmap.put(Disjunction.class, new Pair<>(TT_ASSOC, ClassicalHead.class));

		return tmap;
	}
}