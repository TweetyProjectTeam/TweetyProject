package org.tweetyproject.arg.adf.reasoner.sat.decomposer;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Takes the arguments with the most bipolar parents.
 * 
 * @author Mathias Hofer
 *
 */
public class MostBipolarParentsDecomposer extends AbstractDecomposer {
	/**
	 * 
	 * @param adf AbstractDialecticalFramework
	 */
	public MostBipolarParentsDecomposer(AbstractDialecticalFramework adf) {
		super(adf);
	}

	@Override
	Set<Argument> partition(AbstractDialecticalFramework adf, int size) {
		Map<Argument, Long> outgoingDependent = adf.linksStream()
				.collect(Collectors.groupingBy(Link::getTo, Collectors.filtering(l -> l.getType().isBipolar(), Collectors.counting())));
		
		Set<Argument> mostDependents = outgoingDependent.entrySet().stream()
				.sorted(Comparator.comparing(Entry::getValue, Comparator.reverseOrder()))
				.limit(size)
				.map(Entry::getKey)
				.collect(Collectors.toSet());	
		
		return mostDependents;
	}

}
