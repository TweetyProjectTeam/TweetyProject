package org.tweetyproject.web.pyargservices.aba;



import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.web.pyargservices.Callee;

public class  AbaReasonerGetModelCallee<T extends Formula> extends Callee {
		GeneralAbaReasoner<T> reasoner;
		AbaTheory<T> bbase;
		public  AbaReasonerGetModelCallee(GeneralAbaReasoner<T> reasoner, AbaTheory<T> bbase){
			this.reasoner = reasoner;
			this.bbase = bbase;
		}
		@Override
		public AbaExtension<T> call() throws Exception {
			return this.reasoner.getModel(this.bbase);
		}
}
