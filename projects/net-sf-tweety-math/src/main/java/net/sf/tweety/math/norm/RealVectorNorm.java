package net.sf.tweety.math.norm;

import java.util.Vector;

import net.sf.tweety.math.term.Term;

/**
 * A norm for real vector spaces.
 * @author Matthias Thimm
 *
 */
public interface RealVectorNorm extends Norm<Vector<Double>> {

	/**
	 * Returns the norm as a term of the given terms
	 * @param obj some term vector
	 * @return the term of the norm
	 */
	public Term normTerm(Vector<Term> obj);
	
	/**
	 * Returns the norm as a term of the given terms
	 * @param obj some term array
	 * @return the term of the norm
	 */
	public Term normTerm(Term[] obj);
	
	/**
	 * The distance between the two objects as a term.
	 * @param obj1 some terms
	 * @param obj2 some terms
	 * @return the distance between the two objects as a term
	 */
	public Term distanceTerm(Vector<Term> obj1, Vector<Term> obj2);
	
	/**
	 * The distance between the two objects as a term.
	 * @param obj1 some terms
	 * @param obj2 some terms
	 * @return the distance between the two objects as a term
	 */
	public Term distanceTerm(Term[] obj1, Term[] obj2);
}
