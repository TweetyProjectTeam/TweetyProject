package org.tweetyproject.arg.adf.reasoner.sat.decomposer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.InterpretationIterator;
import org.tweetyproject.arg.adf.semantics.interpretation.TwoValuedInterpretationIterator;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

abstract class AbstractDecomposer implements Decomposer {
	
	@Override
	public Collection<Interpretation> decompose(AbstractDialecticalFramework adf, int desired) {
		Set<Argument> arguments = partition(adf, arguments(adf, desired, 3));
		Iterator<Interpretation> interpretations = new InterpretationIterator(arguments);
		
		List<Interpretation> prefixes = new ArrayList<>();
		while(interpretations.hasNext()) {
			prefixes.add(interpretations.next());
		}
		return prefixes;
	}
	
	/**
	 * 
	 * @return asTwoValued
	 */
	public Decomposer asTwoValued() {
		return new TwoValuedDecomposer();
	}
	
	private static int arguments(AbstractDialecticalFramework adf, int desired, int truthValues) {
		return Math.min( adf.size(), (int) Math.round( Math.log( desired ) / Math.log(truthValues) ) );
	}

	abstract Set<Argument> partition(AbstractDialecticalFramework adf, int size);
	
	private final class TwoValuedDecomposer implements Decomposer {
			
		@Override
		public Collection<Interpretation> decompose(AbstractDialecticalFramework adf, int desired) {
			Set<Argument> arguments = partition(adf, arguments(adf, desired, 2));
			Iterator<Interpretation> interpretations = new TwoValuedInterpretationIterator(arguments);
			
			List<Interpretation> prefixes = new ArrayList<>();
			while(interpretations.hasNext()) {
				prefixes.add(interpretations.next());
			}
			return prefixes;
		}
		
	}

}
