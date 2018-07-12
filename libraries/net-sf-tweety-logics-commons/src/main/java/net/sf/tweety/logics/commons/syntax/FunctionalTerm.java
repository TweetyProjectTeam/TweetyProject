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
package net.sf.tweety.logics.commons.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * A functional term in a logic language, i.e. a functor and a list
 * of argument terms.
 * @author Matthias Thimm, 
 * @author Tim Janus
 */
public class FunctionalTerm extends TermAdapter<Pair<Functor, List<Term<?>>>> {
	
	public FunctionalTerm(Functor functor, Term<?>... terms) {
		super(new Pair<Functor, List<Term<?>>>(functor, Arrays.asList(terms)), 
				functor.getTargetSort());
	}
	
	/**
	 * Creates a new functional term with the given functor and the given list
	 * of arguments.
	 * @param functor the functor of this term
	 * @param arguments the list of arguments of this functional term
	 */
	public FunctionalTerm(Functor functor, List<Term<?>> arguments){
		super(new Pair<Functor, List<Term<?>>>(functor, arguments), functor.getTargetSort());
	}
	
	public FunctionalTerm(FunctionalTerm other) {
		this(other.value.getFirst().clone(), new LinkedList<Term<?>>(other.value.getSecond()));
	}
	
	public Functor getFunctor() {
		return value.getFirst();
	}
	
	public List<Term<?>> getArguments() {
		return Collections.unmodifiableList(value.getSecond());
	}
	
	/**
	 * Checks whether this term is complete, i.e. whether
	 * every argument is set.
	 * @return "true" if the term is complete.
	 */
	public boolean isComplete(){
		return value.getSecond().size() == value.getFirst().getArity();
	}
	
	/**
	 * Creates a new functional term with the given functor.
	 * @param functor
	 */
	public FunctionalTerm(Functor functor){
		this(functor,new ArrayList<Term<?>>());		
	}
	
	/**
	 * Appends the given argument to this term's
	 * arguments and returns itself.
	 * @param term an argument to be added
	 * @return the term itself.
	 * @throws IllegalArgumentException if the given term does not correspond
	 *   to the expected sort or the argument list is complete.
	 */
	public FunctionalTerm addArgument(Term<?> term) throws IllegalArgumentException{
		if(getArguments().size() == getFunctor().getArity())
			throw new IllegalArgumentException("No more arguments expected.");
		if(!getFunctor().getArgumentTypes().get(getArguments().size()).equals(term.getSort())) {
			throw new IllegalArgumentException("The sort \"" + term.getSort() + "\" of the given term does " +
					"not correspond to the expected sort \"" + 
					getFunctor().getArgumentTypes().get(getArguments().size()) + "\"." );
		}
		value.getSecond().add(term);		
		return this;
	}
	
	@Override
	public Set<Term<?>> getTerms() {
		Set<Term<?>> reval = super.getTerms();
		for(Term<?> t: this.getArguments())
			reval.addAll(t.getTerms());
		return reval;
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		Set<C> reval = super.getTerms(cls);
		for(Term<?> t: this.getArguments())
			reval.addAll(t.getTerms(cls));
		return reval;
	}

	@Override
	public Term<?> substitute(Term<?> v, Term<?> t)
			throws IllegalArgumentException {
		if(!v.getSort().equals(t.getSort()))
			throw new IllegalArgumentException("Cannot replace " + v + " by " + t + " because " + v +
					" is of sort " + v.getSort() + " while " + t + " is of sort " + t.getSort() + ".");
		if(v.equals(this)) return t;
		return this;
	}

	@Override
	public String toString(){
		String output = getFunctor().getName();
		if(getArguments().size() == 0) return output;
		output += "(";
		output += getArguments().get(0);
		for(int i = 1; i < getArguments().size(); i++)
			output += ","+getArguments().get(i);
		output += ")";
		return output;
	}
	
	@Override
	public FunctionalTerm clone() {
		return new FunctionalTerm(this);
	}
}
