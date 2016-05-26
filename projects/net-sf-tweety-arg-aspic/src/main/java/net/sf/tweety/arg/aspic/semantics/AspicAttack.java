package net.sf.tweety.arg.aspic.semantics;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.arg.aspic.syntax.AspicNegation;

public class AspicAttack {
	
	AspicOrdering order = new AspicOrdering() {
		@Override
		public boolean leq(AspicArgument a, AspicArgument b) {
			return true;
		}
	};
	
	AspicArgument a,b;
	
	boolean defeats(){
		return undercuts() || rebuts() || undermines();
	}
	
	boolean undercuts(){
		for(AspicInferenceRule r: b.getDefRules())
			if(AspicNegation.negates(a.getConc(), r))
				return true;
		return false;
	}
	
	boolean rebuts(){
		for(AspicInferenceRule r: b.getDefRules())
			if(AspicNegation.negates(a.getConc(), r.getConclusion()))
				return true;
		return false;
	}
	
	boolean undermines(){
		for(AspicInferenceRule r:b.getPrems())
			if(r.isDefeasible() && AspicNegation.negates(a.getConc(), r.getConclusion()))
				return true;
		return false;
	}

}
