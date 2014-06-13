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
package net.sf.tweety.logics.pl.semantics;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.InterpretationSet;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Wrapper for the PossibleWorld providing better representation mechanisms, it knows all
 * the possible propositions (the signature) and therefore provides a complete representation that 
 * maps a boolean to the Proposition.
 * 
 * It also provides ordering of the (proposition, boolean) pairs in the enhanced representation.
 * The default behavior is an alphabetically ordering of the Propositions but it can be changed
 * by the user.
 * 
 * Internally it uses the smaller and therefore faster implementation of PossibleWorld for satisfication test
 * @author Tim Janus
 */
public class NicePossibleWorld 
	extends InterpretationSet<Proposition> 
	implements Comparator<Pair<Proposition, Boolean>> {
	
	/** 
	 * Implementation of possible world with a small memory print and 
	 * fast satisfication tests.
	 */
	private PossibleWorld world;
	
	/**
	 * The set saving all the proposition boolean pairs that represent this
	 * world assuming the current signature.
	 */
	private SortedSet<Pair<Proposition, Boolean>> representation;
	
	/**
	 * Ctor: Generates the possible world using the given set of true propositions and
	 * 			generated a representation of the possible world using the given signature.
	 * 			The default alphabetically sort behavior is used to order the representation.
	 * @param trueInWorld	A set of propositions describing those propositions that are
	 * 						true in this possible world. It has to be a subset of 
	 * 						signature.
	 * @param signature		A set of propositions describing all know propositions by the
	 * 						the problem, the signature. It has to be a super set of
	 * 						trueInWorld
	 */
	public NicePossibleWorld(Collection<Proposition> trueInWorld, Collection<Proposition> signature) {
		this(trueInWorld, signature, null);
	}
	
	/**
	 * Ctor: Generates the possible world using the given set of true propositions and
	 * 			generated a representation of the possible world using the given signature.
	 * 			The sort behavior given in comparator is used to order the representation.
	 * @param trueInWorld	A set of propositions describing those propositions that are
	 * 						true in this possible world. It has to be a subset of 
	 * 						signature.
	 * @param signature		A set of propositions describing all know propositions by the
	 * 						the problem, the signature. It has to be a super set of
	 * 						trueInWorld
	 * 
	 */
	public NicePossibleWorld(Collection<Proposition> trueInWorld, Collection<Proposition> signature, 
			Comparator<Pair<Proposition, Boolean>> comparator) {
		this.world = new PossibleWorld(trueInWorld);
		this.representation = new TreeSet<Pair<Proposition,Boolean>>(comparator == null ? this : comparator);
		for(Proposition p : trueInWorld) {
			this.representation.add(new Pair<Proposition, Boolean>(p, true));
		}
		if(!setSignature(signature)) {
			throw new IllegalArgumentException("The World is not representable by the given set of Propositions");
		}
	}
	
	/**
	 * Changes the signature of the world. That means the set of propositions has changed.
	 * The new set might contain new propositions and delete old propositions. The
	 * representation data structure is updated to reflect the new signature. There is a
	 * border case if a Proposition is deleted that is true in this world. Then the world
	 * is not representable with the given signature anymore, if this is the case then the
	 * representation data structure is cleared.
	 * @param signature	A collection of propositions defining the new signature.
	 * @return	true if representation is successfully updated and false if the world is not
	 * 			representable by the given signature anymore.
	 */
	public boolean setSignature(Collection<Proposition> signature) {
		// First check if the Possible world is valid anymore:
		if(!signature.containsAll(world)) {
			representation.clear();
			return false;
		}

		// Second: Collect the Propositions that have to be added
		Set<Pair<Proposition, Boolean>> toAdd = new HashSet<Pair<Proposition, Boolean>>();
		for(Proposition prop : signature) {
			boolean isNew = true;
			for(Pair<Proposition, Boolean> p : representation) {
				if(prop.equals(p.getFirst())) {
					isNew = false;
					break;
				}
			}
			
			if(isNew) {
				toAdd.add(new Pair<Proposition, Boolean>(prop, false));
			}
		}
		
		// Third: Collect the Propositions that have to be removed
		Set<Pair<Proposition, Boolean>> toRemove = new HashSet<Pair<Proposition, Boolean>>();
		for(Pair<Proposition, Boolean> p : representation) {
			boolean isOld = false;
			for(Proposition prop : signature) {
				if(p.getFirst().equals(prop)) {
					isOld = true;
					break;
				}
			}
			
			if(!isOld) {
				toRemove.add(p);
			}
		}
		
		// Fourth: Update the representation Collection, it sorts automatically using the given Comparator
		representation.removeAll(toRemove);
		representation.addAll(toAdd);
		
		return true;
	}
	
	/**
	 * Changes the used comparator to sort the representation structure and resorts 
	 * the representation. The default behavior is an alphabetically ordering. 
	 * The default behavior is selected if null is given as argument or if an 
	 * instance of the NicePossibleWorld is given as argument.
	 * @param comparator	The new comparator used to sort the representation. If
	 * 						a null is given the default alphabetically ordering is
	 * 						used.
	 */
	public void setComparator(Comparator<Pair<Proposition, Boolean>> comparator) {
		if(comparator == null)
			comparator = this;
		Set<Pair<Proposition, Boolean>> temp = representation;
		representation = new TreeSet<Pair<Proposition,Boolean>>(comparator);
		representation.addAll(temp);
	}
	
	/**
	 * @return	The PossibleWorld instance that is used for satification tests etc.
	 */
	public PossibleWorld getOptimizedWorld() {
		return world;
	}
	
	/**
	 * @return	The representation structure as a set of (proposition, boolean)
	 * 			pairs.
	 */
	public Set<Pair<Proposition, Boolean>> getRepresentationStructure() {
		return Collections.unmodifiableSet(representation);
	}

	@Override
	public boolean satisfies(Formula formula) throws IllegalArgumentException {
		return world.satisfies(formula);
	}

	@Override
	public boolean satisfies(BeliefBase beliefBase)
			throws IllegalArgumentException {
		return world.satisfies(beliefBase);
	}
	
	@Override
	public boolean equals(Object other) {
		if(! (other instanceof NicePossibleWorld)) 
			return false;
		
		NicePossibleWorld pw = (NicePossibleWorld)other;
		return world.equals(pw.world);
	}

	@Override
	public int hashCode() {
		return world.hashCode();
	}
	
	@Override
	public String toString() {
		String reval = "";
		for(Pair<Proposition, Boolean> p : representation) {
			reval += (p.getSecond() ? " " : "-") + p.getFirst().toString();
		}
		return reval;
	}
	
	@Override
	public int compare(Pair<Proposition, Boolean> o1,
			Pair<Proposition, Boolean> o2) {
		
		int reval = o1.getFirst().compareTo(o2.getFirst());
		
		// this is important for providing the same functionality like equals:
		if(reval == 0) {
			return o1.getSecond().compareTo(o2.getSecond());
		}
		
		// but we priorize alphabetically ordering:
		return reval;
	}
	
	public static Set<NicePossibleWorld> getAllPossibleWorlds(Collection<Proposition> signature){
		Set<PossibleWorld> base = PossibleWorld.getAllPossibleWorlds(signature);
		Set<NicePossibleWorld> reval = new HashSet<NicePossibleWorld>();
		for(PossibleWorld pw : base) {
			reval.add(new NicePossibleWorld(pw, signature));
		}
		return reval;
	}
}
