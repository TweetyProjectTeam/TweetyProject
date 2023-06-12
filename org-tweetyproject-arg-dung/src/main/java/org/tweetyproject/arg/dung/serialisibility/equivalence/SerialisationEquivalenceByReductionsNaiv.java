package org.tweetyproject.arg.dung.serialisibility.equivalence;

import java.util.Collection;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.serialisibility.syntax.ReducedFrameworksSequence;

public class SerialisationEquivalenceByReductionsNaiv implements Equivalence<Collection<ReducedFrameworksSequence>>{

	@Override
	public boolean isEquivalent(Collection<ReducedFrameworksSequence> obj1,
			Collection<ReducedFrameworksSequence> obj2) {
		return obj1.equals(obj2);
	}

	@Override
	public boolean isEquivalent(Collection<Collection<ReducedFrameworksSequence>> objects) {
		var first = objects.iterator().next();
		for (var collectionOfReductionSeqs : objects) {
			if(collectionOfReductionSeqs == first) continue;
			if(!isEquivalent(collectionOfReductionSeqs, first)) 
				return false;
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "serialReductSequenceNaivEQ";
	}

}
