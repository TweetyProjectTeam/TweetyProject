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
package net.sf.tweety.arg.dung;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.sf.tweety.arg.dung.semantics.ArgumentRanking;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.LatticeArgumentRanking;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.commons.util.SetTools;

/**
 * This class implements the argument ranking approach of
 * [Grossi, Modgil. On the Graded Acceptability of Arguments. IJCAI 2015]
 * 
 * The used algorithms are simple brute force search algorithms.
 *  
 * @author Matthias Thimm
 */
public class GrossiModgilRankingReasoner extends Reasoner{

	/**
	 * Creates a new reasoner for the given Dung theory
	 * @param theory
	 */
	public GrossiModgilRankingReasoner(DungTheory theory) {
		super(theory);
	}

	/**
	 * Determines the number of attackers from x to y.
	 * @param y some argument
	 * @param x some set of arguments
	 * @return  the number of attackers from x to y.
	 */
	private int numOfAttackers(Argument y, Collection<Argument> x){
		int num = 0;
		for(Argument a: x)
			if(((DungTheory)this.getKnowledgeBase()).isAttackedBy(y, a))
				num++;
		return num;
	}
	
	/**
	 * Implements the graded defense function from Def. 5, i.e.
	 * gradedDefense_m_n(X) = { x | there are not more than m different y s.t.
	 * y attacks x and there are not more than n different z s.t. z attacks
	 * y and z is in X}
	 * @param args some set of arguments
	 * @param m some integer (indicating the number of attackers)
	 * @param n some integer (indicating the number of attackers of attackers)
	 * @return the set of arguments mn-defended by the given set of arguments
	 */
	public Collection<Argument> gradedDefense(Collection<Argument> args, int m, int n){
		DungTheory theory = ((DungTheory)this.getKnowledgeBase());
		Collection<Argument> result = new HashSet<>();
		for(Argument arg: theory){
			int num_attackers = 0;
			for(Argument attacker: theory.getAttackers(arg))
				if(this.numOfAttackers(attacker, args) < n)
					num_attackers++;
			if(num_attackers < m)
				result.add(arg);
		}
		return result;
	}
	
	/**
	 * Implements the graded neutrality function from Def. 6, i.e.
	 * gradedNeutrality_m(X) = {x | there are less than m arguments y
	 * from X that attack x} 
	 * @param args some set of arguments
	 * @param m some integer (the number of attackers)
	 * @return the set of arguments m-neutral to args.
	 */
	public Collection<Argument> gradedNeutrality(Collection<Argument> args, int m){
		DungTheory theory = ((DungTheory)this.getKnowledgeBase());
		Collection<Argument> result = new HashSet<>();
		for(Argument arg: theory)
			if(this.numOfAttackers(arg, args) < m)
				result.add(arg);
		return result;
	}
	
