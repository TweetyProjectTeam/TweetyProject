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
package org.tweetyproject.arg.rankings.reasoner;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.LatticePartialOrder;
import org.tweetyproject.commons.util.SetTools;

/**
 * This class implements the argument ranking approach of
 * [Grossi, Modgil. On the Graded Acceptability of Arguments. IJCAI 2015]
 * 
 * The used algorithms are simple brute force search algorithms.
 *  
 * @author Matthias Thimm
 */
public class IteratedGradedDefenseReasoner extends AbstractRankingReasoner<LatticePartialOrder<Argument, DungTheory>>{

	/**
	 * Determines the number of attackers from x to y.
	 * @param theory a Dung theory.
	 * @param y some argument
	 * @param x some set of arguments
	 * @return  the number of attackers from x to y.
	 */
	private int numOfAttackers(DungTheory theory, Argument y, Collection<Argument> x){
		int num = 0;
		for(Argument a: x)
			if(theory.isAttackedBy(y, a))
				num++;
		return num;
	}
	
	/**
	 * Implements the graded defense function from Def. 5, i.e.
	 * gradedDefense_m_n(X) = { x | there are not more than m different y s.t.
	 * y attacks x and there are not more than n different z s.t. z attacks
	 * y and z is in X}
	 * @param theory a Dung theory
	 * @param args some set of arguments
	 * @param m some integer (indicating the number of attackers)
	 * @param n some integer (indicating the number of attackers of attackers)
	 * @return the set of arguments mn-defended by the given set of arguments
	 */
	public Collection<Argument> gradedDefense(DungTheory theory, Collection<Argument> args, int m, int n){
		Collection<Argument> result = new HashSet<>();
		for(Argument arg: theory){
			int numAttackers = 0;
			for(Argument attacker: theory.getAttackers(arg))
				if(this.numOfAttackers(theory,attacker, args) < n)
					numAttackers++;
			if(numAttackers < m)
				result.add(arg);
		}
		return result;
	}
	
	/**
	 * Implements the graded neutrality function from Def. 6, i.e.
	 * gradedNeutrality_m(X) = {x | there are less than m arguments y
	 * from X that attack x} 
	 * @param theory a Dung theory
	 * @param args some set of arguments
	 * @param m some integer (the number of attackers)
	 * @return the set of arguments m-neutral to args.
	 */
	public Collection<Argument> gradedNeutrality(DungTheory theory, Collection<Argument> args, int m){
		Collection<Argument> result = new HashSet<>();
		for(Argument arg: theory)
			if(this.numOfAttackers(theory,arg, args) < m)
				result.add(arg);
		return result;
	}
	
