package net.sf.tweety.logics.dl.syntax;

import net.sf.tweety.Formula;
import net.sf.tweety.Signature;

/**
 * This abstract class provides the base for terminological and assertional axioms
 * 
 * @author Bastian Wolf
 *
 */

public abstract class Axiom implements Formula {

	public Axiom() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

}
