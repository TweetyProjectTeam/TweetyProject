
package org.tweetyproject.logics.translators.adfpossibilistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;

import org.tweetyproject.logics.translators.adfcl.ConverterADF2CL;


/**
 * 
 * This class performs the Gamma Operator for Possibilistic ADFs introduced in [Heyninck 2021]
 * @author Jonas Schumacher
 *
 */
public class PossibilisticGammaOperator {
	/**
	 * Convert an unsorted Map to a map sorted by value in ascending order
	 * @param map map
	 * @return map sorted by value in ascending order
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<PlFormula, Double> sortByValue(Map<PlFormula, Double> map) {
		List list = new LinkedList(map.entrySet());
	    Collections.sort(list, new Comparator() {
	      @Override
	      public int compare(Object o1, Object o2) {
	        return ((Comparable) ((Map.Entry) (o1)).getValue())
	            .compareTo(((Map.Entry) (o2)).getValue());
	      }
	    });

	    Map<PlFormula, Double> result = new LinkedHashMap<PlFormula, Double>();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	      Map.Entry<PlFormula, Double> entry = (Map.Entry<PlFormula, Double>) it.next();
	      result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	  }

	/**
	 * Implements the Gamma Operator:
	 * Input: Old Possibility Distribution and ADF
	 * Output: New Possibility Distribution
	 * @param args arguments
	 * @throws ParserException parserexception
	 * @throws IOException ioexception
	 */
	public static void main(String[] args) throws ParserException, IOException {
		
		// Set path to ADF
		//String path_to_adf = "src/main/resources/possibilistic_adf_previous_ex6_ex8.txt";
		//String path_to_adf = "src/main/resources/possibilistic_adf_previous_ex7.txt";
		String path_to_adf = "src/main/resources/possibilistic_adf_ex8.txt";
		
		/*
		 * Step 1: Create an ADF D from text file
		 */
		System.out.println("");
		System.out.println("Step 1: Input ADF:");
		NativeMinisatSolver solver = new NativeMinisatSolver();
		LinkStrategy strat = new SatLinkStrategy(solver);
		KppADFFormatParser parser_adf = new KppADFFormatParser(strat, true);
		AbstractDialecticalFramework adf = parser_adf.parse(new File(path_to_adf));
		for (Argument ar : adf.getArguments()) {
			System.out.println("Argument: \t\t " + ar);
			AcceptanceCondition ac = adf.getAcceptanceCondition(ar);
			System.out.println("Acceptance Condition \t" + ac);
		}		
		

		/*
		 * Step 2: Create possibility distribution
		 * Here: set possibility values according to the examples of the paper
		 */
		System.out.println("");
		System.out.println("------------------------------------------");
		System.out.println("");
		System.out.println("Step 2: Input Possibility distribution:");		
		System.out.println("");
		
		
		PlSignature sig = null;
		PossibilityDistribution possDistInput = null;
		ArrayList<Proposition> setOfProps = null;
		
		// Case 1: ADF consists of 2 arguments
		if (adf.size() == 2) {
			
			double[] startValues = {0.5,0.7,0.5,0.5};			// previous version example 7 
			//double[] startValues = {0.5,0.5,0.5,0.5};			// previous version example 8.1
			//double[] startValues = {0.2,0.8,0.2,0.2};			// previous version example 8.2
			//double[] startValues = {0.2,0.2,0.8,0.2};			// previous version example 8.3
			//double[] startValues = {1.0,1.0,1.0,1.0};			// previous version example 8.4 (grounded extension)
			
			sig = new PlSignature();
			sig.add(new Proposition("a"));
			sig.add(new Proposition("b"));
			possDistInput = new PossibilityDistribution(sig);
			
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("a"));
			setOfProps.add(new Proposition("b"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[0]);			// a,b
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("a"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[1]);			// a,-b
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("b"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[2]);			// -a,b
			setOfProps = new ArrayList<Proposition>();
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[3]);			// -a,-b
		}
		
