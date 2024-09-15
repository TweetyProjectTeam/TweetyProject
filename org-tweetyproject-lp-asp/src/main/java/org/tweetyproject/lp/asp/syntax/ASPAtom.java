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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.lp.asp.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tweetyproject.logics.commons.error.LanguageException;
import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.lp.asp.syntax.ASPOperator.ClingoPredicate;
import org.tweetyproject.lp.asp.syntax.ASPOperator.DLVPredicate;

/**
 * This class models an atom, which is a basic structure for building literals
 * and rules for logic programs.
 *
 * @author Tim Janus
 * @author Thomas Vengels
 * @author Matthias Thimm
 * @author Anna Gessler
 *
 */
public class ASPAtom extends ASPLiteral {

	/** The predicate identifying the atom */
	protected Predicate predicate;

	/** A list of arguments of the atom */
	protected List<Term<?>> arguments = new LinkedList<Term<?>>();

	/**
	 * Empty constructor.
	 */
	public ASPAtom() {
		this.predicate = null;
		this.arguments = new ArrayList<Term<?>>();
	}

	/**
	 * Creates a new atom with the given predicate and terms.
	 *
	 * @param p     a predicate
	 * @param terms arguments of the atom
	 */
	public ASPAtom(Predicate p, List<Term<?>> terms) {
		if (p.isTyped())
			throw new IllegalArgumentException(
					"Error: ASP predicates are typeless, the given predicate " + p + " is not.");
		this.predicate = p;
		this.arguments = terms;
	}

	/**
	 * Creates a new atom with the given predicate and terms.
	 *
	 * @param p     a predicate
	 * @param terms arguments of the atom
	 */
	public ASPAtom(Predicate p, Term<?>... terms) {
		if (p.isTyped())
			throw new IllegalArgumentException(
					"Error: ASP predicates are typeless, the given predicate " + p + " is not.");
		this.predicate = p;
		this.arguments = Arrays.asList(terms);
	}

	/**
	 * Creates a new atom with the given predicate name and no terms.
	 *
	 * @param name a name
	 */
	public ASPAtom(String name) {
		this(new Predicate(name));
	}

	/**
	 * Copy-Constructor: Generates a deep copy of the given FOL atom.
	 *
	 * @param other The FOL atom acting as source for the deep copy
	 */
	public ASPAtom(FolAtom other) {
		if (other.getPredicate().isTyped())
			throw new IllegalArgumentException("Error: ASP predicates are typeless, the given atom's predicate "
					+ other.getPredicate() + " is not.");
		this.predicate = new Predicate(other.getPredicate().getName(), other.getArguments().size());
		for (Term<?> t : other.getArguments())
			this.arguments.add((Term<?>) t.clone());
	}

	/**
	 * Copy-Constructor: Generates a deep copy of the given ASP atom.
	 *
	 * @param other The atom acting as source for the deep copy
	 */
	public ASPAtom(ASPAtom other) {
		this.predicate = new Predicate(other.getName(), other.getPredicate().getArity());
		for (Term<?> t : other.getArguments()) {
			this.arguments.add((Term<?>) t.clone());
		}
	}

	/**
	 * Creates an atom with the given predicate as name and the given terms as
	 * argument
	 *
	 * @param symbol The name of the atom
	 * @param terms  A list of Term&lt;?&gt; defining the arguments of the term
	 */
	public ASPAtom(String symbol, Term<?>... terms) {
		this.predicate = new Predicate(symbol, terms.length);
		for (int i = 0; i < terms.length; ++i) {
			this.arguments.add(terms[i]);
		}
	}

	/**
	 * Creates a new ASPAtom with the given predicate.
	 *
	 * @param p a predicate
	 */
	public ASPAtom(Predicate p) {
		this();
		if (p.isTyped())
			throw new IllegalArgumentException(
					"Error: ASP predicates are typeless, the given predicate " + p + " is not.");
		this.predicate = p;
	}

	@Override
	public String getName() {
		return predicate.getName();
	}

	@Override
	public SortedSet<ASPLiteral> getLiterals() {
		SortedSet<ASPLiteral> literals = new TreeSet<ASPLiteral>();
		literals.add(this);
		return literals;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		predicates.add(this.predicate);
		return predicates;
	}

	@Override
	public Set<ASPAtom> getAtoms() {
		Set<ASPAtom> atoms = new HashSet<ASPAtom>();
		atoms.add(this);
		return atoms;
	}

	@Override
	public FolSignature getSignature() {
		FolSignature sig = new FolSignature();
		sig.add(this.predicate);
		sig.addAll(this.arguments);
		return sig;
	}