	/**
	 * Checks whether the given set of arguments is m-conflict-free, cf. 
	 * Def. 9.
	 * @param theory a Dung theory
	 * @param args some set of arguments 
	 * @param m some integer
	 * @return "true" iff args is m-conflict-free
	 */
	public boolean isMConflictFree(DungTheory theory, Collection<Argument> args, int m){
		if(!this.gradedNeutrality(theory, args, m).containsAll(args))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given set of arguments is mn-admissible, cf. 
	 * Def. 9.
	 * @param theory a Dung theory
	 * @param args some set of arguments 
	 * @param m some integer
	 * @param n some integer 
	 * @return "true" iff args is mn-admissible
	 */
	public boolean isMNAdmissible(DungTheory theory, Collection<Argument> args, int m, int n){
		if(!this.gradedNeutrality(theory, args, m).containsAll(args))
			return false;
		if(!this.gradedDefense(theory,args, m, n).containsAll(args))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given set of arguments is mn-complete, cf. 
	 * Def. 9.
	 * @param theory a Dung theory
	 * @param args some set of arguments 
	 * @param m some integer
	 * @param n some integer 
	 * @return "true" iff args is mn-complete
	 */
	public boolean isMNComplete(DungTheory theory, Collection<Argument> args, int m, int n){
		if(!this.gradedNeutrality(theory, args, m).containsAll(args))
			return false;
		if(!this.gradedDefense(theory, args, m, n).equals(args))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given set of arguments is m-stable, cf. 
	 * Def. 9.
	 * @param theory a Dung theory
	 * @param args some set of arguments 
	 * @param m some integer
	 * @return "true" iff args is m-stable
	 */
	public boolean isMStable(DungTheory theory, Collection<Argument> args, int m){
		if(!this.gradedNeutrality(theory, args, m).equals(args))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given set of arguments is mn-grounded, cf. 
	 * Def. 9.
	 * @param theory a Dung theory
	 * @param args some set of arguments 
	 * @param m some integer
	 * @param n some integer 
	 * @return "true" iff args is mn-grounded
	 */
	public boolean isMNGrounded(DungTheory theory, Collection<Argument> args, int m, int n){
		return this.getAllMNGroundedExtensions(theory, m, n).contains(new Extension<DungTheory>(args));
	}
	
	/**
	 * Checks whether the given set of arguments is mn-preferred, cf. 
	 * Def. 9.
	 * @param theory a Dung theory
	 * @param args some set of arguments 
	 * @param m some integer
	 * @param n some integer 
	 * @return "true" iff args is mn-preferred
	 */
	public boolean isMNPreferred(DungTheory theory, Collection<Argument> args, int m, int n){
		return this.getAllMNPreferredExtensions(theory, m, n).contains(new Extension<DungTheory>(args));
	}
	
	/**
	 * Returns all mn-complete extensions.
	 * @param theory a Dung theory
	 * @param m some integer
	 * @param n some integer
	 * @return all mn-complete extensions.
	 */
	public Collection<Extension<DungTheory>> getAllMNCompleteExtensions(DungTheory theory, int m, int n){
		Collection<Extension<DungTheory>> result = new HashSet<>();
		for(Collection<Argument> set : new SetTools<Argument>().subsets(theory)){
			if(this.isMNComplete(theory, set, m, n))
				result.add(new Extension<DungTheory>(set));
		}
		return result;
	}
	
	/**
	 * Returns all mn-preferred extensions.
	 * @param theory a Dung theory
	 * @param m some integer
	 * @param n some integer
	 * @return all mn-preferred extensions.
	 */
	public Collection<Extension<DungTheory>> getAllMNPreferredExtensions(DungTheory theory, int m, int n){
		Collection<Extension<DungTheory>> result = new HashSet<>();
		Collection<Extension<DungTheory>> complete_extensions  = this.getAllMNCompleteExtensions(theory, m, n);
		for(Extension<DungTheory> ext1: complete_extensions){
			boolean isMaximal = true;
			for(Extension<DungTheory> ext2: complete_extensions)
				if(ext1 != ext2){
					if(ext2.containsAll(ext1)){
						isMaximal = false;
						break;
					}
				}
			if(isMaximal)
				result.add(ext1);
		}
		return result;
	}
	
	/**
	 * Returns all mn-grounded extensions.
	 * @param theory a Dung theory
	 * @param m some integer
	 * @param n some integer
	 * @return all mn-grounded extensions.
	 */
	public Collection<Extension<DungTheory>> getAllMNGroundedExtensions(DungTheory theory, int m, int n){
		Collection<Extension<DungTheory>> result = new HashSet<>();
		Collection<Extension<DungTheory>> complete_extensions  = this.getAllMNCompleteExtensions(theory, m, n);
		for(Extension<DungTheory> ext1: complete_extensions){
			boolean isMinimal = true;
			for(Extension<DungTheory> ext2: complete_extensions)
				if(ext1 != ext2){
					if(ext1.containsAll(ext2)){
						isMinimal = false;
						break;
					}
				}
			if(isMinimal)
				result.add(ext1);
		}
		return result;
	}
	
	/**
	 * Returns all m-stable extensions.
	 * @param theory a Dung theory
	 * @param m some integer
	 * @return all m-stable extensions.
	 */
	public Collection<Extension<DungTheory>> getAllMStableExtensions(DungTheory theory, int m){
		Collection<Extension<DungTheory>> result = new HashSet<>();
		for(Collection<Argument> set : new SetTools<Argument>().subsets(theory)){
			if(this.isMStable(theory, set, m))
				result.add(new Extension<DungTheory>(set));
		}
		return result;
	}
	
	/**
	 * Checks whether the given argument "y" is contained in all sets of arguments
	 * in "args". 
	 * @param y some argument
	 * @param exts some set of sets of arguments
	 * @return "true" iff the given argument "y" is contained in all sets of arguments
	 * in "args". 
	 */
	private boolean isContainedInAll(Argument y, Collection<Extension<DungTheory>> exts){
		for(Collection<Argument> set: exts)
			if(!set.contains(y))
				return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModels(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public Collection<LatticePartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
		Collection<LatticePartialOrder<Argument, DungTheory>> ranks = new HashSet<LatticePartialOrder<Argument, DungTheory>>();
		ranks.add(this.getModel(bbase));
		return ranks;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.ModelProvider#getModel(org.tweetyproject.commons.BeliefBase)
	 */
	@Override
	public LatticePartialOrder<Argument, DungTheory> getModel(DungTheory bbase) {
		// compute all mn-complete extensions for all m,n
		Map<Point,Collection<Extension<DungTheory>>> allExt = new HashMap<>();
		for(int m = 1; m < ((DungTheory)bbase).size(); m++)
			for(int n=1; n < ((DungTheory)bbase).size(); n++)
				allExt.put(new Point(m,n), this.getAllMNCompleteExtensions(((DungTheory)bbase), m, n));
		LatticePartialOrder<Argument, DungTheory> ranking = new LatticePartialOrder<Argument, DungTheory>(((DungTheory)bbase));
		for(Argument a: ((DungTheory)bbase))
			for(Argument b: ((DungTheory)bbase))
				if(a != b){
					boolean aImpliesB = true;
					boolean bImpliesA = true;
					for(Collection<Extension<DungTheory>> exts: allExt.values()){
						if(this.isContainedInAll(a, exts) && !this.isContainedInAll(b, exts))
							aImpliesB = false;
						if(this.isContainedInAll(b, exts) && !this.isContainedInAll(a, exts))
							bImpliesA = false;
						if(!aImpliesB && !bImpliesA)
							break;
					}
					if(aImpliesB)
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					if(bImpliesA)
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
				}
		return ranking;
	}

	/**natively installed*/
	@Override
	public boolean isInstalled() {
		return true;
	}

    /** Default Constructor */
    public IteratedGradedDefenseReasoner(){}
}
