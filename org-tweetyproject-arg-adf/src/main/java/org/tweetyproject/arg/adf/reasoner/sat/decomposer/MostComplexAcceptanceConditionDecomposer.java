package org.tweetyproject.arg.adf.reasoner.sat.decomposer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Returns the arguments with the most complex acceptance condition, according to their logical complexity.
 * 
 * @author Mathias Hofer
 *
 */
public class MostComplexAcceptanceConditionDecomposer extends AbstractDecomposer {

	public MostComplexAcceptanceConditionDecomposer(AbstractDialecticalFramework adf) {
		super(adf);
	}

	@Override
	Set<Argument> partition(AbstractDialecticalFramework adf, int size) {
		Map<Argument, Integer> lcMap = new HashMap<>();
		for (Argument arg : adf.getArguments()) {
			lcMap.put(arg, lc(adf.getAcceptanceCondition(arg)));
		}
		
		Set<Argument> mostComplex = lcMap.entrySet().stream()
				.sorted(Comparator.comparing(Entry::getValue, Comparator.reverseOrder()))
				.limit(size)
				.map(Entry::getKey)
				.collect(Collectors.toSet());
		
		return mostComplex;
	}
	
	private static int lc(AcceptanceCondition acc) {
		int childrenCompexity = 0;
		for(AcceptanceCondition child : acc.getChildren()) {
			childrenCompexity += lc(child);
		}
		return 1 + childrenCompexity;
	}

}