	@Override
	public ASPAtom clone() {
		return new ASPAtom(this);
	}

	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> terms = new HashSet<Term<?>>();
		terms.addAll(arguments);
		return terms;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		for (Term<?> arg : arguments) {
			if (arg.getClass().equals(cls)) {
				@SuppressWarnings("unchecked")
				C castArg = (C) arg;
				reval.add(castArg);
			}
			// Recursively add terms for all inner functional terms
			reval.addAll(arg.getTerms(cls));
		}
		return reval;
	}

	@Override
	public Predicate getPredicate() {
		return this.predicate;
	}

	@Override
	public RETURN_SET_PREDICATE setPredicate(Predicate newer) {
		Predicate old = this.predicate;
		this.predicate = newer;
		return AtomImpl.implSetPredicate(old, this.predicate, arguments);
	}

	@Override
	public void addArgument(Term<?> arg) throws LanguageException {
		this.arguments.add(arg);
	}

	@Override
	public List<? extends Term<?>> getArguments() {
		return Collections.unmodifiableList(arguments);
	}

	@Override
	public boolean isComplete() {
		return getTerms().size() == predicate.getArity();
	}

	@Override
	public int compareTo(ASPLiteral o) {
		if (o instanceof StrictNegation) {
			return -1;
		}
		return toString().compareTo(o.toString());
	}

	@Override
	public ASPAtom cloneWithAddedTerm(Term<?> term) {
		Predicate newPredicate = new Predicate(this.predicate.getName(), this.predicate.getArity() + 1);
		List<Term<?>> args = new ArrayList<Term<?>>(this.arguments);
		args.add(term);
		ASPAtom reval = new ASPAtom(newPredicate, args);
		return reval;
	}

	@Override
	public ASPAtom getAtom() {
		return this;
	}

	@Override
	public ASPLiteral complement() {
		return new StrictNegation(this);
	}

	@Override
	public ASPAtom substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		ASPAtom reval = new ASPAtom(this);
		reval.arguments.clear();
		for (int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i).equals(v)) {
				reval.arguments.add(t);
			} else {
				reval.arguments.add(arguments.get(i));
			}
		}
		return reval;
	}

	/**
	 * Retrieves the term at the specified index from the list of arguments.
	 *
	 * <p>
	 * This method allows access to the term stored at the given index in the list
	 * of arguments.
	 * The returned term is of a generic type {@code Term<?>}, meaning it can
	 * represent any type of term.
	 * </p>
	 * @param i the index of the term to retrieve, starting from 0.
	 * @return the {@code Term<?>} at the specified index.
	 * @throws IndexOutOfBoundsException if the index is out of range (i.e.,
	 *                                   {@code i < 0} or
	 *                                   {@code i >= arguments.size()}).
	 */
	public Term<?> getTerm(int i) {
		return this.arguments.get(i);
	}

	@Override
	public String toString() {
		String res = this.predicate.getName();
		if (this.predicate.getArity() > 0) {
			res += "(";
			for (int i = 0; i < arguments.size() - 1; i++) {
				res += arguments.get(i).toString() + ",";
			}
			res += arguments.get(arguments.size() - 1).toString() + ")";
		}
		return res;
	}

	@Override
	public String printToClingo() {
		String res = this.predicate.getName();
		if (this.predicate instanceof DLVPredicate) {
			if (this.predicate.getName().equals("#int") && this.arguments.size() == 3) {
				// #int(X, Y, Z) is true, iff X<=Z<=Y holds
				ComparativeAtom i1 = new ComparativeAtom(ASPOperator.BinaryOperator.LEQ, this.arguments.get(0),
						this.arguments.get(2));
				ComparativeAtom i2 = new ComparativeAtom(ASPOperator.BinaryOperator.LEQ, this.arguments.get(2),
						this.arguments.get(1));
				return i1.printToClingo() + "\n" + i2.printToClingo();
			} else
				throw new IllegalArgumentException(
						"Rule contains DLVPredicate " + this.predicate + " that is not supported by Clingo");
		}
		if (this.predicate.getArity() > 0) {
			res += "(";
			for (int i = 0; i < arguments.size(); i++) {
				String termName = arguments.get(i).toString();
				if (arguments.get(i) instanceof Constant) {
					if (!Character.isLowerCase(termName.charAt(0)))
						throw new IllegalArgumentException("Invalid constant name '" + termName
								+ "' Constants in clingo must start with a lowercase letter.");
				} else if (arguments.get(i) instanceof Variable) {
					// this check is unnecessary because variables in TweetyProject are required to
					// start with
					// an uppercase letter, but just in case
					if (!Character.isUpperCase(termName.charAt(0)) && termName.charAt(0) != '_')
						throw new IllegalArgumentException("Invalid variable name '" + termName
								+ "' Variables in clingo must start with an uppercase letter (exception: '_').");
				}
				if (i < arguments.size() - 1)
					res += termName + ",";
				else
					res += termName + ")";
			}
		}
		return res;
	}

	@Override
	public String printToDLV() {
		String res = this.predicate.getName();
		if (this.predicate instanceof ClingoPredicate) {
			if (this.predicate.getName().equals("#true") && arguments.size() == 0)
				return "true";
			else if (this.predicate.getName().equals("#false") && arguments.size() == 0)
				return "false";
			else
				throw new IllegalArgumentException(
						"Rule contains ClingoPredicate " + this.predicate + " that is not supported by DLV");
		}
		if (this.predicate.getArity() > 0) {
			res += "(";
			for (int i = 0; i < arguments.size(); i++) {
				String termName = arguments.get(i).toString();
				if (arguments.get(i) instanceof Constant) {
					if (!Character.isLowerCase(termName.charAt(0)))
						throw new IllegalArgumentException("Invalid constant name '" + termName
								+ "' Constants in DLV must start with a lowercase letter");
				} else if (arguments.get(i) instanceof Variable) {
					// this check is unnecessary because variables in TweetyProject are required to
					// start with
					// an uppercase letter, but just in case
					if (!Character.isUpperCase(termName.charAt(0)) && termName.charAt(0) != '_')
						throw new IllegalArgumentException("Invalid variable name '" + termName
								+ "' Variables in DLV must start with an uppercase letter (exception: '_').");
				}
				if (i < arguments.size() - 1)
					res += termName + ",";
				else
					res += termName + ")";
			}
		}
		return res;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arguments, predicate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ASPAtom other = (ASPAtom) obj;
		return Objects.equals(arguments, other.arguments) && Objects.equals(predicate, other.predicate);
	}

}
