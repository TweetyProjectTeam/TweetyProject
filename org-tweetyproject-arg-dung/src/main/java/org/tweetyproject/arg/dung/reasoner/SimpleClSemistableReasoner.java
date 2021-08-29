package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;

public class SimpleClSemistableReasoner {


	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	public Set<Set<String>> getModels(ArgumentationFramework<Argument> bbase) {

		Collection<Extension> admissibleExtensions = new SimpleAdmissibleReasoner().getModels(bbase);
		Set<Set<String>> result = new HashSet<Set<String>>();
		for(Extension e: admissibleExtensions) {
			Set<String> defeatedPlusExtension = ((ClaimBasedTheory) bbase).defeats(e);
			defeatedPlusExtension.addAll(((ClaimBasedTheory) bbase).getClaims(e));
			result.add(defeatedPlusExtension);

		}
		Set<Set<String>>resultClone = new HashSet<Set<String>>(result);
		for(Set<String> a : result) {
			for(Set<String> b : result) {
			if(!a.equals(b) && b.containsAll(a))
				resultClone.remove(a);
			}
		}
		return resultClone;	
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */

	public Set<String> getModel(ArgumentationFramework<Argument> bbase) {
		Set<Set<String>> result = getModels(bbase);
		return result.iterator().next();	
	}		
}
