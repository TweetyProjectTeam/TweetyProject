package net.sf.tweety.preferences.unittesting;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.sf.tweety.preferences.Relation;
import net.sf.tweety.util.Triple;
import net.sf.tweety.preferences.PreferenceOrder;
import net.sf.tweety.preferences.io.POParser;
import net.sf.tweety.preferences.io.ParseException;
import net.sf.tweety.preferences.ranking.RankingFunction;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>RankingFunctionTest</code> contains tests for the class <code>{@link RankingFunction}</code>.
 *
 * @generatedBy CodePro at 12.12.12 16:08
 * @author Bastian Wolf
 * @version $Revision: 1.0 $
 */
public class RankingFunctionTest {
	
	/**
	 * method for parsing in files
	 * @param filename containing the PO to be parsed in
	 * @return po if successful, null if not
	 */
	public static PreferenceOrder<String> parseFile(String filename) {
		try {
			PreferenceOrder<String> test = new PreferenceOrder<String>();

			test = POParser.parse(filename);
			return test;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("The given file was not found" + e);
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Error while parsing" + e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * An instance of the class being tested.
	 *
	 * @see RankingFunction
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	private RankingFunction<String> fixture;
	
	/**
	 * The preference order used in this test
	 */
	private PreferenceOrder<String> po;
	
	/**
	 * initializes the preference order
	 */
	public void initializePreferenceOrder(){
		this.po = parseFile("TestA.po");
	}
	
	/**
	 * Return an instance of the class being tested.
	 *
	 * @return an instance of the class being tested
	 *
	 * @see RankingFunction
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	public RankingFunction<String> getFixture()
		throws Exception {
		if (fixture == null) {
			fixture = new RankingFunction<String>(po);
		}
		return fixture;
	}

	/**
	 * Run the RankingFunction() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testRankingFunction_1()
		throws Exception {

		RankingFunction<String> result = new RankingFunction<String>(po);
		System.out.println(result.size());
		// add additional test code here
		assertNotNull(result);
		assertEquals(5, result.size());
	}

	/**
	 * Run the RankingFunction(PreferenceOrder<T>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testRankingFunction_2()
		throws Exception {
		ArrayList<Triple<String, String, Relation>> list = new ArrayList<Triple<String, String, Relation>>();
		list.add(new Triple<String, String, Relation>("x", "y", Relation.LESS));
		list.add(new Triple<String, String, Relation>("x", "z", Relation.LESS));
		list.add(new Triple<String, String, Relation>("y", "z", Relation.LESS));
		PreferenceOrder<String> po = new PreferenceOrder<String>(list);

		RankingFunction<String> result = new RankingFunction<String>(po);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the RankingFunction(PreferenceOrder<T>) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testRankingFunction_3()
		throws Exception {
		RankingFunction<String> result = new RankingFunction<String>(po);

		assertNotNull(result);
	}

	/**
	 * Run the boolean containsKey(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testContainsKey_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		Object key = new Object();

		boolean result = fixture2.containsKey(key);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean containsKey(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testContainsKey_fixture_2()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		String key = "l";

		boolean result = fixture2.containsKey(key);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean containsValue(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testContainsValue_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		Object value = new Object();

		boolean result = fixture2.containsValue(value);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the boolean containsValue(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testContainsValue_fixture_2()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		Object value = null;

		boolean result = fixture2.containsValue(value);

		// add additional test code here
		assertEquals(false, result);
	}

	/**
	 * Run the PreferenceOrder<Object> generatePreferenceOrder() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testGeneratePreferenceOrder_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();

		PreferenceOrder<String> result = fixture2.generatePreferenceOrder();

		// add additional test code here
		assertNotNull(result);
		assertEquals(12, result.size());
	}

	/**
	 * Run the Integer get(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testGet_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		Object key = new Object();

		Integer result = fixture2.get(key);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Integer get(Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testGet_fixture_2()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		Object key = null;

		Integer result = fixture2.get(key);

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the Set<java.util.Map.Entry<Object, Integer>> getElementsByValue(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testGetElementsByValue_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		int val = 0;

		Set<java.util.Map.Entry<String, Integer>> result = fixture2.getElementsByValue(val);

		// add additional test code here
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	/**
	 * Run the Set<java.util.Map.Entry<Object, Integer>> getElementsByValue(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testGetElementsByValue_fixture_2()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		int val = 1;

		Set<java.util.Map.Entry<String, Integer>> result = fixture2.getElementsByValue(val);

		// add additional test code here
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	/**
	 * Run the Set<java.util.Map.Entry<Object, Integer>> getElementsByValue(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testGetElementsByValue_fixture_3()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		int val = 7;

		Set<java.util.Map.Entry<String, Integer>> result = fixture2.getElementsByValue(val);

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Set<java.util.Map.Entry<Object, Integer>> getPredecessors(Entry<T,Integer>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Ignore
	@Test
	public void testGetPredecessors_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		java.util.Map.Entry<String, Integer> element = null;

		Set<java.util.Map.Entry<String, Integer>> result = fixture2.getPredecessors(element);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at net.sf.tweety.preferences.ranking.RankingFunction.getPredecessors(RankingFunction.java:271)
		assertNotNull(result);
	}

	/**
	 * Run the Map<Object, Integer> getRankingFunction() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testGetRankingFunction_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();

		Map<String, Integer> result = fixture2.getRankingFunction();

		// add additional test code here
		assertNotNull(result);
		assertEquals(5, result.size());
	}

	/**
	 * Run the Set<java.util.Map.Entry<Object, Integer>> getSuccessors(Entry<T,Integer>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Ignore
	@Test
	public void testGetSuccessors_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();
		java.util.Map.Entry<String, Integer> element = null;

		Set<java.util.Map.Entry<String, Integer>> result = fixture2.getSuccessors(element);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at net.sf.tweety.preferences.ranking.RankingFunction.getSuccessors(RankingFunction.java:281)
		assertNotNull(result);
	}

	/**
	 * Run the String toString() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testToString_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();

		String result = fixture2.toString();

		// add additional test code here
		assertEquals("{d=2, e=2, b=1, c=1, a=0}", result);
	}

	/**
	 * Run the Collection<Integer> values() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Test
	public void testValues_fixture_1()
		throws Exception {
		RankingFunction<String> fixture2 = getFixture();

		Collection<Integer> result = fixture2.values();

		// add additional test code here
		assertNotNull(result);
		assertEquals(3, result.size());
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
		this.po = parseFile("TestA.po");
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 12.12.12 16:08
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
	 * @generatedBy CodePro at 12.12.12 16:08
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(RankingFunctionTest.class);
	}
	

}