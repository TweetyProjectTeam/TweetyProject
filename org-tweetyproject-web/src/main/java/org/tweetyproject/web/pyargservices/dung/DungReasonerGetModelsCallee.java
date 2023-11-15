package org.tweetyproject.web.pyargservices.dung;

import java.util.Collection;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.pyargservices.Callee;

public class DungReasonerGetModelsCallee extends Callee {
		AbstractExtensionReasoner reasoner;
		DungTheory bbase;
		public DungReasonerGetModelsCallee(AbstractExtensionReasoner reasoner, DungTheory bbase){
			this.reasoner = reasoner;
			this.bbase = bbase;
		}
		@Override
		public Collection<Extension<DungTheory>> call() throws Exception {
			return this.reasoner.getModels(this.bbase);
		}
	
}
