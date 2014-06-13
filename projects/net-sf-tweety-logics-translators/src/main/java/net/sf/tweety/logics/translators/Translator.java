/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.translators;

import java.util.Map;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.error.LanguageException;
import net.sf.tweety.logics.commons.error.LanguageException.LanguageExceptionReason;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.interfaces.AssociativeFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Atom;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows translation between different logic languages, sub classes
 * have to implement the translation between complex formulas but this
 * base class provides methods to translate, predicates, Atoms,
 * Associative formulas and Rules.
 * 
 * To translate more complex formulas subclasses shall override the
 * translateUsingMap() method. It is recommended to call the super 
 * method when overriding to have the correct translate behavior for
 * basic constructs like Atoms, Predicate etc.
 * 
 * The sub class also have to implement the createTranslateMap() method. The map
 * maps a source class to a pair of a target class and an implementation which
 * shall be used for translation. Although sub classes shall provide an easy
 * user-interface with different overloads of methods like: toLang1() and toLang2()
 * the translate map is necessary to support nested formulas like a disjunction
 * of conjunction of several disjunction etc.
 * 
 * @remark The implementations used unchecked generic casts. It is important
 * 			that the subclasses create consistent translation maps, otherwise
 * 			there might be a ClassCastException which is not thrown by this
 * 			code although it is caused by the inconsistent translation
 * 			map.
 * 
 * Also see net.sf.tweety.logics.translate.folprop for a short example
 * implementation.
 * 
 * @author Tim Janus
 */
public abstract class Translator {
	
	/** reference to the logback logger instance */
	private static Logger LOG = LoggerFactory.getLogger(Translator.class);

	public static final int TT_PREDICATE 	= -1;
	public static final int TT_ATOM 		= -2;
	public static final int TT_ASSOC		= -3;
	public static final int TT_RULE			= -4;
	
	Map<Class<?>, Pair<Integer, Class<?>>>	translateMap;
	
	public Translator() {
		translateMap = createTranslateMap();
	}
	
	protected abstract Map<Class<?>, Pair<Integer, Class<?>>> createTranslateMap();
	
	/**
	 * Translates the given source predicate into an instance of the given predicate
	 * class and returns the translation.
	 * @param source		The predicate acting as source for the operation
	 * @param predicateCls	The description of the destination Predicate class
	 * @return				An instance of predicateCls which is syntactically equal to source.
	 * @throws LanguageException
	 */
	public <C extends Predicate> C translatePredicate(Predicate source, 
			Class<C> predicateCls) throws LanguageException {
		if(source == null)
			throw new IllegalArgumentException("Argument 'source' must not be null.");
		C dest = createInstance(predicateCls);
		dest.setName(source.getName());
		for(Sort argType : source.getArgumentTypes()) {
			dest.addArgumentType(argType.clone());
		}
		return dest;
	}
	
	/**
	 * Translates the given source atom into an instance of atomCls and returns the
	 * translation.
	 * @param source		The atom acting as source
	 * @param atomCls		The description of the destination Atom class
	 * @return				The translated atom
	 * @throws LanguageException
	 */
	public Atom translateAtom(Atom source, Class<?> atomCls) 
			throws LanguageException {
		if(source == null)
			throw new IllegalArgumentException("Argument 'source' must not be null.");
		Atom dest = (Atom) createInstance(atomCls);
		Predicate dstPredicate = translatePredicate(source.getPredicate(), dest.getPredicateCls());
		
		dest.setPredicate(dstPredicate);
		for(Term<?> arg : source.getArguments()) {
			dest.addArgument(arg.clone());
		}
		return dest;
	}
	
	
	/**
	 * Translates the given AssociativeFormula into another AssociativeFormula
	 * thats type is given by the parameter assocCls
	 * @param source	The 
	 * @param assocCls
	 */
	public <A extends AssociativeFormula<? extends SimpleLogicalFormula>> 
		AssociativeFormula<?> translateAssociative(A source, Class<?> assocCls) {
		@SuppressWarnings("unchecked")
		AssociativeFormula<? super SimpleLogicalFormula> dest = (AssociativeFormula<? super SimpleLogicalFormula>) createInstance(assocCls);
		for(SimpleLogicalFormula slf : source) {
			SimpleLogicalFormula translated = translateUsingMap(slf);
			dest.add(translated);
		}
		return dest;
	}
	
	protected Pair<Integer, Class<?>> getTranslateInfo(Class<?> cls) {
		return translateMap.get(cls);
	}
	
	public SimpleLogicalFormula translateUsingMap(SimpleLogicalFormula source) {
		Pair<Integer, Class<?>> translateInfo = getTranslateInfo(source.getClass());
		if(translateInfo != null) {
			switch(translateInfo.getFirst()) {
			case TT_ATOM:
				return translateAtom((Atom)source, translateInfo.getSecond());
				
			case TT_ASSOC:
				return translateAssociative((AssociativeFormula<?>)source, translateInfo.getSecond());
				
			case TT_RULE:
				@SuppressWarnings("unchecked")
				Rule<? extends SimpleLogicalFormula, ? extends SimpleLogicalFormula> rule 
					= (Rule<? extends SimpleLogicalFormula, ? extends SimpleLogicalFormula>) source;
				return (SimpleLogicalFormula) translateRule(rule, translateInfo.getSecond());
			}
		} else {
			// todo: error handling
		}
		return null;
	}
	
	public Rule<?,?> translateRule(Rule<? extends SimpleLogicalFormula, ? extends SimpleLogicalFormula> source, Class<?> ruleCls) {
		@SuppressWarnings("unchecked")
		Rule<SimpleLogicalFormula, SimpleLogicalFormula> dest = (Rule<SimpleLogicalFormula, SimpleLogicalFormula>) createInstance(ruleCls);
		
		SimpleLogicalFormula tCon = translateUsingMap(source.getConclusion());
		dest.setConclusion(tCon);
		
		
		return dest;
	}
	
	protected static <T> T createInstance(Class<T> cls) throws LanguageException {
		T reval = null;
		LanguageException ex = null;
		try {
			reval = cls.newInstance();
		} catch (InstantiationException e) {
			ex = new LanguageException("", LanguageExceptionReason.LER_INSTANTIATION,
					e.getMessage());
			e.printStackTrace();
			LOG.debug(e.getMessage());
		} catch (IllegalAccessException e) {
			ex = new LanguageException("", LanguageExceptionReason.LER_ILLEGAL_ACCESSS,
					e.getMessage());
			e.printStackTrace();
			LOG.debug(e.getMessage());
		}
		if(ex != null) {
			throw ex;
		}
		return reval;
	}
}
