package net.sf.tweety.preferences.ranking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.preferences.PreferenceOrder;
import net.sf.tweety.preferences.Relation;
import net.sf.tweety.util.Triple;

/**
 * This class is meant to provide ranking functions to given preference orders
 * and vice versa. To be implemented. A ranking function characterizes a
 * preference order uniquely as: 1.: rank: O -> N+ where O is the set of
 * elements in the preference order. 2.: the sum of all ranks for each element
 * in O is minimal
 * 
 * @todo exception handling for invalid preference orders (total preorder)
 * @author Bastian Wolf
 * @param <T>
 * 
 */

public class RankingFunction<T> extends Functions<T>{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * constructs a new, empty ranking function
	 * caller can use Map-method putAll to fill this empty ranking function
	 */
	public RankingFunction(){
		super();
	}
	
	/**
	 * this constructor creates a ranking function using a given preference
	 * order
	 * 
	 * @param lf
	 *            the given preference order
	 */
	public RankingFunction(LevelingFunction<T> lf) {
			Set<Entry<T, Integer>> tempRF = lf.entrySet();
			HashMap<T, Integer> rf = new HashMap<T, Integer>();
			
			for(Entry<T, Integer> e : tempRF){
				int pre = 0;
				for(Entry<T, Integer> f : tempRF){
					if (!e.equals(f) && f.getValue() < e.getValue()){
						pre++;
					}
				}
				rf.put(e.getKey(), pre);
			}
			this.putAll(rf);
	}
	
	/**
	 * returns the ranking function
	 * 
	 * @return ranking function
	 */
	public Map<T, Integer> getRankingFunction() {
		return this;
	}
	
	/**
	 * this method returns a preference order made out of an ranking function
	 * 
	 * @return a preference order out of a given ranking function
	 */
	public PreferenceOrder<T> generatePreferenceOrder() {
		Set<Triple<T, T, Relation>> tempPO = new HashSet<Triple<T, T, Relation>>();
		
		Map<T, Integer> in = this;

		for (Entry<T, Integer> f : in.entrySet()) {
			for (Entry<T, Integer> s : in.entrySet()) {
				
				if (!f.getKey().equals(s.getKey())){
					if (f.getValue() < s.getValue()){
						Triple<T, T, Relation> rel = new Triple<T, T, Relation>(f.getKey(), s.getKey(), Relation.LESS);
						tempPO.add(rel);
					} else if (f.getValue() == s.getValue()){
						Triple<T, T, Relation> rel = new Triple<T, T, Relation>(f.getKey(), s.getKey(), Relation.LESS_EQUAL);
						tempPO.add(rel);
					} else
						continue;
						
				}				
			}
		}
		PreferenceOrder<T> po = new PreferenceOrder<T>(tempPO);
		return po;
	}

	
	
	/**
	 * weakens the given element in the ranking function
	 * @param element the element being weakened
	 */
	public void weakenElement(T element){
		// the amount of elements that are predecessors of element
		int amount = this.getElementsByValue(this.get(element)-1).size();
		// weakening the elements rank by this amount
		this.put(element, this.get(element)+amount);
		
		// strengthen each element on the same rank as the now weakened element
		for(Entry<T, Integer> e : getElementsByValue(this.get(element))){
			if (!e.getKey().equals(element)){
				this.put(e.getKey(), e.getValue()-1);
			}				
		}
	}
	
	
	/**
	 * strengthens the given element in the ranking function
	 * @param element the element being strengthened 
	 */
	public void strengthenElement(T element){
		// calculating the amount of elements ranked equally to the element
		int amount = this.getElementsByValue(this.get(element)).size()-1;
		// strengthening the elements rank by this amount
		this.put(element, this.get(element)-amount);
		
		// weakening each element on the same rank as the now strengthened element
		for(Entry<T, Integer> e : getElementsByValue(this.get(element))){
			if (!e.getKey().equals(element)){
				this.put(e.getKey(), e.getValue()+1);
			}				
		}
	}
	
}
