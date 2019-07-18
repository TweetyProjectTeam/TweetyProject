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
package net.sf.tweety.logics.ml.semantics;

import java.util.*;

import net.sf.tweety.commons.util.*;
import net.sf.tweety.logics.fol.semantics.HerbrandBase;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * Modified version of HerbrandBase that allows for modal formulas.
 * 
 * The Herbrand base is the set of all possible ground atoms of some
 * given first-order logic.
 * <br>
 * NOTE: We only allow to define a Herbrand base for signatures without
 *   function symbols.
 * @author Matthias Thimm
 * @author Anna Gessler
 * @see net.sf.tweety.logics.fol.semantics.HerbrandBase
 */
public class MlHerbrandBase {

	private HerbrandBase hBase;

	/**
	 * Creates a new Herbrand base for the given signature.
     * <br>
     * NOTE: We only allow to define a Herbrand base for signatures without
     *   function symbols.
	 * @param sig the underlying first-order signature for
	 * 	this Herbrand base. There should be no functors defined in "sig"
	 * @throws IllegalArgumentException if "sig" contains a functor.
	 * @see net.sf.tweety.logics.fol.semantics.HerbrandBase#HerbrandBase(FolSignature sig)
	 */
	public MlHerbrandBase(FolSignature sig) throws IllegalArgumentException{
		this.hBase = new HerbrandBase(sig);
	}
	

	/**
	 * Computes all possible Herbrand interpretations of this Herbrand
	 * base, i.e. all possible subsets of this Herbrand base.
	 * @return all possible Herbrand interpretations of this Herbrand
	 * base, i.e. all possible subsets of this Herbrand base.
	 */
	public Set<MlHerbrandInterpretation> getAllHerbrandInterpretations(){
		Set<MlHerbrandInterpretation> interpretations = new HashSet<MlHerbrandInterpretation>();
		Set<Set<FolAtom>> subsets = new SetTools<FolAtom>().subsets(this.getAtoms());
		for(Set<FolAtom> atoms: subsets)
			interpretations.add(new MlHerbrandInterpretation(atoms));		
		return interpretations;
	}
	
	/**
	 * Returns all atoms of this Herbrand base.
	 * @return all atoms of this Herbrand base.
	 * @see net.sf.tweety.logics.fol.semantics.HerbrandBase#getAtoms()
	 */
	public Set<FolAtom> getAtoms(){
		return hBase.getAtoms();
	}
}
