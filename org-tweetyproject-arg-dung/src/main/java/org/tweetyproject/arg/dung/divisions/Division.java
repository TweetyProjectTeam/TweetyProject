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
package org.tweetyproject.arg.dung.divisions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.graphs.Graph;

/**
 * A pair &lt;P,V&gt; is a division of AAF G w.r.t. semantics X iff there is an X-extension E
 * such that P\subseteq E and E \cap V = {}, cf. [Hunter,Thimm,2014].
 * 
 * @author Matthias Thimm
 */
public class Division extends Pair<Extension<DungTheory>,Extension<DungTheory>>{
	private static final long serialVersionUID = -4217044135734274436L;

	/** archive of requested divisions. */
	private static Map<DungTheory,Collection<Division>> archivedDivisons = new HashMap<DungTheory,Collection<Division>>();
	
	/**
	 * Creates a new division for the given parameters.
	 * @param p some set of arguments.
	 * @param v some set of arguments
	 */
	public Division(Extension<DungTheory> p, Extension<DungTheory> v){
		super(p,v);
	}
	
	/**
	 * Checks whether this division is valid wrt. some of the given extensions
	 * @param exts a collection of extensions.
	 * @return "true" if this division is valid.
	 */
	public boolean isValid(Collection<Extension<DungTheory>> exts){
		for(Extension<DungTheory> e: exts)
			if(this.isValid(e))
				return true;
		return false;
	}
	
	/**
	 * Checks whether this division is valid wrt. the given extension.
	 * @param ext some extension
	 * @return "true" iff this division is valid.
	 */
	public boolean isValid(Extension<DungTheory> ext){
		if(!ext.containsAll(this.getFirst()))
			return false;
		Set<Argument> tmp = new HashSet<Argument>(ext);
		tmp.retainAll(this.getSecond());
		if(tmp.size()>0)
			return false;
		return true;
	}
	
	/**
	 * Returns the dividers for this division, i.e. all sub-theories of the given
	 * theory such that this division is in that sub-theories set of divisions.
	 * @param theory some argumentation framework
	 * @param semantics some semantics
	 * @return the set of dividers of this devision
	 */
	public Collection<DungTheory> getDividers(DungTheory theory, Semantics semantics){
		Collection<DungTheory> result = new HashSet<DungTheory>();
		Collection<Graph<Argument>> subtheories = theory.getSubgraphs();
		for(Graph<Argument> g: subtheories){
			DungTheory sub = new DungTheory(g);
			for(Division d: Division.getDivisions(AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics).getModels(sub), sub)){
				if(d.getFirst().equals(this.getFirst())){
					Extension<DungTheory> e = new Extension<DungTheory>(this.getSecond());
					e.retainAll(sub);
					if(e.equals(d.getSecond()))
						result.add(sub);					
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns all divisions of all given extensions and the given aaf.
	 * @param exts a collection of extensions.
	 * @param aaf a Dung theory
	 * @return the set of divisions of all extensions returned by the given reasoner.
	 */
	public static Collection<Division> getDivisions(Collection<Extension<DungTheory>> exts, DungTheory aaf){
		Collection<Division> result = new HashSet<Division>();
		for(Extension<DungTheory> e: exts)
			result.addAll(Division.getDivisions(e, aaf));
		return result;
	}
	
	/**
	 * Returns all divisions of aaf that arise from the given extension.
	 * @param ext some extension 
	 * @param aaf some Dung theory
	 * @return the set of divisions of aaf that arise from the given extension.
	 */
	public static Collection<Division> getDivisions(Extension<DungTheory> ext, DungTheory aaf){
		Collection<Division> result = new HashSet<Division>();
		Collection<Argument> remaining = new HashSet<Argument>(aaf);
		remaining.removeAll(ext);
		SetTools<Argument> setTools = new SetTools<Argument>();
		Set<Set<Argument>> subsetsExt = setTools.subsets(ext);
		Set<Set<Argument>> subsetsRem = setTools.subsets(remaining);		
		for(Set<Argument> p: subsetsExt)
			for(Set<Argument> v: subsetsRem)
				result.add(new Division(new Extension<DungTheory>(p),new Extension<DungTheory>(v)));
		return result;		
	}
	
	/**
	 * Checks whether the given set of divisions is exhaustive wrt. the given theory.
	 * @param divisions a set of divisions.
	 * @param theory some aaf.
	 * @param semantics some semantics
	 * @return "true" if the given set of divisions is exhaustive.
	 */
	public static boolean isExhaustive(Collection<Division> divisions, DungTheory theory, Semantics semantics){
		Collection<Graph<Argument>> subgraphs = theory.getSubgraphs();
		// convert to Dung theories
		Collection<DungTheory> subtheories = new HashSet<DungTheory>();
		for(Graph<Argument> g: subgraphs)
			subtheories.add(new DungTheory(g));
		for(Division d: divisions){
			subtheories.removeAll(d.getDividers(theory, semantics));			
		}
		return subtheories.isEmpty();
	}
	
	/**
	 * Checks whether the given set o divisions is disjoint wrt. the given theory.
	 * @param divisions a set of divisions
	 * @param theory some aaf
	 * @param semantics some semantics
	 * @return "true" if the given set of divisions is disjoint. 
	 */
	public static boolean isDisjoint(Collection<Division> divisions, DungTheory theory, Semantics semantics){
		for(Division d1: divisions){
			Collection<DungTheory> dividers1 = d1.getDividers(theory, semantics);
			for(Division d2: divisions){
				if(!d1.equals(d2)){					
					Collection<DungTheory> dividers1a = new HashSet<DungTheory>(dividers1);
					dividers1a.retainAll(d2.getDividers(theory, semantics));
					if(!dividers1a.isEmpty()){
						return false;
					}
				}
			}
		}		
		return true;
	}
	
	/**
	 * Returns the standard set of divisions of the given argumentation theory, i.e.
	 * the set of all divisions of the form (A,Arg\A) where A\subseteq Arg and Arg is the
	 * set of arguments of the given theory.
	 * @param theory some theory.
	 * @return the standard set of divisions.
	 */
	public static Collection<Division> getStandardDivisions(DungTheory theory){
		if(Division.archivedDivisons.containsKey(theory))
			return Division.archivedDivisons.get(theory);
		Collection<Division> result = new HashSet<Division>();
		for(Set<Argument> args: new SetTools<Argument>().subsets(theory)){
			Collection<Argument> retainer = new HashSet<Argument>(theory);
			retainer.removeAll(args);
			result.add(new Division(new Extension<DungTheory>(args), new Extension<DungTheory>(retainer)));
		}
		Division.archivedDivisons.put(theory, result);
		return result;
	}
}
