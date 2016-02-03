/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.translators.aspfol;

import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.lp.asp.syntax.*;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import net.sf.tweety.logics.translators.Translator;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;

/**
 * This Translator can translate between FOL and ASP literals (without default
 * negated literals).
 * 
 * @author Tim Janus
 */
public class AspFolTranslator extends Translator
{
	public static final int TT_NEGATION = 1;
	
	/** Default-Ctor */
	public AspFolTranslator() {}
	
	public FOLAtom toFOL(DLPAtom source) {
		return (FOLAtom) this.translateAtom(source, FOLAtom.class);
	}
	
	public DLPAtom toASP(FOLAtom source) {
		return (DLPAtom) this.translateAtom(source, DLPAtom.class);
	}

	public DLPElement toASP(FolFormula source) {
		if(source instanceof FOLAtom) {
			return toASP((FOLAtom)source);
		} else if(source instanceof Negation) {
			return toASP((Negation)source);
		} 
		return null;
	}
	
	public Negation toFOL(DLPNeg source) {
		return new Negation((FOLAtom) 
				this.translateAtom(source.getAtom(), FOLAtom.class));
	}

	public DLPNeg toASP(Negation source) {
		return new DLPNeg((DLPAtom) this.translateAtom(
				source.getAtoms().iterator().next(), DLPAtom.class));
	}
	
	public FolFormula toFOL(DLPLiteral source) {
		if(source instanceof DLPAtom) {
			return toFOL((DLPAtom)source);
		} else if(source instanceof DLPNeg) {
			return toFOL((DLPNeg)source);
		}
		return null;
	}
	
	public Disjunction toFOL(DLPHead source) {
		return (Disjunction) this.translateAssociative(source, Disjunction.class);
	}
	
	public DLPHead toASP(Disjunction source) {
		return (DLPHead) this.translateAssociative(source, DLPHead.class);
	}
	
	@Override
	public SimpleLogicalFormula translateUsingMap(SimpleLogicalFormula source) {
		SimpleLogicalFormula reval = super.translateUsingMap(source);
		if(reval == null) {
			Pair<Integer, Class<?>> translateInfo = getTranslateInfo(source.getClass());
			switch(translateInfo.getFirst()) {
			case TT_NEGATION:
				return translateInfo.getSecond() == Negation.class ? 
						toFOL((DLPNeg)source) : 
						toASP((FOLAtom)source);
			}
		}
		return reval;
	}
	
	@Override
	protected Map<Class<?>, Pair<Integer, Class<?>>> createTranslateMap() {
		Map<Class<?>, Pair<Integer, Class<?>>> tmap = new HashMap<Class<?>, Pair<Integer, Class<?>>>();

		tmap.put(DLPAtom.class, new Pair<Integer, Class<?>>(TT_ATOM, FOLAtom.class));
		tmap.put(FOLAtom.class, new Pair<Integer, Class<?>>(TT_ATOM, DLPAtom.class));
		
		tmap.put(DLPHead.class, new Pair<Integer, Class<?>>(TT_ASSOC, Disjunction.class));
		tmap.put(Disjunction.class, new Pair<Integer, Class<?>>(TT_ASSOC, DLPHead.class));
		
		return tmap;
	}
}
