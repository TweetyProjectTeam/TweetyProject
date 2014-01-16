package net.sf.tweety.math.norm;

import java.util.Vector;

import net.sf.tweety.math.term.Term;

/**
 * Abstract class for real vector norms.
 * 
 * @author Matthias Thimm
 *
 */
public abstract class AbstractRealVectorNorm implements RealVectorNorm {
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#normTerm(java.util.Vector)
	 */
	public abstract Term normTerm(Vector<Term> obj);
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#normTerm(net.sf.tweety.math.term.Term[])
	 */
	public Term normTerm(Term[] obj){
		Vector<Term> v = new Vector<Term>();
		for(Term t: obj)
			v.add(t);
		return this.normTerm(v);
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#distanceTerm(java.util.Vector, java.util.Vector)
	 */
	public abstract Term distanceTerm(Vector<Term> obj1, Vector<Term> obj2);
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.norm.RealVectorNorm#distanceTerm(net.sf.tweety.math.term.Term[], net.sf.tweety.math.term.Term[])
	 */
	public Term distanceTerm(Term[] obj1, Term[] obj2){
		Vector<Term> v1 = new Vector<Term>();
		for(Term t: obj1)
			v1.add(t);
		Vector<Term> v2 = new Vector<Term>();
		for(Term t: obj2)
			v2.add(t);
		return this.distanceTerm(v1,v2);
		
	}
}
