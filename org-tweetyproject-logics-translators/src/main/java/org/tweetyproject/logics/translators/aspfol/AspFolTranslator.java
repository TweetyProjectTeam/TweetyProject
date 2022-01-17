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
public class AspFolTranslator extends Translator
{
	/**TT_Negation*/
	public static final int TT_NEGATION = 1;
	
	/** Default-Ctor */
	public AspFolTranslator() {}
	/**
	 * 
	 * @param source source
	 * @return FolAtom toFOL 
	 */
	public FolAtom toFOL(ASPAtom source) {
		return (FolAtom) this.translateAtom(source, FolAtom.class);
	}
	/**
	 * 
	 * @param source source
	 * @return ASPAtom toASP
	 */
	public ASPAtom toASP(FolAtom source) {
		return (ASPAtom) this.translateAtom(source, ASPAtom.class);
	}
/**
 * 
 * @param source source
 * @return ASPLiteral toASP
 */
	public ASPLiteral toASP(FolFormula source) {
		if(source instanceof FolAtom) {
			return toASP((FolAtom)source);
		} else if(source instanceof Negation) {
			return toASP((Negation)source);
		} 
		return null;
	}
	/**
	 * 
	 * @param source source
	 * @return Negation toFOL
	 */
	public Negation toFOL(StrictNegation source) {
		return new Negation((FolAtom) 
				this.translateAtom(source.getAtom(), FolAtom.class));
	}
/**
 * 
 * @param source source
 * @return StrictNegation toASP
 */
	public StrictNegation toASP(Negation source) {
		return new StrictNegation((ASPAtom) this.translateAtom(
				source.getAtoms().iterator().next(), ASPAtom.class));
	}
	/**
	 * 
	 * @param source source
	 * @return FolFormula toFOL
	 */
	public FolFormula toFOL(ASPLiteral source) {
		if(source instanceof ASPAtom) {
			return toFOL((ASPAtom)source);
		} else if(source instanceof StrictNegation) {
			return toFOL((StrictNegation)source);
		}
		return null;
	}
	/**
	 * 
	 * @param source source
	 * @return Disjunction toFOL
	 */
	public Disjunction toFOL(ClassicalHead source) {
		return (Disjunction) this.translateAssociative(source, Disjunction.class);
	}
	/**
	 * 
	 * @param source source
	 * @return ClassicalHead toASP
	 */
	public ClassicalHead toASP(Disjunction source) {
		return (ClassicalHead) this.translateAssociative(source, ClassicalHead.class);
	}
	
	@Override
	public SimpleLogicalFormula translateUsingMap(SimpleLogicalFormula source) {
		SimpleLogicalFormula reval = super.translateUsingMap(source);
		if(reval == null) {
			Pair<Integer, Class<?>> translateInfo = getTranslateInfo(source.getClass());
			switch(translateInfo.getFirst()) {
			case TT_NEGATION:
				return translateInfo.getSecond() == Negation.class ? 
						toFOL((StrictNegation)source) : 
						toASP((FolAtom)source);
			}
		}
		return reval;
	}
	
	@Override
	protected Map<Class<?>, Pair<Integer, Class<?>>> createTranslateMap() {
		Map<Class<?>, Pair<Integer, Class<?>>> tmap = new HashMap<Class<?>, Pair<Integer, Class<?>>>();

		tmap.put(ASPAtom.class, new Pair<Integer, Class<?>>(TT_ATOM, FolAtom.class));
		tmap.put(FolAtom.class, new Pair<Integer, Class<?>>(TT_ATOM, ASPAtom.class));
		
		tmap.put(ClassicalHead.class, new Pair<Integer, Class<?>>(TT_ASSOC, Disjunction.class));
		tmap.put(Disjunction.class, new Pair<Integer, Class<?>>(TT_ASSOC, ClassicalHead.class));
		
		return tmap;
	}
}
