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
package net.sf.tweety.preferences.unittesting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import net.sf.tweety.preferences.PreferenceOrder;
import net.sf.tweety.preferences.Relation;
import net.sf.tweety.preferences.ranking.LevelingFunction;
import java.util.TreeSet;
import net.sf.tweety.util.Triple;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>PreferenceOrderTest</code> contains tests for the class <code>{@link PreferenceOrder}</code>.
 *
 * @generatedBy CodePro at 12.12.12 15:11
 * @author bwolf
 * @version $Revision: 1.0 $
 */
public class PreferenceOrderTest {
	/**
	 * An instance of the class being tested.
	 *
	 * @see PreferenceOrder
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	private PreferenceOrder<String> fixture1;

	/**
	 * An instance of the class being tested.
	 *
	 * @see PreferenceOrder
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	private PreferenceOrder<Integer> fixture2;

	/**
	 * Return an instance of the class being tested.
	 *
	 * @return an instance of the class being tested
	 *
	 * @see PreferenceOrder
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	public PreferenceOrder<String> getFixture1()
		throws Exception {
		if (fixture1 == null) {
			ArrayList<Triple<String, String, Relation>> list = new ArrayList<Triple<String, String, Relation>>();
			list.add(new Triple<String, String, Relation>("a","b", Relation.LESS));
			fixture1 = new PreferenceOrder<String>(list);
		}
		return fixture1;
	}

	/**
	 * Return an instance of the class being tested.
	 *
	 * @return an instance of the class being tested
	 *
	 * @see PreferenceOrder
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	public PreferenceOrder<Integer> getFixture2()
		throws Exception {
		if (fixture2 == null) {
			fixture2 = new PreferenceOrder<Integer>();
		}
		return fixture2;
	}

	/**
	 * Run the PreferenceOrder() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_1()
		throws Exception {

		PreferenceOrder<String> result = new PreferenceOrder<String>();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the PreferenceOrder(Collection<? extends Triple<T,T,Relation>>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_2()
		throws Exception {
		ArrayList<Triple<Integer, Integer, Relation>> relations = new ArrayList<Triple<Integer, Integer, Relation>>();
		relations.add(new Triple<Integer, Integer, Relation>(1, 2, Relation.LESS));

		PreferenceOrder<Integer> result = new PreferenceOrder<Integer>(relations);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	/**
	 * Run the PreferenceOrder(Collection<? extends Triple<T,T,Relation>>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_3()
		throws Exception {
		HashSet<Triple<String, String, Relation>> relations = new HashSet<Triple<String, String, Relation>>();
		relations.add(new Triple<String, String, Relation>("x","y",Relation.LESS));

		PreferenceOrder<String> result = new PreferenceOrder<String>(relations);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	/**
	 * Run the PreferenceOrder(Collection<? extends Triple<T,T,Relation>>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_4()
		throws Exception {
		LinkedList<Triple<Integer, Integer, Relation>> relations = new LinkedList<Triple<Integer, Integer, Relation>>();
		relations.add(new Triple<Integer, Integer, Relation>());

		PreferenceOrder<Integer> result = new PreferenceOrder<Integer>(relations);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	/**
	 * Run the PreferenceOrder(Collection<? extends Triple<T,T,Relation>>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_5()
		throws Exception {
		TreeSet<Triple<String, String, Relation>> relations = new TreeSet<Triple<String, String, Relation>>();
		relations.add(new Triple<String, String, Relation>());

		PreferenceOrder<String> result = new PreferenceOrder<String>(relations);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	/**
	 * Run the PreferenceOrder(Collection<? extends Triple<T,T,Relation>>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_6()
		throws Exception {
		Collection<? extends Triple<Integer, Integer, Relation>> relations = new ArrayList<Triple<Integer, Integer, Relation>>();

		PreferenceOrder<Integer> result = new PreferenceOrder<Integer>(relations);

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the PreferenceOrder(Collection<? extends Triple<T,T,Relation>>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_7()
		throws Exception {
		Collection<? extends Triple<String, String, Relation>> relations = new HashSet<Triple<String, String, Relation>>();

		PreferenceOrder<String> result = new PreferenceOrder<String>(relations);

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the PreferenceOrder(Collection<? extends Triple<T,T,Relation>>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_8()
		throws Exception {
		Collection<? extends Triple<Integer, Integer, Relation>> relations = new LinkedList<Triple<Integer, Integer, Relation>>();

		PreferenceOrder<Integer> result = new PreferenceOrder<Integer>(relations);

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the PreferenceOrder(Collection<? extends Triple<T,T,Relation>>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testPreferenceOrder_9()
		throws Exception {
		Collection<? extends Triple<String, String, Relation>> relations = new TreeSet<Triple<String, String, Relation>>();

		PreferenceOrder<String> result = new PreferenceOrder<String>(relations);

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the boolean add(Triple<T,T,Relation>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAdd_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Triple<String, String, Relation> t = new Triple<String, String, Relation>("i","j",Relation.LESS);

		boolean result = fixture.add(t);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean add(Triple<T,T,Relation>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAdd_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Triple<Integer, Integer, Relation> t = new Triple<Integer, Integer, Relation>();

		boolean result = fixture.add(t);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		ArrayList<Triple<String, String, Relation>> c = new ArrayList<Triple<String, String, Relation>>();
		c.add(new Triple<String, String, Relation>());

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		HashSet<Triple<Integer, Integer, Relation>> c = new HashSet<Triple<Integer, Integer, Relation>>();
		c.add(new Triple<Integer, Integer, Relation>());

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture1_2()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		LinkedList<Triple<String, String, Relation>> c = new LinkedList<Triple<String, String, Relation>>();
		c.add(new Triple<String, String, Relation>());

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture2_2()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		TreeSet<Triple<Integer, Integer, Relation>> c = new TreeSet<Triple<Integer, Integer, Relation>>();
		c.add(new Triple<Integer, Integer, Relation>());

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture1_3()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<? extends Triple<String, String, Relation>> c = new ArrayList<Triple<String, String, Relation>>();

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture2_3()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<? extends Triple<Integer, Integer, Relation>> c = new HashSet<Triple<Integer, Integer, Relation>>();

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture1_4()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<? extends Triple<String, String, Relation>> c = new LinkedList<Triple<String, String, Relation>>();

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture2_4()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<? extends Triple<Integer, Integer, Relation>> c = new TreeSet<Triple<Integer, Integer, Relation>>();

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture2_5()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		ArrayList<Triple<Integer, Integer, Relation>> c = new ArrayList<Triple<Integer, Integer, Relation>>();
		c.add(new Triple<Integer, Integer, Relation>());

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture1_5()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		HashSet<Triple<String, String, Relation>> c = new HashSet<Triple<String, String, Relation>>();
		c.add(new Triple<String, String, Relation>());

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture2_6()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		LinkedList<Triple<Integer, Integer, Relation>> c = new LinkedList<Triple<Integer, Integer, Relation>>();
		c.add(new Triple<Integer, Integer, Relation>());

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture1_6()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		TreeSet<Triple<String, String, Relation>> c = new TreeSet<Triple<String, String, Relation>>();
		c.add(new Triple<String, String, Relation>());

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture2_7()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<? extends Triple<Integer, Integer, Relation>> c = new ArrayList<Triple<Integer, Integer, Relation>>();

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture1_7()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<? extends Triple<String, String, Relation>> c = new HashSet<Triple<String, String, Relation>>();

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture2_8()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<? extends Triple<Integer, Integer, Relation>> c = new LinkedList<Triple<Integer, Integer, Relation>>();

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean addAll(Collection<? extends Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testAddAll_fixture1_8()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<? extends Triple<String, String, Relation>> c = new TreeSet<Triple<String, String, Relation>>();

		boolean result = fixture.addAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the void clear() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testClear_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		fixture.clear();

		// add additional test code here
	}

	/**
	 * Run the void clear() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testClear_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		fixture.clear();

		// add additional test code here
	}

	/**
	 * Run the boolean contains(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContains_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		String o = "ab";

		boolean result = fixture.contains(o);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean contains(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContains_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Integer o = 100;

		boolean result = fixture.contains(o);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean contains(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContains_fixture2_2()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Object o = new Object();

		boolean result = fixture.contains(o);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean contains(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContains_fixture1_2()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Object o = null;

		boolean result = fixture.contains(o);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean containsAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContainsAll_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new ArrayList<String>();

		boolean result = fixture.containsAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean containsAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContainsAll_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new HashSet<Integer>();

		boolean result = fixture.containsAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean containsAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContainsAll_fixture1_2()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new LinkedList<String>();

		boolean result = fixture.containsAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean containsAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContainsAll_fixture2_2()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new TreeSet<Integer>();

		boolean result = fixture.containsAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean containsAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContainsAll_fixture2_3()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new ArrayList<Integer>();

		boolean result = fixture.containsAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean containsAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContainsAll_fixture1_3()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new HashSet<String>();

		boolean result = fixture.containsAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean containsAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContainsAll_fixture2_4()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new LinkedList<Integer>();

		boolean result = fixture.containsAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean containsAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testContainsAll_fixture1_4()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new TreeSet<String>();

		boolean result = fixture.containsAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the Triple<Object, Object, Relation> get(Triple<T,T,Relation>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testGet_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Triple<String, String, Relation> e = new Triple<String, String, Relation>("a","b", Relation.LESS);

		Triple<String, String, Relation> result = fixture.get(e);

		// add additional test code here
		assertNotNull(result);
		assertEquals("a", result.getFirst());
		assertEquals("b", result.getSecond());
		assertEquals(Relation.LESS, result.getThird());
	}

	/**
	 * Run the Triple<Object, Object, Relation> get(Triple<T,T,Relation>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testGet_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Triple<Integer, Integer, Relation> e = new Triple<Integer, Integer, Relation>();

		Triple<Integer, Integer, Relation> result = fixture.get(e);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Set<Object> getDomainElements() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testGetDomainElements_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		Set<String> result = fixture.getDomainElements();

		// add additional test code here
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	/**
	 * Run the Set<Object> getDomainElements() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testGetDomainElements_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		Set<Integer> result = fixture.getDomainElements();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the LevelingFunction<Object> getLevelingFunction() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testGetLevelingFunction_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		LevelingFunction<String> result = fixture.getLevelingFunction();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at net.sf.tweety.preferences.ranking.LevelingFunction.<init>(LevelingFunction.java:65)
		//       at net.sf.tweety.preferences.PreferenceOrder.getLevelingFunction(PreferenceOrder.java:55)
		assertNotNull(result);
	}

	/**
	 * Run the LevelingFunction<Object> getLevelingFunction() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Ignore
	@Test
	public void testGetLevelingFunction_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		LevelingFunction<Integer> result = fixture.getLevelingFunction();

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the boolean isEmpty() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIsEmpty_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		boolean result = fixture.isEmpty();

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean isEmpty() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIsEmpty_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		boolean result = fixture.isEmpty();

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean isTotal() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIsTotal_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		boolean result = fixture.isTotal();

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean isTotal() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIsTotal_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		boolean result = fixture.isTotal();

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean isTransitive() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIsTransitive_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		boolean result = fixture.isTransitive();

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean isTransitive() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIsTransitive_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		boolean result = fixture.isTransitive();

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean isValid() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIsValid_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		boolean result = fixture.isValid();

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean isValid() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIsValid_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		boolean result = fixture.isValid();

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		Iterator<Triple<String, String, Relation>> result = fixture.iterator();

		// add additional test code here
		assertNotNull(result);
		assertEquals(true, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		Iterator<Triple<Integer, Integer, Relation>> result = fixture.iterator();

		// add additional test code here
		assertNotNull(result);
		assertEquals(false, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator(Set<Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture1_2()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		HashSet<Triple<String, String, Relation>> s = new HashSet<Triple<String, String, Relation>>();
		s.add(new Triple<String, String, Relation>());

		Iterator<Triple<String, String, Relation>> result = fixture.iterator(s);

		// add additional test code here
		assertNotNull(result);
		assertEquals(true, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator(Set<Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture2_2()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		TreeSet<Triple<Integer, Integer, Relation>> s = new TreeSet<Triple<Integer, Integer, Relation>>();
		s.add(new Triple<Integer, Integer, Relation>());

		Iterator<Triple<Integer, Integer, Relation>> result = fixture.iterator(s);

		// add additional test code here
		assertNotNull(result);
		assertEquals(true, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator(Set<Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture1_3()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Set<Triple<String, String, Relation>> s = new HashSet<Triple<String, String, Relation>>();

		Iterator<Triple<String, String, Relation>> result = fixture.iterator(s);

		// add additional test code here
		assertNotNull(result);
		assertEquals(false, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator(Set<Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture2_3()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Set<Triple<Integer, Integer, Relation>> s = new TreeSet<Triple<Integer, Integer, Relation>>();

		Iterator<Triple<Integer, Integer, Relation>> result = fixture.iterator(s);

		// add additional test code here
		assertNotNull(result);
		assertEquals(false, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator(Set<Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture2_4()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		HashSet<Triple<Integer, Integer, Relation>> s = new HashSet<Triple<Integer, Integer, Relation>>();
		s.add(new Triple<Integer, Integer, Relation>());

		Iterator<Triple<Integer, Integer, Relation>> result = fixture.iterator(s);

		// add additional test code here
		assertNotNull(result);
		assertEquals(true, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator(Set<Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture1_4()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		TreeSet<Triple<String, String, Relation>> s = new TreeSet<Triple<String, String, Relation>>();
		s.add(new Triple<String, String, Relation>());

		Iterator<Triple<String, String, Relation>> result = fixture.iterator(s);

		// add additional test code here
		assertNotNull(result);
		assertEquals(true, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator(Set<Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture2_5()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Set<Triple<Integer, Integer, Relation>> s = new HashSet<Triple<Integer, Integer, Relation>>();

		Iterator<Triple<Integer, Integer, Relation>> result = fixture.iterator(s);

		// add additional test code here
		assertNotNull(result);
		assertEquals(false, result.hasNext());
	}

	/**
	 * Run the Iterator<Triple<Object, Object, Relation>> iterator(Set<Triple<T,T,Relation>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testIterator_fixture1_5()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Set<Triple<String, String, Relation>> s = new TreeSet<Triple<String, String, Relation>>();

		Iterator<Triple<String, String, Relation>> result = fixture.iterator(s);

		// add additional test code here
		assertNotNull(result);
		assertEquals(false, result.hasNext());
	}

	/**
	 * Run the boolean remove(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemove_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Object o = new Object();

		boolean result = fixture.remove(o);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean remove(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemove_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Object o = null;

		boolean result = fixture.remove(o);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean remove(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemove_fixture2_2()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Object o = new Object();

		boolean result = fixture.remove(o);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean remove(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemove_fixture1_2()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Object o = null;

		boolean result = fixture.remove(o);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean removeAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemoveAll_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new ArrayList<String>();

		boolean result = fixture.removeAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean removeAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemoveAll_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new HashSet<Integer>();

		boolean result = fixture.removeAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean removeAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemoveAll_fixture1_2()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new LinkedList<String>();

		boolean result = fixture.removeAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean removeAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemoveAll_fixture2_2()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new TreeSet<Integer>();

		boolean result = fixture.removeAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean removeAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemoveAll_fixture2_3()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new ArrayList<Integer>();

		boolean result = fixture.removeAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean removeAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemoveAll_fixture1_3()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new HashSet<String>();

		boolean result = fixture.removeAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean removeAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemoveAll_fixture2_4()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new LinkedList<Integer>();

		boolean result = fixture.removeAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean removeAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRemoveAll_fixture1_4()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new TreeSet<String>();

		boolean result = fixture.removeAll(c);

		// add additional test code here
		assertEquals(true, result);
	}

	/**
	 * Run the boolean retainAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRetainAll_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new ArrayList<String>();

		boolean result = fixture.retainAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean retainAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRetainAll_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new HashSet<Integer>();

		boolean result = fixture.retainAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean retainAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRetainAll_fixture1_2()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new LinkedList<String>();

		boolean result = fixture.retainAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean retainAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRetainAll_fixture2_2()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new TreeSet<Integer>();

		boolean result = fixture.retainAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean retainAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRetainAll_fixture2_3()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new ArrayList<Integer>();

		boolean result = fixture.retainAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean retainAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRetainAll_fixture1_3()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new HashSet<String>();

		boolean result = fixture.retainAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean retainAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRetainAll_fixture2_4()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();
		Collection<Integer> c = new LinkedList<Integer>();

		boolean result = fixture.retainAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean retainAll(Collection<?>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testRetainAll_fixture1_4()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();
		Collection<String> c = new TreeSet<String>();

		boolean result = fixture.retainAll(c);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the int size() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testSize_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		int result = fixture.size();

		// add additional test code here
		assertEquals(1, result);
	}

	/**
	 * Run the int size() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testSize_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		int result = fixture.size();

		// add additional test code here
		assertEquals(0, result);
	}

	/**
	 * Run the Object[] toArray() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testToArray_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		Object[] result = fixture.toArray();

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.length);
		assertNotNull(result[0]);
	}

	/**
	 * Run the Object[] toArray() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testToArray_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		Object[] result = fixture.toArray();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.length);
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testToString_fixture1_1()
		throws Exception {
		PreferenceOrder<String> fixture = getFixture1();

		String result = fixture.toString();

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at net.sf.tweety.preferences.PreferenceOrder.toString(PreferenceOrder.java:244)
		assertNotNull(result);
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Test
	public void testToString_fixture2_1()
		throws Exception {
		PreferenceOrder<Integer> fixture = getFixture2();

		String result = fixture.toString();

		// add additional test code here
		assertEquals("{}", result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 12.12.12 15:11
	 */
	public static void main(String[] args) {
		
		new org.junit.runner.JUnitCore().run(PreferenceOrderTest.class);
	}
}