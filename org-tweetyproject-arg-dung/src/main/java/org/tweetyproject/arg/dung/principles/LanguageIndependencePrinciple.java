/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.dung.principles;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.util.MapTools;
import org.tweetyproject.graphs.Node;

/**
 * Language independence principle <br>
 * A semantics S satisﬁes the language independence principle if and only if <br>
 * ∀AF1 = (A1, R1), ∀AF2 = (A2, R2): <br>
 * AF1 is isomorphic to AF2, ES(AF2) = {M(E) | E ∈ ES(AF1)}, <br>
 * where M(E) = {β ∈ A2 | ∃α ∈ E, β = m(α)}.
 * 
 * @author Julian Sander
 * @see: Baroni P, Giacomin M. On principle-based evaluation of extension-based argumentation semantics. In: Artiﬁcial Intelligence. vol. 171. Elsevier; 2007. p. 675-700.
 *
 */
public class LanguageIndependencePrinciple extends Principle {

	@Override
	public String getName() {
		return "Language Independence";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
		return false;
	}
	
	public boolean IsSatisfied(Collection<DungTheory> theories, AbstractExtensionReasoner ev) {
		for(var theory1 : theories) {
			for(var theory2 : theories) {
				if(!isSatisfied(ev, theory1, theory2)) {
					return false;
				}	
			}
		}
		return true;
	}

	protected boolean isSatisfied(AbstractExtensionReasoner ev, DungTheory theory1,
			DungTheory theory2) {
		MapTools<Argument, Argument> mapTools = new MapTools<Argument,Argument>();
		Set<Map<Argument,Argument>> bijections;
		
		try {
			bijections = mapTools.allBijections(new HashSet<Argument>(theory1.getNodes()), new HashSet<Argument>(theory2.getNodes()));
		} catch (IllegalArgumentException e) {
			return false; // cannot be isomorphic, if number of nodes in both graphs are different
		}
		
		for(Map<Argument,Argument> isomorphism: bijections){
			boolean isIsomorphic = true;
			for(Node a: theory1){
				for(Node b: theory1.getChildren(a)){
					if(!theory2.getChildren(isomorphism.get(a)).contains(isomorphism.get(b))){
						//is not isomorphic
						isIsomorphic = false;
						break;
					}
				}
				if(!isIsomorphic) {
					break;
				}
			}
			if(!isIsomorphic) {
				continue;
			}

			if(CheckIfAllExtenstionIsomorphic(isomorphism, theory1, theory2, ev)) {
				//found bijection, which shows AF are isomorphic and extenions are as well
				return true;
			}
			// look at different bijection
		}
		//no bijection found => not isomorphic
		return false;
	}
	
	private boolean CheckIfAllExtenstionIsomorphic(Map<Argument, Argument> isomorphism, DungTheory theory1, DungTheory theory2, AbstractExtensionReasoner ev) {
		var exts1 = ev.getModels(theory1);
		var exts2 = ev.getModels(theory2);
		for(var ext2 : exts2) {
			if(!CheckForIsomorphicExtension(isomorphism, exts1, ext2)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 
	 * @return TRUE if an isomorphic extension could be found. FALSE otherwise
	 */
	private boolean CheckForIsomorphicExtension(Map<Argument, Argument> isomorphism, Collection<Extension<DungTheory>> exts1, Extension<DungTheory> ext2) {
		for(var ext1 : exts1) {
			var isoExt1 = transformIso(isomorphism, ext1);
			if(ext2.equals(isoExt1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return A transformation of the specified extension, based on the specified isomorphism
	 */
	private Extension<DungTheory> transformIso(Map<Argument, Argument> isomorphism, Extension<DungTheory> ext1) {
		var output = new Extension<DungTheory>();
		for(var a : ext1) {
			output.add(isomorphism.get(a));
		}
		return output;
	}

}
