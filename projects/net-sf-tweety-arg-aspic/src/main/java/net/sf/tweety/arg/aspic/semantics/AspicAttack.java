package net.sf.tweety.arg.aspic.semantics;

import java.util.Comparator;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicInferenceRule;
import net.sf.tweety.arg.aspic.syntax.AspicNegation;

public class AspicAttack {
	
	Comparator<AspicArgument> order = new Comparator<AspicArgument>() {
		@Override
		public int compare(AspicArgument o1, AspicArgument o2) {
			return 0;
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
