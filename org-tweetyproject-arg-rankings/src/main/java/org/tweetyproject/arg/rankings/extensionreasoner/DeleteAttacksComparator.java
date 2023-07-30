package org.tweetyproject.arg.rankings.extensionreasoner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;






/**
 * Implements the <code>ComparisonCriteria<code> Interface to provide the specific implementation in order to compare two
 * Extensions and select the best one. In this case, the best one is the one which satisfies the following condition:
 * 
 * E ⪯τ-delatt AF E′ if the maximal number of attacks from E to E′ that can be
 * deleted s.t. E ⪯τ
 * (E∪E′,R↓E∪E′ ) E′ is greater than or equal to the maximal
 * number of attacks from E′ to E that ca be deleted s.t. E′ ⪯τ
 * (E∪E′,R↓E∪E′ ) E.
 * 
 * @author Benjamin Birner
 *
 */
public class DeleteAttacksComparator implements ComparisonCriteria {

	
	
	
	private final ExtensionRankingSemantics semantics;
	private final boolean cardinalitiMode;
	
	
	
	
	
	
	
	
	
	
	public DeleteAttacksComparator(ExtensionRankingSemantics semantics, boolean cardinalitiMode) {
		
		this.semantics = semantics;
		this.cardinalitiMode = cardinalitiMode;
	}
	
	
	
	
	
	
	
	@Override
	public int compare(DungTheory dung, Extension<DungTheory> e1, Extension<DungTheory> e2) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		DungTheory dungUnion = getDungExtensionUnion(dung, e1, e2);
		LinkedList<Attack> attacksToE2 = getAllAttacksFromE1ToE2(dungUnion,e1,e2);
		LinkedList<Attack> attacksToE1 = getAllAttacksFromE1ToE2(dungUnion,e2,e1);
		List<List<Object>> powerSet1 = getPowerSet(attacksToE2);
		List<List<Object>> powerSet2 = getPowerSet(attacksToE1);
		int cardinality1 = getMaxNumberOfDeletedAttacks(dungUnion, powerSet1, e1, e2);
		int cardinality2 = getMaxNumberOfDeletedAttacks(dungUnion, powerSet2, e2, e1);
		
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
	 * returns all attacks from <code>e1<code> to <code>e2<code>.
	 * 
	 * @param dung a DungTheory
	 * @param e1 an Extension
	 * @param e2 an Extension
	 * @return all attacks from <code>e1<code> to <code>e2<code>.
	 */
	private LinkedList<Attack> getAllAttacksFromE1ToE2(DungTheory dung, Extension<DungTheory> e1, Extension<DungTheory> e2) {
		
		LinkedList<Attack> attacks = new LinkedList<>();
		for( Argument arg : e2) {
			Set<Argument> args = dung.getAttackers(arg); 
			for( Argument arg2 : args) {
				if( !e2.contains(arg2)) {
					attacks.add(new Attack(arg2, arg));
				}
			}
		}
		return attacks;
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
	 * creates the powerset of a set of arguments.
	 * 
	 * @param e an Extension (set of arguments).
	 * @return powerset
	 */
	private List<List<Object>> getPowerSet(LinkedList<Attack> attList){
		Object[] o = attList.toArray();
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
	 * determines the maximum number of attacks from <code>e1<code> to <code>e2<code> that can be deleted so that <code>e1<code>
	 * has the same or a better rank with regard to the extension ranking semantics <code>semantics<code> that is used.
	 * 
	 * @param dung a DungTheory
	 * @param powerSet the powerset of <code>e1<code>
	 * @param e1 an Extension
	 * @param e2 an Extension
	 * @return the maximum number of attacks that can be deleted.
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private int getMaxNumberOfDeletedAttacks(DungTheory dung, List<List<Object>> powerSet, Extension<DungTheory> e1, Extension<DungTheory> e2 ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		
		ExtensionRankingReasoner cf_reasoner = new ExtensionRankingReasoner(semantics);
		for( List<Object> att : powerSet) {
			DungTheory dungClone = dung.clone();
			for( Object o : att) {
				Attack a = (Attack) o;
				dungClone.remove(a);
			}
			List<List<Extension<DungTheory>>> modelList = cf_reasoner.getModels(dungClone,cardinalitiMode);
			for(List<Extension<DungTheory>> list : modelList) {
				
				if(list.contains(e1) && !list.contains(e2)) {
					break;
				}
				if((list.contains(e2) && list.contains(e1)) || (list.contains(e2) && !list.contains(e1))) {
					
					return att.size();
				}
			}
		}
		return 0;	
	}
	
	
	
	

}
