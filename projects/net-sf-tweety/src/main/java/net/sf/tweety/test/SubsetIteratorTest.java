package net.sf.tweety.test;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.util.IncreasingSubsetIterator;
import net.sf.tweety.util.SubsetIterator;

public class SubsetIteratorTest {
	public static void main(String[] args){
		Set<Integer> set = new HashSet<Integer>();
		for(int i = 0; i < 5; i++) set.add(i);
		
		SubsetIterator<Integer> it = new IncreasingSubsetIterator<Integer>(set);
		
		while(it.hasNext())
			System.out.println(it.next());
	}
}