	/**
	 * Checks whether the given set of arguments is m-conflict-free, cf. 
	 * Def. 9.
	 * @param args some set of arguments 
	 * @param m some integer
	 * @return "true" iff args is m-conflict-free
	 */
	public boolean isMConflictFree(Collection<Argument> args, int m){
		if(!this.gradedNeutrality(args, m).containsAll(args))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given set of arguments is mn-admissible, cf. 
	 * Def. 9.
	 * @param args some set of arguments 
	 * @param m some integer
	 * @param n some integer 
	 * @return "true" iff args is mn-admissible
	 */
	public boolean isMNAdmissible(Collection<Argument> args, int m, int n){
		if(!this.gradedNeutrality(args, m).containsAll(args))
			return false;
		if(!this.gradedDefense(args, m, n).containsAll(args))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given set of arguments is mn-complete, cf. 
	 * Def. 9.
	 * @param args some set of arguments 
	 * @param m some integer
	 * @param n some integer 
	 * @return "true" iff args is mn-complete
	 */
	public boolean isMNComplete(Collection<Argument> args, int m, int n){
		if(!this.gradedNeutrality(args, m).containsAll(args))
			return false;
		if(!this.gradedDefense(args, m, n).equals(args))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given set of arguments is m-stable, cf. 
	 * Def. 9.
	 * @param args some set of arguments 
	 * @param m some integer
	 * @return "true" iff args is m-stable
	 */
	public boolean isMStable(Collection<Argument> args, int m){
		if(!this.gradedNeutrality(args, m).equals(args))
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given set of arguments is mn-grounded, cf. 
	 * Def. 9.
	 * @param args some set of arguments 
	 * @param m some integer
	 * @param n some integer 
	 * @return "true" iff args is mn-grounded
	 */
	public boolean isMNGrounded(Collection<Argument> args, int m, int n){
		return this.getAllMNGroundedExtensions(m, n).contains(new Extension(args));
	}
	
	/**
	 * Checks whether the given set of arguments is mn-preferred, cf. 
	 * Def. 9.
	 * @param args some set of arguments 
	 * @param m some integer
	 * @param n some integer 
	 * @return "true" iff args is mn-preferred
	 */
	public boolean isMNPreferred(Collection<Argument> args, int m, int n){
		return this.getAllMNPreferredExtensions(m, n).contains(new Extension(args));
	}
	
	/**
	 * Returns all mn-complete extensions.
	 * @param m some integer
	 * @param n some integer
	 * @return all mn-complete extensions.
	 */
	public Collection<Extension> getAllMNCompleteExtensions(int m, int n){
		DungTheory theory = ((DungTheory)this.getKnowledgeBase());
		Collection<Extension> result = new HashSet<>();
		for(Collection<Argument> set : new SetTools<Argument>().subsets(theory)){
			if(this.isMNComplete(set, m, n))
				result.add(new Extension(set));
		}
		return result;
	}
	
	/**
	 * Returns all mn-preferred extensions.
	 * @param m some integer
	 * @param n some integer
	 * @return all mn-preferred extensions.
	 */
	public Collection<Extension> getAllMNPreferredExtensions(int m, int n){
		Collection<Extension> result = new HashSet<>();
		Collection<Extension> complete_extensions  = this.getAllMNCompleteExtensions(m, n);
		for(Extension ext1: complete_extensions){
			boolean isMaximal = true;
			for(Extension ext2: complete_extensions)
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
	 * @param m some integer
	 * @param n some integer
	 * @return all mn-grounded extensions.
	 */
	public Collection<Extension> getAllMNGroundedExtensions(int m, int n){
		Collection<Extension> result = new HashSet<>();
		Collection<Extension> complete_extensions  = this.getAllMNCompleteExtensions(m, n);
		for(Extension ext1: complete_extensions){
			boolean isMinimal = true;
			for(Extension ext2: complete_extensions)
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
	 * @param m some integer
	 * @param n some integer
	 * @return all m-stable extensions.
	 */
	public Collection<Extension> getAllMStableExtensions(int m){
		DungTheory theory = ((DungTheory)this.getKnowledgeBase());
		Collection<Extension> result = new HashSet<>();
		for(Collection<Argument> set : new SetTools<Argument>().subsets(theory)){
			if(this.isMStable(set, m))
				result.add(new Extension(set));
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
	private boolean isContainedInAll(Argument y, Collection<Extension> exts){
		for(Collection<Argument> set: exts)
			if(!set.contains(y))
				return false;
		return true;
	}
	
	/**
	 * Returns the ranking wrt. complete semantics, cf. Def. 10 
	 * @return the ranking wrt. complete semantics, cf. Def. 10
	 */
	public ArgumentRanking getCompleteRanking(){
		DungTheory theory = ((DungTheory)this.getKnowledgeBase());
		// compute all mn-complete extensions for all m,n
		Map<Point,Collection<Extension>> allExt = new HashMap<>();
		for(int m = 1; m < theory.size(); m++)
			for(int n=1; n < theory.size(); n++)
				allExt.put(new Point(m,n), this.getAllMNCompleteExtensions(m, n));
		LatticeArgumentRanking ranking = new LatticeArgumentRanking(theory);
		for(Argument a: theory)
			for(Argument b: theory)
				if(a != b){
					boolean a_implies_b = true;
					boolean b_implies_a = true;
					for(Collection<Extension> exts: allExt.values()){
						if(this.isContainedInAll(a, exts) && !this.isContainedInAll(b, exts))
							a_implies_b = false;
						if(this.isContainedInAll(b, exts) && !this.isContainedInAll(a, exts))
							b_implies_a = false;
						if(!a_implies_b && !b_implies_a)
							break;
					}
					if(a_implies_b)
						ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
					if(b_implies_a)
						ranking.setStrictlyLessOrEquallyAcceptableThan(b, a);
				}
		return ranking;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		throw new UnsupportedOperationException("Implement me");
	}

}
