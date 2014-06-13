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
package net.sf.tweety.arg.saf.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This class models a basic argument in structured argumentation frameworks, i.e.
 * a claim (a proposition) together with a support (a set of propositions) where
 * the claim is not in the support.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 *
 */
public class BasicArgument extends Argument implements Rule<Proposition, Proposition> {

	/**
	 * The claim of this basic argument. 
	 */
	private Proposition claim;
	
	/**
	 * The support of this basic argument.
	 */
	private Set<Proposition> support;
	
	/**
	 * Deprecated for basic arguments.
	 * @param name
	 */
	@Deprecated
	public BasicArgument(String name){
		super(name);
		throw new IllegalArgumentException("Illegal constructor call for a basic argument");
	}
	
	/**
	 * Creates a new basic argument with the given claim
	 * and empty support.
	 * @param claim a proposition.
	 */
	public BasicArgument(Proposition claim){
		this(claim, new HashSet<Proposition>());
	}
	
	/**
	 * Creates a new basic argument with the given claim
	 * and the given support.
	 * @param claim a proposition
	 * @param support a set of propositions
	 */
	public BasicArgument(Proposition claim, Set<Proposition> support){
		super("<" + claim + "," + support + ">");
		if(support.contains(claim))
			throw new IllegalArgumentException("Claim is contained in support.");
		this.claim = claim;
		this.support = support;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.util.rules.Rule#getConclusion()
	 */
	public Proposition getConclusion(){
		return this.claim;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.util.rules.Rule#getPremise()
	 */
	public Set<Proposition> getPremise(){
		return this.support;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.dung.syntax.Argument#getSignature()
	 */
	public PropositionalSignature getSignature() {
		PropositionalSignature sig = new PropositionalSignature();
		sig.add(this.claim);
		for(Proposition p: this.support)
			sig.add(p);
		return sig;
	}

	@Override
	public boolean isFact() {
		return support.isEmpty();
	}

	@Override
	public boolean isConstraint() {
		return false;
	}

	@Override
	public void setConclusion(Proposition conclusion) {
		this.claim = conclusion;
	}

	@Override
	public void addPremise(Proposition premise) {
		this.support.add(premise);
	}

	@Override
	public void addPremises(Collection<? extends Proposition> premises) {
		this.support.addAll(premises);
	}

}
