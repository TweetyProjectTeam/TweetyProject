package org.tweetyproject.arg.rankings.extensionreasoner;

import java.util.Collection;
import java.util.LinkedList;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;





/**
 * Reasoner for refining extension based semantics.
 * The reasoner selects the "best" Extensions of a set of Extensions concerning a given criteria.
 * Based on "On Supported Inference and Extension Selection in Abstract Argumentation Frameworks" (S. Konieczny et al., 2015)
 * 
 * @author Benjamin Birner
 *
 */
public class CopelandBasedComparisonExtensionReasoner {

	
	/**
	 * selects the best Extensions of a set of Extensions concerning a given criteria by pairwise comparison. 
	 * 
	 * @param extensions a set of Extensions
	 * @param dung an Argumentation Framework
	 * @param criteria the comparison criteria
	 * @return a set of Extensions which are considered as the best ones
	 */
	public LinkedList<Extension<DungTheory>> getModels(Collection<Extension<DungTheory>> extensions, DungTheory dung, ComparisonCriteria criteria) {
		int argmax =  - extensions.size();
		LinkedList<Extension<DungTheory>> bestExt = new LinkedList<Extension<DungTheory>>();
		LinkedList<Extension<DungTheory>> greaterEqual = new LinkedList<Extension<DungTheory>>();
		LinkedList<Extension<DungTheory>> lessEqual = new LinkedList<Extension<DungTheory>>();
		for(Extension<DungTheory> ext1 : extensions) {
			for(Extension<DungTheory> ext2 : extensions) {
				if(!ext1.equals(ext2)) {
					int result = criteria.compare(dung, ext2, ext1);
					if(result < 0) {
						lessEqual.add(ext2);
					}else {
						if(result > 0) {
							greaterEqual.add(ext2);
						}else {
							lessEqual.add(ext2);
							greaterEqual.add(ext2);
						}
					}
				}
			}
			if( argmax < lessEqual.size() - greaterEqual.size()) {
				argmax = lessEqual.size() - greaterEqual.size();
				bestExt.clear();
				bestExt.add(ext1);
			}else {
				if( argmax == lessEqual.size() - greaterEqual.size()) {
					bestExt.add(ext1);
				}
			}
			greaterEqual.clear();
			lessEqual.clear();
		}
		return bestExt;
	}
	
}
