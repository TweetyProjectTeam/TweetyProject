/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.machinelearning.assoc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class AprioriTest {
	List<Collection<String>> db1, db2;
		
	@Before
	public void setUp() {
		db1 = new ArrayList<Collection<String>>();		
		List<String> t = new ArrayList<String>();
		t.add("A");
		t.add("B");
		t.add("C");
		db1.add(t);		
		t = new ArrayList<String>();
		t.add("A");
		t.add("B");
		t.add("D");
		db1.add(t);		
		t = new ArrayList<String>();
		t.add("C");
		t.add("D");
		db1.add(t);		
		t = new ArrayList<String>();
		t.add("A");
		t.add("B");
		t.add("C");
		t.add("D");
		db1.add(t);
		
		db2 = new ArrayList<Collection<String>>();		
		t = new ArrayList<String>();
		t.add("A");
		t.add("B");
		t.add("C");
		t.add("D");		
		db2.add(t);
		
		t = new ArrayList<String>();
		t.add("E");
		t.add("B");
		t.add("D");
		db2.add(t);
		
		t = new ArrayList<String>();
		t.add("E");
		t.add("B");
		t.add("C");
		t.add("D");
		db2.add(t);
		
		t = new ArrayList<String>();
		t.add("A");
		t.add("B");
		t.add("F");
		t.add("E");		
		t.add("D");
		db2.add(t);
		
		t = new ArrayList<String>();
		t.add("A");
		t.add("B");
		t.add("F");
		db2.add(t);
		
		t = new ArrayList<String>();
		t.add("A");
		t.add("C");
		t.add("B");
		t.add("F");
		db2.add(t);
		
		t = new ArrayList<String>();
		t.add("A");
		t.add("D");
		t.add("E");
		t.add("B");
		t.add("F");
		db2.add(t);
		
		t = new ArrayList<String>();
		t.add("A");
		t.add("B");
		t.add("F");
		db2.add(t);
	}
	
	@Test
	public void test1(){	
		AprioriMiner<String> miner = new AprioriMiner<String>(0.5, 0.9);
		Collection<AssociationRule<String>> assoc = miner.mineRules(db1);
		assertEquals(assoc.size(),6);		
	}
	
	@Test
	public void test2(){	
		AssociationRule<String> r = new AssociationRule<String>();
		r.addToConclusion("B");
		r.addToPremise("A");
		
		assertEquals(r.support(db1),0.75,0.001);
		assertEquals(r.confidence(db1),1,0.001);		
	}
	
	@Test
	public void test3(){	
		AprioriMiner<String> miner = new AprioriMiner<String>(0.2, 0.8);
		Collection<AssociationRule<String>> assoc = miner.mineRules(db2);
		assertEquals(assoc.size(), 70);
	}
	
	@Test
	public void test4(){	
		AssociationRule<String> r = new AssociationRule<String>();
		r.addToConclusion("B");
		r.addToConclusion("F");
		r.addToPremise("A");
		
		assertEquals(r.support(db2),0.625,0.001);
		assertEquals(r.confidence(db2),0.833,0.001);		
	}
}
