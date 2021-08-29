package org.tweetyproject.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.ClaimBasedTheory;

public class SimpleClStableReasoner {


	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */
	public Set<Set<String>> getModels(ArgumentationFramework<Argument> bbase) {

		Collection<Extension> admissibleExtensions = new SimpleAdmissibleReasoner().getModels(bbase);
		Set<Set<String>> result = new HashSet<Set<String>>();
		for(Extension e: admissibleExtensions) {
			Set<String> defeatedByExtension = ((ClaimBasedTheory) bbase).defeats(e);
			HashSet<String> allClaims = ((ClaimBasedTheory) bbase).getClaims();
			allClaims.removeAll(defeatedByExtension);
			if(allClaims.equals(((ClaimBasedTheory)bbase).getClaims(e)))
					result.add(((ClaimBasedTheory)bbase).getClaims(e));
		}
				
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
	 */

	public Set<String> getModel(ArgumentationFramework<Argument> bbase) {
		Collection<Extension> admissibleExtensions = new SimpleAdmissibleReasoner().getModels(bbase);

		for(Extension e: admissibleExtensions) {
			Set<String> defeatedByExtension = ((ClaimBasedTheory) bbase).defeats(e);
			HashSet<String> allClaims = ((ClaimBasedTheory) bbase).getClaims();
			allClaims.removeAll(defeatedByExtension);
			if(allClaims.equals(((ClaimBasedTheory)bbase).getClaims(e)))
					return ((ClaimBasedTheory)bbase).getClaims(e);
		}
		return null;	
	}		
}
