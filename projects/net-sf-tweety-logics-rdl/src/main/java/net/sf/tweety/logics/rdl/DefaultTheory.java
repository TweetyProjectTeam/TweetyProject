package net.sf.tweety.logics.rdl;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

/**
 * Models a default theory in Reiter's default logic, see [R. Reiter. A logic for default reasoning. Artificial Intelligence, 13:81–132, 1980].
 * 
 * @author Matthias Thimm
 *
 */
public class DefaultTheory implements BeliefBase{

	/** The set of facts (first-order formulas). */
	private FolBeliefSet facts;
	/** The set of default rules */
	private Collection<DefaultRule> defaults;
	
	
	
	public DefaultTheory(FolBeliefSet facts, Collection<DefaultRule> defaults) {
		super();
		this.facts = facts;
		this.defaults = new ArrayList<DefaultRule>();
		this.defaults.addAll(defaults);
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
	
	
}
