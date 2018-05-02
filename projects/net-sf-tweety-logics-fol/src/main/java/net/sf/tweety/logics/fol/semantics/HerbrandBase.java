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
package net.sf.tweety.logics.fol.semantics;

import java.util.*;

import net.sf.tweety.commons.util.*;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.*;


/**
 * The Herbrand base is the set of all possible ground atoms of some
 * given first-order logic.
 * <br/>
 * NOTE: We only allow to define a Herbrand base for signatures without
 *   function symbols.
 * @author Matthias Thimm
 */
public class HerbrandBase {

	/**
	 * The atoms of this Herbrand base. 
	 */
	private Set<FOLAtom> atoms;
	
	/**
	 * Creates a new Herbrand base for the given signature.
     * <br/>
     * NOTE: We only allow to define a Herbrand base for signatures without
     *   function symbols.
	 * @param sig the underlying first-order signature for
	 * 	this Herbrand base. There should be no functors defined in "sig"
	 * @throws IllegalArgumentationException if "sig" contains a functor.
	 */
	public HerbrandBase(FolSignature sig) throws IllegalArgumentException{
		if(!sig.getFunctors().isEmpty()) throw new IllegalArgumentException("The Herbrand base is defined only for signatures without functors.");
		this.atoms = new HashSet<FOLAtom>();
		for(Predicate p: sig.getPredicates()){
			if(p.getArity() == 0) this.atoms.add(new FOLAtom(p));
			this.atoms.addAll(this.getAllInstantiations(sig, p, new ArrayList<Term<?>>()));
		}
	}
	
	/**
	 * Computes all instantiations of the predicate "p" relative to the signature "sig"
	 * where "arguments" defines the first arguments of the atoms.
	 * @param sig a signature for which the instantiations should be computed.
	 * @param p the predicate of the atoms.
	 * @param arguments the currently set arguments of the atoms.
	 * @return the complete set of instantiations of "p" relative to "sig" and "arguments".
	 */
	private Set<FOLAtom> getAllInstantiations(FolSignature sig, Predicate p, List<Term<?>> arguments){
		if(p.getArity() == arguments.size()){
			Set<FOLAtom> atoms = new HashSet<FOLAtom>();
			atoms.add(new FOLAtom(p,arguments));
			return atoms;
		}
		Sort currentSort = p.getArgumentTypes().get(arguments.size());
		Set<FOLAtom> atoms = new HashSet<FOLAtom>();
		for(Term<?> c: sig.getConstants()){
			if(!c.getSort().equals(currentSort))
				continue;
			List<Term<?>> newArguments = new ArrayList<Term<?>>(arguments);
			newArguments.add(c);
			atoms.addAll(this.getAllInstantiations(sig, p, newArguments));
		}		
		return atoms;
	}
		
	/**
	 * Computes all possible Herbrand interpretations of this Herbrand
	 * base, i.e. all possible subsets of this Herbrand base.
	 * @return all possible Herbrand interpretations of this Herbrand
	 * base, i.e. all possible subsets of this Herbrand base.
	 */
	public Set<HerbrandInterpretation> getAllHerbrandInterpretations(){
		Set<HerbrandInterpretation> interpretations = new HashSet<HerbrandInterpretation>();
		Set<Set<FOLAtom>> subsets = new SetTools<FOLAtom>().subsets(this.getAtoms());
		for(Set<FOLAtom> atoms: subsets)
			interpretations.add(new HerbrandInterpretation(atoms));		
		return interpretations;
	}
	
	/**
	 * Returns all atoms of this Herbrand base.
	 * @return all atoms of this Herbrand base.
	 */
	public Set<FOLAtom> getAtoms(){
		return new HashSet<FOLAtom>(this.atoms);
	}
}