		// Case 2: ADF consists of 3 arguments
		else if (adf.size() == 3) {
			
			double[] startValues = {0.4,0.8,0.4,1.0,0.4,0.8,0.4,0.8};		// example 8 grounded
			//double[] startValues = {0,0,0,1,0,0,0,0};						// example 8 preferred
			
			sig = new PlSignature();
			sig.add(new Proposition("a"));
			sig.add(new Proposition("b"));
			sig.add(new Proposition("c"));
			possDistInput = new PossibilityDistribution(sig);
			
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("a"));
			setOfProps.add(new Proposition("b"));
			setOfProps.add(new Proposition("c"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[0]);			// a,b,c
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("a"));
			setOfProps.add(new Proposition("b"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[1]);			// a,b,-c
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("a"));
			setOfProps.add(new Proposition("c"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[2]);			// a,-b,c
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("a"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[3]);			// a,-b,-c
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("b"));
			setOfProps.add(new Proposition("c"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[4]);			// a,-b,-c
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("b"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[5]);			// -a,b,-c
			setOfProps = new ArrayList<Proposition>();
			setOfProps.add(new Proposition("c"));
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[6]);			// -a,-b,c
			setOfProps = new ArrayList<Proposition>();
			possDistInput.setPossibility(new PossibleWorld(setOfProps), startValues[7]);			// -a,-b,-c
			
		}
		
		System.out.println(possDistInput);
		
		
		/*
		 * Step 3: Create ranking of arguments
		 */
		System.out.println("");
		System.out.println("------------------------------------------");
		System.out.println("");
		System.out.println("Step 3: Order all arguments (and their negation) by the possibility measure of their (negated) acceptance condition");	
		System.out.println("");
		ConverterADF2CL converter = new ConverterADF2CL();
		HashMap<PlFormula,Double> map_possibility = new HashMap<PlFormula,Double>();
		HashMap<PlFormula,Double> map_necessity = new HashMap<PlFormula,Double>();
		
		for (Argument ar : adf.getArguments()) {
			AcceptanceCondition ac = adf.getAcceptanceCondition(ar);
			PlFormula ac_form = converter.getFormulaFromAcc(ac);
			PlFormula ar_form = new Proposition(ar.getName());
			map_possibility.put(ar_form, possDistInput.getPossibility(ac_form));
			map_possibility.put(new Negation(ar_form), possDistInput.getPossibility(new Negation(ac_form)));
			map_necessity.put(ar_form, possDistInput.getNecessity(ac_form));
			map_necessity.put(new Negation(ar_form), possDistInput.getNecessity(new Negation(ac_form)));

		}
		System.out.println("Possibility: " + map_possibility);
		System.out.println("Necessity: " + map_necessity);
		
	    Map<PlFormula, Double> map_possibility_sorted = sortByValue(map_possibility);
		System.out.println("Possibility sorted: " + map_possibility_sorted);	
	    Map<PlFormula, Double> map_necessity_sorted = sortByValue(map_necessity);
		System.out.println("Necessity sorted: " + map_necessity_sorted);	
		
		
		/*
		 * Step 4: Create new Possibility Distribution
		 */
		System.out.println("");
		System.out.println("------------------------------------------");
		System.out.println("");
		System.out.println("Step 4: Create new Possibility distribution:");		
		System.out.println("");
		
		PossibilityDistribution possDistOutput = new PossibilityDistribution(sig);
		
		Collection<PlFormula> currentSet = new ArrayList<PlFormula>();
	    Collection<PlFormula> runningSet = new ArrayList<PlFormula>();
	    
	    for (Map.Entry<PlFormula, Double> entry : map_possibility_sorted.entrySet()) {
	    	System.out.println("");
	    	System.out.println("Next Running Set: " + runningSet);
	    	
	    	System.out.println("Argument: " + entry.getKey() + " with possibility of AC: " + entry.getValue() + " is satisfied by world: ");
	    	currentSet = new ArrayList<PlFormula>(runningSet);
	    	currentSet.add(entry.getKey());
	    	for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds(sig.toCollection())) {
	    		if (w.satisfies(currentSet)) {
	    			System.out.println(w);
	    			possDistOutput.setPossibility(w, entry.getValue());
	    			}
	    		}
	    	runningSet.add(new Negation(entry.getKey()));
	    	}

		System.out.println("");
	    System.out.println("-------------------------------------------------------------");
		System.out.println("");
		System.out.println("Output Probability Distribution: ");
		System.out.println("");
		
		System.out.println(possDistOutput);

	}


    /** Default Constructor */
    public PossibilisticGammaOperator(){}
}
