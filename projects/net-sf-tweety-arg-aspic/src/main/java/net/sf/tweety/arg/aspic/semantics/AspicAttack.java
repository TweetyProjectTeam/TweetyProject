package net.sf.tweety.arg.aspic.semantics;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;
import ruleformulagenerator.RuleFormulaGenerator;

/**
 * @author Nils Geilen
 * 
 * Checks whether an argument defeats another argument
 * 
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class AspicAttack<T extends Invertable> extends Attack {
	
	/** The binary ordring to determine if attacks are successfull **/
	private Comparator<AspicArgument<T>> order = new Comparator<AspicArgument<T>>() {
		@Override
		public int compare(AspicArgument<T> o1, AspicArgument<T> o2) {
			return 0;
		}
	};
	
	boolean successfull = false, shortcut = false;
	/**
	 * Logs attack attempts
	 */
	StringWriter sw = new StringWriter();
	/**
	 * Used to transform ASPIC inference rules into words of the language they range over
	 */
	private RuleFormulaGenerator<T> rfgen ;
	
	
	/**
	 * Creates a new AspicAttack
	 * @param active	the attacking argument
	 * @param passive	the attacked argument
	 */
	public AspicAttack(AspicArgument<T> active, AspicArgument<T> passive) {
		super(active, passive);
	}
		
	/**
	 * Checks for defeats in a list of arguments
	 * @param args	a list of arguments
	 * @param order	an comparator which should compare the arguments in args 
	 * @return a list of all tuples (a,b) with a, b in args where a defeats b
	 */
	public static <T extends Invertable> Collection<AspicAttack<T>> determineAttackRelations(Collection<AspicArgument<T>> args, Comparator<AspicArgument<T>> order, RuleFormulaGenerator<T> rfgen) {
		Collection<AspicAttack<T>> successfull = new ArrayList<>();
		for (AspicArgument<T> active : args) 
			for (AspicArgument<T> passive : args)
				if (active != passive) {
					AspicAttack<T> a = new AspicAttack<T>(active, passive);
					a.setOrder(order);
					a.setRuleFormulaGenerator(rfgen);
					a.setShortcut(true);
					a.attack();
					if(a.isSuccessfull())
						successfull.add(a);
				}
		return successfull;
	}
	
	/**
	 * Marks this attack as successfull
	 * @return whether the attack can stop
	 */
	private boolean setResult() {
		successfull = true;
		sw.write("\t=> defeat\n");
		return shortcut;
	}
	
	/**
	 * @return true iff the attck was successfull
	 */
	public boolean isSuccessfull() {
		return successfull;
	}

	/**
	 * @return the log of all attack attempts on all DefSubs of the attacked argument
	 */
	public String getOutput() {
		return sw.toString();
	}
	
	/**
	 * If shortcut is set the attack will stop after a successfulldefeat on one DefSub
	 * @param shortcut	the new shortcut value
	 */
	public void setShortcut(boolean shortcut) {
		this.shortcut = shortcut;
	}

	/**
	 * Writes a new line to the log
	 */
	private void nl() {
		sw.write("\n");
	}
	
	
	/**
	 * Determines whether the attack is successfull
	 */
	@SuppressWarnings("unchecked")
	public void attack() {
		AspicArgument<T> active = (AspicArgument<T>)getAttacker(),
				passive = (AspicArgument<T>)getAttacked();
		Collection<AspicArgument<T>> defargs = passive.getDefSubs();
		for (AspicArgument<T> a : defargs){
			if(active.getConc().equals(rfgen.getRuleFormula(a.getTopRule()).complement())) {
				sw.write(active + " undercuts "+ passive + " on " + a);
				nl();
				if (setResult()) return;
			}
		}
		for (AspicArgument<T> a : defargs)
			if(active.getConc().equals(a.getConc().complement())) {
				boolean successfull = order.compare(active, a) >= 0;
				sw.write(active + " rebuts "+ passive + " on " + a);
				if(successfull) {
					sw.write(" successfully");
					nl();
					if (setResult()) return;
				}
			}
		for (AspicArgument<T> a : passive.getOrdinaryPremises())
				if(active.getConc().equals(a.getConc().complement())) {
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

	/**
	 * Set an order for the arguments to determine if an attack ends in an defeat
	 * @param order	the new order
	 */
	public void setOrder(Comparator<AspicArgument<T>> order) {
		if(order !=null)
			this.order = order;
	}
	
	/**
	 * Set a new generator to transform rules into words of the language they range over
	 * @param rfg	is the new formula generator
	 */
	public void setRuleFormulaGenerator(RuleFormulaGenerator<T> rfg) {
		rfgen = rfg;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.syntax.Attack#toString()
	 */
	@Override
	public String toString() {
		return getAttacker() + " attacks " + getAttacked();
	}
	
	
	


}
