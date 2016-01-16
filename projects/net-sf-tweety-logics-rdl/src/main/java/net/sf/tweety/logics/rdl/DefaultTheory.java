package net.sf.tweety.logics.rdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

/**
 * Models a default theory in Reiter's default logic, see [R. Reiter. A logic for default reasoning. Artificial Intelligence, 13:81–132, 1980].
 * 
 * @author Matthias Thimm, Nils Geilen
 *
 */
public class DefaultTheory implements BeliefBase{

	/** The set of facts (first-order formulas). */
	private FolBeliefSet facts;
	/** The set of default rules */
	private Collection<DefaultRule> defaults;
	
	
	
	public Collection<DefaultRule> getDefaults() {
		return defaults;
	}

	public DefaultTheory(FolBeliefSet facts, Collection<DefaultRule> defaults) {
		super();
		this.facts = facts;
		this.defaults = new ArrayList<DefaultRule>();
		this.defaults.addAll(defaults);
	}
	
	public DefaultTheory extend(){
		Set<DefaultRule> ds = new HashSet<>();
		for(DefaultRule d: defaults){
			if(d.getUnboundVariables().isEmpty())
				ds.add(d);
			else{
				//System.out.println(d);
				Set<DefaultRule> old = new HashSet<>(), 
						open = new HashSet<>();
				old.add(d);
				for(Variable v:d.getUnboundVariables()){
					// signature correct ?
					for(Constant c:((FolSignature)facts.getSignature()).getConstants())
						for(DefaultRule r:old)
							try{
								open.add((DefaultRule)r.substitute(v, c));
							}catch(Exception e){}
					old =open;
					open = new HashSet<>();
				}
				ds.addAll(old);
				//System.out.println(old);
			}
		}
		return new DefaultTheory(facts, ds);
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		// Use FolSignature 
		return facts.getSignature();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String result = facts +"\n\n";
		for(DefaultRule d : defaults)
			result += d + "\n";
		return result;
	}
	
	public FolBeliefSet getFacts(){
		return facts;
	}
	
	
}
