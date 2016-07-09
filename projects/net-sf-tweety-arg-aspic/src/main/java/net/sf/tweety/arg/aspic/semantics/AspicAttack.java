package net.sf.tweety.arg.aspic.semantics;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.aspic.syntax.AspicNegation;
import net.sf.tweety.arg.dung.syntax.Attack;

public class AspicAttack extends Attack {
	
	/** The binary ordring to determine if attacks are successfull **/
	private Comparator<AspicArgument> order = STD_ORDER;
	private static final Comparator<AspicArgument> STD_ORDER = new Comparator<AspicArgument>() {
		@Override
		public int compare(AspicArgument o1, AspicArgument o2) {
			return 0;
		}
	};
	
	boolean successfull = false, shortcut = false;
	StringWriter sw = new StringWriter();
	
	
	public AspicAttack(AspicArgument active, AspicArgument passive) {
		super(active, passive);
	}
	
	public static Collection<AspicAttack> determineAttackRelations(Collection<AspicArgument> args) {
		return determineAttackRelations(args, STD_ORDER);
	}
	
	public static Collection<AspicAttack> determineAttackRelations(Collection<AspicArgument> args, Comparator<AspicArgument> order) {
		Collection<AspicAttack> successfull = new ArrayList<>();
		for (AspicArgument active : args) 
			for (AspicArgument passive : args)
				if (active != passive) {
					AspicAttack a = new AspicAttack(active, passive);
					a.setOrder(order);
					a.attack(true);
					if(a.isSuccessfull())
						successfull.add(a);
				}
		return successfull;
	}
	
	private boolean setResult() {
		successfull = true;
		sw.write("\t=> defeat\n");
		return shortcut;
	}
	
	public boolean isSuccessfull() {
		return successfull;
	}

	public String getOutput() {
		return sw.toString();
	}
	
	private void nl() {
		sw.write("\n");
	}
	
	public void attack(boolean shortcut) {
		this.shortcut = shortcut;
		attack();
	}
	
	public void attack() {
		AspicArgument active = (AspicArgument)getAttacker(),
				passive = (AspicArgument)getAttacked();
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
		sw.write(active + (successfull? " defeats " : " does not defeat ") + passive);
	}

	public void setOrder(Comparator<AspicArgument> order) {
		if(order !=null)
			this.order = order;
	}

	@Override
	public String toString() {
		return getAttacker() + " attacks " + getAttacked();
	}
	
	
	


}
