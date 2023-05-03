package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;



/**
 * Implements the <code>ComparisonCriteria<code> Interface to provide the specific implementation in order to compare two
 * Extensions and select the best one. In this case, the best one is the one which has most arguments strongly defended 
 * from the other one.  
 * 
 * 
 * @author Benjamin Birner
 *
 */
public class StronglyDefendedComparator implements ComparisonCriteria {

	
	
	@Override
	public int compare(DungTheory dung, Extension<DungTheory> e1, Extension<DungTheory> e2) {
		
		int count1 = 0;
    	int count2 = 0;
    	for(Argument a : e1) {
    		 if(isStronglyDefended(dung,a,e1,e2)) {
    			 count1++;
    		 }
    	}
    	for(Argument a : e2) {
    		if(isStronglyDefended(dung,a,e2,e1)) {
    			count2++;
    		}
    	}
    	return Integer.compare(count1, count2);
	}
	
	
	
	
	/**
	 * This method checks if <code>a<code> in <code>ext1<code> is strongly defended from <code>ext2<code> by <code>ext1<code>.
	 * 
	 * @param dung an AF
	 * @param a an Argument
	 * @param ext1 an Extension
	 * @param ext2 an Extension
	 * @return true if <code>a<code> in <code>ext1<code> is strongly defended from <code>ext2<code> by <code>ext1<code>. Else false.
	 */
	private boolean isStronglyDefended(DungTheory dung, Argument a, Extension<DungTheory> ext1, Extension<DungTheory> ext2) {
		 boolean stronglyDef = false;
         if(dung.isAttacked(a, ext2)) {
        	 Extension<DungTheory> ext1WithoutArg = new Extension<DungTheory>(ext1);
             ext1WithoutArg.remove(a); 
             for(Argument arg1 : dung.getAttackers(a)) {
            	 if(ext2.contains(arg1)) {
            		 for(Argument arg2 : ext1WithoutArg) {
            			 if(dung.isAttackedBy(arg1, arg2)) {
            				 stronglyDef = isStronglyDefended(dung,arg2,ext1WithoutArg,ext2);
            				 if ( stronglyDef == true) {
            					 break;
            				 }
            			 }
            		 }
            		 if(stronglyDef == false) {
            			 return false;
            		 }
            		 stronglyDef = false;
            	 }
             }
         }
		 return true;	
	}

}
