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
package org.tweetyproject.logics.translators;

import java.util.Map;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.commons.util.rules.Rule;
import org.tweetyproject.logics.commons.error.LanguageException;
import org.tweetyproject.logics.commons.error.LanguageException.LanguageExceptionReason;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.interfaces.AssociativeFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Atom;
import org.tweetyproject.logics.commons.syntax.interfaces.SimpleLogicalFormula;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;



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
 * Remark: The implementations used unchecked generic casts. It is important
 * 			that the subclasses create consistent translation maps, otherwise
 * 			there might be a ClassCastException which is not thrown by this
 * 			code although it is caused by the inconsistent translation
 * 			map.
 *
 * Also see org.tweetyproject.logics.translate.folprop for a short example
 * implementation.
 *
 * @author Tim Janus
 */
public abstract class Translator {

    /** Translation type for predicate */
    public static final int TT_PREDICATE = -1;
    /** Translation type for atom */
    public static final int TT_ATOM = -2;
    /** Translation type for associative formula */
    public static final int TT_ASSOC = -3;
    /** Translation type for rule */
    public static final int TT_RULE = -4;

    /** The translation map that maps classes to their respective translation types and associated classes */
    Map<Class<?>, Pair<Integer, Class<?>>> translateMap;

    /**
     * Creates a new Translator instance and initializes the translation map.
     */
    public Translator() {
        translateMap = createTranslateMap();
    }

    /**
     * Creates and returns the translation map for this translator.
     * This method must be implemented by subclasses.
     *
     * @return a map that defines the translation types and associated classes
     */
    protected abstract Map<Class<?>, Pair<Integer, Class<?>>> createTranslateMap();

    /**
     * Translates the given source predicate into an instance of the given predicate class.
     *
     * @param source        the source predicate to be translated
     * @param predicateCls  the target predicate class
     * @return              an instance of the target predicate class that is syntactically equivalent to the source predicate
     * @param <C> the type of predicate
     * @throws LanguageException if there is a language-related issue during translation
     */
    public <C extends Predicate> C translatePredicate(Predicate source, Class<C> predicateCls) throws LanguageException {
        if (source == null) {
            throw new IllegalArgumentException("Argument 'source' must not be null.");
        }
        C dest = createInstance(predicateCls);
        dest.setName(source.getName());
        for (Sort argType : source.getArgumentTypes()) {
            dest.addArgumentType(argType.clone());
        }
        return dest;
    }

    /**
     * Translates the given source atom into an instance of the target atom class.
     *
     * @param source        the source atom to be translated
     * @param atomCls       the target atom class
     * @return              the translated atom
     * @throws LanguageException if there is a language-related issue during translation
     */
    public Atom translateAtom(Atom source, Class<?> atomCls) throws LanguageException {
        if (source == null) {
            throw new IllegalArgumentException("Argument 'source' must not be null.");
        }
        Atom dest = (Atom) createInstance(atomCls);
        Predicate dstPredicate = translatePredicate(source.getPredicate(), dest.getPredicateCls());

        dest.setPredicate(dstPredicate);
        for (Term<?> arg : source.getArguments()) {
            dest.addArgument(arg.clone());
        }
        return dest;
    }

    /**
     * Translates the given source associative formula into an instance of the target associative formula class.
     *
     * @param source    the source associative formula to be translated
     * @param assocCls  the target associative formula class
     * @param <A>       the type of associative formulas
     * @return          the translated associative formula
     */
    public <A extends AssociativeFormula<? extends SimpleLogicalFormula>>
        AssociativeFormula<?> translateAssociative(A source, Class<?> assocCls) {
        @SuppressWarnings("unchecked")
        AssociativeFormula<? super SimpleLogicalFormula> dest = (AssociativeFormula<? super SimpleLogicalFormula>) createInstance(assocCls);
        for (SimpleLogicalFormula slf : source) {
            SimpleLogicalFormula translated = translateUsingMap(slf);
            dest.add(translated);
        }
        return dest;
    }

    /**
     * Returns the translation information for the given class.
     *
     * @param cls the class for which to retrieve the translation information
     * @return    a pair containing the translation type and associated class, or null if no translation info is available
     */
    protected Pair<Integer, Class<?>> getTranslateInfo(Class<?> cls) {
        return translateMap.get(cls);
    }

    /**
     * Translates the given source formula using the translation map.
     *
     * @param source the source formula to be translated
     * @return       the translated formula, or null if translation could not be performed
     */
    public SimpleLogicalFormula translateUsingMap(SimpleLogicalFormula source) {
        Pair<Integer, Class<?>> translateInfo = getTranslateInfo(source.getClass());
        if (translateInfo != null) {
            switch (translateInfo.getFirst()) {
                case TT_ATOM:
                    return translateAtom((Atom) source, translateInfo.getSecond());

                case TT_ASSOC:
                    return translateAssociative((AssociativeFormula<?>) source, translateInfo.getSecond());

                case TT_RULE:
                    @SuppressWarnings("unchecked")
                    Rule<? extends SimpleLogicalFormula, ? extends SimpleLogicalFormula> rule =
                        (Rule<? extends SimpleLogicalFormula, ? extends SimpleLogicalFormula>) source;
                    return (SimpleLogicalFormula) translateRule(rule, translateInfo.getSecond());
            }
        } else {
            // TODO: Handle error case where translation information is not available
        }
        return null;
    }

    /**
     * Translates the given source rule into an instance of the target rule class.
     *
     * @param source    the source rule to be translated
     * @param ruleCls   the target rule class
     * @return          the translated rule
     */
    public Rule<?, ?> translateRule(Rule<? extends SimpleLogicalFormula, ? extends SimpleLogicalFormula> source, Class<?> ruleCls) {
        @SuppressWarnings("unchecked")
        Rule<SimpleLogicalFormula, SimpleLogicalFormula> dest = (Rule<SimpleLogicalFormula, SimpleLogicalFormula>) createInstance(ruleCls);

        SimpleLogicalFormula tCon = translateUsingMap(source.getConclusion());
        dest.setConclusion(tCon);

        return dest;
    }

    /**
     * Creates an instance of the specified class.
     *
     * @param <T> the type of the class to be instantiated
     * @param cls the class to be instantiated
     * @return    an instance of the specified class
     * @throws LanguageException if there is a language-related issue during instantiation
     */
    @SuppressWarnings("deprecation")
    protected static <T> T createInstance(Class<T> cls) throws LanguageException {
        T reval = null;
        LanguageException ex = null;
        try {
            reval = cls.newInstance();
        } catch (InstantiationException e) {
            ex = new LanguageException("", LanguageExceptionReason.LER_INSTANTIATION, e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ex = new LanguageException("", LanguageExceptionReason.LER_ILLEGAL_ACCESSS, e.getMessage());
            e.printStackTrace();
        }
        if (ex != null) {
            throw ex;
        }
        return reval;
    }
}