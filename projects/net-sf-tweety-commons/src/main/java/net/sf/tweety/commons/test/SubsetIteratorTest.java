/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons.test;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.util.IncreasingSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;

public class SubsetIteratorTest {
	public static void main(String[] args){
		Set<Integer> set = new HashSet<Integer>();
		for(int i = 0; i < 5; i++) set.add(i);
		
		SubsetIterator<Integer> it = new IncreasingSubsetIterator<Integer>(set);
		
		while(it.hasNext())
			System.out.println(it.next());
	}
}
