package net.sf.tweety.arg.delp.syntax;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

/**
 * This class models a strict rule in defeasible logic programming.
 *
 * @author Matthias Thimm
 *
 */
public class StrictRule extends DelpRule {

	/**
	 * Default constructor; initializes head and body of the strict rule
	 * @param head a literal
	 * @param body a set of literals
	 */
	public StrictRule(FolFormula head, Set<FolFormula> body){
		super(head,body);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.DelpRule#toString()
	 */
	public String toString(){
		String str = head.toString()+" <- ";
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

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.delp.DelpRule#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t) throws IllegalArgumentException {
		FolFormula newHead = (FolFormula)((FolFormula)this.getConclusion()).substitute(v,t);
		Set<FolFormula> newBody = new HashSet<FolFormula>();
		for(FolFormula f: this.body)
			newBody.add((FolFormula)f.substitute(v, t));
		return new StrictRule(newHead,newBody);
	}


	@Override
	public RelationalFormula clone() {
		throw new UnsupportedOperationException("IMPLEMENT ME");
	}
}
