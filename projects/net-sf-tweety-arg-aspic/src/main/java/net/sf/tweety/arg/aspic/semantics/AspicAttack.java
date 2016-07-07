package net.sf.tweety.arg.aspic.semantics;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Comparator;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicNegation;

public class AspicAttack {
	
	/** The binary ordring to determine if attacks are successfull **/
	Comparator<AspicArgument> order = new Comparator<AspicArgument>() {
		@Override
		public int compare(AspicArgument o1, AspicArgument o2) {
			return 0;
		}
	};
	
	AspicArgument active, passive;
	boolean result = false, shortcut = false;
	StringWriter sw = new StringWriter();
	
	
	public AspicAttack(AspicArgument active, AspicArgument passive) {
		super();
		this.active = active;
		this.passive = passive;
	}
	
	private boolean setResult() {
		result = true;
		sw.write("\t=> defeat\n");
		return shortcut;
	}
	
	public boolean getResult() {
		return result;
	}

	public String getOutput() {
		return sw.toString();
	}
	
	private void nl() {
		sw.write("\n");
	}
	
	public void attack() {
		Collection<AspicArgument> defargs = passive.getDefSubs();
		for (AspicArgument a : defargs)
			if(AspicNegation.negates(active.getConc(), a.getTopRule())) {
				sw.write(active + " undercuts "+ passive + " on " + a);
				nl();
				if (setResult()) return;
			}
		for (AspicArgument a : defargs)
			if(AspicNegation.negates(active.getConc(), a.getConc())) {
				boolean successfull = order.compare(active, a) >= 0;
				sw.write(active + " rebuts "+ passive + " on " + a);
				if(successfull) {
					sw.write(" successfully");
					nl();
					if (setResult()) return;
				}
			}
		for (AspicArgument a : passive.getOrdinaryPremises())
				if(AspicNegation.negates(active.getConc(), a.getConc())) {
					boolean successfull = order.compare(active, a) >= 0;
					sw.write(active + " undermines "+ passive + " on " + a);
					if(successfull) {
						sw.write(" successfully");
						nl();
						if (setResult()) return;
					}
				}
		sw.write(active + (result? " defeats " : " does not defeat ") + passive);
	}

	public void setOrder(Comparator<AspicArgument> order) {
		this.order = order;
	}
	


}
