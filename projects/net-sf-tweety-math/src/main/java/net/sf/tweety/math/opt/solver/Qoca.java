package net.sf.tweety.math.opt.solver;

import java.util.*;

import net.sf.tweety.math.equation.*;
import net.sf.tweety.math.opt.*;
import net.sf.tweety.math.term.*;
import net.sf.tweety.util.*;
import qoca.*;

/**
 * This class implements a wrapper for the Qoca constraint satisfaction solver
 * (http://www.csse.monash.edu.au/projects/qoca/).
 * 
 * @author Matthias Thimm
 *
 */
public class Qoca extends Solver {
	
	/**
	 * The maximum number of iterations Qoca tries to
	 * solve a problem.
	 */
	public static final int MAX_ITERATIONS = 100;

	/**
	 * Creates a new solver for the given csp.
	 * @param problem a constraint satisfaction problem.
	 */
	public Qoca(ConstraintSatisfactionProblem problem) {
		super(problem);		
		if(!problem.isLinear())
			throw new IllegalArgumentException("Qoca can only be used for linear constraint satisfaction problems.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#solve()
	 */
	@Override
	public Map<Variable, Term> solve() {
		QcCassSolver solver = new QcCassSolver();		
		Map<Variable,QcFloat> variables = new HashMap<Variable,QcFloat>();
		int i = 0;
		for(Variable v: this.getProblem().getVariables())
			variables.put(v, new QcFloat("X" + i++, 1/this.getProblem().getVariables().size(), true));
		for(Statement s: this.getProblem()){
			QcConstraint constraint = new QcConstraint();
			Pair<QcLinPoly,Double> poly = this.makeQocaPolynom(s.getLeftTerm().minus(s.getRightTerm()),variables);
			if(s instanceof Equation)
				constraint.makeEq(poly.getFirst(), poly.getSecond());
			else if(s instanceof Inequation){
				Inequation ie = (Inequation) s;
				if(ie.getType() == Inequation.GREATER || ie.getType() == Inequation.GREATER_EQUAL)
					constraint.makeGe(poly.getFirst(), poly.getSecond());
				else constraint.makeLe(poly.getFirst(), poly.getSecond());
			}else
				throw new IllegalArgumentException("Unsupported type of statement: " + s.getClass());
			if(!solver.addConstraint(constraint))
				throw new ProblemInconsistentException();
		}
		boolean solved = false;
		int it = 0;
		while(!solved && Qoca.MAX_ITERATIONS >= it++){
			try{
				solver.solve();
				solved = true;
			}
			catch(ExternalPostconditionException e){ }
			catch(InternalPreconditionException e){ }
		}
		if(!solved)
			throw new ProblemInconsistentException();
		Map<Variable,Term> result = new HashMap<Variable,Term>();
		for(Variable v: this.getProblem().getVariables())
			result.put(v, new FloatConstant(new Float(variables.get(v).getSolvedValue())));	
		return result;
	}
	
	/**
	 * Constructs a qoca polynom from the given term (which is assumed to be linear)
	 * @param t a term
	 * @param variables a map from variables to qcFloats.
	 * @return a pair of a qoca polynom and a double denoting the "rest" of the term
	 */
	private Pair<QcLinPoly,Double> makeQocaPolynom(Term t, Map<Variable,QcFloat> variables){
		// we need the term to be in polynomial normal form.
		Sum term = t.toLinearForm();		
		QcLinPoly polynom = new QcLinPoly();
		Double rest = new Double(0);
		for(Term subterm: term.getTerms()){
			if(subterm instanceof Product){
				// there should be exactly two terms in the product;
				// one of the terms should be a constant and the other a variable
				Product p = (Product) subterm;
				if(p.size() == 1){
					if(p.getTerms().get(0) instanceof FloatConstant){
						rest -= new Double(((FloatConstant)p.getTerms().get(0)).getValue());
					}else if(p.getTerms().get(0) instanceof IntegerConstant){
						rest -= new Double(((IntegerConstant)p.getTerms().get(0)).getValue());				
					}else throw new IllegalArgumentException("This should not happen: term is not in linear normal form.");	
				}else{
					if(p.size() != 2)
						throw new IllegalArgumentException("This should not happen: term is not polynomial.");
					Term first = p.getTerms().get(0);
					Term second = p.getTerms().get(1);
					if((first instanceof Variable) && (second instanceof Constant)){
						Variable v = (Variable) first;
						Constant c = (Constant) second;
						polynom.addUniqueTerm(new QcLinPolyTerm(
								new Double((c instanceof FloatConstant)?(((FloatConstant)c).getValue()):(((IntegerConstant)c).getValue())),
								variables.get(v)));
					}else if((second instanceof Variable) && (first instanceof Constant)){
						Variable v = (Variable) second;
						Constant c = (Constant) first;
						polynom.addUniqueTerm(new QcLinPolyTerm(
								new Double((c instanceof FloatConstant)?(((FloatConstant)c).getValue()):(((IntegerConstant)c).getValue())),
								variables.get(v)));
					}else throw new IllegalArgumentException("This should not happen: term is not in linear normal form.");
				}
			}else if(subterm instanceof FloatConstant){
				rest -= new Double(((FloatConstant)subterm).getValue());
			}else if(subterm instanceof IntegerConstant){
				rest -= new Double(((IntegerConstant)subterm).getValue());				
			}else throw new IllegalArgumentException("Unknown type of term.");
		}		
		return new Pair<QcLinPoly,Double>(polynom,rest);
	}
}
