package net.sf.tweety.arg.delp.syntax;

import java.util.*;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class models a defeasible rule in defeasible logic programming.
 *
 * @author Matthias Thimm
 *
 */
public class DefeasibleRule extends DelpRule {

	/**
	 * Initializes the defeasible rule with the given parameters
	 * @param head a literal
	 * @param body a set of literals
	 */
	public DefeasibleRule(FolFormula head, Set<FolFormula> body){
		super(head,body);
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.DelpRule#toString()
	 */
	public String toString(){
		String str = head.toString()+" -< ";
		Iterator<FolFormula> it = body.iterator();
		if(it.hasNext()){
			FolFormula lit = (FolFormula) it.next();
			str += lit.toString();
		}
		while(it.hasNext()){
			FolFormula lit = (FolFormula) it.next();
			str += ","+lit.toString();
		}
		str += ".";
		return str;
	}

	/**
	 * returns the translation of this rule as a strict rule
	 * @return the translation of this rule as a strict rule
	 */
	public StrictRule toStrictRule(){
		return new StrictRule(this.head,this.body);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.DelpRule#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t)	throws IllegalArgumentException {
		FolFormula newHead = (FolFormula)((FolFormula)this.getConclusion()).substitute(v,t);
		Set<FolFormula> newBody = new HashSet<FolFormula>();
		for(FolFormula f: this.body)
			newBody.add((FolFormula)f.substitute(v, t));
		return new DefeasibleRule(newHead,newBody);
	}

	@Override
	public RelationalFormula clone() {
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}
}
