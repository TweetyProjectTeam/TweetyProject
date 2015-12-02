package net.sf.tweety.logics.rdl;

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
	
	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		// Use FolSignature 
		return null;
	}
}
