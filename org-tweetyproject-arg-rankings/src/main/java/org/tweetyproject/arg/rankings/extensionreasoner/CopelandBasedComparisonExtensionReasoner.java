package org.tweetyproject.arg.rankings.extensionreasoner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;





/**
 * Reasoner for refining extension based semantics.
 * The reasoner ranks Extensions concerning a given criteria.
 * Based on "On Supported Inference and Extension Selection in Abstract Argumentation Frameworks" (S. Konieczny et al., 2015)
 * 
 * @author Benjamin Birner
 *
 */
public class CopelandBasedComparisonExtensionReasoner {

	
	/**
	 * Ranks the Extensions concerning a given criteria by pairwise comparison. 
	 * 
	 * @param extensions a set of Extensions
	 * @param dung an Argumentation Framework
	 * @param criteria the comparison criteria
	 * @return ArrayList including ranked Extensions
	 * @throws IllegalAccessException 
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 */
	public ArrayList<LinkedList<Extension<DungTheory>>> getModels(Collection<Extension<DungTheory>> extensions, DungTheory dung, ComparisonCriteria criteria) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
	
		ArrayList<LinkedList<Extension<DungTheory>>> rankList = new ArrayList<>(2*extensions.size()+1);
		for( int i=0; i < 2*extensions.size()+1; i++ ) {
			rankList.add(new LinkedList<Extension<DungTheory>>());
		}
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
			int rank = greaterEqual.size() - lessEqual.size() + extensions.size();
			rankList.get(rank).add(ext1);
		}
		return rankList;
	}
	
}
