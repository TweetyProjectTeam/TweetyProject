package org.tweetyproject.arg.dung.serialisibility.equivalence;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.serialisibility.syntax.ReducedFrameworksSequence;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class SerialisationEquivalenceByReductions extends SerialisationEquivalence<Collection<ReducedFrameworksSequence>> {

	private SerialisableExtensionReasoner reasoner;
	
	public SerialisationEquivalenceByReductions(Equivalence<Collection<ReducedFrameworksSequence>> comparator,
			SerialisableExtensionReasoner reasoner) {
		super(comparator);
		this.reasoner = reasoner;
	}

	@Override
	protected DungTheory getFramework(Collection<ReducedFrameworksSequence> object) {
		return object.iterator().next().getFirst();
	}

	@Override
	protected Collection<ReducedFrameworksSequence> getRelevantAspect(DungTheory framework) {
		var output = new HashSet<ReducedFrameworksSequence>();
		for(var sequence : this.reasoner.getModelsSequences(framework)) {
			output.add(new ReducedFrameworksSequence(framework, sequence));
		}
		
		return output;
	}
}
