package org.tweetyproject.web.services.spring.src.main.java.com.pyargservices;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

public class DungReasonerGetModelCallee extends DungReasonerCallee {
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
