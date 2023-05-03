package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;



/**
 * Implements the <code>ComparisonCriteria<code> Interface to provide the specific implementation in order to compare two
 * Extensions and select the best one. In this case, the best one is the one which has less Arguments attacked by the other 
 * Extension.
 * 
 * @author Benjamin Birner
 *
 */
public class ArgumentsNonAttackedComparator implements ComparisonCriteria {

	
	
	
	@Override
	public int compare(DungTheory dung, Extension<DungTheory> e1, Extension<DungTheory> e2) {
		int count1 = e1.size();
    	int count2 = e2.size();
    	for(Argument a : e1) {
    		 if(dung.isAttacked(a, e2)) {
    			 count1--;
    		 }
    	}
    	for(Argument a : e2) {
    		if(dung.isAttacked(a, e1)) {
    			count2--;
    		}
    	}
    	return Integer.compare(count1, count2);
		
	}

}
