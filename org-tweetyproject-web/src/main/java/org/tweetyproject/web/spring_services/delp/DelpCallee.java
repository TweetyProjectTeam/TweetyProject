package org.tweetyproject.web.spring_services.delp;

import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.DelpAnswer;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.web.spring_services.Callee;

public class DelpCallee extends Callee {
    DefeasibleLogicProgram delp;
    DelpReasoner reasoner;
    Formula f;
		public DelpCallee(DefeasibleLogicProgram delp,DelpReasoner reasoner,Formula f){
			this.delp = delp;
			this.reasoner = reasoner;
            this.f = f;
		}
		@Override
		public DelpAnswer.Type call() throws Exception {
			return this.reasoner.query(this.delp,(FolFormula) this.f);
		}

}
