package org.tweetyproject.web.pyargservices.aba;
import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.commons.Formula;

import org.tweetyproject.web.pyargservices.Callee;

public class  AbaReasonerQueryCallee<T extends Formula> extends Callee {
		GeneralAbaReasoner<T> reasoner;
		AbaTheory<T> bbase;
		Assumption<T> assumption;
		public  AbaReasonerQueryCallee(GeneralAbaReasoner<T> reasoner, AbaTheory<T> bbase, Assumption<T> a){
			this.reasoner = reasoner;
			this.bbase = bbase;
			this.assumption = a;
		}
		@Override
		public Boolean call() throws Exception {
			return this.reasoner.query(this.bbase,assumption);
		}
}
