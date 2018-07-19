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
package net.sf.tweety.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.util.MapTools;

/**
 * This reasoner for Dung theories performs inference on the CF2 extensions.
 * @author Matthias Thimm
 *
 */
public class SimpleCF2Reasoner extends AbstractExtensionReasoner {

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModels(net.sf.tweety.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Collection<Extension> getModels(DungTheory bbase) {
		Collection<Collection<Argument>> sccs = bbase.getStronglyConnectedComponents();
		Set<Extension> extensions = new HashSet<Extension>();
		if(sccs.size() == 1){
			// an extension for a single scc is a conflict-free set with maximal arguments
			extensions = this.singleAFExtensions(bbase);
		}else{
			// for all strongly connected components, restrict the argumentation framework and get the corresponding extensions
			Map<Collection<Argument>,Set<Extension>> exts = new HashMap<Collection<Argument>,Set<Extension>>();
			for(Collection<Argument> scc: sccs){
				Collection<Argument> t = new HashSet<Argument>(scc);
				t.addAll(this.getOutparents(bbase, scc));
				DungTheory restTheory = new DungTheory(bbase.getRestriction(t));//this.getUP(af, scc, af));
				Set<Extension> e = this.singleAFExtensions(restTheory);
				exts.put(restTheory, e);
			}
			// combine all extensions from all sccs that are compatible
			MapTools<Collection<Argument>,Extension> mapTools = new MapTools<Collection<Argument>,Extension>();			
			boolean noext;
			for(Map<Collection<Argument>,Extension> map: mapTools.allMapsSingleSource(exts)){
				// two extensions from two theories are compatible if they agree on their shared arguments
				noext = false;
				for(Collection<Argument> key1: map.keySet()){					
					for(Collection<Argument> key2: map.keySet()){
						if(key1 != key2){
							Collection<Argument> intersection = new HashSet<Argument>(key1);
							intersection.retainAll(key2);
							for(Argument arg: intersection){
								if((map.get(key1).contains(arg) && !map.get(key2).contains(arg)) || (!map.get(key1).contains(arg) && map.get(key2).contains(arg))){
									noext = true;
									break;
								}
							}
						}
						if(noext) break;
					}
					if(noext) break;
				}			
				if(!noext){
					Extension extension = new Extension();
					for(Extension e: map.values())
						extension.addAll(e);
					extensions.add(extension);
				}
			}			
		}	
		// do minimality check
		boolean valid;
		Set<Extension> result = new HashSet<Extension>();
		for(Extension ext: extensions){
			valid = true;
			for(Extension ext2: extensions){
				if(ext != ext2 && ext2.containsAll(ext)){
					valid = false;
					break;
				}					
			}
			if(valid) 
				result.add(ext);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner#getModel(net.sf.tweety.arg.dung.syntax.DungTheory)
	 */
	@Override
	public Extension getModel(DungTheory bbase) {
		// just return the first found extension
		return this.getModels(bbase).iterator().next();
	}

	/**
	 * Computes the extensions for the single af case.
	 * @param theory
	 * @return
	 */
	private Set<Extension> singleAFExtensions(DungTheory theory){
		// an extension for a single scc is a conflict-free set with maximal arguments (minimality check is performed later)
		SimpleConflictFreeReasoner reasoner = new SimpleConflictFreeReasoner();
		return new HashSet<Extension>(reasoner.getModels(theory));
	}
	
	/**
	 * Returns the set { a in A | a nicht in S und a -> S }
	 * @param af some Dung theory
	 * @param s a set of arguments
	 * @return a set of arguments
	 */
	private Collection<Argument> getOutparents(DungTheory af, Collection<Argument> s){
		Collection<Argument> result = new HashSet<Argument>();
		for(Argument a: af)
			if(!s.contains(a)){
				if(af.isAttackedBy(a, new Extension(s)))
					result.add(a);				
			}
		return result;
	}	

}
