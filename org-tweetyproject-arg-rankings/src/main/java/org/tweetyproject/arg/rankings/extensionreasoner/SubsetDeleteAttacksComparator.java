package org.tweetyproject.arg.rankings.extensionreasoner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;





/**
 * Implements the <code>ComparisonCriteria<code> Interface to provide the specific implementation in order to compare two
 * Extensions and select the best one. In this case, the best one is the one which satisfies the following condition:
 *
 * E ⪯τ-delarg AF E′ if the cardinality of any largest subset S of E such that if all
 *  the attacks from S to E′ are deleted, then E ⪯τ
 * (E∪E′,R↓E∪E′ ) E′ is greater
 *  than or equal to the cardinality of any largest subset of S′ of E′ such that
 *  if all the attacks from S′ to E are deleted, then E′ ⪯τ(
 *  E∪E′,R↓E∪E′ ) E.
 * 
 * @author Benjamin Birner
 *
 */
public class SubsetDeleteAttacksComparator implements ComparisonCriteria {

	
	private final ExtensionRankingSemantics semantics;
	private final boolean cardinalitiMode;
	
	public SubsetDeleteAttacksComparator(ExtensionRankingSemantics semantics, boolean cardinalitiMode) {
		
		this.semantics = semantics;
		this.cardinalitiMode = cardinalitiMode;
	}
	
	
	@Override
	public int compare(DungTheory dung, Extension<DungTheory> e1, Extension<DungTheory> e2) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException{
		
		List<List<Object>> powerSet1 = getPowerSet(e1);
		List<List<Object>> powerSet2 = getPowerSet(e2);
		DungTheory dungUnion = getDungExtensionUnion(dung, e1, e2);
		int cardinality1 = getMaxCardinality(dungUnion, powerSet1, e1, e2);
		int cardinality2 = getMaxCardinality(dungUnion, powerSet2, e2, e1);
		
		if(cardinality1 > cardinality2) {
			
			return 1;
		}else {
			if(cardinality2 > cardinality1) {
				return -1;
			}else {
				return 0;
			}
		}
	}
	
	
	
	
	/**
	 * creates the powerset of a set of arguments.
	 * 
	 * @param e an Extension (set of arguments).
	 * @return powerset
	 */
	private List<List<Object>> getPowerSet(Extension<DungTheory> e){
		Object[] o = e.toArray();
		List<List<Object>> powerSet = new ArrayList<>();
	    int n = o.length;
	    int numSubsets = (int) Math.pow(2, n);

	    for (int i = numSubsets - 1; i >= 0; i--) {
	        List<Object> subset = new ArrayList<>();
	        for (int j = 0; j < n; j++) {
	            if ((i & (1 << j)) != 0) {
	                subset.add(o[j]);
	            }
	        }
	        powerSet.add(subset);
	    }      
	    powerSet.sort((list1, list2) -> Integer.compare(list2.size(), list1.size()));
		
	    return powerSet;
	}
	
	
	
	/**
	 * Deletes all arguments from DungTheory <code>dung<code> which are not included either in <code>e1<code> nor in <code>e2<code>.
	 * Deletes all attacks which are not completely contained in the union of <code>e1<code> and <code>e2<code>.
	 * 
	 * @param dung DungTheory to change.
	 * @param e1 an Extension
	 * @param e2 an Extension
	 * @return the changed DungTheory.
	 */
	private DungTheory getDungExtensionUnion( DungTheory dung, Extension<DungTheory> e1, Extension<DungTheory> e2) {
		 Collection<Argument> nodes = dung.getNodes();
	     Iterator<Argument> iterator = nodes.iterator();
	     while (iterator.hasNext()) {
	         Argument arg = iterator.next();
	         if ( !e1.contains(arg) && !e2.contains(arg)) {
	             iterator.remove();
	             Iterator<Argument> iterator2 = nodes.iterator();
	    	     while (iterator2.hasNext()) {
	    	         Argument arg2 = iterator2.next();
	    	         Attack att = new Attack(arg2,arg);
	    	         if(dung.contains(att)) {
	    	             dung.remove(att);
	    	         }
	    	     }
	         }
	     }
         return dung;
	}
	
	
	
	
	/**
	 * finds the maximum cardinality with respect to the largest possible subset of e1 such that 
	 * <code>e1<code> has the same or better rank than <code>e2<code> in terms of <code>semantics<code> when deleting all 
	 * attacks from this subset to <code>e2<code>. 
	 * 
	 * @param dung a DungTheory
	 * @param powerSet the powerset of <code>e1<code>
	 * @param e1 an Extension
	 * @param e2 an Extension
	 * @return the maximum cardinality of a subset of <code>e1<code> that satisfies the condition
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	private int getMaxCardinality(DungTheory dung, List<List<Object>> powerSet, Extension<DungTheory> e1, Extension<DungTheory> e2 ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException{
		//e1 = E und e2 = E'
		ExtensionRankingReasoner cf_reasoner = new ExtensionRankingReasoner(semantics);
		
		for( List<Object> args : powerSet) {
			DungTheory dungClone = dung.clone();
			for( Object o : args) {
				Argument a1 = (Argument) o;
				for(Argument a2 : e2) {
					if( dungClone.isAttackedBy(a2, a1) == true) {
						Attack att = new Attack(a1,a2);
						dungClone.remove(att);
					}
				}
			}
	        List<List<Extension<DungTheory>>> modelList = cf_reasoner.getModels(dungClone,cardinalitiMode);
			for(List<Extension<DungTheory>> list : modelList) {
				
				if(list.contains(e1) && !list.contains(e2)) {
					break;
				}
				if((list.contains(e2) && list.contains(e1)) || (list.contains(e2) && !list.contains(e1))) {
					
					return args.size();
				}
			}
		}
		return 0; 
	}
	

	
	
}
