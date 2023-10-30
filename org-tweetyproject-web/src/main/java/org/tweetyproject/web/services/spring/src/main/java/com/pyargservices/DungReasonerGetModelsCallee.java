package org.tweetyproject.web.services.spring.src.main.java.com.pyargservices;

import java.util.Collection;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class DungReasonerGetModelsCallee extends DungReasonerCallee {
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
