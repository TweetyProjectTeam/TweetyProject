package org.tweetyproject.web.pyargservices.dung;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.web.pyargservices.Callee;

public class DungReasonerGetModelCallee extends Callee {
		AbstractExtensionReasoner reasoner;
		DungTheory bbase;
		public DungReasonerGetModelCallee(AbstractExtensionReasoner reasoner, DungTheory bbase){
			this.reasoner = reasoner;
			this.bbase = bbase;
		}
		@Override
		public  Extension<DungTheory> call() throws Exception {
			return this.reasoner.getModel(this.bbase);
		}
	
}
