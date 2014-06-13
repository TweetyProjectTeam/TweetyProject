/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.commons.util;

import java.util.*;

/**
 * Iterates over all subsets of a given sets in a random order.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The element class which is iterated.
 */
public class RandomSubsetIterator<T> extends SubsetIterator<T> {

	/** The set over which subsets are iterated. */
	private List<T> set;
		
	/** Whether to avoid duplicates in the iteration. */
	private boolean avoidDuplicates;
	
	/** The random number generator. */
	private Random random;

	/** Only used when avoidDuplicats is set to true. Then
	 * this set contains all (representations of) subsets
	 * already generated (if those are less than half the number
	 * of all subsets) or the subsets not generated yet (otherwise). */
	private Set<BitSet> temp;

	/** Only used when avoidDuplicats is set to true. The number
	 * of already generated subsets. */
	private long generatedSubsets;

	/** Only used when avoidDuplicats is set to true. The number
	 * of all subsets. */
	private double allSubsets;
	
	/** Only used when avoidDuplicats is set to true. Whether
	 * the mode of using this.temp has been switched. */
	private boolean switched;
	
	/** Creates a new subset iterator for the given set.
	 * @param set some set.
	 * @param avoidDuplicates whether to avoid duplicates in the iteration.
	 *  NOTE: setting this value to true might increase computation time
	 *  and needed space drastically.
	 */
	public RandomSubsetIterator(Set<T> set, boolean avoidDuplicates){
		super(set);
		this.set = new ArrayList<T>(set);
		this.avoidDuplicates = avoidDuplicates;
		this.random = new Random();
		if(this.avoidDuplicates){
			this.temp = new HashSet<BitSet>();
			this.generatedSubsets = 0;
			this.allSubsets = Math.pow(2, this.set.size());
			this.switched = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !this.avoidDuplicates || this.generatedSubsets < this.allSubsets;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Set<T> next() {
		if(!this.avoidDuplicates){
			Set<T> result = new HashSet<T>();
			for(T elem: this.set)
				if(this.random.nextBoolean())
					result.add(elem);
			return result;
		}else{
			boolean firstHalf = this.generatedSubsets == 0 || this.allSubsets / this.generatedSubsets > 2; 
			BitSet bitSet = this.generate(this.set.size(), firstHalf);
			Set<T> result = new HashSet<T>();
			for(int i = 0; i < this.set.size(); i++)
				if(bitSet.length() > i && bitSet.get(i))
					result.add(this.set.get(i));			
			this.generatedSubsets++;
			if(!this.switched && this.allSubsets / this.generatedSubsets <= 2){
				this.switched = true;
				Set<BitSet> temp2 = new HashSet<BitSet>();
				bitSet = new BitSet();
				BitSet tmp;
				double numberOfBitSets = Math.pow(2, this.set.size());
				for(long i = 0; i < numberOfBitSets; i++){					
					if(!this.temp.contains(bitSet)){
						tmp = new BitSet();
						tmp.or(bitSet);
						temp2.add(tmp);
					}
					this.increment(bitSet);
				}
				this.temp = temp2;
			}
			return result;
		}
	}
	
	/** Increments the given bit set
	 * @param bitSet some bit set.
	 */
	private void increment(BitSet bitSet){
		boolean carry = true, tmp;
		int i = 0;
		while(carry){
			tmp = carry;
			carry = carry && bitSet.get(i);
			bitSet.set(i, tmp ^ bitSet.get(i));
			i++;
		}
	}

	/**
	 * Generates a new bit set of the given length. If checkForDuplicates
	 * is true then the all bit sets in this.temp are regarded as already
	 * being generated and the new one will be different from all of those.
	 * Furthermore, the new bit set is added to this.temp. If checkForDuplicates
	 * is false, the new bit set will be chosen from this.temp and removed there.  
	 * @param length the length of the bit set.
	 * @param checkForDuplicates whether to check for duplicates (see above).
	 * @return a bit set.
	 */
	private BitSet generate(int length, boolean checkForDuplicates){
		BitSet result;
		if(checkForDuplicates){
			do{
				result = this.generateRandomly(length);
			}while(this.temp.contains(result));
			this.temp.add(result);
			return result;		
		}else{
			long idx = this.random.nextInt(this.temp.size());
			for(BitSet elem: this.temp){
				if(idx == 0){
					this.temp.remove(elem);
					return elem;
				}
				idx--;
			}
		}
		//this should not happen
		throw new RuntimeException("this should not happen");
	}
	
	/** Generates a random bit set of the given length.
	 * @param length some length.
	 * @return a random bit set.
	 */
	private BitSet generateRandomly(int length){
		BitSet result = new BitSet();
		for(int i = 0; i < length; i++)
			if(this.random.nextBoolean())
				result.set(i);
		return result;
	}
}
